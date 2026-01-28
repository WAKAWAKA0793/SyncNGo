package com.example.tripshare.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction // Required for Relations
import com.example.tripshare.data.model.RatingSummary
import com.example.tripshare.data.model.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: ReviewEntity): Long
    @Query("SELECT * FROM reviews WHERE firebaseId = :fid LIMIT 1")
    suspend fun getByFirebaseId(fid: String): ReviewEntity?
    // ✅ UPDATED: Returns the Review AND the User details
    @Transaction
    @Query("""
        SELECT * FROM reviews
        WHERE targetUserId = :userId
        ORDER BY id DESC
    """)
    fun observeReviewsForUser(userId: Long): Flow<List<ReviewWithReviewer>>

    @Query("SELECT * FROM reviews WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ReviewEntity?

    @Query("DELETE FROM reviews WHERE targetUserId = :userId")
    suspend fun deleteForUser(userId: Long)

    @Query("""
    SELECT 
        AVG(rating) AS averageRating,
        COUNT(*) AS reviewCount
    FROM reviews
    WHERE targetUserId = :userId
""")
    suspend fun getRatingSummary(userId: Long): RatingSummary?

    @androidx.room.Update
    suspend fun update(review: ReviewEntity)

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteById(reviewId: Long)
}