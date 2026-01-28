package com.example.tripshare.ui.messages

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.MainActivity
import com.example.tripshare.data.model.GroupMessageEntity
import com.example.tripshare.data.repo.ChatRepository
import com.example.tripshare.ui.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupChatViewModel(
    private val repo: ChatRepository,
    private val tripId: Long,
    private val tripName: String,          // ✅ add this
    private val currentUserId: Long,
    private val currentUserName: String,
    private val currentUserAvatar: String?,
    private val context: Context
) : ViewModel() {

    // In GroupChatViewModel.kt

    init {
        // ✅ PASS currentUserId here
        repo.startGroupChatSync(tripId, currentUserId, viewModelScope)
    }

    private val _tripIdFlow = MutableStateFlow(tripId)

    // 👈 2. Track seen messages to prevent notifying on old ones
    private val notifiedMessageIds = mutableSetOf<Long>()

    val messages: StateFlow<List<GroupMessageEntity>> = _tripIdFlow
        .flatMapLatest { id -> repo.getGroupMessages(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repo.ensureGroupRoomLocal(
                currentUserId = currentUserId,
                tripId = tripId,
                tripName = tripName
            )
        }
        // 👈 3. Observe messages to trigger notifications
        viewModelScope.launch {
            messages.collect { currentList ->
                if (currentList.isEmpty()) return@collect

                // First load: Mark all existing messages as seen (don't notify)
                if (notifiedMessageIds.isEmpty()) {
                    notifiedMessageIds.addAll(currentList.map { it.id })
                    return@collect
                }

                // Check for NEW messages
                currentList.forEach { msg ->
                    if (msg.id !in notifiedMessageIds) {
                        notifiedMessageIds.add(msg.id)

                        // If message is NOT from me, show notification
                        if (msg.senderId != currentUserId) {
                            showNotification(msg)
                        }
                    }
                }
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repo.sendGroupMessage(
                tripId = tripId,
                tripName = tripName,
                currentUserId = currentUserId,
                senderId = currentUserId,
                senderName = currentUserName,
                senderAvatar = currentUserAvatar,
                content = text.trim()
            )
        }
    }


    // 👈 4. Helper to show the notification
    private fun showNotification(msg: GroupMessageEntity) {
        val title = "Group: ${msg.senderName}"
        val body = msg.content

        // Intent to open the app and go to this group chat
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "group_chat") // Handle this in MainActivity
            putExtra("tripId", tripId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            msg.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        NotificationHelper.showNotification(
            context = context,
            channelId = NotificationHelper.CHANNEL_CHAT_MESSAGES,
            id = msg.id.hashCode(),
            title = title,
            body = body,
            contentIntent = pendingIntent
        )
    }
}