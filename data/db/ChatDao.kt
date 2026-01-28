// data/db/ChatDao.kt
package com.example.tripshare.data.db



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tripshare.data.model.ChatRoom
import com.example.tripshare.data.model.GroupMessageEntity
import com.example.tripshare.data.model.Message
import com.example.tripshare.data.model.MessageStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatRoomDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoom(room: ChatRoom)

    @Update
    suspend fun updateRoom(room: ChatRoom)

    @Query("SELECT * FROM chat_rooms WHERE id = :id LIMIT 1")
    suspend fun getRoomById(id: String): ChatRoom?

    @Query("SELECT * FROM chat_rooms WHERE (userAId = :userId OR userBId = :userId) ORDER BY updatedAt DESC")
    fun observeRoomsForUser(userId: String): Flow<List<ChatRoom>>

    // membership helper
    @Query("SELECT COUNT(1) FROM chat_rooms WHERE id = :roomId AND (userAId = :userId OR userBId = :userId)")
    suspend fun isUserMember(roomId: String, userId: String): Int

    @Query("DELETE FROM chat_rooms WHERE id = :roomId")
    suspend fun deleteRoom(roomId: String)
}

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessages(messages: List<Message>)

    @Query("SELECT * FROM messages WHERE status = :status")
    suspend fun getMessagesByStatus(status: MessageStatus): List<Message>

    @Update
    suspend fun updateMessage(message: Message)


    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt ASC")
    fun observeMessagesForChat(chatId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE id = :messageId LIMIT 1")
    suspend fun getMessageById(messageId: String): Message?
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt ASC")
    suspend fun getMessagesForChatOnce(chatId: String): List<Message>

    // in data/db/MessageDao.kt (add)
    @Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId AND status != :readStatus")
    suspend fun countUnread(chatId: String, readStatus: MessageStatus = MessageStatus.READ): Int

    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateStatus(messageId: String, status: MessageStatus)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesForChat(chatId: String)
}

@Dao
interface GroupChatDao {
    @Query("SELECT * FROM group_messages WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getMessagesForTrip(tripId: Long): Flow<List<GroupMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: GroupMessageEntity): Long

    // ✅ ADD THIS: Lookup by Firebase ID
    @Query("SELECT * FROM group_messages WHERE firebaseId = :fid LIMIT 1")
    suspend fun getMessageByFirebaseId(fid: String): GroupMessageEntity?

    @Query("DELETE FROM group_messages WHERE tripId = :tripId")
    suspend fun clearMessagesForTrip(tripId: Long)

    @Update
    suspend fun updateMessage(msg: GroupMessageEntity)
}