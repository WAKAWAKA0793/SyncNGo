package com.example.tripshare.data.db

import androidx.room.Dao
import androidx.room.Query
import com.example.tripshare.data.model.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripHistoryDao {

    // Observe archived trips for a specific user
    @Query("""
        SELECT t.* FROM trips t 
        INNER JOIN trip_participants p ON p.tripId = t.id
        WHERE p.userId = :userId AND t.isArchived = 1
        ORDER BY t.startDate DESC
    """)
    fun observePastTrips(userId: Long): Flow<List<TripEntity>>
}
