package com.example.tripshare.ui.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.db.MessageDao
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.repo.ChatRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Map ChatRoom -> ChatRoomUi by looking up the 'other' user's profile photo.
 * - chatRepo.observeRoomsForUser(userIdString) should emit List<ChatRoom>
 * - userDao should expose a suspend function to get User by Long id.
 */
class ChatListViewModel(
    private val chatRepo: ChatRepository,
    private val userDao: UserDao,
    private val messageDao: MessageDao,
    private val currentUserId: Long
) : ViewModel() {

    private val _roomsUi = MutableStateFlow<List<ChatRoomUi>>(emptyList())
    val roomsUi: StateFlow<List<ChatRoomUi>> = _roomsUi

    private var syncListeners: List<ListenerRegistration> = emptyList()

    init {
        observeRooms()
        startSync() // 2. Start sync on init
    }

    private fun startSync() {
        // Pass the String version of ID since Firestore usually uses Strings
        syncListeners = chatRepo.startChatListSync(currentUserId.toString(), viewModelScope)
    }
    private fun observeRooms() {
        val currentUserIdStr = currentUserId.toString()
        viewModelScope.launch {
            chatRepo.observeRoomsForUser(currentUserIdStr).collect { rooms ->
                // map each ChatRoom to ChatRoomUi
                val list = rooms.map { room ->
                    val otherIdStr = when {
                        room.userAId != null && room.userAId != currentUserIdStr -> room.userAId
                        room.userBId != null && room.userBId != currentUserIdStr -> room.userBId
                        // fallback: take whichever is non-null
                        else -> room.userAId ?: room.userBId
                    }

                    // attempt to lookup other user's avatar
                    val avatar = resolveAvatarForUser(otherIdStr, room.id)

                    // compute unread count using DAO if available
                    val unread = try {
                        // If you added countUnread() to MessageDao, use it:
                        messageDao.countUnread(room.id)
                    } catch (_: Throwable) {
                        0
                    }

                    ChatRoomUi(room = room, avatarUrl = avatar, unreadCount = unread)
                }
                _roomsUi.value = list
            }
        }
    }
    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            try {
                chatRepo.deleteChat(chatId)
                // The observeRoomsForUser flow will automatically emit the new list
                // without the deleted chat, updating the UI.
            } catch (e: Exception) {
                // Optional: Handle error (e.g., expose a SharedFlow for toast messages)
                e.printStackTrace()
            }
        }
    }

    private suspend fun resolveAvatarForUser(otherIdStr: String?, roomId: String): String {
        if (otherIdStr.isNullOrBlank()) {
            return fallbackAvatar(roomId)
        }

        // try to parse as Long (your UserEntity uses Long ids)
        val otherIdLong = otherIdStr.toLongOrNull()
        return if (otherIdLong != null) {
            try {
                val user = userDao.findById(otherIdLong) // adapt method name if different
                user?.profilePhoto ?: fallbackAvatar(roomId)
            } catch (_: Throwable) {
                fallbackAvatar(roomId)
            }
        } else {
            fallbackAvatar(roomId)
        }
    }

    private fun fallbackAvatar(roomId: String): String =
        "https://i.pravatar.cc/64?u=$roomId"


override fun onCleared() {
    super.onCleared()
    // 3. Stop listeners to prevent memory leaks
    syncListeners.forEach { it.remove() }
}
}