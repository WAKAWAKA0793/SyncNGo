package com.example.tripshare.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tripshare.data.model.ReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(report: ReportEntity): Long

    @Query("SELECT * FROM reports WHERE reporterUserId = :userId ORDER BY createdAt DESC")
    fun reportsFiledBy(userId: Long): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE reportedUserId = :userId ORDER BY createdAt DESC")
    fun reportsAgainst(userId: Long): Flow<List<ReportEntity>>
}
