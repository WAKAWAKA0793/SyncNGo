// ChatRoomUi.kt
package com.example.tripshare.ui.messages

import com.example.tripshare.data.model.ChatRoom

data class ChatRoomUi(
    val room: ChatRoom,
    val avatarUrl: String,
    val unreadCount: Int
)
