package com.example.tripshare.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.example.tripshare.data.model.CalendarEventEntity
import com.example.tripshare.data.model.ChatRoom
import com.example.tripshare.data.model.ChecklistCategoryEntity
import com.example.tripshare.data.model.ChecklistItemEntity
import com.example.tripshare.data.model.CommentEntity
import com.example.tripshare.data.model.CostSplitEntity
import com.example.tripshare.data.model.DailyNoteEntity
import com.example.tripshare.data.model.EmergencyContactEntity
import com.example.tripshare.data.model.ExpensePaymentEntity
import com.example.tripshare.data.model.GroupMessageEntity
import com.example.tripshare.data.model.InsurancePolicyEntity
import com.example.tripshare.data.model.ItineraryItemEntity
import com.example.tripshare.data.model.JoinRequestEntity
import com.example.tripshare.data.model.LocalNotificationEntity
import com.example.tripshare.data.model.Message
import com.example.tripshare.data.model.ParticipantInviteEntity
import com.example.tripshare.data.model.PastTripEntity
import com.example.tripshare.data.model.PollEntity
import com.example.tripshare.data.model.PollVoteEntity
import com.example.tripshare.data.model.PostEntity
import com.example.tripshare.data.model.PostLikeEntity
import com.example.tripshare.data.model.ReportEntity
import com.example.tripshare.data.model.ReviewEntity
import com.example.tripshare.data.model.RouteStopEntity
import com.example.tripshare.data.model.SavedChecklistEntity
import com.example.tripshare.data.model.SavedChecklistItemEntity
import com.example.tripshare.data.model.SettlementTransferEntity
import com.example.tripshare.data.model.TripCommentEntity
import com.example.tripshare.data.model.TripDocumentEntity
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.TripMeetingPointEntity
import com.example.tripshare.data.model.TripNoteEntity
import com.example.tripshare.data.model.TripParticipantEntity
import com.example.tripshare.data.model.TripPaymentMethodEntity
import com.example.tripshare.data.model.UserEntity
import com.example.tripshare.data.model.VoteOptionEntity
import com.example.tripshare.data.model.WaitlistEntity

// --- keep entities list and DAOs as you already have them ---
@Database(
    entities = [
        UserEntity::class,
        ReportEntity::class,
        EmergencyContactEntity::class,
        TripEntity::class,
        RouteStopEntity::class,
        TripPaymentMethodEntity::class,
        ParticipantInviteEntity::class,
        TripParticipantEntity::class,
        TripMeetingPointEntity::class,
        PostEntity::class,
        ReviewEntity::class,
        ItineraryItemEntity::class,
        PastTripEntity::class,
        WaitlistEntity::class,
        CalendarEventEntity::class,
        TripDocumentEntity::class,
        DailyNoteEntity::class,
        ExpensePaymentEntity::class,
        CommentEntity::class,
        PollEntity::class,
        VoteOptionEntity::class,
        CostSplitEntity::class,
        ChecklistItemEntity::class,
        ChecklistCategoryEntity::class,
        JoinRequestEntity::class,
        ChatRoom::class,
        Message::class,
        SettlementTransferEntity::class,
        TripNoteEntity::class,
        PostLikeEntity::class,
        SavedChecklistEntity::class,
        SavedChecklistItemEntity::class,
        GroupMessageEntity::class,
        LocalNotificationEntity::class,
        PollVoteEntity::class,
        InsurancePolicyEntity::class,
        TripCommentEntity::class,
    ],
    version = 38,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
    abstract fun postDao(): PostDao
    abstract fun tripHistoryDao(): TripHistoryDao
    abstract fun chatRoomDao(): ChatRoomDao
    abstract fun messageDao(): MessageDao
    abstract fun waitlistDao(): WaitlistDao
    abstract fun expensePaymentDao(): ExpensePaymentDao
    abstract fun reportDao(): ReportDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun commentDao(): CommentDao
    abstract fun voteDao(): VoteDao
    abstract fun costSplitDao(): CostSplitDao
    abstract fun itineraryDao(): ItineraryDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun participantDao(): ParticipantDao
    abstract fun joinRequestDao(): JoinRequestDao
    abstract fun routeDao(): RouteDao
    abstract fun settlementDao(): SettlementDao
    abstract fun tripCalendarDao():TripCalendarDao
    abstract fun reviewDao(): ReviewDao
    abstract fun savedListDao(): SavedListDao
    abstract fun groupChatDao(): GroupChatDao
    abstract fun localNotificationDao(): LocalNotificationDao
    abstract fun insuranceDao(): InsuranceDao
    abstract fun tripCommentDao(): TripCommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tripshare.db"
                )
                    .openHelperFactory(FrameworkSQLiteOpenHelperFactory())
                    .build()

                INSTANCE = db
                db
            }
    }
}

/* -----------------------
   Seeding helper used by onCreate callback
   ----------------------- */

