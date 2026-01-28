// data/model/ReviewEntity.kt
package com.example.tripshare.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val firebaseId: String? = null,
    // ✅ who the review is ABOUT (profile owner)
    val targetUserId: Long,

    // ✅ who WROTE the review
    val reviewerId: Long,
    val reviewerName: String,
    val reviewerAvatarUrl: String,

    val rating: Int,
    val timeAgo: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class RatingSummary(
    val averageRating: Double,
    val reviewCount: Int
)
