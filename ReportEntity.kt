// data/model/ReportEntity.kt
package com.example.tripshare.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reportedUserId: Long,     // the user being reported
    val reporterUserId: Long,     // who filed the report
    val reason: String,           // selected reason
    val description: String?,     // optional text
    val blockUser: Boolean,       // true if block user selected
    val createdAt: Long = System.currentTimeMillis()
)
