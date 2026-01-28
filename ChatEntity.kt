package com.example.tripshare.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

enum class MessageStatus { PENDING, SENT, DELIVERED, READ, FAILED }

/**
 * Chat room entity - simple two-party room. For group chats you can extend with participants table.
 */
@Entity(tableName = "chat_rooms")
data class ChatRoom(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String? = null, // optional display name
    val userAId: String,
    val userBId: String,
    val lastMessage: String? = null,
    val updatedAt: Long = Instant.now().toEpochMilli(),
    val isPrivate: Boolean = true
)

/**
 * Message entity
 */
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatRoom::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("chatId"), Index("senderId")]
)
data class Message(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val chatId: String,
    val senderId: String,
    val content: String,
    val createdAt: Long = Instant.now().toEpochMilli(),
    val status: MessageStatus = MessageStatus.PENDING,
    val edited: Boolean = false
)

@Entity(tableName = "group_messages")
data class GroupMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,          // Links message to the specific trip
    val firebaseId: String? = null,
    val senderId: Long,
    val senderName: String,    // Storing name/avatar here for easier display
    val senderAvatar: String?,
    val content: String,
    val type: String = "text",
    val timestamp: Long = System.currentTimeMillis()
)