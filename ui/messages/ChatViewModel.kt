package com.example.tripshare.ui.messages

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.MainActivity
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.repo.ChatRepository
import com.example.tripshare.ui.notifications.NotificationHelper
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs
import com.example.tripshare.data.model.Message as DomainMessage

data class MessageUi(
    val message: DomainMessage,
    val senderAvatarUrl: String? = null,
    val isMe: Boolean = false
)

data class ChatUiState(
    val messages: List<MessageUi> = emptyList(),
    val sending: Boolean = false,
    val error: String? = null,
    val currentChatId: String? = null,
    val currentUserAvatarUrl: String? = null
)

class ChatViewModel(
    private val initialChatId: String,
    val currentUserId: String,
    private val repository: ChatRepository,
    private val userDao: UserDao,
    private val appContext: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private var chatListener: ListenerRegistration? = null
    private val _uiState = MutableStateFlow(ChatUiState(currentChatId = initialChatId))
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Chat title shown in UI (usually other user's name)
    private val _chatTitle = MutableStateFlow(initialChatId.ifBlank { "Chat" })
    val chatTitle = _chatTitle.asStateFlow()

    // Snapshot of last domain messages (DB canonical)
    @Volatile
    private var lastDomainMessages: List<DomainMessage> = emptyList()

    // Collector job for message flow
    private var messagesCollectorJob: Job? = null

    // Per-sender observers (senderId -> Job)
    private val perSenderJobs = mutableMapOf<String, Job>()

    // Tuning
    private val TEMP_PREFIX = "temp-"
    private val MATCH_WINDOW_MS = 5000L // +/- 5s window to match optimistic to canonical

    init {
        if (initialChatId.isBlank()) {
            Log.w("ChatViewModel", "ViewModel created without an initialChatId!")
            _uiState.update { it.copy(error = "No chat ID provided.") }
            _chatTitle.value = "Chat"
        } else {
            loadCurrentUserAvatar()
            loadChatTitle(initialChatId)
            loadChat(initialChatId)
        }
    }


    private fun loadCurrentUserAvatar() {
        viewModelScope.launch(ioDispatcher) {
            try {
                val id = currentUserId.toLongOrNull()
                if (id != null) {
                    val user = userDao.findById(id)
                    _uiState.update { it.copy(currentUserAvatarUrl = user?.profilePhoto) }
                }
            } catch (e: Exception) {
                Log.w("ChatViewModel", "Could not load current user avatar: ${e.message}")
            }
        }
    }

    private fun loadChatTitle(chatId: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val room = repository.getRoomById(chatId)
                if (room == null) {
                    Log.w("ChatViewModel", "loadChatTitle: Room not found for id $chatId")
                    _chatTitle.value = "Chat"
                    return@launch
                }

                val otherUserIdStr =
                    if (normalizeId(room.userAId) == normalizeId(currentUserId)) room.userBId else room.userAId

                val otherUserIdLong = otherUserIdStr.toLongOrNull()
                if (otherUserIdLong != null) {
                    val otherUser = userDao.findById(otherUserIdLong)
                    _chatTitle.value = otherUser?.name ?: "Chat"
                } else {
                    Log.w("ChatViewModel", "loadChatTitle: Could not parse other user ID '$otherUserIdStr'.")
                    _chatTitle.value = "Chat"
                }
            } catch (e: Exception) {
                Log.w("ChatViewModel", "Failed to load chat title: ${e.message}")
                _chatTitle.value = "Chat"
            }
        }
    }

    private fun normalizeId(id: String?): String {
        if (id == null) return ""
        var s = id.trim().lowercase(Locale.getDefault())
        s = s.removePrefix("user-")
            .removePrefix("id:")
            .removePrefix("temp-")
        return s
    }

    fun loadChat(chatId: String) {
        Log.d("ChatViewModel", "loadChat($chatId)")
        _uiState.update { it.copy(currentChatId = chatId, error = null) }

        // also refresh title when switching chats
        loadChatTitle(chatId)
        chatListener?.remove()
        // Cancel old collectors & per-sender jobs
        messagesCollectorJob?.cancel()
        perSenderJobs.values.forEach { it.cancel() }
        perSenderJobs.clear()
        chatListener = repository.startDirectChatSync(chatId, viewModelScope)
        // trigger best-effort sync
        viewModelScope.launch(ioDispatcher) {
            try {
                Log.d("ChatViewModel", "Syncing messages for $chatId")
                repository.syncRecentMessages(chatId)
            } catch (e: Exception) {
                Log.w("ChatViewModel", "Sync failed for $chatId: ${e.message}")
                _uiState.update { it.copy(error = "Sync failed: ${e.localizedMessage}") }
            }
        }

        // debug snapshot once
        viewModelScope.launch(ioDispatcher) {
            try {
                val dbList = repository.getMessagesOnce(chatId)
                Log.d(
                    "ChatDebug",
                    "DB snapshot for chat=$chatId: ${dbList.size} ids=${dbList.map { it.id }} senders=${dbList.map { it.senderId }} statuses=${dbList.map { it.status }}"
                )
            } catch (e: Exception) {
                Log.w("ChatDebug", "getMessagesOnce failed: ${e.message}")
            }
        }

        messagesCollectorJob = viewModelScope.launch(ioDispatcher) {
            try {
                repository.observeMessages(chatId).collect { domainMessages ->
                    lastDomainMessages = domainMessages
                    val senderIds = domainMessages.map { it.senderId }.distinct()
                    Log.d("ChatViewModel", "messages emitted=${domainMessages.size} senders=$senderIds")

                    // Build avatar map via one-shot lookups
                    val avatarMap = mutableMapOf<String, String?>()
                    for (sid in senderIds) {
                        val avatar = try {
                            val parsed = sid.toLongOrNull()
                            if (parsed != null) userDao.findById(parsed)?.profilePhoto else null
                        } catch (t: Throwable) {
                            Log.w("ChatViewModel", "avatar lookup failed for $sid: ${t.message}")
                            null
                        }
                        avatarMap[sid] = avatar
                    }

                    // Cancel observers for removed senders
                    val removed = perSenderJobs.keys - senderIds.toSet()
                    removed.forEach { k ->
                        perSenderJobs.remove(k)?.cancel()
                        Log.d("ChatViewModel", "cancelled observer for $k")
                    }

                    // Start per-sender observers
                    for (sid in senderIds) {
                        if (perSenderJobs.containsKey(sid)) continue

                        val asLong = sid.toLongOrNull()
                        if (asLong != null) {
                            perSenderJobs[sid] = viewModelScope.launch(ioDispatcher) {
                                try {
                                    userDao.observeUserById(asLong).collect { userEntity ->
                                        val newAvatar = userEntity?.profilePhoto
                                        val oldAvatar = avatarMap[sid]
                                        if (oldAvatar != newAvatar) {
                                            avatarMap[sid] = newAvatar
                                            val uiMessages = lastDomainMessages.map { dm ->
                                                MessageUi(
                                                    message = dm,
                                                    senderAvatarUrl = avatarMap[dm.senderId],
                                                    isMe = normalizeId(dm.senderId) == normalizeId(currentUserId)
                                                )
                                            }
                                            _uiState.update { it.copy(messages = uiMessages, error = null) }
                                        }
                                    }
                                } catch (t: Throwable) {
                                    Log.w("ChatViewModel", "per-sender observe failed for $sid: ${t.message}")
                                }
                            }
                            continue
                        }

                        // non-numeric -> try map to numeric id then observe
                        val mappedId = try {
                            val byEmail = if (sid.contains("@")) userDao.findByEmail(sid) else null
                            byEmail?.id ?: userDao.findUserIdByExactName(sid)
                        } catch (_: Throwable) {
                            null
                        }

                        if (mappedId != null) {
                            perSenderJobs[sid] = viewModelScope.launch(ioDispatcher) {
                                try {
                                    userDao.observeUserById(mappedId).collect { userEntity ->
                                        val newAvatar = userEntity?.profilePhoto
                                        val oldAvatar = avatarMap[sid]
                                        if (oldAvatar != newAvatar) {
                                            avatarMap[sid] = newAvatar
                                            val uiMessages = lastDomainMessages.map { dm ->
                                                MessageUi(
                                                    message = dm,
                                                    senderAvatarUrl = avatarMap[dm.senderId],
                                                    isMe = normalizeId(dm.senderId) == normalizeId(currentUserId)
                                                )
                                            }
                                            _uiState.update { it.copy(messages = uiMessages, error = null) }
                                        }
                                    }
                                } catch (t: Throwable) {
                                    Log.w("ChatViewModel", "per-sender observe (mapped) failed for $sid: ${t.message}")
                                }
                            }
                        } else {
                            Log.d("ChatViewModel", "no numeric mapping for sender '$sid', observer not started")
                        }
                    }

                    val authoritativeUi = domainMessages.map { dm ->
                        MessageUi(
                            message = dm,
                            senderAvatarUrl = avatarMap[dm.senderId],
                            isMe = normalizeId(dm.senderId) == normalizeId(currentUserId)
                        )
                    }

                    val previousUi = _uiState.value.messages
                    fun matchesAuthoritative(temp: DomainMessage): Boolean {
                        return domainMessages.any { auth ->
                            auth.senderId == temp.senderId &&
                                    auth.content == temp.content &&
                                    abs(auth.createdAt - temp.createdAt) <= MATCH_WINDOW_MS
                        }
                    }

                    val unmatchedTemps = previousUi
                        .filter { it.message.id.startsWith(TEMP_PREFIX) && !matchesAuthoritative(it.message) }
                        .map { mu ->
                            val avatar = avatarMap[mu.message.senderId] ?: mu.senderAvatarUrl
                            mu.copy(senderAvatarUrl = avatar)
                        }

                    val combined = (authoritativeUi + unmatchedTemps)

                    val seen = mutableSetOf<String>()
                    val deduped = combined.filter { mu ->
                        val sig = if (mu.message.id.isNotBlank()) mu.message.id
                        else "${mu.message.senderId}|${mu.message.content}|${mu.message.createdAt}"
                        if (seen.contains(sig)) false else {
                            seen.add(sig); true
                        }
                    }

                    val previousIds = previousUi.map { it.message.id }.toSet()
                    val newMessages = deduped.filter { it.message.id !in previousIds }
                    newMessages.lastOrNull { !it.isMe }?.let { showIncomingMessageNotification(it) }

                    _uiState.update { it.copy(messages = deduped, error = null) }
                }
            } catch (t: Throwable) {
                Log.w("ChatViewModel", "messages collector failed: ${t.message}")
                _uiState.update { it.copy(error = t.message ?: "Failed to load messages") }
            }
        }
    }

    fun sendMessage(text: String) {
        val chatId = _uiState.value.currentChatId ?: run {
            Log.w("ChatViewModel", "sendMessage aborted: no current chat")
            return
        }
        if (text.isBlank()) return

        _uiState.update { it.copy(sending = true, error = null) }

        viewModelScope.launch(ioDispatcher) {
            try {
                repository.sendMessage(
                    chatId = chatId,
                    senderId = currentUserId,
                    content = text
                )
                _uiState.update { it.copy(sending = false) }
            } catch (e: Exception) {
                Log.w("ChatViewModel", "sendMessage failed: ${e.message}")
                _uiState.update {
                    it.copy(
                        sending = false,
                        error = "Send failed: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun showIncomingMessageNotification(msg: MessageUi) {
        if (msg.isMe) return

        val title = "New message"
        val body = msg.message.content.takeIf { it.isNotBlank() } ?: "New message in chat"

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("deep_link_type", "chat")
            putExtra("deep_link_id", msg.message.chatId)
        }

        val requestCode = msg.message.id.hashCode()
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        NotificationHelper.showNotification(
            context = appContext,
            channelId = NotificationHelper.CHANNEL_CHAT_MESSAGES,
            id = msg.message.id.hashCode(),
            title = title,
            body = body,
            contentIntent = pendingIntent
        )
    }

    fun retryMessage(message: DomainMessage) {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(error = null) }
            try {
                repository.sendMessage(
                    chatId = message.chatId,
                    senderId = currentUserId,
                    content = message.content
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Retry failed: ${e.localizedMessage}") }
            }
        }
    }

    fun markMessageRead(messageId: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                repository.markMessageRead(messageId)
            } catch (_: Exception) {
                // ignore
            }
        }
    }

    fun switchChat(newChatId: String) {
        if (newChatId == _uiState.value.currentChatId) return
        perSenderJobs.values.forEach { it.cancel() }
        perSenderJobs.clear()
        messagesCollectorJob?.cancel()
        loadChat(newChatId)
    }

    override fun onCleared() {
        super.onCleared()
        chatListener?.remove()
        messagesCollectorJob?.cancel()
        perSenderJobs.values.forEach { it.cancel() }
        perSenderJobs.clear()
    }
}
