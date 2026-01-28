package com.example.tripshare.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index("email", unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val firebaseId: String? = null,
    // required at registration
    val name: String = "",
    val email: String = "",
    val passwordHash: String = "",   // ⚠️ in production, store a hash not plain text
    val icNumber: String? = null,
    // verification
    val verificationMethod: VerificationMethod = VerificationMethod.EMAIL,
    val phoneNumber: String? = null,
    val verifiedEmail: String? = null,
    // profile fields (editable later in profile screen)
    val location: String = "",
    val bio: String = "",
    val profilePhoto: String? = null,
    // reputation & community
    val verified: Boolean = false,
    val tripsCompleted: Int = 0,
    // metadata
    val createdAt: Long = System.currentTimeMillis()
)


enum class VerificationMethod { PHONE, EMAIL }

@Entity(
    tableName = "emergency_contacts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class EmergencyContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val name: String,
    val relationship: String,
    val phone: String,
    val firebaseId: String? = null
)

private fun userInitials(name: String): String {
    val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    if (parts.isEmpty()) return "U"
    val first = parts.getOrNull(0)?.firstOrNull()?.uppercaseChar() ?: 'U'
    val second = parts.getOrNull(1)?.firstOrNull()?.uppercaseChar()
    return if (second != null) "$first$second" else "$first"
}

@Entity(tableName = "local_notifications")
data class LocalNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipientId: Long,
    val type: String,            // "JOIN_REQUEST", "PAYMENT", "TRIP_REMINDER"
    val title: String,
    val body: String,
    val relatedId: Long?,        // tripId, requesterId etc.
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(
    tableName = "insurance_policies",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class InsurancePolicyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val providerName: String,
    val policyNumber: String,
    val emergencyPhone: String, // 24/7 Assistance number
    val claimUrl: String        // Web link to file a claim
)