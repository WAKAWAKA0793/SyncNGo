package com.example.tripshare.data.remote

import com.example.tripshare.data.model.ChatRoom
import com.example.tripshare.data.model.Message
import com.example.tripshare.data.model.MessageStatus

interface RemoteChatService {
    /**
     * Sends message to remote backend (persist + fanout) and returns the authoritative message (with server timestamp or id).
     * Should throw an exception on failure.
     */
    suspend fun sendMessageRemote(message: Message): Message

    /**
     * Fetch unread / recent messages for a chat (pagination omitted for brevity).
     * Return messages sorted ascending by createdAt.
     */
    suspend fun fetchRecentMessages(chatId: String, since: Long? = null): List<Message>

    /**
     * Create chat room on backend if not exists.
     */
    suspend fun createChatRoomRemote(room: ChatRoom): ChatRoom

    /**
     * Optionally mark message as read/delivered on server.
     */
    suspend fun updateMessageStatusRemote(messageId: String, status: MessageStatus)
}