package com.example.tripshare.data.remote

import com.example.tripshare.data.model.Message

/**
 * Adapter so ChatRepository (which expects RemoteChatApi) can use RemoteChatService.
 */
class RemoteChatApiAdapter(
    private val service: RemoteChatService
) : RemoteChatApi {
    override suspend fun sendMessageRemote(chatId: String, senderId: String, content: String): Boolean {
        // Build a domain Message (local id will be replaced by service when it returns authoritative message)
        val local = Message(
            id = java.util.UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = senderId,
            content = content,
            createdAt = System.currentTimeMillis()
        )
        return try {
            val authoritative = service.sendMessageRemote(local)
            authoritative != null
        } catch (t: Throwable) {
            false
        }
    }

    override suspend fun fetchRecentMessages(chatId: String): List<RemoteMessageDto> {
        // Map Message -> RemoteMessageDto expected by RemoteChatApi.fetchRecentMessages (if used)
        val list = service.fetchRecentMessages(chatId)
        return list.map { msg ->
            RemoteMessageDto(
                id = msg.id,
                chatId = msg.chatId,
                senderId = msg.senderId,
                content = msg.content,
                createdAt = msg.createdAt,
                status = msg.status.name
            )
        }
    }
}
