package com.example.tripshare.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waitlist")
data class WaitlistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tripId: Long,
    val firebaseId: String? = null,
    val tripName: String,
    val location: String,
    val date: String,
    val position: Int = 0,
    val alertsEnabled: Boolean = true,
    val tripImageUrl: String? = null,
    val userId: Long
)

@Entity(tableName = "join_requests")
data class JoinRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tripId: Long,
    val userId: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING" // PENDING | ACCEPTED | REJECTED
)