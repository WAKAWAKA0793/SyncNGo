package com.example.tripshare.`data`.db

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.room.RoomOpenHelper
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import java.lang.Class
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import javax.`annotation`.processing.Generated
import kotlin.Any
import kotlin.Boolean
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.Set

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class AppDatabase_Impl : AppDatabase() {
  private val _userDao: Lazy<UserDao> = lazy {
    UserDao_Impl(this)
  }


  private val _tripDao: Lazy<TripDao> = lazy {
    TripDao_Impl(this)
  }


  private val _postDao: Lazy<PostDao> = lazy {
    PostDao_Impl(this)
  }


  private val _tripHistoryDao: Lazy<TripHistoryDao> = lazy {
    TripHistoryDao_Impl(this)
  }


  private val _chatRoomDao: Lazy<ChatRoomDao> = lazy {
    ChatRoomDao_Impl(this)
  }


  private val _messageDao: Lazy<MessageDao> = lazy {
    MessageDao_Impl(this)
  }


  private val _waitlistDao: Lazy<WaitlistDao> = lazy {
    WaitlistDao_Impl(this)
  }


  private val _expensePaymentDao: Lazy<ExpensePaymentDao> = lazy {
    ExpensePaymentDao_Impl(this)
  }


  private val _reportDao: Lazy<ReportDao> = lazy {
    ReportDao_Impl(this)
  }


  private val _emergencyContactDao: Lazy<EmergencyContactDao> = lazy {
    EmergencyContactDao_Impl(this)
  }


  private val _commentDao: Lazy<CommentDao> = lazy {
    CommentDao_Impl(this)
  }


  private val _voteDao: Lazy<VoteDao> = lazy {
    VoteDao_Impl(this)
  }


  private val _costSplitDao: Lazy<CostSplitDao> = lazy {
    CostSplitDao_Impl(this)
  }


  private val _itineraryDao: Lazy<ItineraryDao> = lazy {
    ItineraryDao_Impl(this)
  }


  private val _checklistDao: Lazy<ChecklistDao> = lazy {
    ChecklistDao_Impl(this)
  }


  private val _participantDao: Lazy<ParticipantDao> = lazy {
    ParticipantDao_Impl(this)
  }


  private val _joinRequestDao: Lazy<JoinRequestDao> = lazy {
    JoinRequestDao_Impl(this)
  }


  private val _routeDao: Lazy<RouteDao> = lazy {
    RouteDao_Impl(this)
  }


  private val _settlementDao: Lazy<SettlementDao> = lazy {
    SettlementDao_Impl(this)
  }


  private val _tripCalendarDao: Lazy<TripCalendarDao> = lazy {
    TripCalendarDao_Impl(this)
  }


  private val _reviewDao: Lazy<ReviewDao> = lazy {
    ReviewDao_Impl(this)
  }


  private val _savedListDao: Lazy<SavedListDao> = lazy {
    SavedListDao_Impl(this)
  }


  private val _groupChatDao: Lazy<GroupChatDao> = lazy {
    GroupChatDao_Impl(this)
  }


  private val _localNotificationDao: Lazy<LocalNotificationDao> = lazy {
    LocalNotificationDao_Impl(this)
  }


  private val _insuranceDao: Lazy<InsuranceDao> = lazy {
    InsuranceDao_Impl(this)
  }


  private val _tripCommentDao: Lazy<TripCommentDao> = lazy {
    TripCommentDao_Impl(this)
  }


  protected override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
    val _openCallback: SupportSQLiteOpenHelper.Callback = RoomOpenHelper(config, object :
        RoomOpenHelper.Delegate(38) {
      public override fun createAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `firebaseId` TEXT, `name` TEXT NOT NULL, `email` TEXT NOT NULL, `passwordHash` TEXT NOT NULL, `icNumber` TEXT, `verificationMethod` TEXT NOT NULL, `phoneNumber` TEXT, `verifiedEmail` TEXT, `location` TEXT NOT NULL, `bio` TEXT NOT NULL, `profilePhoto` TEXT, `verified` INTEGER NOT NULL, `tripsCompleted` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_users_email` ON `users` (`email`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `reports` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `reportedUserId` INTEGER NOT NULL, `reporterUserId` INTEGER NOT NULL, `reason` TEXT NOT NULL, `description` TEXT, `blockUser` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `emergency_contacts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `name` TEXT NOT NULL, `relationship` TEXT NOT NULL, `phone` TEXT NOT NULL, `firebaseId` TEXT, FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_emergency_contacts_userId` ON `emergency_contacts` (`userId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `trips` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `firebaseId` TEXT, `organizerId` TEXT NOT NULL, `category` TEXT NOT NULL, `visibility` TEXT NOT NULL, `startDate` INTEGER, `endDate` INTEGER, `maxParticipants` INTEGER NOT NULL, `costSharing` INTEGER NOT NULL, `paymentDeadline` INTEGER, `waitlistEnabled` INTEGER NOT NULL, `budget` REAL, `budgetDisplay` TEXT, `description` TEXT NOT NULL, `coverImgUrl` TEXT, `isArchived` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `route_stops` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `type` TEXT NOT NULL, `label` TEXT NOT NULL, `lat` REAL, `lng` REAL, `orderInRoute` INTEGER NOT NULL, `startDate` TEXT, `endDate` TEXT, `nights` INTEGER NOT NULL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_route_stops_tripId` ON `route_stops` (`tripId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_route_stops_tripId_orderInRoute` ON `route_stops` (`tripId`, `orderInRoute`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `trip_payment_methods` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `method` TEXT NOT NULL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_payment_methods_tripId` ON `trip_payment_methods` (`tripId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `participant_invites` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `identifier` TEXT NOT NULL, `status` TEXT NOT NULL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_participant_invites_tripId` ON `participant_invites` (`tripId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `trip_participants` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, `email` TEXT NOT NULL, `displayName` TEXT NOT NULL, `status` TEXT NOT NULL, `role` TEXT NOT NULL, `joinedAt` INTEGER NOT NULL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_participants_tripId` ON `trip_participants` (`tripId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_participants_userId` ON `trip_participants` (`userId`)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_trip_participants_tripId_userId` ON `trip_participants` (`tripId`, `userId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `trip_meeting_points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `label` TEXT NOT NULL, `lat` REAL, `lng` REAL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_meeting_points_tripId` ON `trip_meeting_points` (`tripId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `posts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `firebaseId` TEXT, `userId` INTEGER NOT NULL, `userName` TEXT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `text` TEXT, `userAvatar` TEXT, `location` TEXT, `timeAgo` TEXT NOT NULL, `imageUrl` TEXT, `likes` INTEGER NOT NULL, `comments` INTEGER NOT NULL, `shares` INTEGER NOT NULL, FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_userId` ON `posts` (`userId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `reviews` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `firebaseId` TEXT, `targetUserId` INTEGER NOT NULL, `reviewerId` INTEGER NOT NULL, `reviewerName` TEXT NOT NULL, `reviewerAvatarUrl` TEXT NOT NULL, `rating` INTEGER NOT NULL, `timeAgo` TEXT NOT NULL, `text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `itinerary_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `firebaseId` TEXT, `day` INTEGER NOT NULL, `title` TEXT NOT NULL, `date` TEXT NOT NULL, `time` TEXT NOT NULL, `location` TEXT, `notes` TEXT, `category` TEXT, `attachment` TEXT, `assignedTo` TEXT, `endDate` TEXT, `endTime` TEXT, `lat` REAL, `lng` REAL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_itinerary_items_tripId` ON `itinerary_items` (`tripId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_itinerary_items_tripId_day` ON `itinerary_items` (`tripId`, `day`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_itinerary_items_tripId_date` ON `itinerary_items` (`tripId`, `date`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `past_trips` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `location` TEXT NOT NULL, `date` TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `waitlist` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `firebaseId` TEXT, `tripName` TEXT NOT NULL, `location` TEXT NOT NULL, `date` TEXT NOT NULL, `position` INTEGER NOT NULL, `alertsEnabled` INTEGER NOT NULL, `tripImageUrl` TEXT, `userId` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `calendar_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `title` TEXT NOT NULL, `date` TEXT NOT NULL, `time` TEXT, `location` TEXT, `category` TEXT NOT NULL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_calendar_events_tripId` ON `calendar_events` (`tripId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_calendar_events_tripId_date` ON `calendar_events` (`tripId`, `date`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `trip_documents` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `fileName` TEXT NOT NULL, `fileSize` TEXT NOT NULL, `fileUrl` TEXT NOT NULL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_documents_tripId` ON `trip_documents` (`tripId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_notes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `note` TEXT NOT NULL, `reminderTime` TEXT, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_notes_tripId` ON `daily_notes` (`tripId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `expense_payments` (`expensePaymentId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `firebaseId` TEXT, `payerUserId` INTEGER NOT NULL, `payeeUserId` INTEGER NOT NULL, `title` TEXT NOT NULL, `amount` REAL NOT NULL, `category` TEXT NOT NULL, `receiptImage` TEXT, `paymentStatus` TEXT NOT NULL, `paidAtMillis` INTEGER, `dueAtMillis` INTEGER, `itineraryPlanId` INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `comments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `postId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, `text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`postId`) REFERENCES `posts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_postId` ON `comments` (`postId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `polls` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `firebaseId` TEXT, `question` TEXT NOT NULL, `allowMultiple` INTEGER NOT NULL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_polls_tripId` ON `polls` (`tripId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `vote_options` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `pollId` INTEGER NOT NULL, `firebaseId` TEXT, `option` TEXT NOT NULL, `votes` INTEGER NOT NULL, FOREIGN KEY(`pollId`) REFERENCES `polls`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vote_options_pollId` ON `vote_options` (`pollId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `cost_split` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `firebaseId` TEXT, `expensePaymentId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, `amountOwed` REAL NOT NULL, `status` TEXT NOT NULL, `splitMode` INTEGER NOT NULL, `shareCount` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `checklist_items` (`itemId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `categoryId` INTEGER NOT NULL, `firebaseId` TEXT, `title` TEXT NOT NULL, `completed` INTEGER NOT NULL, `dueDate` INTEGER, `note` TEXT, `sort` INTEGER NOT NULL, `quantity` INTEGER NOT NULL, FOREIGN KEY(`categoryId`) REFERENCES `checklist_categories`(`categoryId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_checklist_items_categoryId` ON `checklist_items` (`categoryId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `checklist_categories` (`categoryId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `firebaseId` TEXT, `categoryName` TEXT NOT NULL, `sort` INTEGER NOT NULL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_checklist_categories_tripId` ON `checklist_categories` (`tripId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `join_requests` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `status` TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `chat_rooms` (`id` TEXT NOT NULL, `title` TEXT, `userAId` TEXT NOT NULL, `userBId` TEXT NOT NULL, `lastMessage` TEXT, `updatedAt` INTEGER NOT NULL, `isPrivate` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `messages` (`id` TEXT NOT NULL, `chatId` TEXT NOT NULL, `senderId` TEXT NOT NULL, `content` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `status` TEXT NOT NULL, `edited` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`chatId`) REFERENCES `chat_rooms`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_messages_chatId` ON `messages` (`chatId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_messages_senderId` ON `messages` (`senderId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `settlement_transfers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `firebaseId` TEXT, `payerUserId` INTEGER NOT NULL, `receiverUserId` INTEGER NOT NULL, `amount` REAL NOT NULL, `currency` TEXT NOT NULL, `createdAtMillis` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `trip_notes` (`noteId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `firebaseId` TEXT, `tripId` INTEGER, `date` TEXT NOT NULL, `note` TEXT NOT NULL)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_notes_tripId` ON `trip_notes` (`tripId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_notes_date` ON `trip_notes` (`date`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `post_likes` (`postId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, PRIMARY KEY(`postId`, `userId`), FOREIGN KEY(`postId`) REFERENCES `posts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_post_likes_postId` ON `post_likes` (`postId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_post_likes_userId` ON `post_likes` (`userId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `saved_checklists` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `name` TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `saved_checklist_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `checklistId` INTEGER NOT NULL, `title` TEXT NOT NULL, `quantity` INTEGER NOT NULL, FOREIGN KEY(`checklistId`) REFERENCES `saved_checklists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_saved_checklist_items_checklistId` ON `saved_checklist_items` (`checklistId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `group_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `firebaseId` TEXT, `senderId` INTEGER NOT NULL, `senderName` TEXT NOT NULL, `senderAvatar` TEXT, `content` TEXT NOT NULL, `type` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `local_notifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recipientId` INTEGER NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, `body` TEXT NOT NULL, `relatedId` INTEGER, `timestamp` INTEGER NOT NULL, `isRead` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `poll_votes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `pollId` INTEGER NOT NULL, `optionId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_poll_votes_pollId` ON `poll_votes` (`pollId`)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_poll_votes_pollId_userId_optionId` ON `poll_votes` (`pollId`, `userId`, `optionId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `insurance_policies` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `providerName` TEXT NOT NULL, `policyNumber` TEXT NOT NULL, `emergencyPhone` TEXT NOT NULL, `claimUrl` TEXT NOT NULL, FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_insurance_policies_userId` ON `insurance_policies` (`userId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `trip_comments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `planId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, `text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`planId`) REFERENCES `itinerary_items`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_comments_planId` ON `trip_comments` (`planId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_comments_userId` ON `trip_comments` (`userId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'ff5ed4da016f106bdfff713796e1eb78')")
      }

      public override fun dropAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `users`")
        db.execSQL("DROP TABLE IF EXISTS `reports`")
        db.execSQL("DROP TABLE IF EXISTS `emergency_contacts`")
        db.execSQL("DROP TABLE IF EXISTS `trips`")
        db.execSQL("DROP TABLE IF EXISTS `route_stops`")
        db.execSQL("DROP TABLE IF EXISTS `trip_payment_methods`")
        db.execSQL("DROP TABLE IF EXISTS `participant_invites`")
        db.execSQL("DROP TABLE IF EXISTS `trip_participants`")
        db.execSQL("DROP TABLE IF EXISTS `trip_meeting_points`")
        db.execSQL("DROP TABLE IF EXISTS `posts`")
        db.execSQL("DROP TABLE IF EXISTS `reviews`")
        db.execSQL("DROP TABLE IF EXISTS `itinerary_items`")
        db.execSQL("DROP TABLE IF EXISTS `past_trips`")
        db.execSQL("DROP TABLE IF EXISTS `waitlist`")
        db.execSQL("DROP TABLE IF EXISTS `calendar_events`")
        db.execSQL("DROP TABLE IF EXISTS `trip_documents`")
        db.execSQL("DROP TABLE IF EXISTS `daily_notes`")
        db.execSQL("DROP TABLE IF EXISTS `expense_payments`")
        db.execSQL("DROP TABLE IF EXISTS `comments`")
        db.execSQL("DROP TABLE IF EXISTS `polls`")
        db.execSQL("DROP TABLE IF EXISTS `vote_options`")
        db.execSQL("DROP TABLE IF EXISTS `cost_split`")
        db.execSQL("DROP TABLE IF EXISTS `checklist_items`")
        db.execSQL("DROP TABLE IF EXISTS `checklist_categories`")
        db.execSQL("DROP TABLE IF EXISTS `join_requests`")
        db.execSQL("DROP TABLE IF EXISTS `chat_rooms`")
        db.execSQL("DROP TABLE IF EXISTS `messages`")
        db.execSQL("DROP TABLE IF EXISTS `settlement_transfers`")
        db.execSQL("DROP TABLE IF EXISTS `trip_notes`")
        db.execSQL("DROP TABLE IF EXISTS `post_likes`")
        db.execSQL("DROP TABLE IF EXISTS `saved_checklists`")
        db.execSQL("DROP TABLE IF EXISTS `saved_checklist_items`")
        db.execSQL("DROP TABLE IF EXISTS `group_messages`")
        db.execSQL("DROP TABLE IF EXISTS `local_notifications`")
        db.execSQL("DROP TABLE IF EXISTS `poll_votes`")
        db.execSQL("DROP TABLE IF EXISTS `insurance_policies`")
        db.execSQL("DROP TABLE IF EXISTS `trip_comments`")
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onDestructiveMigration(db)
          }
        }
      }

      public override fun onCreate(db: SupportSQLiteDatabase) {
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onCreate(db)
          }
        }
      }

      public override fun onOpen(db: SupportSQLiteDatabase) {
        mDatabase = db
        db.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(db)
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onOpen(db)
          }
        }
      }

      public override fun onPreMigrate(db: SupportSQLiteDatabase) {
        dropFtsSyncTriggers(db)
      }

      public override fun onPostMigrate(db: SupportSQLiteDatabase) {
      }

      public override fun onValidateSchema(db: SupportSQLiteDatabase):
          RoomOpenHelper.ValidationResult {
        val _columnsUsers: HashMap<String, TableInfo.Column> = HashMap<String, TableInfo.Column>(15)
        _columnsUsers.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("email", TableInfo.Column("email", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("passwordHash", TableInfo.Column("passwordHash", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("icNumber", TableInfo.Column("icNumber", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("verificationMethod", TableInfo.Column("verificationMethod", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("phoneNumber", TableInfo.Column("phoneNumber", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("verifiedEmail", TableInfo.Column("verifiedEmail", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("location", TableInfo.Column("location", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("bio", TableInfo.Column("bio", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("profilePhoto", TableInfo.Column("profilePhoto", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("verified", TableInfo.Column("verified", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("tripsCompleted", TableInfo.Column("tripsCompleted", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsers: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesUsers: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesUsers.add(TableInfo.Index("index_users_email", true, listOf("email"),
            listOf("ASC")))
        val _infoUsers: TableInfo = TableInfo("users", _columnsUsers, _foreignKeysUsers,
            _indicesUsers)
        val _existingUsers: TableInfo = read(db, "users")
        if (!_infoUsers.equals(_existingUsers)) {
          return RoomOpenHelper.ValidationResult(false, """
              |users(com.example.tripshare.data.model.UserEntity).
              | Expected:
              |""".trimMargin() + _infoUsers + """
              |
              | Found:
              |""".trimMargin() + _existingUsers)
        }
        val _columnsReports: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(7)
        _columnsReports.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("reportedUserId", TableInfo.Column("reportedUserId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("reporterUserId", TableInfo.Column("reporterUserId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("reason", TableInfo.Column("reason", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("blockUser", TableInfo.Column("blockUser", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysReports: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesReports: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoReports: TableInfo = TableInfo("reports", _columnsReports, _foreignKeysReports,
            _indicesReports)
        val _existingReports: TableInfo = read(db, "reports")
        if (!_infoReports.equals(_existingReports)) {
          return RoomOpenHelper.ValidationResult(false, """
              |reports(com.example.tripshare.data.model.ReportEntity).
              | Expected:
              |""".trimMargin() + _infoReports + """
              |
              | Found:
              |""".trimMargin() + _existingReports)
        }
        val _columnsEmergencyContacts: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(6)
        _columnsEmergencyContacts.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEmergencyContacts.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEmergencyContacts.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEmergencyContacts.put("relationship", TableInfo.Column("relationship", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEmergencyContacts.put("phone", TableInfo.Column("phone", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEmergencyContacts.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEmergencyContacts: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysEmergencyContacts.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("id")))
        val _indicesEmergencyContacts: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesEmergencyContacts.add(TableInfo.Index("index_emergency_contacts_userId", false,
            listOf("userId"), listOf("ASC")))
        val _infoEmergencyContacts: TableInfo = TableInfo("emergency_contacts",
            _columnsEmergencyContacts, _foreignKeysEmergencyContacts, _indicesEmergencyContacts)
        val _existingEmergencyContacts: TableInfo = read(db, "emergency_contacts")
        if (!_infoEmergencyContacts.equals(_existingEmergencyContacts)) {
          return RoomOpenHelper.ValidationResult(false, """
              |emergency_contacts(com.example.tripshare.data.model.EmergencyContactEntity).
              | Expected:
              |""".trimMargin() + _infoEmergencyContacts + """
              |
              | Found:
              |""".trimMargin() + _existingEmergencyContacts)
        }
        val _columnsTrips: HashMap<String, TableInfo.Column> = HashMap<String, TableInfo.Column>(17)
        _columnsTrips.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("organizerId", TableInfo.Column("organizerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("visibility", TableInfo.Column("visibility", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("startDate", TableInfo.Column("startDate", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("endDate", TableInfo.Column("endDate", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("maxParticipants", TableInfo.Column("maxParticipants", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("costSharing", TableInfo.Column("costSharing", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("paymentDeadline", TableInfo.Column("paymentDeadline", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("waitlistEnabled", TableInfo.Column("waitlistEnabled", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("budget", TableInfo.Column("budget", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("budgetDisplay", TableInfo.Column("budgetDisplay", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("coverImgUrl", TableInfo.Column("coverImgUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTrips.put("isArchived", TableInfo.Column("isArchived", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTrips: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesTrips: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoTrips: TableInfo = TableInfo("trips", _columnsTrips, _foreignKeysTrips,
            _indicesTrips)
        val _existingTrips: TableInfo = read(db, "trips")
        if (!_infoTrips.equals(_existingTrips)) {
          return RoomOpenHelper.ValidationResult(false, """
              |trips(com.example.tripshare.data.model.TripEntity).
              | Expected:
              |""".trimMargin() + _infoTrips + """
              |
              | Found:
              |""".trimMargin() + _existingTrips)
        }
        val _columnsRouteStops: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(10)
        _columnsRouteStops.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRouteStops.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRouteStops.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRouteStops.put("label", TableInfo.Column("label", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRouteStops.put("lat", TableInfo.Column("lat", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRouteStops.put("lng", TableInfo.Column("lng", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRouteStops.put("orderInRoute", TableInfo.Column("orderInRoute", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRouteStops.put("startDate", TableInfo.Column("startDate", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRouteStops.put("endDate", TableInfo.Column("endDate", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRouteStops.put("nights", TableInfo.Column("nights", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRouteStops: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysRouteStops.add(TableInfo.ForeignKey("trips", "CASCADE", "CASCADE",
            listOf("tripId"), listOf("id")))
        val _indicesRouteStops: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(2)
        _indicesRouteStops.add(TableInfo.Index("index_route_stops_tripId", false, listOf("tripId"),
            listOf("ASC")))
        _indicesRouteStops.add(TableInfo.Index("index_route_stops_tripId_orderInRoute", false,
            listOf("tripId", "orderInRoute"), listOf("ASC", "ASC")))
        val _infoRouteStops: TableInfo = TableInfo("route_stops", _columnsRouteStops,
            _foreignKeysRouteStops, _indicesRouteStops)
        val _existingRouteStops: TableInfo = read(db, "route_stops")
        if (!_infoRouteStops.equals(_existingRouteStops)) {
          return RoomOpenHelper.ValidationResult(false, """
              |route_stops(com.example.tripshare.data.model.RouteStopEntity).
              | Expected:
              |""".trimMargin() + _infoRouteStops + """
              |
              | Found:
              |""".trimMargin() + _existingRouteStops)
        }
        val _columnsTripPaymentMethods: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(3)
        _columnsTripPaymentMethods.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripPaymentMethods.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTripPaymentMethods.put("method", TableInfo.Column("method", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTripPaymentMethods: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysTripPaymentMethods.add(TableInfo.ForeignKey("trips", "CASCADE", "CASCADE",
            listOf("tripId"), listOf("id")))
        val _indicesTripPaymentMethods: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesTripPaymentMethods.add(TableInfo.Index("index_trip_payment_methods_tripId", false,
            listOf("tripId"), listOf("ASC")))
        val _infoTripPaymentMethods: TableInfo = TableInfo("trip_payment_methods",
            _columnsTripPaymentMethods, _foreignKeysTripPaymentMethods, _indicesTripPaymentMethods)
        val _existingTripPaymentMethods: TableInfo = read(db, "trip_payment_methods")
        if (!_infoTripPaymentMethods.equals(_existingTripPaymentMethods)) {
          return RoomOpenHelper.ValidationResult(false, """
              |trip_payment_methods(com.example.tripshare.data.model.TripPaymentMethodEntity).
              | Expected:
              |""".trimMargin() + _infoTripPaymentMethods + """
              |
              | Found:
              |""".trimMargin() + _existingTripPaymentMethods)
        }
        val _columnsParticipantInvites: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(4)
        _columnsParticipantInvites.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsParticipantInvites.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsParticipantInvites.put("identifier", TableInfo.Column("identifier", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsParticipantInvites.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysParticipantInvites: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysParticipantInvites.add(TableInfo.ForeignKey("trips", "CASCADE", "CASCADE",
            listOf("tripId"), listOf("id")))
        val _indicesParticipantInvites: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesParticipantInvites.add(TableInfo.Index("index_participant_invites_tripId", false,
            listOf("tripId"), listOf("ASC")))
        val _infoParticipantInvites: TableInfo = TableInfo("participant_invites",
            _columnsParticipantInvites, _foreignKeysParticipantInvites, _indicesParticipantInvites)
        val _existingParticipantInvites: TableInfo = read(db, "participant_invites")
        if (!_infoParticipantInvites.equals(_existingParticipantInvites)) {
          return RoomOpenHelper.ValidationResult(false, """
              |participant_invites(com.example.tripshare.data.model.ParticipantInviteEntity).
              | Expected:
              |""".trimMargin() + _infoParticipantInvites + """
              |
              | Found:
              |""".trimMargin() + _existingParticipantInvites)
        }
        val _columnsTripParticipants: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(8)
        _columnsTripParticipants.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripParticipants.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripParticipants.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripParticipants.put("email", TableInfo.Column("email", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripParticipants.put("displayName", TableInfo.Column("displayName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTripParticipants.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripParticipants.put("role", TableInfo.Column("role", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripParticipants.put("joinedAt", TableInfo.Column("joinedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTripParticipants: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(2)
        _foreignKeysTripParticipants.add(TableInfo.ForeignKey("trips", "CASCADE", "CASCADE",
            listOf("tripId"), listOf("id")))
        _foreignKeysTripParticipants.add(TableInfo.ForeignKey("users", "CASCADE", "CASCADE",
            listOf("userId"), listOf("id")))
        val _indicesTripParticipants: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(3)
        _indicesTripParticipants.add(TableInfo.Index("index_trip_participants_tripId", false,
            listOf("tripId"), listOf("ASC")))
        _indicesTripParticipants.add(TableInfo.Index("index_trip_participants_userId", false,
            listOf("userId"), listOf("ASC")))
        _indicesTripParticipants.add(TableInfo.Index("index_trip_participants_tripId_userId", true,
            listOf("tripId", "userId"), listOf("ASC", "ASC")))
        val _infoTripParticipants: TableInfo = TableInfo("trip_participants",
            _columnsTripParticipants, _foreignKeysTripParticipants, _indicesTripParticipants)
        val _existingTripParticipants: TableInfo = read(db, "trip_participants")
        if (!_infoTripParticipants.equals(_existingTripParticipants)) {
          return RoomOpenHelper.ValidationResult(false, """
              |trip_participants(com.example.tripshare.data.model.TripParticipantEntity).
              | Expected:
              |""".trimMargin() + _infoTripParticipants + """
              |
              | Found:
              |""".trimMargin() + _existingTripParticipants)
        }
        val _columnsTripMeetingPoints: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(5)
        _columnsTripMeetingPoints.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripMeetingPoints.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripMeetingPoints.put("label", TableInfo.Column("label", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripMeetingPoints.put("lat", TableInfo.Column("lat", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripMeetingPoints.put("lng", TableInfo.Column("lng", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTripMeetingPoints: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysTripMeetingPoints.add(TableInfo.ForeignKey("trips", "CASCADE", "CASCADE",
            listOf("tripId"), listOf("id")))
        val _indicesTripMeetingPoints: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesTripMeetingPoints.add(TableInfo.Index("index_trip_meeting_points_tripId", false,
            listOf("tripId"), listOf("ASC")))
        val _infoTripMeetingPoints: TableInfo = TableInfo("trip_meeting_points",
            _columnsTripMeetingPoints, _foreignKeysTripMeetingPoints, _indicesTripMeetingPoints)
        val _existingTripMeetingPoints: TableInfo = read(db, "trip_meeting_points")
        if (!_infoTripMeetingPoints.equals(_existingTripMeetingPoints)) {
          return RoomOpenHelper.ValidationResult(false, """
              |trip_meeting_points(com.example.tripshare.data.model.TripMeetingPointEntity).
              | Expected:
              |""".trimMargin() + _infoTripMeetingPoints + """
              |
              | Found:
              |""".trimMargin() + _existingTripMeetingPoints)
        }
        val _columnsPosts: HashMap<String, TableInfo.Column> = HashMap<String, TableInfo.Column>(14)
        _columnsPosts.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("userName", TableInfo.Column("userName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("content", TableInfo.Column("content", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("text", TableInfo.Column("text", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("userAvatar", TableInfo.Column("userAvatar", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("location", TableInfo.Column("location", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("timeAgo", TableInfo.Column("timeAgo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("likes", TableInfo.Column("likes", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("comments", TableInfo.Column("comments", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("shares", TableInfo.Column("shares", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPosts: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysPosts.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("id")))
        val _indicesPosts: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesPosts.add(TableInfo.Index("index_posts_userId", false, listOf("userId"),
            listOf("ASC")))
        val _infoPosts: TableInfo = TableInfo("posts", _columnsPosts, _foreignKeysPosts,
            _indicesPosts)
        val _existingPosts: TableInfo = read(db, "posts")
        if (!_infoPosts.equals(_existingPosts)) {
          return RoomOpenHelper.ValidationResult(false, """
              |posts(com.example.tripshare.data.model.PostEntity).
              | Expected:
              |""".trimMargin() + _infoPosts + """
              |
              | Found:
              |""".trimMargin() + _existingPosts)
        }
        val _columnsReviews: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(10)
        _columnsReviews.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("targetUserId", TableInfo.Column("targetUserId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("reviewerId", TableInfo.Column("reviewerId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("reviewerName", TableInfo.Column("reviewerName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("reviewerAvatarUrl", TableInfo.Column("reviewerAvatarUrl", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("rating", TableInfo.Column("rating", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("timeAgo", TableInfo.Column("timeAgo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("text", TableInfo.Column("text", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysReviews: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesReviews: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoReviews: TableInfo = TableInfo("reviews", _columnsReviews, _foreignKeysReviews,
            _indicesReviews)
        val _existingReviews: TableInfo = read(db, "reviews")
        if (!_infoReviews.equals(_existingReviews)) {
          return RoomOpenHelper.ValidationResult(false, """
              |reviews(com.example.tripshare.data.model.ReviewEntity).
              | Expected:
              |""".trimMargin() + _infoReviews + """
              |
              | Found:
              |""".trimMargin() + _existingReviews)
        }
        val _columnsItineraryItems: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(16)
        _columnsItineraryItems.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("day", TableInfo.Column("day", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("date", TableInfo.Column("date", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("time", TableInfo.Column("time", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("location", TableInfo.Column("location", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("category", TableInfo.Column("category", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("attachment", TableInfo.Column("attachment", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("assignedTo", TableInfo.Column("assignedTo", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("endDate", TableInfo.Column("endDate", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("endTime", TableInfo.Column("endTime", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("lat", TableInfo.Column("lat", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsItineraryItems.put("lng", TableInfo.Column("lng", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysItineraryItems: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysItineraryItems.add(TableInfo.ForeignKey("trips", "CASCADE", "CASCADE",
            listOf("tripId"), listOf("id")))
        val _indicesItineraryItems: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(3)
        _indicesItineraryItems.add(TableInfo.Index("index_itinerary_items_tripId", false,
            listOf("tripId"), listOf("ASC")))
        _indicesItineraryItems.add(TableInfo.Index("index_itinerary_items_tripId_day", false,
            listOf("tripId", "day"), listOf("ASC", "ASC")))
        _indicesItineraryItems.add(TableInfo.Index("index_itinerary_items_tripId_date", false,
            listOf("tripId", "date"), listOf("ASC", "ASC")))
        val _infoItineraryItems: TableInfo = TableInfo("itinerary_items", _columnsItineraryItems,
            _foreignKeysItineraryItems, _indicesItineraryItems)
        val _existingItineraryItems: TableInfo = read(db, "itinerary_items")
        if (!_infoItineraryItems.equals(_existingItineraryItems)) {
          return RoomOpenHelper.ValidationResult(false, """
              |itinerary_items(com.example.tripshare.data.model.ItineraryItemEntity).
              | Expected:
              |""".trimMargin() + _infoItineraryItems + """
              |
              | Found:
              |""".trimMargin() + _existingItineraryItems)
        }
        val _columnsPastTrips: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(4)
        _columnsPastTrips.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPastTrips.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPastTrips.put("location", TableInfo.Column("location", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPastTrips.put("date", TableInfo.Column("date", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPastTrips: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesPastTrips: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoPastTrips: TableInfo = TableInfo("past_trips", _columnsPastTrips,
            _foreignKeysPastTrips, _indicesPastTrips)
        val _existingPastTrips: TableInfo = read(db, "past_trips")
        if (!_infoPastTrips.equals(_existingPastTrips)) {
          return RoomOpenHelper.ValidationResult(false, """
              |past_trips(com.example.tripshare.data.model.PastTripEntity).
              | Expected:
              |""".trimMargin() + _infoPastTrips + """
              |
              | Found:
              |""".trimMargin() + _existingPastTrips)
        }
        val _columnsWaitlist: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(10)
        _columnsWaitlist.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWaitlist.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWaitlist.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWaitlist.put("tripName", TableInfo.Column("tripName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWaitlist.put("location", TableInfo.Column("location", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWaitlist.put("date", TableInfo.Column("date", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWaitlist.put("position", TableInfo.Column("position", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWaitlist.put("alertsEnabled", TableInfo.Column("alertsEnabled", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWaitlist.put("tripImageUrl", TableInfo.Column("tripImageUrl", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWaitlist.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWaitlist: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesWaitlist: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoWaitlist: TableInfo = TableInfo("waitlist", _columnsWaitlist, _foreignKeysWaitlist,
            _indicesWaitlist)
        val _existingWaitlist: TableInfo = read(db, "waitlist")
        if (!_infoWaitlist.equals(_existingWaitlist)) {
          return RoomOpenHelper.ValidationResult(false, """
              |waitlist(com.example.tripshare.data.model.WaitlistEntity).
              | Expected:
              |""".trimMargin() + _infoWaitlist + """
              |
              | Found:
              |""".trimMargin() + _existingWaitlist)
        }
        val _columnsCalendarEvents: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(7)
        _columnsCalendarEvents.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCalendarEvents.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCalendarEvents.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCalendarEvents.put("date", TableInfo.Column("date", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCalendarEvents.put("time", TableInfo.Column("time", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCalendarEvents.put("location", TableInfo.Column("location", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCalendarEvents.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCalendarEvents: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysCalendarEvents.add(TableInfo.ForeignKey("trips", "CASCADE", "CASCADE",
            listOf("tripId"), listOf("id")))
        val _indicesCalendarEvents: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(2)
        _indicesCalendarEvents.add(TableInfo.Index("index_calendar_events_tripId", false,
            listOf("tripId"), listOf("ASC")))
        _indicesCalendarEvents.add(TableInfo.Index("index_calendar_events_tripId_date", false,
            listOf("tripId", "date"), listOf("ASC", "ASC")))
        val _infoCalendarEvents: TableInfo = TableInfo("calendar_events", _columnsCalendarEvents,
            _foreignKeysCalendarEvents, _indicesCalendarEvents)
        val _existingCalendarEvents: TableInfo = read(db, "calendar_events")
        if (!_infoCalendarEvents.equals(_existingCalendarEvents)) {
          return RoomOpenHelper.ValidationResult(false, """
              |calendar_events(com.example.tripshare.data.model.CalendarEventEntity).
              | Expected:
              |""".trimMargin() + _infoCalendarEvents + """
              |
              | Found:
              |""".trimMargin() + _existingCalendarEvents)
        }
        val _columnsTripDocuments: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(5)
        _columnsTripDocuments.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripDocuments.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripDocuments.put("fileName", TableInfo.Column("fileName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripDocuments.put("fileSize", TableInfo.Column("fileSize", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripDocuments.put("fileUrl", TableInfo.Column("fileUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTripDocuments: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysTripDocuments.add(TableInfo.ForeignKey("trips", "CASCADE", "CASCADE",
            listOf("tripId"), listOf("id")))
        val _indicesTripDocuments: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesTripDocuments.add(TableInfo.Index("index_trip_documents_tripId", false,
            listOf("tripId"), listOf("ASC")))
        val _infoTripDocuments: TableInfo = TableInfo("trip_documents", _columnsTripDocuments,
            _foreignKeysTripDocuments, _indicesTripDocuments)
        val _existingTripDocuments: TableInfo = read(db, "trip_documents")
        if (!_infoTripDocuments.equals(_existingTripDocuments)) {
          return RoomOpenHelper.ValidationResult(false, """
              |trip_documents(com.example.tripshare.data.model.TripDocumentEntity).
              | Expected:
              |""".trimMargin() + _infoTripDocuments + """
              |
              | Found:
              |""".trimMargin() + _existingTripDocuments)
        }
        val _columnsDailyNotes: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(4)
        _columnsDailyNotes.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyNotes.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyNotes.put("note", TableInfo.Column("note", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyNotes.put("reminderTime", TableInfo.Column("reminderTime", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDailyNotes: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysDailyNotes.add(TableInfo.ForeignKey("trips", "CASCADE", "CASCADE",
            listOf("tripId"), listOf("id")))
        val _indicesDailyNotes: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesDailyNotes.add(TableInfo.Index("index_daily_notes_tripId", false, listOf("tripId"),
            listOf("ASC")))
        val _infoDailyNotes: TableInfo = TableInfo("daily_notes", _columnsDailyNotes,
            _foreignKeysDailyNotes, _indicesDailyNotes)
        val _existingDailyNotes: TableInfo = read(db, "daily_notes")
        if (!_infoDailyNotes.equals(_existingDailyNotes)) {
          return RoomOpenHelper.ValidationResult(false, """
              |daily_notes(com.example.tripshare.data.model.DailyNoteEntity).
              | Expected:
              |""".trimMargin() + _infoDailyNotes + """
              |
              | Found:
              |""".trimMargin() + _existingDailyNotes)
        }
        val _columnsExpensePayments: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(13)
        _columnsExpensePayments.put("expensePaymentId", TableInfo.Column("expensePaymentId",
            "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("payerUserId", TableInfo.Column("payerUserId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("payeeUserId", TableInfo.Column("payeeUserId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("amount", TableInfo.Column("amount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("receiptImage", TableInfo.Column("receiptImage", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("paymentStatus", TableInfo.Column("paymentStatus", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("paidAtMillis", TableInfo.Column("paidAtMillis", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("dueAtMillis", TableInfo.Column("dueAtMillis", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpensePayments.put("itineraryPlanId", TableInfo.Column("itineraryPlanId",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExpensePayments: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesExpensePayments: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoExpensePayments: TableInfo = TableInfo("expense_payments", _columnsExpensePayments,
            _foreignKeysExpensePayments, _indicesExpensePayments)
        val _existingExpensePayments: TableInfo = read(db, "expense_payments")
        if (!_infoExpensePayments.equals(_existingExpensePayments)) {
          return RoomOpenHelper.ValidationResult(false, """
              |expense_payments(com.example.tripshare.data.model.ExpensePaymentEntity).
              | Expected:
              |""".trimMargin() + _infoExpensePayments + """
              |
              | Found:
              |""".trimMargin() + _existingExpensePayments)
        }
        val _columnsComments: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(5)
        _columnsComments.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComments.put("postId", TableInfo.Column("postId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComments.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComments.put("text", TableInfo.Column("text", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComments.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysComments: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysComments.add(TableInfo.ForeignKey("posts", "CASCADE", "NO ACTION",
            listOf("postId"), listOf("id")))
        val _indicesComments: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesComments.add(TableInfo.Index("index_comments_postId", false, listOf("postId"),
            listOf("ASC")))
        val _infoComments: TableInfo = TableInfo("comments", _columnsComments, _foreignKeysComments,
            _indicesComments)
        val _existingComments: TableInfo = read(db, "comments")
        if (!_infoComments.equals(_existingComments)) {
          return RoomOpenHelper.ValidationResult(false, """
              |comments(com.example.tripshare.data.model.CommentEntity).
              | Expected:
              |""".trimMargin() + _infoComments + """
              |
              | Found:
              |""".trimMargin() + _existingComments)
        }
        val _columnsPolls: HashMap<String, TableInfo.Column> = HashMap<String, TableInfo.Column>(5)
        _columnsPolls.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPolls.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPolls.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPolls.put("question", TableInfo.Column("question", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPolls.put("allowMultiple", TableInfo.Column("allowMultiple", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPolls: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysPolls.add(TableInfo.ForeignKey("trips", "CASCADE", "CASCADE", listOf("tripId"),
            listOf("id")))
        val _indicesPolls: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesPolls.add(TableInfo.Index("index_polls_tripId", false, listOf("tripId"),
            listOf("ASC")))
        val _infoPolls: TableInfo = TableInfo("polls", _columnsPolls, _foreignKeysPolls,
            _indicesPolls)
        val _existingPolls: TableInfo = read(db, "polls")
        if (!_infoPolls.equals(_existingPolls)) {
          return RoomOpenHelper.ValidationResult(false, """
              |polls(com.example.tripshare.data.model.PollEntity).
              | Expected:
              |""".trimMargin() + _infoPolls + """
              |
              | Found:
              |""".trimMargin() + _existingPolls)
        }
        val _columnsVoteOptions: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(5)
        _columnsVoteOptions.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVoteOptions.put("pollId", TableInfo.Column("pollId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVoteOptions.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVoteOptions.put("option", TableInfo.Column("option", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVoteOptions.put("votes", TableInfo.Column("votes", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysVoteOptions: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysVoteOptions.add(TableInfo.ForeignKey("polls", "CASCADE", "CASCADE",
            listOf("pollId"), listOf("id")))
        val _indicesVoteOptions: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesVoteOptions.add(TableInfo.Index("index_vote_options_pollId", false,
            listOf("pollId"), listOf("ASC")))
        val _infoVoteOptions: TableInfo = TableInfo("vote_options", _columnsVoteOptions,
            _foreignKeysVoteOptions, _indicesVoteOptions)
        val _existingVoteOptions: TableInfo = read(db, "vote_options")
        if (!_infoVoteOptions.equals(_existingVoteOptions)) {
          return RoomOpenHelper.ValidationResult(false, """
              |vote_options(com.example.tripshare.data.model.VoteOptionEntity).
              | Expected:
              |""".trimMargin() + _infoVoteOptions + """
              |
              | Found:
              |""".trimMargin() + _existingVoteOptions)
        }
        val _columnsCostSplit: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(9)
        _columnsCostSplit.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCostSplit.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCostSplit.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCostSplit.put("expensePaymentId", TableInfo.Column("expensePaymentId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCostSplit.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCostSplit.put("amountOwed", TableInfo.Column("amountOwed", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCostSplit.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCostSplit.put("splitMode", TableInfo.Column("splitMode", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCostSplit.put("shareCount", TableInfo.Column("shareCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCostSplit: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesCostSplit: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoCostSplit: TableInfo = TableInfo("cost_split", _columnsCostSplit,
            _foreignKeysCostSplit, _indicesCostSplit)
        val _existingCostSplit: TableInfo = read(db, "cost_split")
        if (!_infoCostSplit.equals(_existingCostSplit)) {
          return RoomOpenHelper.ValidationResult(false, """
              |cost_split(com.example.tripshare.data.model.CostSplitEntity).
              | Expected:
              |""".trimMargin() + _infoCostSplit + """
              |
              | Found:
              |""".trimMargin() + _existingCostSplit)
        }
        val _columnsChecklistItems: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(9)
        _columnsChecklistItems.put("itemId", TableInfo.Column("itemId", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistItems.put("categoryId", TableInfo.Column("categoryId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistItems.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistItems.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistItems.put("completed", TableInfo.Column("completed", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistItems.put("dueDate", TableInfo.Column("dueDate", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistItems.put("note", TableInfo.Column("note", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistItems.put("sort", TableInfo.Column("sort", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistItems.put("quantity", TableInfo.Column("quantity", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChecklistItems: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysChecklistItems.add(TableInfo.ForeignKey("checklist_categories", "CASCADE",
            "NO ACTION", listOf("categoryId"), listOf("categoryId")))
        val _indicesChecklistItems: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesChecklistItems.add(TableInfo.Index("index_checklist_items_categoryId", false,
            listOf("categoryId"), listOf("ASC")))
        val _infoChecklistItems: TableInfo = TableInfo("checklist_items", _columnsChecklistItems,
            _foreignKeysChecklistItems, _indicesChecklistItems)
        val _existingChecklistItems: TableInfo = read(db, "checklist_items")
        if (!_infoChecklistItems.equals(_existingChecklistItems)) {
          return RoomOpenHelper.ValidationResult(false, """
              |checklist_items(com.example.tripshare.data.model.ChecklistItemEntity).
              | Expected:
              |""".trimMargin() + _infoChecklistItems + """
              |
              | Found:
              |""".trimMargin() + _existingChecklistItems)
        }
        val _columnsChecklistCategories: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(5)
        _columnsChecklistCategories.put("categoryId", TableInfo.Column("categoryId", "INTEGER",
            true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistCategories.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistCategories.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistCategories.put("categoryName", TableInfo.Column("categoryName", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChecklistCategories.put("sort", TableInfo.Column("sort", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChecklistCategories: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysChecklistCategories.add(TableInfo.ForeignKey("trips", "CASCADE", "NO ACTION",
            listOf("tripId"), listOf("id")))
        val _indicesChecklistCategories: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesChecklistCategories.add(TableInfo.Index("index_checklist_categories_tripId", false,
            listOf("tripId"), listOf("ASC")))
        val _infoChecklistCategories: TableInfo = TableInfo("checklist_categories",
            _columnsChecklistCategories, _foreignKeysChecklistCategories,
            _indicesChecklistCategories)
        val _existingChecklistCategories: TableInfo = read(db, "checklist_categories")
        if (!_infoChecklistCategories.equals(_existingChecklistCategories)) {
          return RoomOpenHelper.ValidationResult(false, """
              |checklist_categories(com.example.tripshare.data.model.ChecklistCategoryEntity).
              | Expected:
              |""".trimMargin() + _infoChecklistCategories + """
              |
              | Found:
              |""".trimMargin() + _existingChecklistCategories)
        }
        val _columnsJoinRequests: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(5)
        _columnsJoinRequests.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsJoinRequests.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsJoinRequests.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsJoinRequests.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsJoinRequests.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysJoinRequests: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesJoinRequests: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoJoinRequests: TableInfo = TableInfo("join_requests", _columnsJoinRequests,
            _foreignKeysJoinRequests, _indicesJoinRequests)
        val _existingJoinRequests: TableInfo = read(db, "join_requests")
        if (!_infoJoinRequests.equals(_existingJoinRequests)) {
          return RoomOpenHelper.ValidationResult(false, """
              |join_requests(com.example.tripshare.data.model.JoinRequestEntity).
              | Expected:
              |""".trimMargin() + _infoJoinRequests + """
              |
              | Found:
              |""".trimMargin() + _existingJoinRequests)
        }
        val _columnsChatRooms: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(7)
        _columnsChatRooms.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatRooms.put("title", TableInfo.Column("title", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatRooms.put("userAId", TableInfo.Column("userAId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatRooms.put("userBId", TableInfo.Column("userBId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatRooms.put("lastMessage", TableInfo.Column("lastMessage", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatRooms.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatRooms.put("isPrivate", TableInfo.Column("isPrivate", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChatRooms: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesChatRooms: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoChatRooms: TableInfo = TableInfo("chat_rooms", _columnsChatRooms,
            _foreignKeysChatRooms, _indicesChatRooms)
        val _existingChatRooms: TableInfo = read(db, "chat_rooms")
        if (!_infoChatRooms.equals(_existingChatRooms)) {
          return RoomOpenHelper.ValidationResult(false, """
              |chat_rooms(com.example.tripshare.data.model.ChatRoom).
              | Expected:
              |""".trimMargin() + _infoChatRooms + """
              |
              | Found:
              |""".trimMargin() + _existingChatRooms)
        }
        val _columnsMessages: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(7)
        _columnsMessages.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMessages.put("chatId", TableInfo.Column("chatId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMessages.put("senderId", TableInfo.Column("senderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMessages.put("content", TableInfo.Column("content", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMessages.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMessages.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMessages.put("edited", TableInfo.Column("edited", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMessages: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysMessages.add(TableInfo.ForeignKey("chat_rooms", "CASCADE", "NO ACTION",
            listOf("chatId"), listOf("id")))
        val _indicesMessages: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(2)
        _indicesMessages.add(TableInfo.Index("index_messages_chatId", false, listOf("chatId"),
            listOf("ASC")))
        _indicesMessages.add(TableInfo.Index("index_messages_senderId", false, listOf("senderId"),
            listOf("ASC")))
        val _infoMessages: TableInfo = TableInfo("messages", _columnsMessages, _foreignKeysMessages,
            _indicesMessages)
        val _existingMessages: TableInfo = read(db, "messages")
        if (!_infoMessages.equals(_existingMessages)) {
          return RoomOpenHelper.ValidationResult(false, """
              |messages(com.example.tripshare.data.model.Message).
              | Expected:
              |""".trimMargin() + _infoMessages + """
              |
              | Found:
              |""".trimMargin() + _existingMessages)
        }
        val _columnsSettlementTransfers: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(8)
        _columnsSettlementTransfers.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSettlementTransfers.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSettlementTransfers.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSettlementTransfers.put("payerUserId", TableInfo.Column("payerUserId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSettlementTransfers.put("receiverUserId", TableInfo.Column("receiverUserId",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSettlementTransfers.put("amount", TableInfo.Column("amount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSettlementTransfers.put("currency", TableInfo.Column("currency", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSettlementTransfers.put("createdAtMillis", TableInfo.Column("createdAtMillis",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSettlementTransfers: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesSettlementTransfers: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoSettlementTransfers: TableInfo = TableInfo("settlement_transfers",
            _columnsSettlementTransfers, _foreignKeysSettlementTransfers,
            _indicesSettlementTransfers)
        val _existingSettlementTransfers: TableInfo = read(db, "settlement_transfers")
        if (!_infoSettlementTransfers.equals(_existingSettlementTransfers)) {
          return RoomOpenHelper.ValidationResult(false, """
              |settlement_transfers(com.example.tripshare.data.model.SettlementTransferEntity).
              | Expected:
              |""".trimMargin() + _infoSettlementTransfers + """
              |
              | Found:
              |""".trimMargin() + _existingSettlementTransfers)
        }
        val _columnsTripNotes: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(5)
        _columnsTripNotes.put("noteId", TableInfo.Column("noteId", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripNotes.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripNotes.put("tripId", TableInfo.Column("tripId", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripNotes.put("date", TableInfo.Column("date", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripNotes.put("note", TableInfo.Column("note", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTripNotes: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesTripNotes: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(2)
        _indicesTripNotes.add(TableInfo.Index("index_trip_notes_tripId", false, listOf("tripId"),
            listOf("ASC")))
        _indicesTripNotes.add(TableInfo.Index("index_trip_notes_date", false, listOf("date"),
            listOf("ASC")))
        val _infoTripNotes: TableInfo = TableInfo("trip_notes", _columnsTripNotes,
            _foreignKeysTripNotes, _indicesTripNotes)
        val _existingTripNotes: TableInfo = read(db, "trip_notes")
        if (!_infoTripNotes.equals(_existingTripNotes)) {
          return RoomOpenHelper.ValidationResult(false, """
              |trip_notes(com.example.tripshare.data.model.TripNoteEntity).
              | Expected:
              |""".trimMargin() + _infoTripNotes + """
              |
              | Found:
              |""".trimMargin() + _existingTripNotes)
        }
        val _columnsPostLikes: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(2)
        _columnsPostLikes.put("postId", TableInfo.Column("postId", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPostLikes.put("userId", TableInfo.Column("userId", "INTEGER", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPostLikes: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(2)
        _foreignKeysPostLikes.add(TableInfo.ForeignKey("posts", "CASCADE", "NO ACTION",
            listOf("postId"), listOf("id")))
        _foreignKeysPostLikes.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("id")))
        val _indicesPostLikes: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(2)
        _indicesPostLikes.add(TableInfo.Index("index_post_likes_postId", false, listOf("postId"),
            listOf("ASC")))
        _indicesPostLikes.add(TableInfo.Index("index_post_likes_userId", false, listOf("userId"),
            listOf("ASC")))
        val _infoPostLikes: TableInfo = TableInfo("post_likes", _columnsPostLikes,
            _foreignKeysPostLikes, _indicesPostLikes)
        val _existingPostLikes: TableInfo = read(db, "post_likes")
        if (!_infoPostLikes.equals(_existingPostLikes)) {
          return RoomOpenHelper.ValidationResult(false, """
              |post_likes(com.example.tripshare.data.model.PostLikeEntity).
              | Expected:
              |""".trimMargin() + _infoPostLikes + """
              |
              | Found:
              |""".trimMargin() + _existingPostLikes)
        }
        val _columnsSavedChecklists: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(3)
        _columnsSavedChecklists.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedChecklists.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedChecklists.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSavedChecklists: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesSavedChecklists: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoSavedChecklists: TableInfo = TableInfo("saved_checklists", _columnsSavedChecklists,
            _foreignKeysSavedChecklists, _indicesSavedChecklists)
        val _existingSavedChecklists: TableInfo = read(db, "saved_checklists")
        if (!_infoSavedChecklists.equals(_existingSavedChecklists)) {
          return RoomOpenHelper.ValidationResult(false, """
              |saved_checklists(com.example.tripshare.data.model.SavedChecklistEntity).
              | Expected:
              |""".trimMargin() + _infoSavedChecklists + """
              |
              | Found:
              |""".trimMargin() + _existingSavedChecklists)
        }
        val _columnsSavedChecklistItems: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(4)
        _columnsSavedChecklistItems.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedChecklistItems.put("checklistId", TableInfo.Column("checklistId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedChecklistItems.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedChecklistItems.put("quantity", TableInfo.Column("quantity", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSavedChecklistItems: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysSavedChecklistItems.add(TableInfo.ForeignKey("saved_checklists", "CASCADE",
            "NO ACTION", listOf("checklistId"), listOf("id")))
        val _indicesSavedChecklistItems: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesSavedChecklistItems.add(TableInfo.Index("index_saved_checklist_items_checklistId",
            false, listOf("checklistId"), listOf("ASC")))
        val _infoSavedChecklistItems: TableInfo = TableInfo("saved_checklist_items",
            _columnsSavedChecklistItems, _foreignKeysSavedChecklistItems,
            _indicesSavedChecklistItems)
        val _existingSavedChecklistItems: TableInfo = read(db, "saved_checklist_items")
        if (!_infoSavedChecklistItems.equals(_existingSavedChecklistItems)) {
          return RoomOpenHelper.ValidationResult(false, """
              |saved_checklist_items(com.example.tripshare.data.model.SavedChecklistItemEntity).
              | Expected:
              |""".trimMargin() + _infoSavedChecklistItems + """
              |
              | Found:
              |""".trimMargin() + _existingSavedChecklistItems)
        }
        val _columnsGroupMessages: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(9)
        _columnsGroupMessages.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMessages.put("tripId", TableInfo.Column("tripId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMessages.put("firebaseId", TableInfo.Column("firebaseId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMessages.put("senderId", TableInfo.Column("senderId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMessages.put("senderName", TableInfo.Column("senderName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMessages.put("senderAvatar", TableInfo.Column("senderAvatar", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMessages.put("content", TableInfo.Column("content", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMessages.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMessages.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysGroupMessages: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesGroupMessages: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoGroupMessages: TableInfo = TableInfo("group_messages", _columnsGroupMessages,
            _foreignKeysGroupMessages, _indicesGroupMessages)
        val _existingGroupMessages: TableInfo = read(db, "group_messages")
        if (!_infoGroupMessages.equals(_existingGroupMessages)) {
          return RoomOpenHelper.ValidationResult(false, """
              |group_messages(com.example.tripshare.data.model.GroupMessageEntity).
              | Expected:
              |""".trimMargin() + _infoGroupMessages + """
              |
              | Found:
              |""".trimMargin() + _existingGroupMessages)
        }
        val _columnsLocalNotifications: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(8)
        _columnsLocalNotifications.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLocalNotifications.put("recipientId", TableInfo.Column("recipientId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLocalNotifications.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLocalNotifications.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLocalNotifications.put("body", TableInfo.Column("body", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLocalNotifications.put("relatedId", TableInfo.Column("relatedId", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLocalNotifications.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLocalNotifications.put("isRead", TableInfo.Column("isRead", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLocalNotifications: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesLocalNotifications: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoLocalNotifications: TableInfo = TableInfo("local_notifications",
            _columnsLocalNotifications, _foreignKeysLocalNotifications, _indicesLocalNotifications)
        val _existingLocalNotifications: TableInfo = read(db, "local_notifications")
        if (!_infoLocalNotifications.equals(_existingLocalNotifications)) {
          return RoomOpenHelper.ValidationResult(false, """
              |local_notifications(com.example.tripshare.data.model.LocalNotificationEntity).
              | Expected:
              |""".trimMargin() + _infoLocalNotifications + """
              |
              | Found:
              |""".trimMargin() + _existingLocalNotifications)
        }
        val _columnsPollVotes: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(5)
        _columnsPollVotes.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPollVotes.put("pollId", TableInfo.Column("pollId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPollVotes.put("optionId", TableInfo.Column("optionId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPollVotes.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPollVotes.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPollVotes: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesPollVotes: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(2)
        _indicesPollVotes.add(TableInfo.Index("index_poll_votes_pollId", false, listOf("pollId"),
            listOf("ASC")))
        _indicesPollVotes.add(TableInfo.Index("index_poll_votes_pollId_userId_optionId", true,
            listOf("pollId", "userId", "optionId"), listOf("ASC", "ASC", "ASC")))
        val _infoPollVotes: TableInfo = TableInfo("poll_votes", _columnsPollVotes,
            _foreignKeysPollVotes, _indicesPollVotes)
        val _existingPollVotes: TableInfo = read(db, "poll_votes")
        if (!_infoPollVotes.equals(_existingPollVotes)) {
          return RoomOpenHelper.ValidationResult(false, """
              |poll_votes(com.example.tripshare.data.model.PollVoteEntity).
              | Expected:
              |""".trimMargin() + _infoPollVotes + """
              |
              | Found:
              |""".trimMargin() + _existingPollVotes)
        }
        val _columnsInsurancePolicies: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(6)
        _columnsInsurancePolicies.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInsurancePolicies.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInsurancePolicies.put("providerName", TableInfo.Column("providerName", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInsurancePolicies.put("policyNumber", TableInfo.Column("policyNumber", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInsurancePolicies.put("emergencyPhone", TableInfo.Column("emergencyPhone", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInsurancePolicies.put("claimUrl", TableInfo.Column("claimUrl", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysInsurancePolicies: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysInsurancePolicies.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("id")))
        val _indicesInsurancePolicies: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesInsurancePolicies.add(TableInfo.Index("index_insurance_policies_userId", false,
            listOf("userId"), listOf("ASC")))
        val _infoInsurancePolicies: TableInfo = TableInfo("insurance_policies",
            _columnsInsurancePolicies, _foreignKeysInsurancePolicies, _indicesInsurancePolicies)
        val _existingInsurancePolicies: TableInfo = read(db, "insurance_policies")
        if (!_infoInsurancePolicies.equals(_existingInsurancePolicies)) {
          return RoomOpenHelper.ValidationResult(false, """
              |insurance_policies(com.example.tripshare.data.model.InsurancePolicyEntity).
              | Expected:
              |""".trimMargin() + _infoInsurancePolicies + """
              |
              | Found:
              |""".trimMargin() + _existingInsurancePolicies)
        }
        val _columnsTripComments: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(5)
        _columnsTripComments.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripComments.put("planId", TableInfo.Column("planId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripComments.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripComments.put("text", TableInfo.Column("text", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTripComments.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTripComments: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(2)
        _foreignKeysTripComments.add(TableInfo.ForeignKey("itinerary_items", "CASCADE", "CASCADE",
            listOf("planId"), listOf("id")))
        _foreignKeysTripComments.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("id")))
        val _indicesTripComments: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(2)
        _indicesTripComments.add(TableInfo.Index("index_trip_comments_planId", false,
            listOf("planId"), listOf("ASC")))
        _indicesTripComments.add(TableInfo.Index("index_trip_comments_userId", false,
            listOf("userId"), listOf("ASC")))
        val _infoTripComments: TableInfo = TableInfo("trip_comments", _columnsTripComments,
            _foreignKeysTripComments, _indicesTripComments)
        val _existingTripComments: TableInfo = read(db, "trip_comments")
        if (!_infoTripComments.equals(_existingTripComments)) {
          return RoomOpenHelper.ValidationResult(false, """
              |trip_comments(com.example.tripshare.data.model.TripCommentEntity).
              | Expected:
              |""".trimMargin() + _infoTripComments + """
              |
              | Found:
              |""".trimMargin() + _existingTripComments)
        }
        return RoomOpenHelper.ValidationResult(true, null)
      }
    }, "ff5ed4da016f106bdfff713796e1eb78", "b013bf2b4719fa7f7e92b6be77dc091d")
    val _sqliteConfig: SupportSQLiteOpenHelper.Configuration =
        SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build()
    val _helper: SupportSQLiteOpenHelper = config.sqliteOpenHelperFactory.create(_sqliteConfig)
    return _helper
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: HashMap<String, String> = HashMap<String, String>(0)
    val _viewTables: HashMap<String, Set<String>> = HashMap<String, Set<String>>(0)
    return InvalidationTracker(this, _shadowTablesMap, _viewTables,
        "users","reports","emergency_contacts","trips","route_stops","trip_payment_methods","participant_invites","trip_participants","trip_meeting_points","posts","reviews","itinerary_items","past_trips","waitlist","calendar_events","trip_documents","daily_notes","expense_payments","comments","polls","vote_options","cost_split","checklist_items","checklist_categories","join_requests","chat_rooms","messages","settlement_transfers","trip_notes","post_likes","saved_checklists","saved_checklist_items","group_messages","local_notifications","poll_votes","insurance_policies","trip_comments")
  }

  public override fun clearAllTables() {
    super.assertNotMainThread()
    val _db: SupportSQLiteDatabase = super.openHelper.writableDatabase
    val _supportsDeferForeignKeys: Boolean = android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.LOLLIPOP
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE")
      }
      super.beginTransaction()
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE")
      }
      _db.execSQL("DELETE FROM `users`")
      _db.execSQL("DELETE FROM `reports`")
      _db.execSQL("DELETE FROM `emergency_contacts`")
      _db.execSQL("DELETE FROM `trips`")
      _db.execSQL("DELETE FROM `route_stops`")
      _db.execSQL("DELETE FROM `trip_payment_methods`")
      _db.execSQL("DELETE FROM `participant_invites`")
      _db.execSQL("DELETE FROM `trip_participants`")
      _db.execSQL("DELETE FROM `trip_meeting_points`")
      _db.execSQL("DELETE FROM `posts`")
      _db.execSQL("DELETE FROM `reviews`")
      _db.execSQL("DELETE FROM `itinerary_items`")
      _db.execSQL("DELETE FROM `past_trips`")
      _db.execSQL("DELETE FROM `waitlist`")
      _db.execSQL("DELETE FROM `calendar_events`")
      _db.execSQL("DELETE FROM `trip_documents`")
      _db.execSQL("DELETE FROM `daily_notes`")
      _db.execSQL("DELETE FROM `expense_payments`")
      _db.execSQL("DELETE FROM `comments`")
      _db.execSQL("DELETE FROM `polls`")
      _db.execSQL("DELETE FROM `vote_options`")
      _db.execSQL("DELETE FROM `cost_split`")
      _db.execSQL("DELETE FROM `checklist_items`")
      _db.execSQL("DELETE FROM `checklist_categories`")
      _db.execSQL("DELETE FROM `join_requests`")
      _db.execSQL("DELETE FROM `chat_rooms`")
      _db.execSQL("DELETE FROM `messages`")
      _db.execSQL("DELETE FROM `settlement_transfers`")
      _db.execSQL("DELETE FROM `trip_notes`")
      _db.execSQL("DELETE FROM `post_likes`")
      _db.execSQL("DELETE FROM `saved_checklists`")
      _db.execSQL("DELETE FROM `saved_checklist_items`")
      _db.execSQL("DELETE FROM `group_messages`")
      _db.execSQL("DELETE FROM `local_notifications`")
      _db.execSQL("DELETE FROM `poll_votes`")
      _db.execSQL("DELETE FROM `insurance_policies`")
      _db.execSQL("DELETE FROM `trip_comments`")
      super.setTransactionSuccessful()
    } finally {
      super.endTransaction()
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE")
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close()
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM")
      }
    }
  }

  protected override fun getRequiredTypeConverters(): Map<Class<out Any>, List<Class<out Any>>> {
    val _typeConvertersMap: HashMap<Class<out Any>, List<Class<out Any>>> =
        HashMap<Class<out Any>, List<Class<out Any>>>()
    _typeConvertersMap.put(UserDao::class.java, UserDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TripDao::class.java, TripDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(PostDao::class.java, PostDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TripHistoryDao::class.java, TripHistoryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ChatRoomDao::class.java, ChatRoomDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MessageDao::class.java, MessageDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WaitlistDao::class.java, WaitlistDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ExpensePaymentDao::class.java,
        ExpensePaymentDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ReportDao::class.java, ReportDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(EmergencyContactDao::class.java,
        EmergencyContactDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CommentDao::class.java, CommentDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(VoteDao::class.java, VoteDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CostSplitDao::class.java, CostSplitDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ItineraryDao::class.java, ItineraryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ChecklistDao::class.java, ChecklistDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ParticipantDao::class.java, ParticipantDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(JoinRequestDao::class.java, JoinRequestDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(RouteDao::class.java, RouteDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SettlementDao::class.java, SettlementDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TripCalendarDao::class.java,
        TripCalendarDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ReviewDao::class.java, ReviewDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SavedListDao::class.java, SavedListDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(GroupChatDao::class.java, GroupChatDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(LocalNotificationDao::class.java,
        LocalNotificationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(InsuranceDao::class.java, InsuranceDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TripCommentDao::class.java, TripCommentDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecs(): Set<Class<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: HashSet<Class<out AutoMigrationSpec>> =
        HashSet<Class<out AutoMigrationSpec>>()
    return _autoMigrationSpecsSet
  }

  public override
      fun getAutoMigrations(autoMigrationSpecs: Map<Class<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = ArrayList<Migration>()
    return _autoMigrations
  }

  public override fun userDao(): UserDao = _userDao.value

  public override fun tripDao(): TripDao = _tripDao.value

  public override fun postDao(): PostDao = _postDao.value

  public override fun tripHistoryDao(): TripHistoryDao = _tripHistoryDao.value

  public override fun chatRoomDao(): ChatRoomDao = _chatRoomDao.value

  public override fun messageDao(): MessageDao = _messageDao.value

  public override fun waitlistDao(): WaitlistDao = _waitlistDao.value

  public override fun expensePaymentDao(): ExpensePaymentDao = _expensePaymentDao.value

  public override fun reportDao(): ReportDao = _reportDao.value

  public override fun emergencyContactDao(): EmergencyContactDao = _emergencyContactDao.value

  public override fun commentDao(): CommentDao = _commentDao.value

  public override fun voteDao(): VoteDao = _voteDao.value

  public override fun costSplitDao(): CostSplitDao = _costSplitDao.value

  public override fun itineraryDao(): ItineraryDao = _itineraryDao.value

  public override fun checklistDao(): ChecklistDao = _checklistDao.value

  public override fun participantDao(): ParticipantDao = _participantDao.value

  public override fun joinRequestDao(): JoinRequestDao = _joinRequestDao.value

  public override fun routeDao(): RouteDao = _routeDao.value

  public override fun settlementDao(): SettlementDao = _settlementDao.value

  public override fun tripCalendarDao(): TripCalendarDao = _tripCalendarDao.value

  public override fun reviewDao(): ReviewDao = _reviewDao.value

  public override fun savedListDao(): SavedListDao = _savedListDao.value

  public override fun groupChatDao(): GroupChatDao = _groupChatDao.value

  public override fun localNotificationDao(): LocalNotificationDao = _localNotificationDao.value

  public override fun insuranceDao(): InsuranceDao = _insuranceDao.value

  public override fun tripCommentDao(): TripCommentDao = _tripCommentDao.value
}
