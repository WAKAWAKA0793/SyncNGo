package com.example.tripshare.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "past_trips"
)
data class PastTripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,        // "Ulu Ai"
    val location: String,    // "Sarawak, Malaysia"
    val date: String         // "April 2024"
)

