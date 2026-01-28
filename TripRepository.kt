package com.example.tripshare.data.repo

import android.util.Log
import com.example.tripshare.data.db.ChecklistDao
import com.example.tripshare.data.db.ItineraryDao
import com.example.tripshare.data.db.ParticipantDao
import com.example.tripshare.data.db.RouteDao
import com.example.tripshare.data.db.SavedListDao
import com.example.tripshare.data.db.TripDao
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.db.VoteDao
import com.example.tripshare.data.db.WaitlistDao
import com.example.tripshare.data.model.CalendarEventEntity
import com.example.tripshare.data.model.ChecklistCategoryEntity
import com.example.tripshare.data.model.ChecklistItemEntity
import com.example.tripshare.data.model.ItineraryItemEntity
import com.example.tripshare.data.model.ParticipantInviteEntity
import com.example.tripshare.data.model.ParticipantRole
import com.example.tripshare.data.model.ParticipationStatus
import com.example.tripshare.data.model.PollEntity
import com.example.tripshare.data.model.PollVoteEntity
import com.example.tripshare.data.model.RouteStopEntity
import com.example.tripshare.data.model.SavedChecklistEntity
import com.example.tripshare.data.model.SavedChecklistItemEntity
import com.example.tripshare.data.model.StopType
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.TripMeetingPointEntity
import com.example.tripshare.data.model.TripParticipantEntity
import com.example.tripshare.data.model.TripPaymentMethodEntity
import com.example.tripshare.data.model.UserEntity
import com.example.tripshare.data.model.VoteOptionEntity
import com.example.tripshare.data.model.WaitlistEntity
import com.example.tripshare.ui.trip.ItemUi
import com.example.tripshare.ui.trip.PlanEntryUi
import com.example.tripshare.ui.trip.PlanType
import com.example.tripshare.ui.trip.RouteStopUi
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

// ─────────────────────────── Waitlist ───────────────────────────

class WaitlistRepository(
    private val dao: WaitlistDao,
    private val tripDao: TripDao,
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun observeForUser(userId: Long) = dao.observeForUser(userId)
    fun observeForTrip(tripId: Long) = dao.observeForTrip(tripId)

    // ✅ 1. SYNC: Listen for Waitlist Changes from Cloud
    fun startWaitlistSync(tripId: Long, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val trip = tripDao.getById(tripId) ?: return@launch
            val tripFid = trip.firebaseId ?: return@launch

            firestore.collection("trips").document(tripFid).collection("waitlist")
                .orderBy("timestamp") // Order by time joined
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener

                    scope.launch(Dispatchers.IO) {
                        // Calculate positions based on cloud order
                        snapshots.documents.forEachIndexed { index, doc ->
                            val fid = doc.id
                            val userFid = doc.getString("userUid") ?: return@forEachIndexed
                            val displayName = doc.getString("displayName") ?: "Traveller"
                            val photoUrl = doc.getString("photoUrl")
                            val email = doc.getString("email") ?: ""

                            // 1. Resolve User (Auto-Create if missing to ensure sync works)
                            var user = userDao.findByFirebaseId(userFid)
                            if (user == null) {
                                // Create Stub User so waitlist item can be displayed
                                val newUser = UserEntity(
                                    firebaseId = userFid,
                                    name = displayName,
                                    email = email,
                                    profilePhoto = photoUrl
                                )
                                val newId = userDao.insertUser(newUser)
                                user = newUser.copy(id = newId)
                            }

                            val userId = user.id

                            // 2. Resolve Trip Data (WaitlistEntity needs these)
                            val tripEnt = tripDao.getById(tripId) ?: return@forEachIndexed

                            // 3. Upsert
                            val existing = dao.getByFirebaseId(fid)
                            val position = index + 1

                            val entity = WaitlistEntity(
                                id = existing?.id ?: 0L,
                                firebaseId = fid,
                                tripId = tripId,
                                tripName = tripEnt.name,
                                location = "", // Fill if available on TripEntity
                                date = tripEnt.startDate?.toString() ?: "",
                                position = position,
                                alertsEnabled = existing?.alertsEnabled ?: true,
                                tripImageUrl = tripEnt.coverImgUrl,
                                userId = userId
                            )

                            if (existing != null) {
                                dao.update(entity)
                            } else {
                                dao.insert(entity)
                            }
                        }
                    }
                }
        }
    }

    // ✅ 2. JOIN: Upload to Cloud (Include Profile Info)
    suspend fun joinWaitlist(tripId: Long, userId: Long) {
        val trip = tripDao.getById(tripId) ?: return
        val tripFid = trip.firebaseId ?: return

        val user = userDao.getUserById(userId) ?: return
        val userFid = user.firebaseId ?: return

        // 1. Create Cloud Document (Use UserUID as ID to prevent dupes)
        val waitlistRef = firestore.collection("trips").document(tripFid)
            .collection("waitlist").document(userFid)

        // ✅ FIX: Send Name/Photo so other devices can sync even if they don't know this user
        val data = hashMapOf(
            "userUid" to userFid,
            "displayName" to (user.name),
            "email" to (user.email),
            "photoUrl" to (user.profilePhoto),
            "timestamp" to System.currentTimeMillis()
        )
        waitlistRef.set(data, SetOptions.merge())

        // 2. Add Locally (Optimistic)
        // Note: The Sync listener will overwrite this shortly with the correct Position.
        val entity = WaitlistEntity(
            tripId = tripId,
            tripName = trip.name,
            location = "",
            date = trip.startDate?.toString() ?: "",
            position = 999, // Temp position
            userId = userId,
            firebaseId = userFid // Use same ID
        )
        dao.insert(entity)
    }

    // ✅ 3. LEAVE: Delete from Cloud
    suspend fun leaveWaitlist(item: WaitlistEntity) {
        dao.delete(item)

        if (item.firebaseId != null) {
            val trip = tripDao.getById(item.tripId) ?: return
            val tripFid = trip.firebaseId ?: return

            firestore.collection("trips").document(tripFid)
                .collection("waitlist").document(item.firebaseId)
                .delete()
        }
    }
    suspend fun add(entry: WaitlistEntity) {
        val existing = dao.findByUser(entry.tripId, entry.userId)
        if (existing == null) {
            val currentSize = dao.countForTrip(entry.tripId)
            val newEntry = entry.copy(position = currentSize + 1)
            dao.insert(newEntry)
        }
    }

    suspend fun remove(entry: WaitlistEntity) = dao.delete(entry)

    suspend fun toggleAlert(entry: WaitlistEntity, enabled: Boolean) {
        dao.update(entry.copy(alertsEnabled = enabled))
    }

    suspend fun findByUser(tripId: Long, userId: Long): WaitlistEntity? =
        dao.findByUser(tripId, userId)
}

// ─────────────────────────── Trips ───────────────────────────

class TripRepository(
    private val tripDao: TripDao,
    private val participantDao: ParticipantDao,
    private val routeDao: RouteDao,
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val zone: ZoneId = ZoneId.systemDefault()
) {

    suspend fun getTripById(id: Long): TripEntity? = tripDao.getById(id)

    fun observeJoinedActiveTrips(userId: Long): Flow<List<TripEntity>> =
        tripDao.observeJoinedActiveTrips(userId)

    suspend fun getTrip(id: Long): TripEntity? =
        tripDao.getTripById(id)

    suspend fun updateTripImage(tripId: Long, uri: String) {
        tripDao.updateTripImage(tripId, uri)
        // optional: push to cloud if you store coverImgUrl in Firestore
    }

    /**
     * ✅ Correct: update local AND push to Firestore
     * (So other devices will see changes.)
     */
    suspend fun updateTrip(trip: TripEntity) {
        tripDao.update(trip)
        pushTripToCloud(trip)
    }

    suspend fun migratePastTrips(daysAfterEnd: Long = 3) {
        val cutoffEpochDay = LocalDate.now(zone).minusDays(daysAfterEnd).toEpochDay()
        val toArchive = tripDao.getTripsEndedBefore(cutoffEpochDay)

        if (toArchive.isEmpty()) return

        // 1. Update Local DB
        tripDao.markArchived(toArchive.map { it.id })

        // 2. ✅ Push to Cloud (Crucial!)
        toArchive.forEach { trip ->
            val fid = trip.firebaseId
            if (fid != null) {
                try {
                    firestore.collection("trips").document(fid)
                        .update("isArchived", true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    suspend fun insertMeetingPoints(points: List<TripMeetingPointEntity>) {
        tripDao.insertMeetingPoints(points)
    }

    suspend fun deleteTrip(tripId: Long) {
        // 1. Fetch the trip first to get its Firebase ID
        val trip = tripDao.getById(tripId)
        val fid = trip?.firebaseId

        // 2. Delete from Cloud Firestore
        if (fid != null) {
            try {
                // This deletes the main trip document.
                // Note: Firestore does not automatically delete subcollections (like messages/expenses)
                // unless you use a Cloud Function, but this removes the trip from the app.
                firestore.collection("trips").document(fid)
                    .delete()
                    .addOnFailureListener { e -> e.printStackTrace() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 3. Delete Locally
        tripDao.deleteTrip(tripId)
    }

    suspend fun deleteMeetingPointsForTrip(tripId: Long) {
        tripDao.deleteMeetingPointsForTrip(tripId)
    }

    suspend fun addOwnerParticipant(
        tripId: Long,
        userId: Long,
        displayName: String,
        email: String?
    ) {
        participantDao.upsert(
            TripParticipantEntity(
                tripId = tripId,
                userId = userId,
                displayName = displayName,
                email = email ?: "",
                role = ParticipantRole.OWNER
            )
        )
    }

    fun observeTrip(id: Long): Flow<TripEntity?> =
        tripDao.observeById(id)

    fun observeTrips(): Flow<List<TripEntity>> =
        tripDao.observeAllTrips()

    fun observeJoinedTripIds(userId: Long): Flow<Set<Long>> =
        tripDao.observeJoinedTripIds(userId).map { it.toSet() }

    fun observeUserMemberships(userId: Long): Flow<Set<Long>> =
        tripDao.observeTripIdsForUser(userId).map { it.toSet() }

    fun observeTripMembers(tripId: Long): Flow<List<UserEntity>> =
        participantDao.observeUsersForTrip(tripId)

    fun observeIsUserOnTrip(tripId: Long, userId: Long): Flow<Boolean> =
        tripDao.observeIsUserParticipant(tripId, userId).map { it > 0 }

    suspend fun isUserOnTripOnce(tripId: Long, userId: Long): Boolean =
        tripDao.isUserParticipant(tripId, userId) > 0

    suspend fun archiveTrip(tripId: Long) {
        val trip = tripDao.getById(tripId) ?: return
        val updatedTrip = trip.copy(endDate = trip.endDate ?: LocalDate.now(zone))
        tripDao.update(updatedTrip)
        tripDao.archiveTrip(tripId)

        // push archive state
        pushTripToCloud(updatedTrip.copy(isArchived = true))
    }
    fun startParticipantSync(tripId: Long, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val trip = tripDao.getById(tripId) ?: return@launch
            val tripFid = trip.firebaseId ?: return@launch

            firestore.collection("trips").document(tripFid)
                .collection("participants")
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener

                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshots.documents) {
                            val userFid = doc.id // We use User UID as doc ID
                            val displayName = doc.getString("name") ?: "Member"
                            val email = doc.getString("email") ?: ""
                            val photoUrl = doc.getString("photoUrl")
                            val roleStr = doc.getString("role") ?: "MEMBER"

                            // 1. Ensure User Exists Locally
                            var user = userDao.findByFirebaseId(userFid)
                            if (user == null) {
                                val newUser = UserEntity(
                                    firebaseId = userFid,
                                    name = displayName,
                                    email = email,
                                    profilePhoto = photoUrl
                                )
                                val newId = userDao.insertUser(newUser)
                                user = newUser.copy(id = newId)
                            } else {
                                // Update profile info if changed
                                userDao.updateUser(user.copy(name = displayName, profilePhoto = photoUrl))
                            }

                            // 2. Upsert into TripParticipantEntity (The Join Table)
                            val role = try {
                                ParticipantRole.valueOf(roleStr)
                            } catch (e: Exception) { ParticipantRole.MEMBER }

                            participantDao.upsert(
                                TripParticipantEntity(
                                    tripId = tripId,
                                    userId = user.id,
                                    displayName = displayName,
                                    email = email,
                                    role = role,
                                    status = com.example.tripshare.data.model.ParticipationStatus.JOINED
                                )
                            )
                        }
                    }
                }
        }
    }
    /**
     * CREATE:
     * - generate cloud id (firebaseId)
     * - save Room
     * - save Firestore
     */
    suspend fun createTrip(
        trip: TripEntity,
        routes: List<RouteStopEntity>,
        payments: List<TripPaymentMethodEntity>,
        invites: List<ParticipantInviteEntity>,
        events: List<CalendarEventEntity>,
        creatorId: Long,
        creatorName: String,
        creatorEmail: String,
        creatorFirebaseId: String
    ): Long {
        val cloudId = UUID.randomUUID().toString()
        val tripWithCloudId = trip.copy(firebaseId = cloudId, organizerId = creatorFirebaseId) // Store Firebase UID of organizer

        // 1. Save Local
        val localId = tripDao.insertTripWithAll(tripWithCloudId, routes, payments, invites, events)

        // 2. Add Owner Locally
        participantDao.upsert(
            TripParticipantEntity(
                tripId = localId,
                userId = creatorId,
                displayName = creatorName,
                email = creatorEmail,
                role = ParticipantRole.OWNER,
                status = ParticipationStatus.JOINED
            )
        )

        // 3. Save Cloud Trip Doc
        saveTripToCloud(
            trip = tripWithCloudId.copy(id = localId),
            routes = routes,
            organizerFirebaseId = creatorFirebaseId
        )

        // 4. ✅ FIX: Write Owner to 'participants' subcollection immediately
        val participantRef = firestore.collection("trips").document(cloudId)
            .collection("participants").document(creatorFirebaseId)
// fetch creator avatar from local DB
        val creatorPhotoUrl = userDao.getUserById(creatorId)?.profilePhoto

        participantRef.set(mapOf(
            "name" to creatorName,
            "email" to creatorEmail,
            "photoUrl" to creatorPhotoUrl, // ✅ pass in or fetch from user table
            "role" to "OWNER",
            "joinedAt" to System.currentTimeMillis()
        ))

        return localId
    }

    /**
     * ✅ Firestore write for new trips (includes memberIds for user-scoped sync)
     */
    private suspend fun saveTripToCloud(
        trip: TripEntity,
        routes: List<RouteStopEntity>,
        organizerFirebaseId: String
    ) {
        val tripMap = hashMapOf(
            "firebaseId" to trip.firebaseId,
            "name" to trip.name,
            "organizerId" to trip.organizerId,
            "memberIds" to listOf(trip.organizerId), // VERY IMPORTANT
            "startDate" to trip.startDate?.toString(),
            "endDate" to trip.endDate?.toString(),
            "maxParticipants" to trip.maxParticipants,
            "description" to trip.description,
            "category" to trip.category.name,
            "visibility" to trip.visibility.name,
            "isArchived" to trip.isArchived,
            "budget" to trip.budget,
            "budgetDisplay" to trip.budgetDisplay,
            "lastUpdated" to System.currentTimeMillis()
        )

        tripMap["stops"] = routes.map {
            mapOf(
                "label" to it.label,
                "type" to it.type.name,
                "lat" to it.lat,
                "lng" to it.lng,
                "orderInRoute" to it.orderInRoute,
                "nights" to it.nights
            )
        }

        firestore.collection("trips")
            .document(trip.firebaseId!!)
            .set(tripMap, SetOptions.merge())
            .await() // ✅ THIS WAS MISSING
    }


    /**
     * ✅ OPTIMIZED: Fire-and-forget
     */
    private fun pushTripToCloud(trip: TripEntity) {
        val fid = trip.firebaseId ?: return
        try {
            val map = hashMapOf(
                "firebaseId" to fid,
                "name" to trip.name,
                "organizerId" to trip.organizerId,
                "startDate" to trip.startDate?.toString(),
                "endDate" to trip.endDate?.toString(),
                "description" to trip.description,
                "category" to trip.category.name,
                "visibility" to trip.visibility.name,
                "isArchived" to trip.isArchived,
                "lastUpdated" to System.currentTimeMillis()
            )

            // 👇 CHANGED: Removed .await()
            firestore.collection("trips")
                .document(fid)
                .set(map, SetOptions.merge())
                .addOnFailureListener { e -> e.printStackTrace() }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun joinTrip(tripId: Long, tripFirebaseId: String, myUserId: Long, myName: String, myEmail: String) {
        // 1. CLOUD: Tell Firestore you are joining (so others can see you)
        // This updates the 'memberIds' array on the server
        val myFirebaseId = userDao.getUserById(myUserId)?.firebaseId ?: return

        // ✅ CLOUD: store Firebase UID in memberIds
        addMemberToTripCloud(tripFirebaseId, myFirebaseId)

        // 2. LOCAL: Update your own phone's database
        participantDao.upsert(
            TripParticipantEntity(
                tripId = tripId,
                userId = myUserId,
                displayName = myName,
                email = myEmail,
                role = ParticipantRole.MEMBER
            )
        )
    }
    /**
     * ✅ Correct per-user trip sync for all devices:
     * Listen only trips where currentUserId is in memberIds.
     *
     * Call this once after login:
     * val reg = repo.startUserTripsSync(currentUserIdString, viewModelScope)
     * reg.remove() on logout.
     */
    fun startUserTripsSync(
        currentUserId: String,
        scope: CoroutineScope
    ): com.google.firebase.firestore.ListenerRegistration {
        Log.d("SyncDebug", "Starting sync listener for User: $currentUserId")

        // 1. Query ALL trips (Active & Archived) for this user
        val q = firestore.collection("trips")
            .whereArrayContains("memberIds", currentUserId)
            // ❌ REMOVE THIS LINE: .whereEqualTo("isArchived", false)
            // ✅ We want ALL trips so History syncs too.
            .orderBy("lastUpdated", Query.Direction.DESCENDING)


        return q.addSnapshotListener(MetadataChanges.INCLUDE) { snapshots, e ->
            if (e != null) {
                Log.e("SyncDebug", "Listen failed: ${e.message}")
                return@addSnapshotListener
            }

            if (snapshots == null || snapshots.isEmpty) {
                Log.d("SyncDebug", "No trips found in cloud.")
                return@addSnapshotListener
            }

            if (snapshots.metadata.hasPendingWrites()) {
                return@addSnapshotListener
            }

            scope.launch(Dispatchers.IO) {
                for (doc in snapshots.documents) {
                    val firebaseId = doc.getString("firebaseId") ?: continue
                    val name = doc.getString("name") ?: "Unknown Trip"
                    val organizerId = doc.getString("organizerId") ?: ""
                    val description = doc.getString("description") ?: ""
                    val categoryRaw = doc.getString("category") ?: "General"
                    val visibilityRaw = doc.getString("visibility") ?: "Private"

                    // ✅ This field now controls if it appears in "Active" or "History"
                    val isArchived = doc.getBoolean("isArchived") ?: false

                    val maxParticipants = doc.getLong("maxParticipants")?.toInt() ?: 0
                    val startDateStr = doc.getString("startDate")
                    val endDateStr = doc.getString("endDate")

                    val startDate = startDateStr?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                    val endDate = endDateStr?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

                    val localTrip = tripDao.getTripByFirebaseId(firebaseId)

                    val tripEntity = if (localTrip != null) {
                        localTrip.copy(
                            firebaseId = firebaseId,
                            name = name,
                            organizerId = organizerId,
                            description = description,
                            category = runCatching {
                                com.example.tripshare.data.model.TripCategory.valueOf(categoryRaw)
                            }.getOrDefault(com.example.tripshare.data.model.TripCategory.General),
                            visibility = runCatching {
                                com.example.tripshare.data.model.Visibility.valueOf(visibilityRaw)
                            }.getOrDefault(com.example.tripshare.data.model.Visibility.Private),
                            startDate = startDate,
                            endDate = endDate,
                            isArchived = isArchived, // Local DB updates here
                            maxParticipants = maxParticipants
                        )
                    } else {
                        TripEntity(
                            id = 0L,
                            firebaseId = firebaseId,
                            name = name,
                            organizerId = organizerId,
                            description = description,
                            category = runCatching {
                                com.example.tripshare.data.model.TripCategory.valueOf(categoryRaw)
                            }.getOrDefault(com.example.tripshare.data.model.TripCategory.General),
                            visibility = runCatching {
                                com.example.tripshare.data.model.Visibility.valueOf(visibilityRaw)
                            }.getOrDefault(com.example.tripshare.data.model.Visibility.Private),
                            startDate = startDate,
                            endDate = endDate,
                            isArchived = isArchived,
                            maxParticipants = maxParticipants
                        )
                    }

                    val localId = if (localTrip != null) {
                        tripDao.update(tripEntity)
                        localTrip.id
                    } else {
                        tripDao.insertTrip(tripEntity)
                    }

                    // Sync Stops
                    val stops = parseStopsFromCloud(doc.get("stops"), localId)
                    routeDao.deleteByTripId(localId)
                    routeDao.insertAll(stops)
                }
            }
        }
    }

    fun startGlobalSync(
        currentUserId: String,
        scope: CoroutineScope
    ): com.google.firebase.firestore.ListenerRegistration {
        Log.d("SyncDebug", "Starting GLOBAL sync...")

        // 1. Query ALL active trips (No memberIds filter, No visibility filter)
        val q = firestore.collection("trips")
            .whereEqualTo("isArchived", false)
            .orderBy("lastUpdated", Query.Direction.DESCENDING)

        return q.addSnapshotListener(MetadataChanges.INCLUDE) { snapshots, e ->
            if (e != null) {
                Log.e("SyncDebug", "Listen failed: ${e.message}")
                return@addSnapshotListener
            }
            if (snapshots == null || snapshots.isEmpty) return@addSnapshotListener
            if (snapshots.metadata.hasPendingWrites()) return@addSnapshotListener

            scope.launch(Dispatchers.IO) {
                for (doc in snapshots.documents) {
                    val firebaseId = doc.getString("firebaseId") ?: continue

                    // --- Parse Trip Data ---
                    val name = doc.getString("name") ?: "Unknown Trip"
                    val organizerId = doc.getString("organizerId") ?: ""
                    val description = doc.getString("description") ?: ""
                    val categoryRaw = doc.getString("category") ?: "General"
                    val visibilityRaw = doc.getString("visibility") ?: "Public" // Default to Public if missing
                    val startDate = doc.getString("startDate")?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                    val endDate = doc.getString("endDate")?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                    val maxParticipants = doc.getLong("maxParticipants")?.toInt() ?: 0
                    // Get the list of members from Cloud to check if I am one of them
                    val memberIds = doc.get("memberIds") as? List<String> ?: emptyList()

                    // --- Save to Local Database ---
                    val localTrip = tripDao.getTripByFirebaseId(firebaseId)
                    val tripEntity = (localTrip ?: TripEntity(
                        name = name,
                        firebaseId = firebaseId
                    )).copy(
                        name = name,
                        organizerId = organizerId,
                        description = description,
                        category = runCatching { com.example.tripshare.data.model.TripCategory.valueOf(categoryRaw) }.getOrDefault(com.example.tripshare.data.model.TripCategory.General),
                        visibility = runCatching { com.example.tripshare.data.model.Visibility.valueOf(visibilityRaw) }.getOrDefault(com.example.tripshare.data.model.Visibility.Public),
                        startDate = startDate,
                        endDate = endDate,
                        isArchived = false
                    )

                    val localId = if (localTrip != null) {
                        tripDao.update(tripEntity)
                        localTrip.id
                    } else {
                        tripDao.insertTrip(tripEntity)
                    }

                    // --- Sync Stops ---
                    val stops = parseStopsFromCloud(doc.get("stops"), localId)
                    routeDao.deleteByTripId(localId)
                    routeDao.insertAll(stops)

                    // --- ✅ Auto-Join Logic ---
                    // If my ID is in the cloud 'memberIds' list, ensure I am marked as a participant locally.
                    if (memberIds.contains(currentUserId)) {
                        val isParticipant = tripDao.isUserParticipant(localId, currentUserId.toLong()) > 0
                        if (!isParticipant) {

                            // 🟢 FIX: Fetch actual user details instead of hardcoding "Me"
                            // (Assuming you have access to userDao in TripRepository, pass it in constructor if missing)
                            val currentUser = userDao.getUserById(currentUserId.toLong())

                            val displayName = currentUser?.name ?: "Me"
                            val email = currentUser?.email ?: ""
                            val role = if (organizerId == currentUserId) ParticipantRole.OWNER else ParticipantRole.MEMBER

                            participantDao.upsert(
                                TripParticipantEntity(
                                    tripId = localId,
                                    userId = currentUserId.toLong(),
                                    displayName = displayName, // Use real name
                                    email = email,             // Use real email
                                    role = role,
                                    status = com.example.tripshare.data.model.ParticipationStatus.JOINED
                                )
                            )
                        }
                    }
                }
            }
        }
    }
    /**
     * ✅ MemberIds maintenance helper:
     * Call this when someone joins (so they can sync this trip on their devices).
     */
    suspend fun addMemberToTripCloud(tripFirebaseId: String, memberUserId: String) {
        firestore.collection("trips")
            .document(tripFirebaseId)
            .update(
                mapOf(
                    "memberIds" to FieldValue.arrayUnion(memberUserId),
                    "lastUpdated" to System.currentTimeMillis()
                )
            )
            .await()
    }

    /**
     * Parse embedded stop maps into RouteStopEntity list.
     */
    private fun parseStopsFromCloud(data: Any?, tripId: Long): List<RouteStopEntity> {
        if (data !is List<*>) return emptyList()

        return data.mapNotNull { item ->
            val map = item as? Map<String, Any?> ?: return@mapNotNull null
            RouteStopEntity(
                id = 0L,
                tripId = tripId,
                label = map["label"] as? String ?: "",
                type = StopType.valueOf(map["type"] as? String ?: StopType.STOP.name),
                lat = map["lat"] as? Double,
                lng = map["lng"] as? Double,
                orderInRoute = (map["orderInRoute"] as? Long)?.toInt() ?: 0,
                nights = (map["nights"] as? Long)?.toInt() ?: 0,
                startDate = null,
                endDate = null
            )
        }
    }
}

class VoteRepository(
    private val dao: VoteDao,
    private val tripDao: TripDao,
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun observePolls(tripId: Long) = dao.observePolls(tripId)
    fun observeOptions(pollId: Long) = dao.observeOptions(pollId)
    fun observeVoters(pollId: Long) = dao.observePollVoters(pollId)

    // ✅ 1. SYNC: Listen for Polls
    fun startSync(tripId: Long, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val trip = tripDao.getById(tripId) ?: return@launch
            val tripFid = trip.firebaseId ?: return@launch

            firestore.collection("trips").document(tripFid).collection("polls")
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener

                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshots.documents) {
                            val pollFid = doc.id
                            val question = doc.getString("question") ?: ""
                            val allowMultiple = doc.getBoolean("allowMultiple") ?: false

                            // 1a. Upsert Poll
                            val existingPoll = dao.getPollByFirebaseId(pollFid)
                            val pollId = if (existingPoll != null) {
                                dao.updatePollHeader(existingPoll.id, question, allowMultiple)
                                existingPoll.id
                            } else {
                                // ✅ FIX: Use named arguments to prevent type mismatch
                                dao.insertPoll(
                                    PollEntity(
                                        id = 0,
                                        firebaseId = pollFid,
                                        tripId = tripId,
                                        question = question,
                                        allowMultiple = allowMultiple
                                    )
                                )
                            }

                            // 1b. Upsert Options
                            val optionsList = doc.get("options") as? List<Map<String, Any>> ?: emptyList()
                            optionsList.forEach { optMap ->
                                val optFid = optMap["firebaseId"] as? String ?: ""
                                val text = optMap["text"] as? String ?: ""
                                val votes = (optMap["votes"] as? Long)?.toInt() ?: 0

                                val existingOpt = dao.getOptionByFirebaseId(optFid)
                                if (existingOpt != null) {
                                    dao.updateOptionText(existingOpt.id, text)
                                    dao.setVoteCount(existingOpt.id, votes)
                                } else {
                                    // ✅ FIX: Use named arguments
                                    dao.insertOption(
                                        VoteOptionEntity(
                                            id = 0,
                                            firebaseId = optFid,
                                            pollId = pollId,
                                            option = text,
                                            votes = votes
                                        )
                                    )
                                }
                            }

                            // 1c. Listen to User Votes
                            launch { syncUserVotes(tripFid, pollFid, pollId) }
                        }
                    }
                }
        }
    }

    private suspend fun syncUserVotes(tripFid: String, pollFid: String, localPollId: Long) {
        firestore.collection("trips").document(tripFid)
            .collection("polls").document(pollFid)
            .collection("user_votes")
            .addSnapshotListener { snapshots, e ->
                if (snapshots == null) return@addSnapshotListener
                CoroutineScope(Dispatchers.IO).launch {
                    for (doc in snapshots.documents) {
                        val userFid = doc.id
                        val optionFids = doc.get("optionFids") as? List<String> ?: emptyList()

                        val user = userDao.findByFirebaseId(userFid) ?: continue

                        dao.clearUserVotes(localPollId, user.id)

                        for (optFid in optionFids) {
                            val option = dao.getOptionByFirebaseId(optFid)
                            if (option != null) {
                                dao.insertUserVote(PollVoteEntity(0, localPollId, option.id, user.id))
                            }
                        }
                    }
                }
            }
    }


    // ──────────────────────────────────────────────────────────────────────
    // 1. VOTE FUNCTION (Strict One User One Vote)
    // ──────────────────────────────────────────────────────────────────────
    suspend fun vote(poll: PollEntity, optionId: Long, userId: Long) {
        val trip = tripDao.getById(poll.tripId) ?: return
        val tripFid = trip.firebaseId ?: return
        val pollFid = poll.firebaseId ?: return

        // 1. Get the target option's Cloud ID
        val option = dao.getOptionsList(poll.id).find { it.id == optionId } ?: return
        val optionFid = option.firebaseId ?: return

        // 2. Get the User's Cloud ID
        val user = userDao.getUserById(userId) ?: return
        val userFid = user.firebaseId ?: return

        // 3. Cloud References
        val pollRef = firestore.collection("trips").document(tripFid)
            .collection("polls").document(pollFid)
        val userVoteRef = pollRef.collection("user_votes").document(userFid)

        try {
            firestore.runTransaction { transaction ->
                val pollSnap = transaction.get(pollRef)
                val userVoteSnap = transaction.get(userVoteRef)

                // Get current list of options (map) from Cloud to update counts
                val optionsList = pollSnap.get("options") as? List<Map<String, Any>> ?: emptyList()

                // Get the IDs the user has currently selected
                val currentVotedOptions = userVoteSnap.get("optionFids") as? List<String> ?: emptyList()
                val newVotedOptions = ArrayList(currentVotedOptions)

                // Track which Option IDs need incrementing or decrementing
                var incrementFid: String? = null
                var decrementFid: String? = null

                if (poll.allowMultiple) {
                    // ── MULTIPLE CHOICE LOGIC ──
                    // Toggle behavior: If selected, remove it. If not, add it.
                    if (newVotedOptions.contains(optionFid)) {
                        newVotedOptions.remove(optionFid)
                        decrementFid = optionFid
                    } else {
                        newVotedOptions.add(optionFid)
                        incrementFid = optionFid
                    }
                } else {
                    // ── SINGLE CHOICE LOGIC ──
                    // If they clicked the same option they already voted for, do nothing (or toggle off).
                    // Here we assume "Radio Button" style: clicking a new one switches vote.

                    if (newVotedOptions.contains(optionFid)) {
                        // User clicked the option they already selected.
                        // Optional: Do nothing? Or Deselect?
                        // Let's assume we keep it selected (Standard Radio Button behavior).
                        return@runTransaction
                    }

                    // If they voted for something else previously, remove that vote
                    if (newVotedOptions.isNotEmpty()) {
                        decrementFid = newVotedOptions[0] // Remove the old one
                        newVotedOptions.clear()
                    }

                    // Add the new vote
                    newVotedOptions.add(optionFid)
                    incrementFid = optionFid
                }

                // 4. Update the "votes" count inside the options array
                val updatedOptions = optionsList.map { opt ->
                    val fid = opt["firebaseId"] as? String
                    val votes = (opt["votes"] as? Long)?.toInt() ?: 0

                    val newVotes = when (fid) {
                        incrementFid -> votes + 1
                        decrementFid -> maxOf(0, votes - 1)
                        else -> votes
                    }
                    opt.plus("votes" to newVotes)
                }

                // 5. Commit changes
                transaction.update(pollRef, "options", updatedOptions)
                transaction.set(userVoteRef, mapOf("optionFids" to newVotedOptions))
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 2. DELETE FUNCTION (Cloud + Local)
    // ──────────────────────────────────────────────────────────────────────
    suspend fun deletePoll(pollId: Long) {
        // 1. Get Poll Details for Firebase ID
        val poll = dao.getPollById(pollId)

        if (poll?.firebaseId != null) {
            val trip = tripDao.getById(poll.tripId)
            val tripFid = trip?.firebaseId

            if (tripFid != null) {
                try {
                    // Delete from Cloud
                    firestore.collection("trips").document(tripFid)
                        .collection("polls").document(poll.firebaseId)
                        .delete()
                        .await()

                    // Note: Subcollections (user_votes) are not auto-deleted in standard Firestore
                    // unless you use a Cloud Function, but strictly for the UI, deleting the poll doc is enough.
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // 2. Delete from Local DB
        dao.deletePoll(pollId)
    }

    // ──────────────────────────────────────────────────────────────────────
    // 3. EDIT FUNCTION (Header + Smart Option Merge)
    // ──────────────────────────────────────────────────────────────────────
    suspend fun updatePoll(pollId: Long, question: String, options: List<String>, allowMultiple: Boolean) {
        val poll = dao.getPollById(pollId) ?: return
        val trip = tripDao.getById(poll.tripId) ?: return
        val tripFid = trip.firebaseId ?: return
        val pollFid = poll.firebaseId ?: return

        val pollRef = firestore.collection("trips").document(tripFid)
            .collection("polls").document(pollFid)

        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(pollRef)

                // 1. Get existing options (to preserve vote counts if text matches)
                val oldOptionsList = snapshot.get("options") as? List<Map<String, Any>> ?: emptyList()

                // 2. Build new options list
                val newCloudOptions = options.filter { it.isNotBlank() }.map { newText ->
                    // Check if this text existed before
                    val match = oldOptionsList.find { (it["text"] as? String) == newText }

                    if (match != null) {
                        // KEEP existing ID and Votes
                        match
                    } else {
                        // CREATE new option with 0 votes
                        mapOf(
                            "firebaseId" to UUID.randomUUID().toString(),
                            "text" to newText,
                            "votes" to 0
                        )
                    }
                }

                // 3. Update the Document
                transaction.update(pollRef, mapOf(
                    "question" to question,
                    "allowMultiple" to allowMultiple,
                    "options" to newCloudOptions
                ))
            }.await()

            // Note: The Local DB will automatically update via the 'startSync' listener
            // once the cloud write succeeds.

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ... (Keep existing createPoll) ...
    suspend fun createPoll(tripId: Long, question: String, allowMultiple: Boolean, options: List<String>) {
        val trip = tripDao.getById(tripId) ?: return
        val tripFid = trip.firebaseId ?: return

        val pollRef = firestore.collection("trips").document(tripFid).collection("polls").document()
        val pollFid = pollRef.id

        val cloudOptions = options.filter { it.isNotBlank() }.map { text ->
            val optFid = UUID.randomUUID().toString()
            mapOf("firebaseId" to optFid, "text" to text.trim(), "votes" to 0)
        }

        // Save Local (Optimistic)
        val pollId = dao.insertPoll(
            PollEntity(
                id = 0,
                firebaseId = pollFid,
                tripId = tripId,
                question = question,
                allowMultiple = allowMultiple
            )
        )

        cloudOptions.forEach { map ->
            dao.insertOption(
                VoteOptionEntity(
                    id = 0,
                    firebaseId = map["firebaseId"] as String,
                    pollId = pollId,
                    option = map["text"] as String,
                    votes = 0
                )
            )
        }

        // Save Cloud
        val pollMap = hashMapOf(
            "question" to question,
            "allowMultiple" to allowMultiple,
            "options" to cloudOptions,
            "timestamp" to System.currentTimeMillis()
        )
        pollRef.set(pollMap)
    }
}
// ─────────────────────────── Itinerary (SYNCED) ───────────────────────────

class ItineraryRepository(
    private val dao: ItineraryDao,
    private val tripDao: TripDao, // 👈 Need this to find the Trip's Firebase ID
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE
    private val TIME_FMT = DateTimeFormatter.ofPattern("HH:mm")

    // ✅ 1. SYNC: Listen for Cloud Changes
    fun startSync(tripId: Long, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val trip = tripDao.getById(tripId) ?: return@launch
            val tripFid = trip.firebaseId ?: return@launch

            firestore.collection("trips").document(tripFid).collection("itinerary")
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener
                    if (snapshots.metadata.hasPendingWrites()) return@addSnapshotListener

                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshots.documents) {
                            val fid = doc.id
                            val title = doc.getString("title") ?: ""
                            val category = doc.getString("category") ?: "Activity"
                            val date = doc.getString("date") ?: ""
                            val time = doc.getString("time") ?: ""
                            val location = doc.getString("location")
                            val notes = doc.getString("notes")
                            val lat = doc.getDouble("lat")
                            val lng = doc.getDouble("lng")
                            val endDate = doc.getString("endDate")
                            val endTime = doc.getString("endTime")

                            val existing = dao.getByFirebaseId(fid)

                            val entity = ItineraryItemEntity(
                                id = existing?.id ?: 0L, // Update if exists, Insert if 0
                                firebaseId = fid,
                                tripId = tripId,
                                day = 0, // Legacy field
                                title = title,
                                date = date,
                                time = time,
                                location = location,
                                notes = notes,
                                category = category,
                                attachment = null,
                                assignedTo = null,
                                endDate = endDate,
                                endTime = endTime,
                                lat = lat,
                                lng = lng
                            )

                            if (existing != null) {
                                dao.update(entity)
                            } else {
                                dao.insert(entity)
                            }
                        }
                    }
                }
        }
    }

    // ✅ 2. UPLOAD: Save to Cloud
    private suspend fun uploadItem(item: ItineraryItemEntity) {
        val trip = tripDao.getById(item.tripId) ?: return
        val tripFid = trip.firebaseId ?: return

        // If no firebaseId locally, create one
        val docRef = if (item.firebaseId != null) {
            firestore.collection("trips").document(tripFid).collection("itinerary").document(item.firebaseId)
        } else {
            firestore.collection("trips").document(tripFid).collection("itinerary").document()
        }

        // Save the new ID locally if needed
        if (item.firebaseId == null) {
            dao.update(item.copy(firebaseId = docRef.id))
        }

        val map = hashMapOf(
            "title" to item.title,
            "category" to (item.category ?: "Activity"),
            "date" to item.date,
            "time" to item.time,
            "location" to item.location,
            "notes" to item.notes,
            "lat" to item.lat,
            "lng" to item.lng,
            "endDate" to item.endDate,
            "endTime" to item.endTime,
            "updatedAt" to System.currentTimeMillis()
        )
        docRef.set(map, SetOptions.merge())
    }

    private suspend fun deleteFromCloud(item: ItineraryItemEntity) {
        val trip = tripDao.getById(item.tripId) ?: return
        val tripFid = trip.firebaseId ?: return
        val itemFid = item.firebaseId ?: return

        firestore.collection("trips").document(tripFid)
            .collection("itinerary").document(itemFid)
            .delete()
    }

    // ────────────────────────────────────────────────────────────────────────
    // CRUD Methods (Updated to call Upload)
    // ────────────────────────────────────────────────────────────────────────

    fun observePlans(tripId: Long): Flow<List<PlanEntryUi>> =
        dao.observeItemsForTrip(tripId).map { rows -> rows.map { it.toUi() } }

    suspend fun insertPlan(tripId: Long, plan: PlanEntryUi): Long {
        val entity = ItineraryItemEntity(
            id = 0,
            tripId = tripId,
            day = 0,
            title = plan.title,
            date = plan.date.format(DATE_FMT),
            time = plan.time?.format(TIME_FMT) ?: "",
            location = plan.subtitle,
            notes = plan.description,
            category = plan.type.name,
            attachment = null,
            assignedTo = null,
            endDate = plan.endDate?.format(DATE_FMT),
            endTime = plan.endTime?.format(TIME_FMT),
            lat = plan.lat,
            lng = plan.lng
        )
        val localId = dao.insert(entity)

        // 🚀 SYNC: Upload immediately
        uploadItem(entity.copy(id = localId))

        return localId
    }

    suspend fun quickAddPlan(
        tripId: Long,
        type: PlanType,
        date: LocalDate = LocalDate.now(),
        time: LocalTime? = null,
        title: String = type.label,
        subtitle: String? = null,
        location: String? = null
    ): Long {
        val entity = ItineraryItemEntity(
            id = 0,
            tripId = tripId,
            day = 0,
            title = title,
            date = date.format(DATE_FMT),
            time = time?.format(TIME_FMT) ?: "",
            location = location,
            notes = subtitle,
            category = type.name,
            attachment = null,
            assignedTo = null,
            endDate = null,
            endTime = null,
            lat = null,
            lng = null
        )
        val localId = dao.insert(entity)

        // 🚀 SYNC
        uploadItem(entity.copy(id = localId))

        return localId
    }

    suspend fun updatePlan(tripId: Long, plan: PlanEntryUi) {
        val entity = ItineraryItemEntity(
            id = plan.id,
            tripId = tripId,
            day = 0,
            title = plan.title,
            date = plan.date.format(DATE_FMT),
            time = plan.time?.format(TIME_FMT) ?: "",
            location = plan.subtitle,
            notes = plan.description,
            category = plan.type.name,
            attachment = null,
            assignedTo = null,
            endDate = plan.endDate?.format(DATE_FMT),
            endTime = plan.endTime?.format(TIME_FMT),
            lat = plan.lat,
            lng = plan.lng
        )
        // Check if we need to fetch firebaseId from DB first to ensure update works correctly
        val existing = tripDao.getItineraryById(plan.id)
        val finalEntity = entity.copy(firebaseId = existing?.firebaseId)

        dao.update(finalEntity)

        // 🚀 SYNC
        uploadItem(finalEntity)
    }

    suspend fun deletePlan(planId: Long) {
        val item = tripDao.getItineraryById(planId)
        if (item != null) {
            dao.deleteById(planId)
            deleteFromCloud(item)
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Specialized Adds (Flight, Lodging, etc.) - These call insertPlan/quickAddPlan,
    // so they are AUTOMATICALLY synced now! No changes needed below.
    // ────────────────────────────────────────────────────────────────────────

    suspend fun addFlightPlan(
        tripId: Long,
        depDate: LocalDate,
        depTime: LocalTime?,
        airline: String,
        flightNumber: String,
        origin: String,
        confirmation: String?,
        lat: Double?,
        lng: Double?
    ): Long {
        val title = "Flight ${flightNumber.trim()} ($airline)"
        val ui = PlanEntryUi(
            id = 0,
            date = depDate,
            time = depTime,
            title = title,
            subtitle = origin,
            description = confirmation,
            type = PlanType.Flight,
            lat = lat,
            lng = lng
        )
        return insertPlan(tripId, ui)
    }

    suspend fun addLodgingPlans(
        tripId: Long,
        lodgingName: String,
        checkInDate: LocalDate,
        checkInTime: LocalTime?,
        checkOutDate: LocalDate?,
        checkOutTime: LocalTime?,
        address: String?,
        phone: String?,
        website: String?,
        email: String?,
        confirmation: String?,
        lat: Double?,
        lng: Double?
    ): Pair<Long, Long> {
        val notes = buildString {
            if (!address.isNullOrBlank()) append("Address: $address\n")
            if (!phone.isNullOrBlank()) append("Phone: $phone\n")
            if (!website.isNullOrBlank()) append("Website: $website\n")
            if (!email.isNullOrBlank()) append("Email: $email\n")
            if (!confirmation.isNullOrBlank()) append("Conf#: $confirmation\n")
        }.trim()

        val checkInUi = PlanEntryUi(
            id = 0,
            date = checkInDate,
            time = checkInTime,
            title = "Check-in: ${lodgingName.ifBlank { "Lodging" }}",
            subtitle = address ?: lodgingName,
            description = notes,
            type = PlanType.Lodging,
            lat = lat,
            lng = lng
        )
        val inId = insertPlan(tripId, checkInUi)

        var outId = 0L
        if (checkOutDate != null) {
            val checkOutUi = PlanEntryUi(
                id = 0,
                date = checkOutDate,
                time = checkOutTime,
                title = "Check-out: ${lodgingName.ifBlank { "Lodging" }}",
                subtitle = address ?: lodgingName,
                description = notes,
                type = PlanType.Lodging,
                lat = lat,
                lng = lng
            )
            outId = insertPlan(tripId, checkOutUi)
        }
        return Pair(inId, outId)
    }

    suspend fun addRestaurantPlan(
        tripId: Long,
        name: String,
        dineDate: LocalDate,
        dineTime: LocalTime?,
        address: String?,
        people: Int?,
        lat: Double?,
        lng: Double?,
        notes: String?
    ): Long {
        val sb = buildString {
            if (!address.isNullOrBlank()) append("Address: $address\n")
            if (people != null) append("Guests: $people\n")
            if (!notes.isNullOrBlank()) append(notes.trim()).append("\n")
        }.trim()

        val ui = PlanEntryUi(
            id = 0,
            date = dineDate,
            time = dineTime,
            title = name.ifBlank { "Restaurant" },
            subtitle = address,
            description = sb,
            type = PlanType.Restaurant,
            lat = lat,
            lng = lng
        )
        return insertPlan(tripId, ui)
    }

    suspend fun addCarRentalPlan(
        tripId: Long,
        agency: String,
        confirmation: String?,
        description: String?,
        carName: String?,
        carType: String,
        pickupName: String,
        pickupAddr: String?,
        pickupDate: LocalDate,
        pickupTime: LocalTime?,
        dropName: String,
        dropAddr: String?,
        dropDate: LocalDate,
        dropTime: LocalTime?,
        lat: Double?,
        lng: Double?
    ): Long {
        val commonHeader = buildString {
            append(agency)
            if (!carType.isBlank()) append(" • $carType")
            if (!carName.isNullOrBlank()) append(" • $carName")
        }.ifBlank { "Car rental" }

        val pickupNotes = buildString {
            if (!pickupAddr.isNullOrBlank()) append("Pick-up address: $pickupAddr\n")
            if (!confirmation.isNullOrBlank()) append("Conf#: $confirmation\n")
            if (!description.isNullOrBlank()) append("Notes: $description\n")
            append("Drop-off: $dropName")
            if (!dropAddr.isNullOrBlank()) append(" ($dropAddr)")
        }.trim()

        val ui = PlanEntryUi(
            id = 0,
            date = pickupDate,
            time = pickupTime,
            title = "Car pick-up – $commonHeader",
            subtitle = pickupName,
            description = pickupNotes,
            type = PlanType.CarRental,
            lat = lat,
            lng = lng
        )
        val pickId = insertPlan(tripId, ui)

        val dropNotes = buildString {
            if (!dropAddr.isNullOrBlank()) append("Drop-off address: $dropAddr\n")
            if (!confirmation.isNullOrBlank()) append("Conf#: $confirmation\n")
            if (!description.isNullOrBlank()) append("Notes: $description\n")
            append("Pick-up: $pickupName")
            if (!pickupAddr.isNullOrBlank()) append(" ($pickupAddr)")
        }.trim()

        val dropUi = PlanEntryUi(
            id = 0,
            date = dropDate,
            time = dropTime,
            title = "Car drop-off – $commonHeader",
            subtitle = dropName,
            description = dropNotes,
            type = PlanType.CarRental,
            lat = lat,
            lng = lng
        )
        insertPlan(tripId, dropUi)

        return pickId
    }

    suspend fun addRailPlan(
        tripId: Long,
        carrier: String,
        confirmation: String?,
        depStation: String,
        depAddress: String?,
        depDate: LocalDate,
        depTime: LocalTime?,
        arrStation: String,
        arrAddress: String?,
        arrDate: LocalDate,
        arrTime: LocalTime?
    ): Long {
        val notes = buildString {
            if (!confirmation.isNullOrBlank()) append("Conf#: $confirmation\n")
            append("Departure: $depStation\n")
            if (!depAddress.isNullOrBlank()) append("$depAddress\n")

            append("\nArrival: $arrStation\n")
            if (!arrAddress.isNullOrBlank()) append("$arrAddress\n")
            append("Arrives: $arrDate")
            if (arrTime != null) append(" ${arrTime}\n")
        }.trim()

        val ui = PlanEntryUi(
            id = 0,
            date = depDate,
            time = depTime,
            title = carrier.ifBlank { "Rail" },
            subtitle = depStation,
            description = notes,
            type = PlanType.Rail,
            endDate = arrDate,
            endTime = arrTime,
            lat = null,
            lng = null
        )
        return insertPlan(tripId, ui)
    }

    suspend fun addCruisePlan(
        tripId: Long,
        cruiseLine: String,
        shipName: String,
        confirmation: String?,
        startPortName: String,
        startPortAddr: String?,
        startDate: LocalDate,
        startTime: LocalTime?,
        endPortName: String,
        endPortAddr: String?,
        endDate: LocalDate,
        endTime: LocalTime?,
        lat: Double?,
        lng: Double?
    ): Long {
        val sb = buildString {
            if (shipName.isNotBlank()) append("Ship: $shipName\n")
            if (!confirmation.isNullOrBlank()) append("Conf#: $confirmation\n")

            append("Start port: $startPortName\n")
            if (!startPortAddr.isNullOrBlank()) append("$startPortAddr\n")

            append("\nEnd port: $endPortName\n")
            if (!endPortAddr.isNullOrBlank()) append("$endPortAddr\n")
            append("Arrives: ${endDate}\n")
            if (endTime != null) append(" ${endTime}\n")
        }.trim()

        val ui = PlanEntryUi(
            id = 0,
            date = startDate,
            time = startTime,
            title = cruiseLine.ifBlank { "Cruise" },
            subtitle = startPortName,
            description = sb,
            type = PlanType.Cruise,
            lat = lat,
            lng = lng,
            endDate = endDate,
            endTime = endTime
        )
        return insertPlan(tripId, ui)
    }

    private fun ItineraryItemEntity.toUi(): PlanEntryUi {
        val ld = runCatching { LocalDate.parse(date, DATE_FMT) }.getOrElse { LocalDate.now() }
        val lt = time.takeIf { it.isNotBlank() }?.let { runCatching { LocalTime.parse(it, TIME_FMT) }.getOrNull() }

        val pType = mapCategoryToPlanType(category)

        val sub = location?.takeIf { it.isNotBlank() } ?: notes
        val desc = notes?.takeIf { it.isNotBlank() }

        val parsedEndDate = endDate?.takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it, DATE_FMT) }.getOrNull() }
        val parsedEndTime = endTime?.takeIf { it.isNotBlank() }?.let { runCatching { LocalTime.parse(it, TIME_FMT) }.getOrNull() }

        return PlanEntryUi(
            id = id,
            date = ld,
            time = lt,
            title = title,
            subtitle = sub,
            description = desc,
            type = pType,
            lat = lat,
            lng = lng,
            endDate = parsedEndDate,
            endTime = parsedEndTime
        )
    }

    private fun mapCategoryToPlanType(category: String?): PlanType {
        if (category.isNullOrBlank()) return PlanType.Activity
        PlanType.values().firstOrNull { it.name.equals(category, true) }?.let { return it }
        PlanType.values().firstOrNull { it.label.equals(category, true) }?.let { return it }
        return PlanType.Activity
    }
}

// ─────────────────────────── Checklist ───────────────────────────

class ChecklistRepository(
    private val dao: ChecklistDao,
    private val savedListDao: SavedListDao,
    private val tripDao: TripDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun startSync(tripId: Long, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val trip = tripDao.getById(tripId) ?: return@launch
            val tripFid = trip.firebaseId ?: return@launch

            // 1. Listen for CATEGORIES
            firestore.collection("trips").document(tripFid).collection("checklist_categories")
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener
                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshots.documents) {
                            val fid = doc.id
                            val name = doc.getString("name") ?: "Category"
                            val sort = doc.getLong("sort")?.toInt() ?: 0

                            val existing = dao.getCategoryByFirebaseId(fid)
                            val entity = existing?.copy(categoryName = name, sort = sort)
                                ?: ChecklistCategoryEntity(
                                    tripId = tripId,
                                    categoryName = name,
                                    sort = sort,
                                    firebaseId = fid
                                )

                            if (existing != null) dao.updateCategory(entity) else dao.insertCategory(entity)
                        }
                        // Handle deletions if needed (compare IDs)
                    }
                }

            // 2. Listen for ITEMS
            firestore.collection("trips").document(tripFid).collection("checklist_items")
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener
                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshots.documents) {
                            val fid = doc.id
                            val catFid = doc.getString("categoryFid") ?: continue
                            val title = doc.getString("title") ?: ""
                            val completed = doc.getBoolean("completed") ?: false
                            val quantity = doc.getLong("quantity")?.toInt() ?: 1
                            val note = doc.getString("note")

                            // Find local category ID using the Cloud Category ID
                            val localCat = dao.getCategoryByFirebaseId(catFid) ?: continue // Wait for cat sync

                            val existing = dao.getItemByFirebaseId(fid)
                            val entity = existing?.copy(
                                title = title,
                                completed = completed,
                                quantity = quantity,
                                note = note
                            ) ?: ChecklistItemEntity(
                                categoryId = localCat.categoryId,
                                title = title,
                                completed = completed,
                                quantity = quantity,
                                note = note,
                                firebaseId = fid
                            )

                            if (existing != null) dao.updateItem(entity) else dao.insertItem(entity)
                        }
                    }
                }
        }
    }

    // ─────────────────────────── CLOUD UPLOAD HELPERS ───────────────────────────

    private suspend fun uploadCategory(catId: Long) {
        val cat = dao.getCategoryById(catId) ?: return
        val trip = tripDao.getById(cat.tripId) ?: return
        val tripFid = trip.firebaseId ?: return

        // Create doc ref (if no firebaseId, generate one)
        val docRef = if (cat.firebaseId != null) {
            firestore.collection("trips").document(tripFid).collection("checklist_categories").document(cat.firebaseId)
        } else {
            firestore.collection("trips").document(tripFid).collection("checklist_categories").document()
        }

        // Save new ID locally
        if (cat.firebaseId == null) {
            dao.updateCategory(cat.copy(firebaseId = docRef.id))
        }

        docRef.set(mapOf(
            "name" to cat.categoryName,
            "sort" to cat.sort,
            "updatedAt" to System.currentTimeMillis()
        ), SetOptions.merge())
    }

    private suspend fun uploadItem(itemId: Long) {
        val item = dao.getItemById(itemId) ?: return
        val cat = dao.getCategoryById(item.categoryId) ?: return
        val trip = tripDao.getById(cat.tripId) ?: return
        val tripFid = trip.firebaseId ?: return

        // Ensure category has a cloud ID first
        if (cat.firebaseId == null) uploadCategory(cat.categoryId)
        val parentCat = dao.getCategoryById(cat.categoryId) ?: return
        val parentCatFid = parentCat.firebaseId ?: return

        val docRef = if (item.firebaseId != null) {
            firestore.collection("trips").document(tripFid).collection("checklist_items").document(item.firebaseId)
        } else {
            firestore.collection("trips").document(tripFid).collection("checklist_items").document()
        }

        if (item.firebaseId == null) {
            dao.updateItem(item.copy(firebaseId = docRef.id))
        }

        docRef.set(mapOf(
            "categoryFid" to parentCatFid,
            "title" to item.title,
            "completed" to item.completed,
            "quantity" to item.quantity,
            "note" to item.note,
            "updatedAt" to System.currentTimeMillis()
        ), SetOptions.merge())
    }

    private suspend fun deleteCategoryCloud(catId: Long) {
        val cat = dao.getCategoryById(catId) ?: return
        if (cat.firebaseId == null) return
        val trip = tripDao.getById(cat.tripId) ?: return
        val tripFid = trip.firebaseId ?: return

        firestore.collection("trips").document(tripFid)
            .collection("checklist_categories").document(cat.firebaseId).delete()
    }

    private suspend fun deleteItemCloud(itemId: Long) {
        val item = dao.getItemById(itemId) ?: return
        if (item.firebaseId == null) return
        val cat = dao.getCategoryById(item.categoryId) ?: return
        val trip = tripDao.getById(cat.tripId) ?: return
        val tripFid = trip.firebaseId ?: return

        firestore.collection("trips").document(tripFid)
            .collection("checklist_items").document(item.firebaseId).delete()
    }
    fun observeCategoriesWithItems(tripId: Long) = dao.observeCategoriesWithItems(tripId)

    suspend fun addCategory(tripId: Long, name: String, sort: Int = 0): Long {
        val id = dao.insertCategory(ChecklistCategoryEntity(tripId = tripId, categoryName = name, sort = sort))
        uploadCategory(id) // 🚀 Push to Cloud
        return id
    }

    suspend fun addCategoryReturnId(tripId: Long, name: String): Long {
        return addCategory(tripId, name)
    }

    suspend fun addItem(categoryId: Long, title: String, dueDate: LocalDate? = null, note: String? = null, sort: Int = 0): Long {
        val id = dao.insertItem(ChecklistItemEntity(categoryId = categoryId, title = title, dueDate = dueDate, note = note, completed = false, sort = sort))
        uploadItem(id) // 🚀 Push to Cloud
        return id
    }

    suspend fun toggle(itemId: Long, completed: Boolean) {
        dao.toggle(itemId, completed)
        uploadItem(itemId) // 🚀 Push to Cloud
    }

    suspend fun setQuantity(itemId: Long, quantity: Int) {
        dao.setQuantity(itemId, quantity)
        uploadItem(itemId) // 🚀 Push to Cloud
    }

    suspend fun deleteItem(itemId: Long) {
        deleteItemCloud(itemId) // 🚀 Delete from Cloud
        dao.deleteItem(itemId)
    }

    suspend fun deleteCategory(categoryId: Long) {
        deleteCategoryCloud(categoryId) // 🚀 Delete from Cloud
        dao.deleteCategory(categoryId)
    }

    fun observeSavedLists(userId: Long): Flow<List<SavedChecklistEntity>> =
        savedListDao.observeSavedLists(userId)


    suspend fun deleteSavedList(id: Long) = savedListDao.delete(id)

    suspend fun saveTemplate(userId: Long, name: String, items: List<ItemUi>) {
        val checklist = SavedChecklistEntity(userId = userId, name = name)
        val newId = savedListDao.insert(checklist)

        val savedItems = items.map {
            SavedChecklistItemEntity(
                checklistId = newId,
                title = it.title,
                quantity = it.quantity
            )
        }
        savedListDao.insertItems(savedItems)
    }

    suspend fun importTemplate(tripId: Long, listId: Long, listName: String) {
        val newCatId = dao.insertCategory(ChecklistCategoryEntity(tripId = tripId, categoryName = listName))
        val savedItems = savedListDao.getItems(listId)

        savedItems.forEach {
            dao.insertItem(
                ChecklistItemEntity(
                    categoryId = newCatId,
                    title = it.title,
                    quantity = it.quantity,
                    completed = false
                )
            )
        }
    }
}

// ─────────────────────────── Route / Stops ───────────────────────────

class RouteRepository(private val dao: RouteDao) {
    private val ISO = DateTimeFormatter.ISO_LOCAL_DATE

    suspend fun getRouteStops(tripId: Long): List<RouteStopEntity> =
        dao.getByTripId(tripId)

    suspend fun deleteAllStopsForTrip(tripId: Long) {
        dao.deleteByTripId(tripId)
    }

    fun observeStopsUi(tripId: Long): Flow<List<RouteStopUi>> =
        dao.observeStops(tripId).map { rows ->
            val ordered = rows.sortedBy { it.orderInRoute }

            ordered.mapIndexed { index, e ->
                val prev = ordered.getOrNull(index - 1)

                val km = if (prev != null) {
                    haversineKm(
                        prev.lat ?: 0.0,
                        prev.lng ?: 0.0,
                        e.lat ?: 0.0,
                        e.lng ?: 0.0
                    )
                } else null

                val start = e.startDate?.let { LocalDate.parse(it, ISO) }
                val end = when {
                    e.endDate != null -> LocalDate.parse(e.endDate, ISO)
                    start != null && e.nights > 0 -> start.plusDays(e.nights.toLong())
                    else -> start
                }

                RouteStopUi(
                    id = e.id,
                    name = e.label,
                    start = start ?: LocalDate.now(),
                    end = end ?: (start ?: LocalDate.now()),
                    nights = kotlin.math.max(e.nights, 0),
                    lat = e.lat ?: 0.0,
                    lng = e.lng ?: 0.0,
                    distanceFromPrevKm = km?.roundToInt()
                )
            }
        }

    suspend fun insertRouteStops(stops: List<RouteStopEntity>) {
        dao.insertAll(stops)
    }

    suspend fun changeNights(stopId: Long, delta: Int) {
        dao.incrementNights(stopId, delta)
    }

    suspend fun setDates(stopId: Long, start: LocalDate?, end: LocalDate?) {
        dao.setDates(stopId, start?.format(ISO), end?.format(ISO))
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a =
            sin(dLat / 2).pow(2.0) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}