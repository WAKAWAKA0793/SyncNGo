package com.example.tripshare.data.remote

/**
 * Small, minimal contract used by the adapter / old ChatRepository variant.
 */
interface RemoteChatApi {
    /**
     * Sends a message to the remote server.
     * Return true on success.
     */
    suspend fun sendMessageRemote(chatId: String, senderId: String, content: String): Boolean

    /**
     * Fetch recent messages for chat. Adapter will convert service DTOs to this.
     */
    suspend fun fetchRecentMessages(chatId: String): List<RemoteMessageDto>
}
data class RemoteMessageDto(
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val createdAt: Long,
    val status: String? = null
)