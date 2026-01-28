package com.example.tripshare.data.repo

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.tripshare.data.db.ChatRoomDao
import com.example.tripshare.data.db.GroupChatDao
import com.example.tripshare.data.db.MessageDao
import com.example.tripshare.data.db.TripDao
import com.example.tripshare.data.model.ChatRoom
import com.example.tripshare.data.model.GroupMessageEntity
import com.example.tripshare.data.model.Message
import com.example.tripshare.data.model.MessageStatus
import com.example.tripshare.data.remote.RemoteChatService
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID


class ChatRepository(
    private val chatRoomDao: ChatRoomDao,
    private val messageDao: MessageDao,
    private val groupChatDao: GroupChatDao,
    private val remote: RemoteChatService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val tripDao: TripDao, // 👈 Add TripDao
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getGroupMessages(tripId: Long): Flow<List<GroupMessageEntity>> {
        return groupChatDao.getMessagesForTrip(tripId)
    }
    // In ChatRepository.kt
    private fun dmRoomRef(chatId: String) =
        firestore.collection("dm_rooms").document(chatId)

    private fun dmMessagesRef(chatId: String) =
        dmRoomRef(chatId).collection("messages")

    fun startDirectChatSync(chatId: String, scope: CoroutineScope): ListenerRegistration {
        // Listen to cloud changes and upsert into Room
        return dmMessagesRef(chatId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshots, e ->
                if (e != null || snapshots == null) return@addSnapshotListener
                if (snapshots.metadata.hasPendingWrites()) return@addSnapshotListener

                scope.launch(Dispatchers.IO) {
                    for (doc in snapshots.documents) {
                        val mid = doc.id
                        val senderId = doc.getString("senderId") ?: continue
                        val content = doc.getString("content") ?: ""
                        val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

                        // upsert into local Room (Message has PK id, REPLACE in DAO) :contentReference[oaicite:4]{index=4}
                        messageDao.insertMessage(
                            Message(
                                id = mid,
                                chatId = chatId,
                                senderId = senderId,
                                content = content,
                                createdAt = createdAt,
                                status = MessageStatus.SENT
                            )
                        )
                    }
                }
            }
    }

    suspend fun sendDirectMessageFirestore(chatId: String, senderId: String, content: String) =
        withContext(ioDispatcher) {

            // ensure room exists locally so it appears in list (ChatRoom PK is id) :contentReference[oaicite:5]{index=5}
            val existingRoom = chatRoomDao.getRoomById(chatId)
            if (existingRoom == null) {
                // best-effort placeholder; your openDirectChat already creates correct userA/userB :contentReference[oaicite:6]{index=6}
                chatRoomDao.insertRoom(
                    ChatRoom(
                        id = chatId,
                        userAId = senderId,
                        userBId = "",
                        title = null,
                        lastMessage = null,
                        updatedAt = System.currentTimeMillis(),
                        isPrivate = true
                    )
                )
            }

            // optimistic local insert
            val localId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            messageDao.insertMessage(
                Message(
                    id = localId,
                    chatId = chatId,
                    senderId = senderId,
                    content = content,
                    createdAt = now,
                    status = MessageStatus.PENDING
                )
            )

            // write to firestore using stable message id (recommended)
            val cloudId = localId
            val msgMap = hashMapOf(
                "senderId" to senderId,
                "content" to content,
                "createdAt" to now
            )

            // update room doc (metadata)
            dmRoomRef(chatId).set(
                hashMapOf(
                    "lastMessage" to content,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()

            // write message doc
            dmMessagesRef(chatId).document(cloudId).set(msgMap).await()

            // mark local as SENT (same id)
            messageDao.updateStatus(localId, MessageStatus.SENT)

            // update preview locally
            chatRoomDao.getRoomById(chatId)?.let { r ->
                chatRoomDao.updateRoom(r.copy(lastMessage = content, updatedAt = now))
            }
        }

    fun startGroupChatSync(tripId: Long, currentUserId: Long, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val trip = tripDao.getById(tripId) ?: return@launch
            val tripFid = trip.firebaseId ?: return@launch
            val myIdStr = currentUserId.toString()

            firestore.collection("trips").document(tripFid).collection("group_messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener
                    if (snapshots.metadata.hasPendingWrites()) return@addSnapshotListener

                    scope.launch(Dispatchers.IO) {
                        // Variables to track the newest message in this batch
                        var latestMsgContent: String? = null
                        var latestMsgTime: Long = 0L

                        for (doc in snapshots.documents) {
                            val fid = doc.id
                            val senderId = (doc.getLong("senderId") ?: 0L)
                            val content = doc.getString("content") ?: ""
                            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                            val senderName = doc.getString("senderName") ?: "Unknown"
                            val senderAvatar = doc.getString("senderAvatar")

                            val existing = groupChatDao.getMessageByFirebaseId(fid)

                            if (existing == null) {
                                val newMsg = GroupMessageEntity(
                                    id = 0,
                                    firebaseId = fid,
                                    tripId = tripId,
                                    senderId = senderId,
                                    senderName = senderName,
                                    senderAvatar = senderAvatar,
                                    content = content,
                                    timestamp = timestamp
                                )
                                groupChatDao.insertMessage(newMsg)

                                // Check if this is the newest message we've seen in this batch
                                if (timestamp > latestMsgTime) {
                                    latestMsgTime = timestamp
                                    latestMsgContent = content
                                }
                            }
                        }

                        // ✅ FIX: Update the Chat Room Preview in 'chat_rooms' table
                        if (latestMsgContent != null) {
                            val roomId = groupRoomId(tripId)
                            val existingRoom = chatRoomDao.getRoomById(roomId)

                            if (existingRoom != null) {
                                // Room exists: just update the preview text and time
                                chatRoomDao.updateRoom(
                                    existingRoom.copy(
                                        lastMessage = latestMsgContent,
                                        updatedAt = latestMsgTime
                                    )
                                )
                            } else {
                                // Room missing: Create it so it shows up in the list!
                                val newRoom = ChatRoom(
                                    id = roomId,
                                    title = trip.name,
                                    userAId = myIdStr, // Set owner to SELF so your query finds it
                                    userBId = myIdStr,
                                    lastMessage = latestMsgContent,
                                    updatedAt = latestMsgTime,
                                    isPrivate = false
                                )
                                chatRoomDao.insertRoom(newRoom)
                            }
                        }
                    }
                }
        }
    }

    suspend fun sendGroupMessage(
        tripId: Long,
        tripName: String,
        currentUserId: Long,
        senderId: Long,
        senderName: String,
        senderAvatar: String?,
        content: String
    ) {
        // 1. Save Locally (Optimistic UI)
        val tempMsg = GroupMessageEntity(
            tripId = tripId,
            senderId = senderId,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        val localId = groupChatDao.insertMessage(tempMsg)

        // 2. Upload to Cloud
        try {
            val trip = tripDao.getById(tripId) ?: return
            val tripFid = trip.firebaseId ?: return

            val msgMap = hashMapOf(
                "senderId" to senderId,
                "senderName" to senderName,
                "senderAvatar" to senderAvatar,
                "content" to content,
                "timestamp" to System.currentTimeMillis()
            )

            // Add to subcollection
            val docRef = firestore.collection("trips").document(tripFid)
                .collection("group_messages").add(msgMap).await()

            // Update local with Firebase ID
            groupChatDao.updateMessage(tempMsg.copy(id = localId, firebaseId = docRef.id))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun deterministicDmId(a: String, b: String): String {
        val (x, y) = if (a <= b) a to b else b to a
        return "dm:$x-$y"
    }
    private fun groupRoomId(tripId: Long) = "group:$tripId"

    suspend fun getRoomById(roomId: String): ChatRoom? = withContext(ioDispatcher) {
        return@withContext chatRoomDao.getRoomById(roomId)
    }
    suspend fun insertLocalMessage(message: Message) = withContext(ioDispatcher) {
        try {
            messageDao.insertMessage(message)
            Log.d("ChatRepo", "insertLocalMessage OK id=${message.id} chat=${message.chatId} sender=${message.senderId}")
        } catch (e: Exception) {
            Log.w("ChatRepo", "insertLocalMessage FAILED id=${message.id} chat=${message.chatId} error=${e.message}")
            throw e
        }
    }


    suspend fun updateStatus(messageId: String, status: MessageStatus) = withContext(ioDispatcher) {
        try {
            messageDao.updateStatus(messageId, status)
            Log.d("ChatRepo", "updateStatus OK id=$messageId status=$status")
        } catch (e: Exception) {
            Log.w("ChatRepo", "updateStatus FAILED id=$messageId error=${e.message}")
            throw e
        }
    }

    suspend fun getMessagesOnce(chatId: String): List<Message> = withContext(ioDispatcher) {
        // simple direct query — add this DAO method if not present
        messageDao.getMessagesForChatOnce(chatId)
    }

    val remoteStub = object : RemoteChatService {
        override suspend fun sendMessageRemote(message: Message): Message {
            // simulate network latency
            delay(200)
            val canonicalId = if (message.id.isBlank()) UUID.randomUUID().toString() else message.id
            return message.copy(id = canonicalId, createdAt = System.currentTimeMillis(), status = MessageStatus.SENT)
        }
        override suspend fun fetchRecentMessages(chatId: String, since: Long?) = emptyList<Message>()
        override suspend fun createChatRoomRemote(room: ChatRoom) = room
        override suspend fun updateMessageStatusRemote(messageId: String, status: MessageStatus) { /*no-op*/ }
    }
    /** Observe all rooms for current user (UI layer should provide the userId filter if needed). */
    fun observeRoomsForUser(userId: String): Flow<List<ChatRoom>> =
        chatRoomDao.observeRoomsForUser(userId)
            .distinctUntilChanged()
    suspend fun isUserMember(roomId: String, userId: String): Boolean = withContext(ioDispatcher) {
        return@withContext (chatRoomDao.isUserMember(roomId, userId) > 0)
    }
    /** Observe messages for a chat (real-time via DB Flow). */
    fun observeMessages(chatId: String): Flow<List<Message>> =
        messageDao.observeMessagesForChat(chatId)
            .distinctUntilChanged()

    /**
     * Create or ensure a chat room exists locally and remotely, returns the room id.
     * If a matching room exists return it.
     */
    suspend fun ensureChatRoom(userAId: String, userBId: String, title: String? = null): ChatRoom =
        withContext(ioDispatcher) {
            // naive deterministic id: you may want to let server generate id instead
            val roomId = generateDeterministicRoomId(userAId, userBId)

            val existing = chatRoomDao.getRoomById(roomId)
            if (existing != null) return@withContext existing

            val room = ChatRoom(id = roomId, title = title, userAId = userAId, userBId = userBId)
            chatRoomDao.insertRoom(room)

            // create remotely (best-effort). If remote fails, we still keep local room.
            try {
                val remoteRoom = remote.createChatRoomRemote(room)
                // update local metadata from remote if needed (timestamps, canonical id)
                chatRoomDao.updateRoom(remoteRoom.copy(updatedAt = Instant.now().toEpochMilli()))
                return@withContext remoteRoom
            } catch (e: Exception) {
                // log but return local room
                return@withContext room
            }
        }
    suspend fun ensureGroupRoomLocal(
        currentUserId: Long,
        tripId: Long,
        tripName: String
    ) = withContext(ioDispatcher) {
        val me = currentUserId.toString()
        val id = groupRoomId(tripId)

        val existing = chatRoomDao.getRoomById(id)
        if (existing != null) {
            // keep title synced (in case trip name changed)
            if (existing.title != tripName) {
                chatRoomDao.updateRoom(existing.copy(title = tripName))
            }
            return@withContext
        }

        // ChatRoom requires userAId/userBId non-null :contentReference[oaicite:4]{index=4}
        val room = ChatRoom(
            id = id,
            title = tripName,
            userAId = me,
            userBId = me,
            lastMessage = "Group chat",
            updatedAt = System.currentTimeMillis(),
            isPrivate = false
        )
        chatRoomDao.insertRoom(room)
    }

    /**
     * Send group message + update preview in chat_rooms so it shows in ChatList
     */
    suspend fun sendGroupMessage(
        tripId: Long,
        tripName: String,              // ✅ add this
        currentUserId: Long,           // ✅ add this (so we can ensure room)
        senderId: Long,
        senderName: String,
        senderAvatar: String?,
        content: String,
        type: String = "text"
    ) = withContext(ioDispatcher) {
        // 1) ensure room exists so it appears in chat list
        ensureGroupRoomLocal(
            currentUserId = currentUserId,
            tripId = tripId,
            tripName = tripName
        )

        // 2) insert message in group_messages (your existing logic) :contentReference[oaicite:5]{index=5}
        val msg = GroupMessageEntity(
            tripId = tripId,
            senderId = senderId,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            type = type
        )
        groupChatDao.insertMessage(msg)

        // 3) update preview in chat_rooms
        val roomId = groupRoomId(tripId)
        chatRoomDao.getRoomById(roomId)?.let { room ->
            chatRoomDao.updateRoom(
                room.copy(
                    title = tripName,
                    lastMessage = content,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
    /**
     * Send a message: create local message (PENDING) → push remote → update local status and server id/timestamp if returned.
     *
     * This is the main integration point for UI. Returns the local message id.
     */
    suspend fun sendMessage(chatId: String, senderId: String, content: String): String =
        withContext(ioDispatcher) {
            // 1. Create a stable ID for the message
            val localId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()

            // 2. Insert into Local Room DB (Optimistic UI)
            val localMessage = Message(
                id = localId,
                chatId = chatId,
                senderId = senderId,
                content = content,
                createdAt = now,
                status = MessageStatus.PENDING
            )

            // Ensure the room exists locally
            val room = chatRoomDao.getRoomById(chatId)
            if (room == null) {
                // Create placeholder room if missing
                val newRoom = ChatRoom(
                    id = chatId,
                    userAId = senderId,
                    userBId = "", // You might need logic to find the other ID here
                    updatedAt = now
                )
                chatRoomDao.insertRoom(newRoom)
            }

            messageDao.insertMessage(localMessage)

            // 3. ACTUAL FIREBASE UPLOAD (Replaces the 'remoteStub' call)
            try {
                // Reference to the subcollection: dm_rooms/{chatId}/messages
                val msgMap = hashMapOf(
                    "senderId" to senderId,
                    "content" to content,
                    "createdAt" to now
                )

                // A. Update the Room metadata (Last message & timestamp)
                firestore.collection("dm_rooms").document(chatId).set(
                    hashMapOf(
                        "lastMessage" to content,
                        "updatedAt" to FieldValue.serverTimestamp(),
                        // ensure users array exists if needed for security rules
                        // "users" to listOf(senderId, receiverId)
                    ),
                    SetOptions.merge()
                ).await()

                // B. Write the message document
                firestore.collection("dm_rooms").document(chatId)
                    .collection("messages").document(localId)
                    .set(msgMap)
                    .await()

                // 4. Update Status to SENT
                messageDao.updateStatus(localId, MessageStatus.SENT)

                return@withContext localId

            } catch (e: Exception) {
                Log.e("ChatRepository", "Upload failed", e)
                messageDao.updateStatus(localId, MessageStatus.FAILED)
                throw e
            }
        }

    suspend fun deleteChat(chatId: String) = withContext(ioDispatcher) {
        // 1. Delete messages (Optional if Foreign Key CASCADE is set, but good for safety)
        messageDao.deleteMessagesForChat(chatId)

        // 2. Delete the room itself
        chatRoomDao.deleteRoom(chatId)
    }
    fun observeMessagesForChat(chatId: String): Flow<List<Message>> {
        return messageDao.observeMessagesForChat(chatId)
            .flowOn(ioDispatcher)
    }

    suspend fun countUnreadForChat(chatId: String): Int = withContext(ioDispatcher) {
        try {
            messageDao.countUnread(chatId)
        } catch (e: Exception) {
            0
        }
    }

    fun startChatListSync(currentUserId: String, scope: CoroutineScope): List<ListenerRegistration> {
        val listeners = mutableListOf<ListenerRegistration>()

        // Helper function to handle the snapshots from both queries
        fun handleSnapshot(snapshots: com.google.firebase.firestore.QuerySnapshot?) {
            if (snapshots == null) return

            scope.launch(ioDispatcher) {
                for (doc in snapshots.documents) {
                    val id = doc.id
                    val lastMessage = doc.getString("lastMessage")
                    val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                    val userA = doc.getString("userAId") ?: ""
                    val userB = doc.getString("userBId") ?: ""

                    // 1. Check if room exists locally
                    val existing = chatRoomDao.getRoomById(id)

                    if (existing != null) {
                        // 2. Update existing room (Sync last message & time)
                        // Only update if remote is newer or different to avoid loop
                        if (existing.lastMessage != lastMessage || existing.updatedAt != updatedAt) {
                            chatRoomDao.updateRoom(
                                existing.copy(
                                    lastMessage = lastMessage,
                                    updatedAt = updatedAt
                                )
                            )
                        }
                    } else {
                        // 3. Insert new room found from server
                        val newRoom = ChatRoom(
                            id = id,
                            userAId = userA,
                            userBId = userB,
                            lastMessage = lastMessage,
                            updatedAt = updatedAt,
                            isPrivate = true
                        )
                        chatRoomDao.insertRoom(newRoom)
                    }
                }
            }
        }

        // Listener 1: Rooms where I am User A
        listeners.add(
            firestore.collection("dm_rooms")
                .whereEqualTo("userAId", currentUserId)
                .addSnapshotListener { snaps, _ -> handleSnapshot(snaps) }
        )

        // Listener 2: Rooms where I am User B
        listeners.add(
            firestore.collection("dm_rooms")
                .whereEqualTo("userBId", currentUserId)
                .addSnapshotListener { snaps, _ -> handleSnapshot(snaps) }
        )

        return listeners
    }
    // in ChatRepository.kt
    // inside ChatRepository (make sure you have ioDispatcher, chatRoomDao, remote, etc.)
    suspend fun openDirectChat(meId: Long, otherId: Long, title: String? = null): String? = withContext(ioDispatcher) {
        try {
            val meStr = meId.toString()
            val otherStr = otherId.toString()
            val roomId = deterministicDmId(meStr, otherStr)

            val existing = chatRoomDao.getRoomById(roomId)
            if (existing != null) return@withContext existing.id

            // create local private DM
            val room = ChatRoom(
                id = roomId,
                title = title,
                userAId = meStr,
                userBId = otherStr,
                lastMessage = null,
                updatedAt = System.currentTimeMillis(),
                isPrivate = true
            )
            chatRoomDao.insertRoom(room)

            // try create remote (best effort) and update local metadata
            return@withContext try {
                val created = remote.createChatRoomRemote(room)
                chatRoomDao.updateRoom(created)
                created.id
            } catch (e: Exception) {
                room.id
            }
        } catch (e: Exception) {
            null
        }
    }


    /**
     * Pull recent messages from server and persist locally (use when opening chat or on background sync).
     */
    suspend fun syncRecentMessages(chatId: String, since: Long? = null) =
        withContext(ioDispatcher) {
            val fetched = remote.fetchRecentMessages(chatId, since)
            if (fetched.isNotEmpty()) {
                messageDao.insertMessages(fetched)
                // update room preview/updatedAt if server messages include newer timestamps
                val latest = fetched.maxByOrNull { it.createdAt }
                if (latest != null) {
                    val room = chatRoomDao.getRoomById(chatId)
                    if (room != null && latest.createdAt > room.updatedAt) {
                        chatRoomDao.updateRoom(room.copy(lastMessage = latest.content, updatedAt = latest.createdAt))
                    }
                }
            }
        }

    suspend fun markMessageRead(messageId: String) = withContext(ioDispatcher) {
        messageDao.updateStatus(messageId, MessageStatus.READ)
        try {
            remote.updateMessageStatusRemote(messageId, MessageStatus.READ)
        } catch (_: Exception) {
            // ignore remote failure; local change keeps UX consistent
        }
    }

    private fun generateDeterministicRoomId(a: String, b: String): String {
        // deterministic ordering so room for A+B equals B+A
        val (x, y) = if (a <= b) a to b else b to a
        return "$x|$y" // simple; replace with hash if you prefer shorter ids
    }


    suspend fun sendGroupMessage(
        tripId: Long,
        senderId: Long,
        senderName: String,
        senderAvatar: String?,
        content: String
    ) {
        val msg = GroupMessageEntity(
            tripId = tripId,
            senderId = senderId,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content
        )
        groupChatDao.insertMessage(msg)
    }

    companion object {
        // ✅ Now this is accessible as ChatRepository.remoteStub
        val remoteStub = object : RemoteChatService {
            override suspend fun sendMessageRemote(message: Message): Message {
                // simulate network latency
                kotlinx.coroutines.delay(200)
                val canonicalId = if (message.id.isBlank()) java.util.UUID.randomUUID().toString() else message.id
                return message.copy(id = canonicalId, createdAt = System.currentTimeMillis(), status = MessageStatus.SENT)
            }
            override suspend fun fetchRecentMessages(chatId: String, since: Long?) = emptyList<Message>()
            override suspend fun createChatRoomRemote(room: ChatRoom) = room
            override suspend fun updateMessageStatusRemote(messageId: String, status: MessageStatus) { /*no-op*/ }
        }
    }

}
