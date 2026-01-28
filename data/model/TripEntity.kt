package com.example.tripshare.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

// ─── Trip ─────────────────────────────────────────────────────────────
@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val firebaseId: String? = null,
    val organizerId: String = "",
    val category: TripCategory = TripCategory.General,
    val visibility: Visibility = Visibility.Private,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val maxParticipants: Int = 0,
    val costSharing: Boolean = false,
    val paymentDeadline: LocalDate? = null,
    val waitlistEnabled: Boolean = false,
    val budget: Double? = null,
    val budgetDisplay: String? = null,
    val description: String = "",
    val coverImgUrl: String? = null,
    val isArchived: Boolean = false
) {

}


enum class TripCategory(val label: String) {
    General("General"),
    Leisure("Leisure"),
    Adventure("Adventure"),
    Business("Business"),
    Cultural_Heritage("Cultural / Heritage"),
    Eco_Sustainable("Eco / Sustainable"),
    Wellness_Medical("Wellness / Medical"),
    Food("Food"),
    Urban("Urban"),
    Rural("Rural"),
    Mountain("Mountain"),
    Sea_Coastal("Sea / Coastal");

    override fun toString(): String = label
}
enum class Visibility { Public,Private }
enum class CostSharingMethod { EQUAL_SPLIT, CUSTOM_CONTRIBUTIONS, ORGANIZER_PAYS }

// ─── Route Stops ─────────────────────────────────────────────────────
@Entity(
    tableName = "route_stops",
    foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [
        Index("tripId"),
        Index(value = ["tripId", "orderInRoute"])
    ]
)
data class RouteStopEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tripId: Long,
    val type: StopType,
    val label: String,
    val lat: Double? = null,
    val lng: Double? = null,
    val orderInRoute: Int = 0,

    // NEW: per-stop dates
    val startDate: String? = null,   // ISO yyyy-MM-dd
    val endDate: String? = null,     // ISO yyyy-MM-dd
    val nights: Int = 1              // computed/edited per stop
)
enum class StopType { START, END, STOP }

// ─── Payment Methods ─────────────────────────────────────────────────
@Entity(
    tableName = "trip_payment_methods",
    foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("tripId")]
)
data class TripPaymentMethodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tripId: Long,
    val method: PaymentMethod
)
enum class PaymentMethod { EWALLET, CASH, BANK_TRANSFER }

// ─── Invites ─────────────────────────────────────────────────────────
@Entity(
    tableName = "participant_invites",
    foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("tripId")]
)
data class ParticipantInviteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tripId: Long,
    val identifier: String,
    val status: InviteStatus = InviteStatus.INVITED
)
enum class InviteStatus { INVITED, ACCEPTED, DECLINED }


// ─── Polls / Votes ───────────────────────────────────────────────────
@Entity(
    tableName = "polls",
    foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("tripId")]
)
data class PollEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val firebaseId: String? = null,
    val question: String,
    val allowMultiple: Boolean = false
)

@Entity(
    tableName = "vote_options",
    foreignKeys = [ForeignKey(
        entity = PollEntity::class,
        parentColumns = ["id"],
        childColumns = ["pollId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("pollId")]
)
data class VoteOptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pollId: Long,
    val firebaseId: String? = null,
    val option: String,
    val votes: Int = 0
)

@Entity(
    tableName = "poll_votes",
    indices = [
        Index("pollId"),
        Index(value = ["pollId", "userId", "optionId"], unique = true)
    ]
)
data class PollVoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pollId: Long,
    val optionId: Long,
    val userId: Long,
    val createdAt: Long = System.currentTimeMillis()
)

data class PollVoterDetail(
    val optionId: Long,
    val userId: Long,
    val displayName: String,
    val avatarUrl: String? = null
)

// ─── Itinerary ───────────────────────────────────────────────────────
@Entity(
    tableName = "itinerary_items",
    foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [
        Index("tripId"),
        Index(value = ["tripId","day"]),
        Index(value = ["tripId","date"])
    ]
)
data class ItineraryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val firebaseId: String? = null,
    val day: Int,
    val title: String,
    val date: String,
    val time: String,
    val location: String?,
    val notes: String?,
    val category: String? = null,
    val attachment: String? = null,
    val assignedTo: String? = null,
    val endDate: String? = null,
    val endTime: String? = null,
    val lat: Double?,     // nullable if no map location
    val lng: Double?      // nullable if no map location
)


// ─── Calendar Events ─────────────────────────────────────────────────
@Entity(
    tableName = "calendar_events",
    foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [
        Index("tripId"),
        Index(value = ["tripId","date"])
    ]
)
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tripId: Long,
    val title: String,
    val date: String,
    val time: String?,
    val location: String?,
    val category: String
)

// ─── Documents ───────────────────────────────────────────────────────
@Entity(
    tableName = "trip_documents",
    foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("tripId")]
)
data class TripDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tripId: Long,
    val fileName: String,
    val fileSize: String,
    val fileUrl: String
)

// ─── Daily Notes ─────────────────────────────────────────────────────
@Entity(
    tableName = "daily_notes",
    foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index("tripId")]
)
data class DailyNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tripId: Long,
    val note: String,
    val reminderTime: String? = null
)

// ─── Participants ────────────────────────────────────────────────────
@Entity(
    tableName = "trip_participants",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("tripId"),
        Index("userId"),
        Index(value = ["tripId","userId"], unique = true)
    ]
)
data class TripParticipantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tripId: Long,
    val userId: Long,
    val email: String,
    val displayName: String,
    val status: ParticipationStatus = ParticipationStatus.JOINED,
    val role: ParticipantRole = ParticipantRole.MEMBER,
    val joinedAt: Long = System.currentTimeMillis()
)


@Entity( tableName = "trip_meeting_points", foreignKeys = [ForeignKey( entity = TripEntity::class, parentColumns = ["id"], childColumns = ["tripId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE )], indices = [Index("tripId")] )
data class TripMeetingPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tripId: Long,
    val label: String,
    val lat: Double? = null,
    val lng: Double? = null
)
enum class ParticipantRole { OWNER, MEMBER }
enum class ParticipationStatus { JOINED, INVITED, PENDING, LEFT }

@Entity(
    tableName = "checklist_categories",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class ChecklistCategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId: Long = 0,
    val tripId: Long,
    val firebaseId: String? = null,
    val categoryName: String,
    val sort: Int = 0
)
@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = ChecklistCategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)

data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val itemId: Long = 0,
    val categoryId: Long,
    val firebaseId: String? = null,
    val title: String,
    val completed: Boolean = false,
    val dueDate: LocalDate? = null,
    val note: String? = null,
    val sort: Int = 0,
    val quantity: Int = 1                // NEW
)

@Entity(
    tableName = "trip_notes",
    indices = [
        Index("tripId"),
        Index("date")
    ]
)
data class TripNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val noteId: Long = 0L,
    val firebaseId: String? = null,
    val tripId: Long? = null,
    val date: String,   // yyyy-MM-dd (LocalDate.toString())
    val note: String
)

@Entity(tableName = "saved_checklists")
data class SavedChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String
)

@Entity(
    tableName = "saved_checklist_items",
    foreignKeys = [ForeignKey(entity = SavedChecklistEntity::class, parentColumns = ["id"], childColumns = ["checklistId"], onDelete = CASCADE)],
    indices = [Index("checklistId")]
)
data class SavedChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val checklistId: Long,
    val title: String,
    val quantity: Int = 1
)

// ─── Comments on Itinerary Plans ─────────────────────────────────────
@Entity(
    tableName = "trip_comments",
    foreignKeys = [
        // 1. Link to the specific Itinerary Item (Plan)
        ForeignKey(
            entity = ItineraryItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE, // Delete comment if Plan is deleted
            onUpdate = ForeignKey.CASCADE
        ),
        // 2. Link to the User who wrote it
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Delete comment if User is deleted
        )
    ],
    indices = [
        Index("planId"), // Optimize loading comments for a plan
        Index("userId")
    ]
)
data class TripCommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val planId: Long,
    val userId: Long,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

// ─── Helper for joining Comment + User Data ──────────────────────────
// Use this class in your DAO to fetch the comment along with the author's name/photo
data class TripCommentWithAuthor(
    @androidx.room.Embedded val comment: TripCommentEntity,
    @androidx.room.Relation(
        parentColumn = "userId",
        entityColumn = "id" // Assuming UserEntity's primary key is 'id'
    )
    val author: UserEntity?
)