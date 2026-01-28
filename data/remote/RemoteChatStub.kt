package com.example.tripshare.data.remote

import com.example.tripshare.data.model.ChatRoom
import com.example.tripshare.data.model.Message
import com.example.tripshare.data.model.MessageStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Simple in-memory RemoteChatService stub for local development and UI testing.
 *
 * - Simulates small network latency.
 * - Stores messages per chat in a ConcurrentHashMap.
 * - Returns "authoritative" message (server timestamp and optionally server id).
 *
 * Replace with a real RemoteChatService implementation for production.
 */
class RemoteChatStub(
    private val simulatedLatencyMs: Long = 200L
) : RemoteChatService {

    // chatId -> list of messages (kept in insertion order)
    private val messagesStore: MutableMap<String, MutableList<Message>> = ConcurrentHashMap()

    // chatId -> ChatRoom (simple store)
    private val chatRooms: MutableMap<String, ChatRoom> = ConcurrentHashMap()

    override suspend fun sendMessageRemote(message: Message): Message = withContext(Dispatchers.IO) {
        // simulate latency
        delay(simulatedLatencyMs)

        // produce "server-side" id and timestamp if missing
        val serverId = message.id.ifBlank { UUID.randomUUID().toString() }
        val serverTs = System.currentTimeMillis()

        val authoritative = message.copy(
            id = serverId,
            createdAt = serverTs,
            status = MessageStatus.SENT
        )

        // insert into in-memory store
        val list = messagesStore.computeIfAbsent(authoritative.chatId) { mutableListOf() }
        synchronized(list) {
            // ensure no duplicate id (replace if exists)
            val idx = list.indexOfFirst { it.id == authoritative.id }
            if (idx >= 0) list[idx] = authoritative else list.add(authoritative)
        }

        authoritative
    }

    override suspend fun fetchRecentMessages(chatId: String, since: Long?): List<Message> = withContext(Dispatchers.IO) {
        delay(simulatedLatencyMs / 2)
        val list = messagesStore[chatId] ?: emptyList()
        if (since == null) {
            // return a copy to avoid callers mutating internal list
            synchronized(list) { return@withContext list.toList() }
        } else {
            synchronized(list) {
                return@withContext list.filter { it.createdAt > since }.toList()
            }
        }
    }

    override suspend fun createChatRoomRemote(room: ChatRoom): ChatRoom = withContext(Dispatchers.IO) {
        delay(simulatedLatencyMs / 2)
        val serverId = room.id.ifBlank { UUID.randomUUID().toString() }
        val serverRoom = room.copy(id = serverId)
        chatRooms[serverId] = serverRoom
        serverRoom
    }

    override suspend fun updateMessageStatusRemote(messageId: String, status: MessageStatus) = withContext(Dispatchers.IO) {
        delay(simulatedLatencyMs / 3)
        // find message and update status
        messagesStore.values.forEach { list ->
            synchronized(list) {
                val idx = list.indexOfFirst { it.id == messageId }
                if (idx >= 0) {
                    val old = list[idx]
                    list[idx] = old.copy(status = status)
                    return@withContext
                }
            }
        }
        // if not found, no-op in stub
    }

    // Helper: (optional) seed a chat with messages for demo/tests
    fun seedChat(chatId: String, seed: List<Message>) {
        val list = messagesStore.computeIfAbsent(chatId) { mutableListOf() }
        synchronized(list) {
            list.addAll(seed)
        }
    }

    // Optional helper to clear state (useful in tests)
    fun clearAll() {
        messagesStore.clear()
        chatRooms.clear()
    }
}
