// data/db/WaitlistDao.kt
package com.example.tripshare.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tripshare.data.model.JoinRequestEntity
import com.example.tripshare.data.model.WaitlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaitlistDao {

    @Query("""
    SELECT * FROM waitlist
    WHERE userId = :userId
    ORDER BY id DESC
""")
    fun observeForUser(userId: Long): Flow<List<WaitlistEntity>>

    @Query("DELETE FROM waitlist WHERE tripId = :tripId AND userId = :userId")
    suspend fun deleteByUserId(tripId: Long, userId: Long)
    @Query("SELECT * FROM waitlist WHERE firebaseId = :fid LIMIT 1")
    suspend fun getByFirebaseId(fid: String): WaitlistEntity?
    @Query("SELECT * FROM waitlist WHERE tripId = :tripId ORDER BY position ASC")
    suspend fun getForTrip(tripId: Long): List<WaitlistEntity>

    @Query("SELECT maxParticipants FROM trips WHERE id = :tripId")
    suspend fun getMaxParticipants(tripId: Long): Int

    @Query("SELECT * FROM waitlist WHERE tripId = :tripId ORDER BY position ASC")
    fun observeForTrip(tripId: Long): Flow<List<WaitlistEntity>>

    @Query("SELECT * FROM waitlist WHERE tripId = :tripId AND userId = :userId LIMIT 1")
    suspend fun findByUser(tripId: Long, userId: Long): WaitlistEntity?

    @Query("SELECT COUNT(*) FROM waitlist WHERE tripId = :tripId")
    suspend fun countForTrip(tripId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WaitlistEntity)

    @Query("SELECT * FROM waitlist WHERE tripId = :tripId")
    fun observeWaitlist(tripId: Long): Flow<List<WaitlistEntity>>

    @Update
    suspend fun update(entry: WaitlistEntity)

    @Delete
    suspend fun delete(entry: WaitlistEntity)
}
@Dao
interface JoinRequestDao {
    @Query("SELECT * FROM join_requests WHERE tripId = :tripId AND status = 'PENDING' ORDER BY createdAt ASC")
    fun observePending(tripId: Long): Flow<List<JoinRequestEntity>>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM join_requests 
            WHERE tripId = :tripId AND userId = :userId 
            LIMIT 1
        )
    """)
    suspend fun exists(tripId: Long, userId: Long): Boolean

    @Insert
    suspend fun insert(req: JoinRequestEntity): Long

    @Query("SELECT * FROM join_requests WHERE tripId=:tripId")
    fun observeByTrip(tripId: Long): Flow<List<JoinRequestEntity>>

    @Update
    suspend fun update(req: JoinRequestEntity)

    @Query("DELETE FROM join_requests WHERE id = :id")
    suspend fun delete(id: Long)
}