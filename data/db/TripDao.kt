package com.example.tripshare.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.tripshare.data.model.CalendarEventEntity
import com.example.tripshare.data.model.ChecklistCategoryEntity
import com.example.tripshare.data.model.ChecklistItemEntity
import com.example.tripshare.data.model.DailyNoteEntity
import com.example.tripshare.data.model.ItineraryItemEntity
import com.example.tripshare.data.model.ParticipantInviteEntity
import com.example.tripshare.data.model.PollEntity
import com.example.tripshare.data.model.PollVoteEntity
import com.example.tripshare.data.model.PollVoterDetail
import com.example.tripshare.data.model.RouteStopEntity
import com.example.tripshare.data.model.SavedChecklistEntity
import com.example.tripshare.data.model.SavedChecklistItemEntity
import com.example.tripshare.data.model.TripCommentEntity
import com.example.tripshare.data.model.TripCommentWithAuthor
import com.example.tripshare.data.model.TripDocumentEntity
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.TripMeetingPointEntity
import com.example.tripshare.data.model.TripNoteEntity
import com.example.tripshare.data.model.TripParticipantEntity
import com.example.tripshare.data.model.TripPaymentMethodEntity
import com.example.tripshare.data.model.UserEntity
import com.example.tripshare.data.model.VoteOptionEntity
import com.example.tripshare.data.model.WaitlistEntity
import kotlinx.coroutines.flow.Flow

// ─── TripDao ─────────────────────────────────────────────────────────
@Dao
interface TripDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouteStops(stops: List<RouteStopEntity>)
    // exact match inside a trip (case-insensitive)
    @Query("""
SELECT u.id FROM users u
JOIN trip_participants p ON p.userId = u.id
WHERE p.tripId = :tripId AND LOWER(u.name) = LOWER(:name)
LIMIT 1
""")
    suspend fun findUserIdByNameInTripExact(tripId: Long, name: String): Long?
    @Query("SELECT * FROM trips WHERE firebaseId = :fid LIMIT 1")
    suspend fun getTripByFirebaseId(fid: String): TripEntity?

    @Query("""
SELECT u.id FROM users u
JOIN trip_participants p ON p.userId = u.id
WHERE p.tripId = :tripId AND LOWER(u.name) LIKE LOWER(:prefix) || '%'
ORDER BY LENGTH(u.name) ASC
LIMIT 1
""")
    suspend fun findUserIdByNameInTripStartsWith(tripId: Long, prefix: String): Long?

    @Query("SELECT * FROM trips WHERE isArchived = 0 ORDER BY startDate ASC")
    fun observeActiveTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE isArchived = 1 ORDER BY endDate DESC")
    fun observePastTrips(): Flow<List<TripEntity>>

    @Update
    suspend fun update(trip: TripEntity)

    // 👇 ADD THIS: Fetch a single trip (if not already present)
    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripById(id: Long): TripEntity?

    @Query("""
        SELECT * FROM trips
        WHERE endDate IS NOT NULL
          AND endDate <= :cutoffEpochDay
          AND isArchived = 0
    """)
    suspend fun getTripsEndedBefore(cutoffEpochDay: Long): List<TripEntity>

    @Query("UPDATE trips SET isArchived = 1 WHERE id IN (:ids)")
    suspend fun markArchived(ids: List<Long>)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTrip(tripId: Long)
    /* Optional filters */
    @Query("""
        SELECT * FROM trips
        WHERE isArchived = 0 AND name LIKE '%' || :q || '%'
        ORDER BY startDate ASC
    """)
    fun searchActive(q: String): Flow<List<TripEntity>>

    @Query("SELECT id FROM trips ORDER BY id DESC LIMIT 1")
    suspend fun getLatestTripId(): Long?
    @Insert
    suspend fun insertCalendarEvents(events: List<CalendarEventEntity>)
    @Query("""
    SELECT t.id
    FROM trips t
    JOIN trip_participants p ON p.tripId = t.id
    WHERE p.userId = :userId
    ORDER BY t.id DESC
    LIMIT 1
""")
    suspend fun getLatestTripIdForUser(userId: Long): Long?
    @Transaction
    suspend fun insertTripWithAll(
        trip: TripEntity,
        routes: List<RouteStopEntity>,
        payments: List<TripPaymentMethodEntity>,
        invites: List<ParticipantInviteEntity>,
        events: List<CalendarEventEntity> // <--- NEW PARAMETER
    ): Long {
        val newTripId = insertTrip(trip)

        if (routes.isNotEmpty()) {
            insertRouteStops(routes.map { it.copy(tripId = newTripId) })
        }
        if (payments.isNotEmpty()) {
            insertPaymentMethods(payments.map { it.copy(tripId = newTripId) })
        }
        if (invites.isNotEmpty()) {
            insertInvites(invites.map { it.copy(tripId = newTripId) })
        }

        // 3. Insert the events (Trip Start / Trip End)
        if (events.isNotEmpty()) {
            insertCalendarEvents(events.map { it.copy(tripId = newTripId) })
        }

        return newTripId
    }

    @Query("SELECT * FROM trips WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<TripEntity?>

    @Query("UPDATE trips SET coverImgUrl = :uri WHERE id = :tripId")
    suspend fun updateTripImage(tripId: Long, uri: String)

    @Query("SELECT * FROM trip_participants WHERE tripId = :tripId")
    suspend fun getParticipants(tripId: Long): List<TripParticipantEntity>

    @Query("SELECT COUNT(*) FROM trip_participants WHERE tripId = :tripId")
    suspend fun getParticipantCount(tripId: Long): Int

    @Query("""
    SELECT t.* FROM trips t
    INNER JOIN trip_participants p ON p.tripId = t.id
    WHERE p.userId = :userId
      AND t.isArchived = 0
    ORDER BY t.startDate ASC
""")
    fun observeJoinedActiveTrips(userId: Long): Flow<List<TripEntity>>

    @Query("""
    SELECT * FROM trips 
    WHERE organizerId = :userId 
    OR id IN (SELECT tripId FROM trip_participants WHERE userId = :userId)
""")
    fun observeTripsForUser(userId: Long): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TripEntity?
    // Inserts
    @Insert suspend fun insertTrip(trip: TripEntity): Long
    @Insert suspend fun insertRoute(stops: List<RouteStopEntity>)
    @Insert suspend fun insertPaymentMethods(methods: List<TripPaymentMethodEntity>)
    @Insert suspend fun insertInvites(invites: List<ParticipantInviteEntity>)


    // Queries / observables
    @Query("SELECT tripId FROM trip_participants WHERE userId = :userId")
    fun observeTripIdsForUser(userId: Long): Flow<List<Long>>

    @Query("SELECT tripId FROM trip_participants WHERE userId = :userId")
    fun observeJoinedTripIds(userId: Long): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM trip_participants WHERE tripId = :tripId AND userId = :userId")
    fun observeIsUserParticipant(tripId: Long, userId: Long): Flow<Int>

    @Transaction
    @Query("SELECT * FROM trips WHERE id = :id")
    fun observeTripFull(id: Long): Flow<TripFullAggregate?> // Define TripFullAggregate if you use it

    @Transaction
    @Query("SELECT * FROM trips ORDER BY id DESC")
    fun observeAllTripFull(): Flow<List<TripFullAggregate>>

    @Query("SELECT * FROM trips ORDER BY id DESC")
    fun observeAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId ORDER BY time")
    fun observeItinerary(tripId: Long): Flow<List<ItineraryItemEntity>>

    @Query("SELECT * FROM itinerary_items WHERE id = :id LIMIT 1")
    suspend fun getItineraryById(id: Long): ItineraryItemEntity?

    @Insert suspend fun insertItinerary(item: ItineraryItemEntity): Long
    @Update suspend fun updateItinerary(item: ItineraryItemEntity)
    @Delete suspend fun deleteItinerary(item: ItineraryItemEntity)

    @Insert
    suspend fun insertParticipants(participants: List<TripParticipantEntity>)

    @Query("SELECT * FROM trip_participants WHERE tripId = :tripId")
    fun observeParticipants(tripId: Long): Flow<List<TripParticipantEntity>>

    @Query("SELECT COUNT(*) FROM trip_participants WHERE tripId = :tripId AND userId = :userId")
    suspend fun isUserParticipant(tripId: Long, userId: Long): Int

    @Insert
    suspend fun insertMeetingPoints(points: List<TripMeetingPointEntity>)


    @Query("SELECT * FROM trip_meeting_points WHERE tripId = :tripId")
    fun observeMeetingPoints(tripId: Long): Flow<List<TripMeetingPointEntity>>
    // TripDao
    @Query("UPDATE trips SET isArchived = 1 WHERE id = :tripId")
    suspend fun archiveTrip(tripId: Long)

    @Query("DELETE FROM trip_meeting_points WHERE tripId = :tripId")
    suspend fun deleteMeetingPointsForTrip(tripId: Long)

    // Archived trips for a given user (so all members see it)
    @Query(
        """
        SELECT t.* FROM trips t
        INNER JOIN trip_participants p ON p.tripId = t.id
        WHERE p.userId = :userId AND t.isArchived = 1
        ORDER BY t.startDate DESC
        """
    )
    fun observeArchivedTripsForUser(userId: Long): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE isArchived = 0")
    fun getActiveTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE isArchived = 1")
    fun getArchivedTrips(): Flow<List<TripEntity>>

    @Insert
    suspend fun insertWaitlist(waitlist: WaitlistEntity) // define if used

    @Query("DELETE FROM route_stops WHERE tripId = :tripId")
    suspend fun deleteRouteStopsForTrip(tripId: Long)

    @Transaction
    suspend fun updateTripFromCloud(
        trip: TripEntity,
        stops: List<RouteStopEntity>
    ) {
        // 1. Update the Trip details
        update(trip)

        // 2. Refresh Stops: Delete old ones and insert new ones from Cloud
        // (This ensures consistency if stops were reordered/deleted on another device)
        deleteRouteStopsForTrip(trip.id) // You defined this in RouteDao, or add DELETE FROM route_stops WHERE tripId = :tid here
        insertRouteStops(stops.map { it.copy(tripId = trip.id) })
    }
}

// ─── TripCalendarDao ─────────────────────────────────────────────────
@Dao
interface TripCalendarDao {
    @Query("SELECT * FROM calendar_events WHERE tripId = :tripId ORDER BY date, time")
    fun observeEvents(tripId: Long): Flow<List<CalendarEventEntity>>
    @Insert suspend fun addEvent(event: CalendarEventEntity)

    @Query("SELECT * FROM trip_documents WHERE tripId = :tripId")
    fun observeDocs(tripId: Long): Flow<List<TripDocumentEntity>>
    @Insert suspend fun addDoc(doc: TripDocumentEntity)

    @Insert suspend fun addNote(note: DailyNoteEntity)
    @Update suspend fun updateNote(note: DailyNoteEntity)
    @Query("""
    SELECT * FROM trip_notes
    WHERE (
        (:tripId IS NULL AND tripId IS NULL) OR (tripId = :tripId)
    )
    AND date = :date
    LIMIT 1
""")
    suspend fun getNoteByDate(tripId: Long?, date: String): TripNoteEntity?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: TripNoteEntity): Long

    @Update
    suspend fun updateNote(note: TripNoteEntity)

    // 2. The flow your UI is observing
    @Query("SELECT * FROM trip_notes WHERE tripId = :tripId")
    fun getNotesFlow(tripId: Long): Flow<List<TripNoteEntity>>

    @Query("""
    SELECT * FROM trip_notes
    WHERE tripId = :tripId
    ORDER BY date
""")
    fun observeNotes(tripId: Long): Flow<List<TripNoteEntity>>

    @Query("""
    SELECT * FROM trip_notes
    WHERE tripId IS NULL
    ORDER BY date DESC
""")
    fun observeGlobalNotes(): Flow<List<TripNoteEntity>>


    @Query(
        """
        DELETE FROM trip_notes
        WHERE tripId = :tripId AND date = :date
        """
    )
    suspend fun deleteNoteForDay(tripId: Long, date: String)

    // ✅ ADD THIS: Lookup by Firebase ID for Sync
    @Query("SELECT * FROM trip_notes WHERE firebaseId = :fid LIMIT 1")
    suspend fun getNoteByFirebaseId(fid: String): TripNoteEntity?

    // ✅ ADD THIS: For updating local ID after cloud creation
    @Query("SELECT * FROM trip_notes WHERE noteId = :id LIMIT 1")
    suspend fun getNoteById(id: Long): TripNoteEntity?
    /**
     * Upsert pattern: delete existing note for that (tripId, date),
     * then insert new one if text is not blank.
     */
    @Transaction
    suspend fun upsertNote(tripId: Long, date: String, text: String) {
        deleteNoteForDay(tripId, date)
        if (text.isNotBlank()) {
            insertNote(
                TripNoteEntity(
                    tripId = tripId,
                    date = date,
                    note = text
                )
            )
        }
    }
}
// In data/db/TripDao.kt

@Dao
interface VoteDao {
    // ─── Create ───
    @Insert
    suspend fun insertPoll(poll: PollEntity): Long

    @Insert
    suspend fun insertOption(option: VoteOptionEntity)

    // ─── Read ───
    @Query("SELECT * FROM polls WHERE tripId = :tripId")
    fun observePolls(tripId: Long): Flow<List<PollEntity>>

    @Query("SELECT * FROM vote_options WHERE pollId = :pollId ORDER BY id ASC")
    fun observeOptions(pollId: Long): Flow<List<VoteOptionEntity>>

    // Helper to get options synchronously for update logic
    @Query("SELECT * FROM vote_options WHERE pollId = :pollId ORDER BY id ASC")
    suspend fun getOptionsList(pollId: Long): List<VoteOptionEntity>

    @Query("SELECT * FROM poll_votes WHERE pollId = :pollId AND userId = :userId")
    suspend fun getUserVotes(pollId: Long, userId: Long): List<PollVoteEntity>
    @Query("SELECT * FROM polls WHERE firebaseId = :fid LIMIT 1")
    suspend fun getPollByFirebaseId(fid: String): PollEntity?

    @Query("SELECT * FROM polls WHERE id = :pollId LIMIT 1")
    suspend fun getPollById(pollId: Long): PollEntity?

    @Query("SELECT * FROM vote_options WHERE firebaseId = :fid LIMIT 1")
    suspend fun getOptionByFirebaseId(fid: String): VoteOptionEntity?
    // ─── Update ───
    @Query("UPDATE polls SET question = :question, allowMultiple = :allowMultiple WHERE id = :pollId")
    suspend fun updatePollHeader(pollId: Long, question: String, allowMultiple: Boolean)
    @Query("UPDATE vote_options SET votes = :count WHERE id = :optionId")
    suspend fun setVoteCount(optionId: Long, count: Int)
    @Query("UPDATE vote_options SET option = :text WHERE id = :optionId")
    suspend fun updateOptionText(optionId: Long, text: String)

    @Query("""
    SELECT 
        pv.optionId, 
        pv.userId, 
        u.name as displayName,  
        u.profilePhoto            
    FROM poll_votes pv
    INNER JOIN users u ON pv.userId = u.id
    WHERE pv.pollId = :pollId
""")
    fun observePollVoters(pollId: Long): Flow<List<PollVoterDetail>>
    // ─── Delete ───
    @Query("DELETE FROM polls WHERE id = :pollId")
    suspend fun deletePoll(pollId: Long)

    @Query("DELETE FROM vote_options WHERE id = :optionId")
    suspend fun deleteOption(optionId: Long)

    // ─── Voting Logic ───
    @Query("UPDATE vote_options SET votes = votes + 1 WHERE id = :optionId")
    suspend fun incrementVote(optionId: Long)
    @Query("DELETE FROM poll_votes WHERE pollId = :pollId AND userId = :userId")
    suspend fun clearUserVotes(pollId: Long, userId: Long)
    @Query("UPDATE vote_options SET votes = votes - 1 WHERE id = :optionId AND votes > 0")
    suspend fun decrementVote(optionId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserVote(vote: PollVoteEntity)

    @Query("DELETE FROM poll_votes WHERE pollId = :pollId AND userId = :userId")
    suspend fun deleteUserVotesForPoll(pollId: Long, userId: Long)

    @Query("DELETE FROM poll_votes WHERE pollId = :pollId AND userId = :userId AND optionId = :optionId")
    suspend fun deleteUserVote(pollId: Long, userId: Long, optionId: Long)
}

// ─── ItineraryDao ────────────────────────────────────────────────────
@Dao
interface ItineraryDao {

    @Query("""
        SELECT * FROM itinerary_items
        WHERE tripId = :tripId
        ORDER BY date ASC, time ASC
    """)
    fun observeItemsForTrip(tripId: Long): Flow<List<ItineraryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItineraryItemEntity): Long

    @Query("SELECT * FROM itinerary_items WHERE firebaseId = :fid LIMIT 1")
    suspend fun getByFirebaseId(fid: String): ItineraryItemEntity?
    @Update
    suspend fun update(item: ItineraryItemEntity)

    @Delete
    suspend fun delete(item: ItineraryItemEntity)

    @Query("DELETE FROM itinerary_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId ORDER BY date, time")
    fun observePlans(tripId: Long): Flow<List<ItineraryItemEntity>>

}

@Dao
interface ChecklistDao {

    @Transaction
    @Query("""
        SELECT * FROM checklist_categories
        WHERE tripId = :tripId
        ORDER BY sort, categoryId
    """)
    fun observeCategoriesWithItems(tripId: Long): Flow<List<CategoryWithItems>>
    // Trips
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTrip(entity: TripEntity): Long

    @Query("SELECT * FROM trips ORDER BY id DESC")
    fun observeTrips(): Flow<List<TripEntity>>

    // Categories
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(entity: ChecklistCategoryEntity): Long

    @Transaction
    @Query("SELECT * FROM trips WHERE id = :id") // or tripId = :id if that's your PK
    suspend fun getTripFull(id: Long): TripFullAggregate

    // Items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(entity: ChecklistItemEntity): Long
    @Query("SELECT * FROM checklist_categories WHERE firebaseId = :fid LIMIT 1")
    suspend fun getCategoryByFirebaseId(fid: String): ChecklistCategoryEntity?

    @Query("SELECT * FROM checklist_items WHERE firebaseId = :fid LIMIT 1")
    suspend fun getItemByFirebaseId(fid: String): ChecklistItemEntity?

    @Query("SELECT * FROM checklist_categories WHERE categoryId = :id")
    suspend fun getCategoryById(id: Long): ChecklistCategoryEntity?

    @Query("SELECT * FROM checklist_items WHERE itemId = :id")
    suspend fun getItemById(id: Long): ChecklistItemEntity?
    @Query("UPDATE checklist_items SET completed = :completed WHERE itemId = :itemId")
    suspend fun setCompleted(itemId: Long, completed: Boolean)
    @Query("DELETE FROM checklist_items WHERE itemId = :itemId")
    suspend fun deleteItem(itemId: Long)

    @Query("DELETE FROM checklist_categories WHERE categoryId = :categoryId")
    suspend fun deleteCategory(categoryId: Long)
    @Query("UPDATE checklist_items SET completed = :completed WHERE itemId = :itemId")
    suspend fun toggle(itemId: Long, completed: Boolean)

    @Query("UPDATE checklist_items SET quantity = :quantity WHERE itemId = :itemId")
    suspend fun setQuantity(itemId: Long, quantity: Int)
    @Transaction
    suspend fun toggleCompleted(itemId: Long, current: Boolean) = setCompleted(itemId, !current)

    @Update
    suspend fun updateCategory(cat: ChecklistCategoryEntity)

    @Update
    suspend fun updateItem(item: ChecklistItemEntity)
}

@Dao
interface ParticipantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(participant: TripParticipantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(participants: List<TripParticipantEntity>)

    @Query("SELECT * FROM trip_participants WHERE tripId = :tripId ORDER BY displayName COLLATE NOCASE ASC")
    fun observeParticipantsForTrip(tripId: Long): Flow<List<TripParticipantEntity>>

    // One-off read
    @Query("SELECT * FROM trip_participants WHERE tripId = :tripId ORDER BY displayName COLLATE NOCASE ASC")
    suspend fun getParticipantsForTrip(tripId: Long): List<TripParticipantEntity>

    // Delete by participant id (hard delete)
    @Query("DELETE FROM trip_participants WHERE id = :participantId AND tripId = :tripId")
    suspend fun deleteParticipant(tripId: Long, participantId: Long): Int
    // returns number of rows deleted

    // Delete by email as fallback
    @Query("DELETE FROM trip_participants WHERE email = :email AND tripId = :tripId")
    suspend fun deleteParticipantByEmail(tripId: Long, email: String): Int

    // Insert / upsert participant
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: TripParticipantEntity): Long

    // Optional: remove all participants for a trip
    @Query("DELETE FROM trip_participants WHERE tripId = :tripId")
    suspend fun deleteAllParticipantsForTrip(tripId: Long): Int
    @Query(
        """
        SELECT u.*
        FROM users AS u
        INNER JOIN trip_participants AS tp
            ON tp.userId = u.id
        WHERE tp.tripId = :tripId
        ORDER BY u.name COLLATE NOCASE
        """
    )
    fun observeUsersForTrip(tripId: Long): Flow<List<UserEntity>>
    @Query(
        """
        SELECT u.*
        FROM users AS u
        INNER JOIN trip_participants AS tp
            ON tp.userId = u.id
        WHERE tp.tripId = :tripId
        ORDER BY u.name COLLATE NOCASE
        """
    )
    suspend fun getUsersForTripOnce(tripId: Long): List<UserEntity>
    @Query("""
        SELECT COUNT(*) FROM trip_participants 
        WHERE tripId = :tripId AND userId IN (:userA, :userB)
    """)
    suspend fun countMembership(tripId: Long, userA: Long, userB: Long): Int

    @Query("SELECT * FROM trip_participants WHERE tripId = :tripId")
    suspend fun getByTrip(tripId: Long): List<TripParticipantEntity>


}

@Dao
interface RouteDao {
    @Query("""
        SELECT * FROM route_stops
        WHERE tripId = :tripId
        ORDER BY orderInRoute ASC
    """)
    fun observeStops(tripId: Long): Flow<List<RouteStopEntity>>

    @Query("UPDATE route_stops SET nights = nights + :delta WHERE id = :stopId")
    suspend fun incrementNights(stopId: Long, delta: Int)

    @Query("UPDATE route_stops SET startDate = :start, endDate = :end WHERE id = :stopId")
    suspend fun setDates(stopId: Long, start: String?, end: String?)

    @Query("SELECT * FROM route_stops WHERE tripId = :tripId ORDER BY orderInRoute ASC")
    suspend fun getByTripId(tripId: Long): List<RouteStopEntity>

    // 👇 ADD THIS: Clear stops before re-saving (easiest way to handle reordering/deletions)
    @Query("DELETE FROM route_stops WHERE tripId = :tripId")
    suspend fun deleteByTripId(tripId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stop: RouteStopEntity): Long

    @Update
    suspend fun update(stop: RouteStopEntity)

    @Delete
    suspend fun delete(stop: RouteStopEntity)

    @Insert
    suspend fun insertAll(stops: List<RouteStopEntity>)
}

// In TripDao.kt or SavedListDao.kt
@Dao
interface SavedListDao {
    @Query("SELECT * FROM saved_checklists WHERE userId = :userId")
    fun observeSavedLists(userId: Long): Flow<List<SavedChecklistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checklist: SavedChecklistEntity): Long

    @Query("DELETE FROM saved_checklists WHERE id = :id")
    suspend fun delete(id: Long)
    @Insert
    suspend fun insertList(list: SavedChecklistEntity): Long

    @Insert
    suspend fun insertItems(items: List<SavedChecklistItemEntity>)

    @Query("SELECT * FROM saved_checklist_items WHERE checklistId = :listId")
    suspend fun getItems(listId: Long): List<SavedChecklistItemEntity>
}

@Dao
interface TripCommentDao {

    @Transaction // ⭐ REQUIRED for @Relation
    @Query("""
        SELECT * FROM trip_comments
        WHERE planId = :planId
        ORDER BY timestamp ASC
    """)
    fun observeCommentsForPlan(
        planId: Long
    ): Flow<List<TripCommentWithAuthor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: TripCommentEntity)

    @Query("DELETE FROM trip_comments WHERE id = :commentId")
    suspend fun delete(commentId: Long)
}
