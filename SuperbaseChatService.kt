package com.example.tripshare.data.remote

import com.example.tripshare.data.model.ChatRoom
import com.example.tripshare.data.model.Message
import com.example.tripshare.data.model.MessageStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

// Minimal example: uses HTTP to call Supabase REST endpoints (table = messages, chat_rooms).
// For production prefer official SDK or Retrofit + authentication.
class SupabaseChatService(
    private val supabaseUrl: String,
    private val supabaseKey: String
) : com.example.tripshare.data.remote.RemoteChatService {

    private fun baseHeaders(): Map<String, String> = mapOf(
        "apikey" to supabaseKey,
        "Authorization" to "Bearer $supabaseKey",
        "Content-Type" to "application/json"
    )

    override suspend fun sendMessageRemote(message: Message): Message = withContext(Dispatchers.IO) {
        // POST to "messages" table
        val endpoint = "$supabaseUrl/rest/v1/messages"
        val json = JSONObject().apply {
            put("id", message.id.ifBlank { UUID.randomUUID().toString() })
            put("chat_id", message.chatId)
            put("sender_id", message.senderId)
            put("content", message.content)
            put("created_at", message.createdAt)
            put("status", message.status.name)
        }

        val url = URL(endpoint)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            baseHeaders().forEach { (k, v) -> setRequestProperty(k, v) }
            // Optionally: Prefer "Prefer: return=representation" to get inserted row back
            setRequestProperty("Prefer", "return=representation")
        }

        conn.outputStream.use { it.write(json.toString().toByteArray()) }

        val code = conn.responseCode
        if (code in 200..299) {
            val resp = conn.inputStream.bufferedReader().readText()
            // Supabase returns an array if return=representation
            val arr = JSONArray(resp)
            if (arr.length() > 0) {
                val obj = arr.getJSONObject(0)
                return@withContext message.copy(
                    id = obj.optString("id", message.id),
                    createdAt = obj.optLong("created_at", System.currentTimeMillis()),
                    status = MessageStatus.SENT
                )
            }
            return@withContext message.copy(status = MessageStatus.SENT, createdAt = System.currentTimeMillis())
        } else {
            val err = conn.errorStream?.bufferedReader()?.readText()
            throw RuntimeException("Supabase send failed: $code / $err")
        }
    }

    override suspend fun fetchRecentMessages(chatId: String, since: Long?): List<Message> = withContext(Dispatchers.IO) {
        val endpoint = "$supabaseUrl/rest/v1/messages?chat_id=eq.$chatId&order=created_at.asc"
        val url = URL(endpoint)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            baseHeaders().forEach { (k, v) -> setRequestProperty(k, v) }
        }
        val code = conn.responseCode
        if (code in 200..299) {
            val resp = conn.inputStream.bufferedReader().readText()
            val arr = JSONArray(resp)
            val out = mutableListOf<Message>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    Message(
                        id = o.optString("id"),
                        chatId = o.optString("chat_id"),
                        senderId = o.optString("sender_id"),
                        content = o.optString("content"),
                        createdAt = o.optLong("created_at", System.currentTimeMillis()),
                        status = try { MessageStatus.valueOf(o.optString("status")) } catch(_:Throwable){ MessageStatus.SENT }
                    )
                )
            }
            return@withContext out
        } else {
            val err = conn.errorStream?.bufferedReader()?.readText()
            throw RuntimeException("Supabase fetch failed: $code / $err")
        }
    }

    override suspend fun createChatRoomRemote(room: ChatRoom): ChatRoom = withContext(Dispatchers.IO) {
        // similar POST to chat_rooms table — omitted for brevity; echo room
        room
    }

    override suspend fun updateMessageStatusRemote(messageId: String, status: MessageStatus) {
        // PATCH endpoint to update status on Supabase (omitted)
    }
}
