package com.example.tripshare.`data`.db

import android.database.Cursor
import android.os.CancellationSignal
import androidx.collection.LongSparseArray
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.appendPlaceholders
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.newStringBuilder
import androidx.room.util.query
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.CalendarEventEntity
import com.example.tripshare.`data`.model.InviteStatus
import com.example.tripshare.`data`.model.ItineraryItemEntity
import com.example.tripshare.`data`.model.ParticipantInviteEntity
import com.example.tripshare.`data`.model.ParticipantRole
import com.example.tripshare.`data`.model.ParticipationStatus
import com.example.tripshare.`data`.model.PaymentMethod
import com.example.tripshare.`data`.model.RouteStopEntity
import com.example.tripshare.`data`.model.StopType
import com.example.tripshare.`data`.model.TripCategory
import com.example.tripshare.`data`.model.TripEntity
import com.example.tripshare.`data`.model.TripMeetingPointEntity
import com.example.tripshare.`data`.model.TripParticipantEntity
import com.example.tripshare.`data`.model.TripPaymentMethodEntity
import com.example.tripshare.`data`.model.Visibility
import com.example.tripshare.`data`.model.WaitlistEntity
import java.lang.Class
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import java.time.LocalDate
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class TripDao_Impl(
  __db: RoomDatabase,
) : TripDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfRouteStopEntity: EntityInsertionAdapter<RouteStopEntity>

  private val __insertionAdapterOfCalendarEventEntity: EntityInsertionAdapter<CalendarEventEntity>

  private val __insertionAdapterOfTripEntity: EntityInsertionAdapter<TripEntity>

  private val __converters: Converters = Converters()

  private val __insertionAdapterOfRouteStopEntity_1: EntityInsertionAdapter<RouteStopEntity>

  private val __insertionAdapterOfTripPaymentMethodEntity:
      EntityInsertionAdapter<TripPaymentMethodEntity>

  private val __insertionAdapterOfParticipantInviteEntity:
      EntityInsertionAdapter<ParticipantInviteEntity>

  private val __insertionAdapterOfItineraryItemEntity: EntityInsertionAdapter<ItineraryItemEntity>

  private val __insertionAdapterOfTripParticipantEntity:
      EntityInsertionAdapter<TripParticipantEntity>

  private val __insertionAdapterOfTripMeetingPointEntity:
      EntityInsertionAdapter<TripMeetingPointEntity>

  private val __insertionAdapterOfWaitlistEntity: EntityInsertionAdapter<WaitlistEntity>

  private val __deletionAdapterOfItineraryItemEntity:
      EntityDeletionOrUpdateAdapter<ItineraryItemEntity>

  private val __updateAdapterOfTripEntity: EntityDeletionOrUpdateAdapter<TripEntity>

  private val __updateAdapterOfItineraryItemEntity:
      EntityDeletionOrUpdateAdapter<ItineraryItemEntity>

  private val __preparedStmtOfDeleteTrip: SharedSQLiteStatement

  private val __preparedStmtOfUpdateTripImage: SharedSQLiteStatement

  private val __preparedStmtOfArchiveTrip: SharedSQLiteStatement

  private val __preparedStmtOfDeleteMeetingPointsForTrip: SharedSQLiteStatement

  private val __preparedStmtOfDeleteRouteStopsForTrip: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfRouteStopEntity = object :
        EntityInsertionAdapter<RouteStopEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `route_stops` (`id`,`tripId`,`type`,`label`,`lat`,`lng`,`orderInRoute`,`startDate`,`endDate`,`nights`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: RouteStopEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindString(3, __StopType_enumToString(entity.type))
        statement.bindString(4, entity.label)
        val _tmpLat: Double? = entity.lat
        if (_tmpLat == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpLat)
        }
        val _tmpLng: Double? = entity.lng
        if (_tmpLng == null) {
          statement.bindNull(6)
        } else {
          statement.bindDouble(6, _tmpLng)
        }
        statement.bindLong(7, entity.orderInRoute.toLong())
        val _tmpStartDate: String? = entity.startDate
        if (_tmpStartDate == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmpStartDate)
        }
        val _tmpEndDate: String? = entity.endDate
        if (_tmpEndDate == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpEndDate)
        }
        statement.bindLong(10, entity.nights.toLong())
      }
    }
    this.__insertionAdapterOfCalendarEventEntity = object :
        EntityInsertionAdapter<CalendarEventEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `calendar_events` (`id`,`tripId`,`title`,`date`,`time`,`location`,`category`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: CalendarEventEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindString(3, entity.title)
        statement.bindString(4, entity.date)
        val _tmpTime: String? = entity.time
        if (_tmpTime == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmpTime)
        }
        val _tmpLocation: String? = entity.location
        if (_tmpLocation == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpLocation)
        }
        statement.bindString(7, entity.category)
      }
    }
    this.__insertionAdapterOfTripEntity = object : EntityInsertionAdapter<TripEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `trips` (`id`,`name`,`firebaseId`,`organizerId`,`category`,`visibility`,`startDate`,`endDate`,`maxParticipants`,`costSharing`,`paymentDeadline`,`waitlistEnabled`,`budget`,`budgetDisplay`,`description`,`coverImgUrl`,`isArchived`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: TripEntity) {
        statement.bindLong(1, entity.id)
        statement.bindString(2, entity.name)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindString(4, entity.organizerId)
        val _tmp: String? = __converters.catTo(entity.category)
        if (_tmp == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmp)
        }
        val _tmp_1: String? = __converters.visTo(entity.visibility)
        if (_tmp_1 == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmp_1)
        }
        val _tmpStartDate: LocalDate? = entity.startDate
        val _tmp_2: Long? = __converters.toEpochDay(_tmpStartDate)
        if (_tmp_2 == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmp_2)
        }
        val _tmpEndDate: LocalDate? = entity.endDate
        val _tmp_3: Long? = __converters.toEpochDay(_tmpEndDate)
        if (_tmp_3 == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmp_3)
        }
        statement.bindLong(9, entity.maxParticipants.toLong())
        val _tmp_4: Int = if (entity.costSharing) 1 else 0
        statement.bindLong(10, _tmp_4.toLong())
        val _tmpPaymentDeadline: LocalDate? = entity.paymentDeadline
        val _tmp_5: Long? = __converters.toEpochDay(_tmpPaymentDeadline)
        if (_tmp_5 == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmp_5)
        }
        val _tmp_6: Int = if (entity.waitlistEnabled) 1 else 0
        statement.bindLong(12, _tmp_6.toLong())
        val _tmpBudget: Double? = entity.budget
        if (_tmpBudget == null) {
          statement.bindNull(13)
        } else {
          statement.bindDouble(13, _tmpBudget)
        }
        val _tmpBudgetDisplay: String? = entity.budgetDisplay
        if (_tmpBudgetDisplay == null) {
          statement.bindNull(14)
        } else {
          statement.bindString(14, _tmpBudgetDisplay)
        }
        statement.bindString(15, entity.description)
        val _tmpCoverImgUrl: String? = entity.coverImgUrl
        if (_tmpCoverImgUrl == null) {
          statement.bindNull(16)
        } else {
          statement.bindString(16, _tmpCoverImgUrl)
        }
        val _tmp_7: Int = if (entity.isArchived) 1 else 0
        statement.bindLong(17, _tmp_7.toLong())
      }
    }
    this.__insertionAdapterOfRouteStopEntity_1 = object :
        EntityInsertionAdapter<RouteStopEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `route_stops` (`id`,`tripId`,`type`,`label`,`lat`,`lng`,`orderInRoute`,`startDate`,`endDate`,`nights`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: RouteStopEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindString(3, __StopType_enumToString(entity.type))
        statement.bindString(4, entity.label)
        val _tmpLat: Double? = entity.lat
        if (_tmpLat == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpLat)
        }
        val _tmpLng: Double? = entity.lng
        if (_tmpLng == null) {
          statement.bindNull(6)
        } else {
          statement.bindDouble(6, _tmpLng)
        }
        statement.bindLong(7, entity.orderInRoute.toLong())
        val _tmpStartDate: String? = entity.startDate
        if (_tmpStartDate == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmpStartDate)
        }
        val _tmpEndDate: String? = entity.endDate
        if (_tmpEndDate == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpEndDate)
        }
        statement.bindLong(10, entity.nights.toLong())
      }
    }
    this.__insertionAdapterOfTripPaymentMethodEntity = object :
        EntityInsertionAdapter<TripPaymentMethodEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `trip_payment_methods` (`id`,`tripId`,`method`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: TripPaymentMethodEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindString(3, __PaymentMethod_enumToString(entity.method))
      }
    }
    this.__insertionAdapterOfParticipantInviteEntity = object :
        EntityInsertionAdapter<ParticipantInviteEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `participant_invites` (`id`,`tripId`,`identifier`,`status`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: ParticipantInviteEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindString(3, entity.identifier)
        statement.bindString(4, __InviteStatus_enumToString(entity.status))
      }
    }
    this.__insertionAdapterOfItineraryItemEntity = object :
        EntityInsertionAdapter<ItineraryItemEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `itinerary_items` (`id`,`tripId`,`firebaseId`,`day`,`title`,`date`,`time`,`location`,`notes`,`category`,`attachment`,`assignedTo`,`endDate`,`endTime`,`lat`,`lng`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ItineraryItemEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindLong(4, entity.day.toLong())
        statement.bindString(5, entity.title)
        statement.bindString(6, entity.date)
        statement.bindString(7, entity.time)
        val _tmpLocation: String? = entity.location
        if (_tmpLocation == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmpLocation)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpNotes)
        }
        val _tmpCategory: String? = entity.category
        if (_tmpCategory == null) {
          statement.bindNull(10)
        } else {
          statement.bindString(10, _tmpCategory)
        }
        val _tmpAttachment: String? = entity.attachment
        if (_tmpAttachment == null) {
          statement.bindNull(11)
        } else {
          statement.bindString(11, _tmpAttachment)
        }
        val _tmpAssignedTo: String? = entity.assignedTo
        if (_tmpAssignedTo == null) {
          statement.bindNull(12)
        } else {
          statement.bindString(12, _tmpAssignedTo)
        }
        val _tmpEndDate: String? = entity.endDate
        if (_tmpEndDate == null) {
          statement.bindNull(13)
        } else {
          statement.bindString(13, _tmpEndDate)
        }
        val _tmpEndTime: String? = entity.endTime
        if (_tmpEndTime == null) {
          statement.bindNull(14)
        } else {
          statement.bindString(14, _tmpEndTime)
        }
        val _tmpLat: Double? = entity.lat
        if (_tmpLat == null) {
          statement.bindNull(15)
        } else {
          statement.bindDouble(15, _tmpLat)
        }
        val _tmpLng: Double? = entity.lng
        if (_tmpLng == null) {
          statement.bindNull(16)
        } else {
          statement.bindDouble(16, _tmpLng)
        }
      }
    }
    this.__insertionAdapterOfTripParticipantEntity = object :
        EntityInsertionAdapter<TripParticipantEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `trip_participants` (`id`,`tripId`,`userId`,`email`,`displayName`,`status`,`role`,`joinedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: TripParticipantEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindLong(3, entity.userId)
        statement.bindString(4, entity.email)
        statement.bindString(5, entity.displayName)
        statement.bindString(6, __ParticipationStatus_enumToString(entity.status))
        statement.bindString(7, __ParticipantRole_enumToString(entity.role))
        statement.bindLong(8, entity.joinedAt)
      }
    }
    this.__insertionAdapterOfTripMeetingPointEntity = object :
        EntityInsertionAdapter<TripMeetingPointEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `trip_meeting_points` (`id`,`tripId`,`label`,`lat`,`lng`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: TripMeetingPointEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindString(3, entity.label)
        val _tmpLat: Double? = entity.lat
        if (_tmpLat == null) {
          statement.bindNull(4)
        } else {
          statement.bindDouble(4, _tmpLat)
        }
        val _tmpLng: Double? = entity.lng
        if (_tmpLng == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpLng)
        }
      }
    }
    this.__insertionAdapterOfWaitlistEntity = object : EntityInsertionAdapter<WaitlistEntity>(__db)
        {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `waitlist` (`id`,`tripId`,`firebaseId`,`tripName`,`location`,`date`,`position`,`alertsEnabled`,`tripImageUrl`,`userId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: WaitlistEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindString(4, entity.tripName)
        statement.bindString(5, entity.location)
        statement.bindString(6, entity.date)
        statement.bindLong(7, entity.position.toLong())
        val _tmp: Int = if (entity.alertsEnabled) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        val _tmpTripImageUrl: String? = entity.tripImageUrl
        if (_tmpTripImageUrl == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpTripImageUrl)
        }
        statement.bindLong(10, entity.userId)
      }
    }
    this.__deletionAdapterOfItineraryItemEntity = object :
        EntityDeletionOrUpdateAdapter<ItineraryItemEntity>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `itinerary_items` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ItineraryItemEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfTripEntity = object : EntityDeletionOrUpdateAdapter<TripEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `trips` SET `id` = ?,`name` = ?,`firebaseId` = ?,`organizerId` = ?,`category` = ?,`visibility` = ?,`startDate` = ?,`endDate` = ?,`maxParticipants` = ?,`costSharing` = ?,`paymentDeadline` = ?,`waitlistEnabled` = ?,`budget` = ?,`budgetDisplay` = ?,`description` = ?,`coverImgUrl` = ?,`isArchived` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: TripEntity) {
        statement.bindLong(1, entity.id)
        statement.bindString(2, entity.name)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindString(4, entity.organizerId)
        val _tmp: String? = __converters.catTo(entity.category)
        if (_tmp == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmp)
        }
        val _tmp_1: String? = __converters.visTo(entity.visibility)
        if (_tmp_1 == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmp_1)
        }
        val _tmpStartDate: LocalDate? = entity.startDate
        val _tmp_2: Long? = __converters.toEpochDay(_tmpStartDate)
        if (_tmp_2 == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmp_2)
        }
        val _tmpEndDate: LocalDate? = entity.endDate
        val _tmp_3: Long? = __converters.toEpochDay(_tmpEndDate)
        if (_tmp_3 == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmp_3)
        }
        statement.bindLong(9, entity.maxParticipants.toLong())
        val _tmp_4: Int = if (entity.costSharing) 1 else 0
        statement.bindLong(10, _tmp_4.toLong())
        val _tmpPaymentDeadline: LocalDate? = entity.paymentDeadline
        val _tmp_5: Long? = __converters.toEpochDay(_tmpPaymentDeadline)
        if (_tmp_5 == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmp_5)
        }
        val _tmp_6: Int = if (entity.waitlistEnabled) 1 else 0
        statement.bindLong(12, _tmp_6.toLong())
        val _tmpBudget: Double? = entity.budget
        if (_tmpBudget == null) {
          statement.bindNull(13)
        } else {
          statement.bindDouble(13, _tmpBudget)
        }
        val _tmpBudgetDisplay: String? = entity.budgetDisplay
        if (_tmpBudgetDisplay == null) {
          statement.bindNull(14)
        } else {
          statement.bindString(14, _tmpBudgetDisplay)
        }
        statement.bindString(15, entity.description)
        val _tmpCoverImgUrl: String? = entity.coverImgUrl
        if (_tmpCoverImgUrl == null) {
          statement.bindNull(16)
        } else {
          statement.bindString(16, _tmpCoverImgUrl)
        }
        val _tmp_7: Int = if (entity.isArchived) 1 else 0
        statement.bindLong(17, _tmp_7.toLong())
        statement.bindLong(18, entity.id)
      }
    }
    this.__updateAdapterOfItineraryItemEntity = object :
        EntityDeletionOrUpdateAdapter<ItineraryItemEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `itinerary_items` SET `id` = ?,`tripId` = ?,`firebaseId` = ?,`day` = ?,`title` = ?,`date` = ?,`time` = ?,`location` = ?,`notes` = ?,`category` = ?,`attachment` = ?,`assignedTo` = ?,`endDate` = ?,`endTime` = ?,`lat` = ?,`lng` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ItineraryItemEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindLong(4, entity.day.toLong())
        statement.bindString(5, entity.title)
        statement.bindString(6, entity.date)
        statement.bindString(7, entity.time)
        val _tmpLocation: String? = entity.location
        if (_tmpLocation == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmpLocation)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpNotes)
        }
        val _tmpCategory: String? = entity.category
        if (_tmpCategory == null) {
          statement.bindNull(10)
        } else {
          statement.bindString(10, _tmpCategory)
        }
        val _tmpAttachment: String? = entity.attachment
        if (_tmpAttachment == null) {
          statement.bindNull(11)
        } else {
          statement.bindString(11, _tmpAttachment)
        }
        val _tmpAssignedTo: String? = entity.assignedTo
        if (_tmpAssignedTo == null) {
          statement.bindNull(12)
        } else {
          statement.bindString(12, _tmpAssignedTo)
        }
        val _tmpEndDate: String? = entity.endDate
        if (_tmpEndDate == null) {
          statement.bindNull(13)
        } else {
          statement.bindString(13, _tmpEndDate)
        }
        val _tmpEndTime: String? = entity.endTime
        if (_tmpEndTime == null) {
          statement.bindNull(14)
        } else {
          statement.bindString(14, _tmpEndTime)
        }
        val _tmpLat: Double? = entity.lat
        if (_tmpLat == null) {
          statement.bindNull(15)
        } else {
          statement.bindDouble(15, _tmpLat)
        }
        val _tmpLng: Double? = entity.lng
        if (_tmpLng == null) {
          statement.bindNull(16)
        } else {
          statement.bindDouble(16, _tmpLng)
        }
        statement.bindLong(17, entity.id)
      }
    }
    this.__preparedStmtOfDeleteTrip = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM trips WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfUpdateTripImage = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE trips SET coverImgUrl = ? WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfArchiveTrip = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE trips SET isArchived = 1 WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteMeetingPointsForTrip = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM trip_meeting_points WHERE tripId = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteRouteStopsForTrip = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM route_stops WHERE tripId = ?"
        return _query
      }
    }
  }

  public override suspend fun insertRouteStops(stops: List<RouteStopEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfRouteStopEntity.insert(stops)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertCalendarEvents(events: List<CalendarEventEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfCalendarEventEntity.insert(events)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertTrip(trip: TripEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfTripEntity.insertAndReturnId(trip)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertRoute(stops: List<RouteStopEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfRouteStopEntity_1.insert(stops)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertPaymentMethods(methods: List<TripPaymentMethodEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfTripPaymentMethodEntity.insert(methods)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertInvites(invites: List<ParticipantInviteEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfParticipantInviteEntity.insert(invites)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertItinerary(item: ItineraryItemEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfItineraryItemEntity.insertAndReturnId(item)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertParticipants(participants: List<TripParticipantEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfTripParticipantEntity.insert(participants)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertMeetingPoints(points: List<TripMeetingPointEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfTripMeetingPointEntity.insert(points)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertWaitlist(waitlist: WaitlistEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfWaitlistEntity.insert(waitlist)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteItinerary(item: ItineraryItemEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfItineraryItemEntity.handle(item)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun update(trip: TripEntity): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfTripEntity.handle(trip)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateItinerary(item: ItineraryItemEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfItineraryItemEntity.handle(item)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertTripWithAll(
    trip: TripEntity,
    routes: List<RouteStopEntity>,
    payments: List<TripPaymentMethodEntity>,
    invites: List<ParticipantInviteEntity>,
    events: List<CalendarEventEntity>,
  ): Long = __db.withTransaction {
    super@TripDao_Impl.insertTripWithAll(trip, routes, payments, invites, events)
  }

  public override suspend fun updateTripFromCloud(trip: TripEntity, stops: List<RouteStopEntity>) {
    __db.withTransaction {
      super@TripDao_Impl.updateTripFromCloud(trip, stops)
    }
  }

  public override suspend fun deleteTrip(tripId: Long): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteTrip.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, tripId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteTrip.release(_stmt)
      }
    }
  })

  public override suspend fun updateTripImage(tripId: Long, uri: String): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUpdateTripImage.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, uri)
      _argIndex = 2
      _stmt.bindLong(_argIndex, tripId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfUpdateTripImage.release(_stmt)
      }
    }
  })

  public override suspend fun archiveTrip(tripId: Long): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfArchiveTrip.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, tripId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfArchiveTrip.release(_stmt)
      }
    }
  })

  public override suspend fun deleteMeetingPointsForTrip(tripId: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteMeetingPointsForTrip.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, tripId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteMeetingPointsForTrip.release(_stmt)
      }
    }
  })

  public override suspend fun deleteRouteStopsForTrip(tripId: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteRouteStopsForTrip.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, tripId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteRouteStopsForTrip.release(_stmt)
      }
    }
  })

  public override suspend fun findUserIdByNameInTripExact(tripId: Long, name: String): Long? {
    val _sql: String = """
        |
        |SELECT u.id FROM users u
        |JOIN trip_participants p ON p.userId = u.id
        |WHERE p.tripId = ? AND LOWER(u.name) = LOWER(?)
        |LIMIT 1
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    _argIndex = 2
    _statement.bindString(_argIndex, name)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Long?> {
      public override fun call(): Long? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Long?
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null
            } else {
              _result = _cursor.getLong(0)
            }
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getTripByFirebaseId(fid: String): TripEntity? {
    val _sql: String = "SELECT * FROM trips WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<TripEntity?> {
      public override fun call(): TripEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: TripEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _result =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun findUserIdByNameInTripStartsWith(tripId: Long, prefix: String):
      Long? {
    val _sql: String = """
        |
        |SELECT u.id FROM users u
        |JOIN trip_participants p ON p.userId = u.id
        |WHERE p.tripId = ? AND LOWER(u.name) LIKE LOWER(?) || '%'
        |ORDER BY LENGTH(u.name) ASC
        |LIMIT 1
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    _argIndex = 2
    _statement.bindString(_argIndex, prefix)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Long?> {
      public override fun call(): Long? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Long?
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null
            } else {
              _result = _cursor.getLong(0)
            }
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun observeActiveTrips(): Flow<List<TripEntity>> {
    val _sql: String = "SELECT * FROM trips WHERE isArchived = 0 ORDER BY startDate ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips"), object :
        Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observePastTrips(): Flow<List<TripEntity>> {
    val _sql: String = "SELECT * FROM trips WHERE isArchived = 1 ORDER BY endDate DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips"), object :
        Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun getTripById(id: Long): TripEntity? {
    val _sql: String = "SELECT * FROM trips WHERE id = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<TripEntity?> {
      public override fun call(): TripEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: TripEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _result =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getTripsEndedBefore(cutoffEpochDay: Long): List<TripEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM trips
        |        WHERE endDate IS NOT NULL
        |          AND endDate <= ?
        |          AND isArchived = 0
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, cutoffEpochDay)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun searchActive(q: String): Flow<List<TripEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM trips
        |        WHERE isArchived = 0 AND name LIKE '%' || ? || '%'
        |        ORDER BY startDate ASC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, q)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips"), object :
        Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun getLatestTripId(): Long? {
    val _sql: String = "SELECT id FROM trips ORDER BY id DESC LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Long?> {
      public override fun call(): Long? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Long?
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null
            } else {
              _result = _cursor.getLong(0)
            }
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getLatestTripIdForUser(userId: Long): Long? {
    val _sql: String = """
        |
        |    SELECT t.id
        |    FROM trips t
        |    JOIN trip_participants p ON p.tripId = t.id
        |    WHERE p.userId = ?
        |    ORDER BY t.id DESC
        |    LIMIT 1
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Long?> {
      public override fun call(): Long? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Long?
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null
            } else {
              _result = _cursor.getLong(0)
            }
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun observeById(id: Long): Flow<TripEntity?> {
    val _sql: String = "SELECT * FROM trips WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips"), object : Callable<TripEntity?> {
      public override fun call(): TripEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: TripEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _result =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun getParticipants(tripId: Long): List<TripParticipantEntity> {
    val _sql: String = "SELECT * FROM trip_participants WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<TripParticipantEntity>>
        {
      public override fun call(): List<TripParticipantEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfEmail: Int = getColumnIndexOrThrow(_cursor, "email")
          val _cursorIndexOfDisplayName: Int = getColumnIndexOrThrow(_cursor, "displayName")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfRole: Int = getColumnIndexOrThrow(_cursor, "role")
          val _cursorIndexOfJoinedAt: Int = getColumnIndexOrThrow(_cursor, "joinedAt")
          val _result: MutableList<TripParticipantEntity> =
              ArrayList<TripParticipantEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripParticipantEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpEmail: String
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail)
            val _tmpDisplayName: String
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName)
            val _tmpStatus: ParticipationStatus
            _tmpStatus = __ParticipationStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus))
            val _tmpRole: ParticipantRole
            _tmpRole = __ParticipantRole_stringToEnum(_cursor.getString(_cursorIndexOfRole))
            val _tmpJoinedAt: Long
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt)
            _item =
                TripParticipantEntity(_tmpId,_tmpTripId,_tmpUserId,_tmpEmail,_tmpDisplayName,_tmpStatus,_tmpRole,_tmpJoinedAt)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getParticipantCount(tripId: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM trip_participants WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Int> {
      public override fun call(): Int {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Int
          if (_cursor.moveToFirst()) {
            val _tmp: Int
            _tmp = _cursor.getInt(0)
            _result = _tmp
          } else {
            _result = 0
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun observeJoinedActiveTrips(userId: Long): Flow<List<TripEntity>> {
    val _sql: String = """
        |
        |    SELECT t.* FROM trips t
        |    INNER JOIN trip_participants p ON p.tripId = t.id
        |    WHERE p.userId = ?
        |      AND t.isArchived = 0
        |    ORDER BY t.startDate ASC
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips", "trip_participants"), object :
        Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeTripsForUser(userId: Long): Flow<List<TripEntity>> {
    val _sql: String = """
        |
        |    SELECT * FROM trips 
        |    WHERE organizerId = ? 
        |    OR id IN (SELECT tripId FROM trip_participants WHERE userId = ?)
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    _argIndex = 2
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips", "trip_participants"), object :
        Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun getById(id: Long): TripEntity? {
    val _sql: String = "SELECT * FROM trips WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<TripEntity?> {
      public override fun call(): TripEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: TripEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _result =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun observeTripIdsForUser(userId: Long): Flow<List<Long>> {
    val _sql: String = "SELECT tripId FROM trip_participants WHERE userId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trip_participants"), object :
        Callable<List<Long>> {
      public override fun call(): List<Long> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: MutableList<Long> = ArrayList<Long>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: Long
            _item = _cursor.getLong(0)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeJoinedTripIds(userId: Long): Flow<List<Long>> {
    val _sql: String = "SELECT tripId FROM trip_participants WHERE userId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trip_participants"), object :
        Callable<List<Long>> {
      public override fun call(): List<Long> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: MutableList<Long> = ArrayList<Long>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: Long
            _item = _cursor.getLong(0)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeIsUserParticipant(tripId: Long, userId: Long): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM trip_participants WHERE tripId = ? AND userId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    _argIndex = 2
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trip_participants"), object :
        Callable<Int> {
      public override fun call(): Int {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Int
          if (_cursor.moveToFirst()) {
            val _tmp: Int
            _tmp = _cursor.getInt(0)
            _result = _tmp
          } else {
            _result = 0
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeTripFull(id: Long): Flow<TripFullAggregate?> {
    val _sql: String = "SELECT * FROM trips WHERE id = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    return CoroutinesRoom.createFlow(__db, true, arrayOf("route_stops", "trip_participants",
        "trip_meeting_points", "itinerary_items", "trip_payment_methods", "participant_invites",
        "trips"), object : Callable<TripFullAggregate?> {
      public override fun call(): TripFullAggregate? {
        __db.beginTransaction()
        try {
          val _cursor: Cursor = query(__db, _statement, true, null)
          try {
            val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
            val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
            val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
            val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
            val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
            val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
            val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
            val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
            val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor,
                "maxParticipants")
            val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
            val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor,
                "paymentDeadline")
            val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor,
                "waitlistEnabled")
            val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
            val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
            val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
            val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
            val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
            val _collectionRouteStops: LongSparseArray<ArrayList<RouteStopEntity>> =
                LongSparseArray<ArrayList<RouteStopEntity>>()
            val _collectionParticipants: LongSparseArray<ArrayList<TripParticipantEntity>> =
                LongSparseArray<ArrayList<TripParticipantEntity>>()
            val _collectionMeetingPoints: LongSparseArray<ArrayList<TripMeetingPointEntity>> =
                LongSparseArray<ArrayList<TripMeetingPointEntity>>()
            val _collectionItinerary: LongSparseArray<ArrayList<ItineraryItemEntity>> =
                LongSparseArray<ArrayList<ItineraryItemEntity>>()
            val _collectionPaymentMethods: LongSparseArray<ArrayList<TripPaymentMethodEntity>> =
                LongSparseArray<ArrayList<TripPaymentMethodEntity>>()
            val _collectionInvites: LongSparseArray<ArrayList<ParticipantInviteEntity>> =
                LongSparseArray<ArrayList<ParticipantInviteEntity>>()
            while (_cursor.moveToNext()) {
              val _tmpKey: Long
              _tmpKey = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionRouteStops.containsKey(_tmpKey)) {
                _collectionRouteStops.put(_tmpKey, ArrayList<RouteStopEntity>())
              }
              val _tmpKey_1: Long
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionParticipants.containsKey(_tmpKey_1)) {
                _collectionParticipants.put(_tmpKey_1, ArrayList<TripParticipantEntity>())
              }
              val _tmpKey_2: Long
              _tmpKey_2 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionMeetingPoints.containsKey(_tmpKey_2)) {
                _collectionMeetingPoints.put(_tmpKey_2, ArrayList<TripMeetingPointEntity>())
              }
              val _tmpKey_3: Long
              _tmpKey_3 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionItinerary.containsKey(_tmpKey_3)) {
                _collectionItinerary.put(_tmpKey_3, ArrayList<ItineraryItemEntity>())
              }
              val _tmpKey_4: Long
              _tmpKey_4 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionPaymentMethods.containsKey(_tmpKey_4)) {
                _collectionPaymentMethods.put(_tmpKey_4, ArrayList<TripPaymentMethodEntity>())
              }
              val _tmpKey_5: Long
              _tmpKey_5 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionInvites.containsKey(_tmpKey_5)) {
                _collectionInvites.put(_tmpKey_5, ArrayList<ParticipantInviteEntity>())
              }
            }
            _cursor.moveToPosition(-1)
            __fetchRelationshiprouteStopsAscomExampleTripshareDataModelRouteStopEntity(_collectionRouteStops)
            __fetchRelationshiptripParticipantsAscomExampleTripshareDataModelTripParticipantEntity(_collectionParticipants)
            __fetchRelationshiptripMeetingPointsAscomExampleTripshareDataModelTripMeetingPointEntity(_collectionMeetingPoints)
            __fetchRelationshipitineraryItemsAscomExampleTripshareDataModelItineraryItemEntity(_collectionItinerary)
            __fetchRelationshiptripPaymentMethodsAscomExampleTripshareDataModelTripPaymentMethodEntity(_collectionPaymentMethods)
            __fetchRelationshipparticipantInvitesAscomExampleTripshareDataModelParticipantInviteEntity(_collectionInvites)
            val _result: TripFullAggregate?
            if (_cursor.moveToFirst()) {
              val _tmpTrip: TripEntity
              val _tmpId: Long
              _tmpId = _cursor.getLong(_cursorIndexOfId)
              val _tmpName: String
              _tmpName = _cursor.getString(_cursorIndexOfName)
              val _tmpFirebaseId: String?
              if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
                _tmpFirebaseId = null
              } else {
                _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
              }
              val _tmpOrganizerId: String
              _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
              val _tmpCategory: TripCategory
              val _tmp: String?
              if (_cursor.isNull(_cursorIndexOfCategory)) {
                _tmp = null
              } else {
                _tmp = _cursor.getString(_cursorIndexOfCategory)
              }
              val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
              if (_tmp_1 == null) {
                error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
              } else {
                _tmpCategory = _tmp_1
              }
              val _tmpVisibility: Visibility
              val _tmp_2: String?
              if (_cursor.isNull(_cursorIndexOfVisibility)) {
                _tmp_2 = null
              } else {
                _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
              }
              val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
              if (_tmp_3 == null) {
                error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
              } else {
                _tmpVisibility = _tmp_3
              }
              val _tmpStartDate: LocalDate?
              val _tmp_4: Long?
              if (_cursor.isNull(_cursorIndexOfStartDate)) {
                _tmp_4 = null
              } else {
                _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
              }
              _tmpStartDate = __converters.fromEpochDay(_tmp_4)
              val _tmpEndDate: LocalDate?
              val _tmp_5: Long?
              if (_cursor.isNull(_cursorIndexOfEndDate)) {
                _tmp_5 = null
              } else {
                _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
              }
              _tmpEndDate = __converters.fromEpochDay(_tmp_5)
              val _tmpMaxParticipants: Int
              _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
              val _tmpCostSharing: Boolean
              val _tmp_6: Int
              _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
              _tmpCostSharing = _tmp_6 != 0
              val _tmpPaymentDeadline: LocalDate?
              val _tmp_7: Long?
              if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
                _tmp_7 = null
              } else {
                _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
              }
              _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
              val _tmpWaitlistEnabled: Boolean
              val _tmp_8: Int
              _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
              _tmpWaitlistEnabled = _tmp_8 != 0
              val _tmpBudget: Double?
              if (_cursor.isNull(_cursorIndexOfBudget)) {
                _tmpBudget = null
              } else {
                _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
              }
              val _tmpBudgetDisplay: String?
              if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
                _tmpBudgetDisplay = null
              } else {
                _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
              }
              val _tmpDescription: String
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
              val _tmpCoverImgUrl: String?
              if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
                _tmpCoverImgUrl = null
              } else {
                _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
              }
              val _tmpIsArchived: Boolean
              val _tmp_9: Int
              _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
              _tmpIsArchived = _tmp_9 != 0
              _tmpTrip =
                  TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
              val _tmpRouteStopsCollection: ArrayList<RouteStopEntity>
              val _tmpKey_6: Long
              _tmpKey_6 = _cursor.getLong(_cursorIndexOfId)
              _tmpRouteStopsCollection = checkNotNull(_collectionRouteStops.get(_tmpKey_6))
              val _tmpParticipantsCollection: ArrayList<TripParticipantEntity>
              val _tmpKey_7: Long
              _tmpKey_7 = _cursor.getLong(_cursorIndexOfId)
              _tmpParticipantsCollection = checkNotNull(_collectionParticipants.get(_tmpKey_7))
              val _tmpMeetingPointsCollection: ArrayList<TripMeetingPointEntity>
              val _tmpKey_8: Long
              _tmpKey_8 = _cursor.getLong(_cursorIndexOfId)
              _tmpMeetingPointsCollection = checkNotNull(_collectionMeetingPoints.get(_tmpKey_8))
              val _tmpItineraryCollection: ArrayList<ItineraryItemEntity>
              val _tmpKey_9: Long
              _tmpKey_9 = _cursor.getLong(_cursorIndexOfId)
              _tmpItineraryCollection = checkNotNull(_collectionItinerary.get(_tmpKey_9))
              val _tmpPaymentMethodsCollection: ArrayList<TripPaymentMethodEntity>
              val _tmpKey_10: Long
              _tmpKey_10 = _cursor.getLong(_cursorIndexOfId)
              _tmpPaymentMethodsCollection = checkNotNull(_collectionPaymentMethods.get(_tmpKey_10))
              val _tmpInvitesCollection: ArrayList<ParticipantInviteEntity>
              val _tmpKey_11: Long
              _tmpKey_11 = _cursor.getLong(_cursorIndexOfId)
              _tmpInvitesCollection = checkNotNull(_collectionInvites.get(_tmpKey_11))
              _result =
                  TripFullAggregate(_tmpTrip,_tmpRouteStopsCollection,_tmpParticipantsCollection,_tmpMeetingPointsCollection,_tmpItineraryCollection,_tmpPaymentMethodsCollection,_tmpInvitesCollection)
            } else {
              _result = null
            }
            __db.setTransactionSuccessful()
            return _result
          } finally {
            _cursor.close()
          }
        } finally {
          __db.endTransaction()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeAllTripFull(): Flow<List<TripFullAggregate>> {
    val _sql: String = "SELECT * FROM trips ORDER BY id DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, true, arrayOf("route_stops", "trip_participants",
        "trip_meeting_points", "itinerary_items", "trip_payment_methods", "participant_invites",
        "trips"), object : Callable<List<TripFullAggregate>> {
      public override fun call(): List<TripFullAggregate> {
        __db.beginTransaction()
        try {
          val _cursor: Cursor = query(__db, _statement, true, null)
          try {
            val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
            val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
            val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
            val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
            val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
            val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
            val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
            val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
            val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor,
                "maxParticipants")
            val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
            val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor,
                "paymentDeadline")
            val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor,
                "waitlistEnabled")
            val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
            val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
            val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
            val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
            val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
            val _collectionRouteStops: LongSparseArray<ArrayList<RouteStopEntity>> =
                LongSparseArray<ArrayList<RouteStopEntity>>()
            val _collectionParticipants: LongSparseArray<ArrayList<TripParticipantEntity>> =
                LongSparseArray<ArrayList<TripParticipantEntity>>()
            val _collectionMeetingPoints: LongSparseArray<ArrayList<TripMeetingPointEntity>> =
                LongSparseArray<ArrayList<TripMeetingPointEntity>>()
            val _collectionItinerary: LongSparseArray<ArrayList<ItineraryItemEntity>> =
                LongSparseArray<ArrayList<ItineraryItemEntity>>()
            val _collectionPaymentMethods: LongSparseArray<ArrayList<TripPaymentMethodEntity>> =
                LongSparseArray<ArrayList<TripPaymentMethodEntity>>()
            val _collectionInvites: LongSparseArray<ArrayList<ParticipantInviteEntity>> =
                LongSparseArray<ArrayList<ParticipantInviteEntity>>()
            while (_cursor.moveToNext()) {
              val _tmpKey: Long
              _tmpKey = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionRouteStops.containsKey(_tmpKey)) {
                _collectionRouteStops.put(_tmpKey, ArrayList<RouteStopEntity>())
              }
              val _tmpKey_1: Long
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionParticipants.containsKey(_tmpKey_1)) {
                _collectionParticipants.put(_tmpKey_1, ArrayList<TripParticipantEntity>())
              }
              val _tmpKey_2: Long
              _tmpKey_2 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionMeetingPoints.containsKey(_tmpKey_2)) {
                _collectionMeetingPoints.put(_tmpKey_2, ArrayList<TripMeetingPointEntity>())
              }
              val _tmpKey_3: Long
              _tmpKey_3 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionItinerary.containsKey(_tmpKey_3)) {
                _collectionItinerary.put(_tmpKey_3, ArrayList<ItineraryItemEntity>())
              }
              val _tmpKey_4: Long
              _tmpKey_4 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionPaymentMethods.containsKey(_tmpKey_4)) {
                _collectionPaymentMethods.put(_tmpKey_4, ArrayList<TripPaymentMethodEntity>())
              }
              val _tmpKey_5: Long
              _tmpKey_5 = _cursor.getLong(_cursorIndexOfId)
              if (!_collectionInvites.containsKey(_tmpKey_5)) {
                _collectionInvites.put(_tmpKey_5, ArrayList<ParticipantInviteEntity>())
              }
            }
            _cursor.moveToPosition(-1)
            __fetchRelationshiprouteStopsAscomExampleTripshareDataModelRouteStopEntity(_collectionRouteStops)
            __fetchRelationshiptripParticipantsAscomExampleTripshareDataModelTripParticipantEntity(_collectionParticipants)
            __fetchRelationshiptripMeetingPointsAscomExampleTripshareDataModelTripMeetingPointEntity(_collectionMeetingPoints)
            __fetchRelationshipitineraryItemsAscomExampleTripshareDataModelItineraryItemEntity(_collectionItinerary)
            __fetchRelationshiptripPaymentMethodsAscomExampleTripshareDataModelTripPaymentMethodEntity(_collectionPaymentMethods)
            __fetchRelationshipparticipantInvitesAscomExampleTripshareDataModelParticipantInviteEntity(_collectionInvites)
            val _result: MutableList<TripFullAggregate> =
                ArrayList<TripFullAggregate>(_cursor.getCount())
            while (_cursor.moveToNext()) {
              val _item: TripFullAggregate
              val _tmpTrip: TripEntity
              val _tmpId: Long
              _tmpId = _cursor.getLong(_cursorIndexOfId)
              val _tmpName: String
              _tmpName = _cursor.getString(_cursorIndexOfName)
              val _tmpFirebaseId: String?
              if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
                _tmpFirebaseId = null
              } else {
                _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
              }
              val _tmpOrganizerId: String
              _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
              val _tmpCategory: TripCategory
              val _tmp: String?
              if (_cursor.isNull(_cursorIndexOfCategory)) {
                _tmp = null
              } else {
                _tmp = _cursor.getString(_cursorIndexOfCategory)
              }
              val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
              if (_tmp_1 == null) {
                error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
              } else {
                _tmpCategory = _tmp_1
              }
              val _tmpVisibility: Visibility
              val _tmp_2: String?
              if (_cursor.isNull(_cursorIndexOfVisibility)) {
                _tmp_2 = null
              } else {
                _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
              }
              val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
              if (_tmp_3 == null) {
                error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
              } else {
                _tmpVisibility = _tmp_3
              }
              val _tmpStartDate: LocalDate?
              val _tmp_4: Long?
              if (_cursor.isNull(_cursorIndexOfStartDate)) {
                _tmp_4 = null
              } else {
                _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
              }
              _tmpStartDate = __converters.fromEpochDay(_tmp_4)
              val _tmpEndDate: LocalDate?
              val _tmp_5: Long?
              if (_cursor.isNull(_cursorIndexOfEndDate)) {
                _tmp_5 = null
              } else {
                _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
              }
              _tmpEndDate = __converters.fromEpochDay(_tmp_5)
              val _tmpMaxParticipants: Int
              _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
              val _tmpCostSharing: Boolean
              val _tmp_6: Int
              _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
              _tmpCostSharing = _tmp_6 != 0
              val _tmpPaymentDeadline: LocalDate?
              val _tmp_7: Long?
              if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
                _tmp_7 = null
              } else {
                _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
              }
              _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
              val _tmpWaitlistEnabled: Boolean
              val _tmp_8: Int
              _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
              _tmpWaitlistEnabled = _tmp_8 != 0
              val _tmpBudget: Double?
              if (_cursor.isNull(_cursorIndexOfBudget)) {
                _tmpBudget = null
              } else {
                _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
              }
              val _tmpBudgetDisplay: String?
              if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
                _tmpBudgetDisplay = null
              } else {
                _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
              }
              val _tmpDescription: String
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
              val _tmpCoverImgUrl: String?
              if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
                _tmpCoverImgUrl = null
              } else {
                _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
              }
              val _tmpIsArchived: Boolean
              val _tmp_9: Int
              _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
              _tmpIsArchived = _tmp_9 != 0
              _tmpTrip =
                  TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
              val _tmpRouteStopsCollection: ArrayList<RouteStopEntity>
              val _tmpKey_6: Long
              _tmpKey_6 = _cursor.getLong(_cursorIndexOfId)
              _tmpRouteStopsCollection = checkNotNull(_collectionRouteStops.get(_tmpKey_6))
              val _tmpParticipantsCollection: ArrayList<TripParticipantEntity>
              val _tmpKey_7: Long
              _tmpKey_7 = _cursor.getLong(_cursorIndexOfId)
              _tmpParticipantsCollection = checkNotNull(_collectionParticipants.get(_tmpKey_7))
              val _tmpMeetingPointsCollection: ArrayList<TripMeetingPointEntity>
              val _tmpKey_8: Long
              _tmpKey_8 = _cursor.getLong(_cursorIndexOfId)
              _tmpMeetingPointsCollection = checkNotNull(_collectionMeetingPoints.get(_tmpKey_8))
              val _tmpItineraryCollection: ArrayList<ItineraryItemEntity>
              val _tmpKey_9: Long
              _tmpKey_9 = _cursor.getLong(_cursorIndexOfId)
              _tmpItineraryCollection = checkNotNull(_collectionItinerary.get(_tmpKey_9))
              val _tmpPaymentMethodsCollection: ArrayList<TripPaymentMethodEntity>
              val _tmpKey_10: Long
              _tmpKey_10 = _cursor.getLong(_cursorIndexOfId)
              _tmpPaymentMethodsCollection = checkNotNull(_collectionPaymentMethods.get(_tmpKey_10))
              val _tmpInvitesCollection: ArrayList<ParticipantInviteEntity>
              val _tmpKey_11: Long
              _tmpKey_11 = _cursor.getLong(_cursorIndexOfId)
              _tmpInvitesCollection = checkNotNull(_collectionInvites.get(_tmpKey_11))
              _item =
                  TripFullAggregate(_tmpTrip,_tmpRouteStopsCollection,_tmpParticipantsCollection,_tmpMeetingPointsCollection,_tmpItineraryCollection,_tmpPaymentMethodsCollection,_tmpInvitesCollection)
              _result.add(_item)
            }
            __db.setTransactionSuccessful()
            return _result
          } finally {
            _cursor.close()
          }
        } finally {
          __db.endTransaction()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeAllTrips(): Flow<List<TripEntity>> {
    val _sql: String = "SELECT * FROM trips ORDER BY id DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips"), object :
        Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeItinerary(tripId: Long): Flow<List<ItineraryItemEntity>> {
    val _sql: String = "SELECT * FROM itinerary_items WHERE tripId = ? ORDER BY time"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("itinerary_items"), object :
        Callable<List<ItineraryItemEntity>> {
      public override fun call(): List<ItineraryItemEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfDay: Int = getColumnIndexOrThrow(_cursor, "day")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfTime: Int = getColumnIndexOrThrow(_cursor, "time")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfNotes: Int = getColumnIndexOrThrow(_cursor, "notes")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfAttachment: Int = getColumnIndexOrThrow(_cursor, "attachment")
          val _cursorIndexOfAssignedTo: Int = getColumnIndexOrThrow(_cursor, "assignedTo")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfEndTime: Int = getColumnIndexOrThrow(_cursor, "endTime")
          val _cursorIndexOfLat: Int = getColumnIndexOrThrow(_cursor, "lat")
          val _cursorIndexOfLng: Int = getColumnIndexOrThrow(_cursor, "lng")
          val _result: MutableList<ItineraryItemEntity> =
              ArrayList<ItineraryItemEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: ItineraryItemEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpDay: Int
            _tmpDay = _cursor.getInt(_cursorIndexOfDay)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpTime: String
            _tmpTime = _cursor.getString(_cursorIndexOfTime)
            val _tmpLocation: String?
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            }
            val _tmpNotes: String?
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes)
            }
            val _tmpCategory: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmpAttachment: String?
            if (_cursor.isNull(_cursorIndexOfAttachment)) {
              _tmpAttachment = null
            } else {
              _tmpAttachment = _cursor.getString(_cursorIndexOfAttachment)
            }
            val _tmpAssignedTo: String?
            if (_cursor.isNull(_cursorIndexOfAssignedTo)) {
              _tmpAssignedTo = null
            } else {
              _tmpAssignedTo = _cursor.getString(_cursorIndexOfAssignedTo)
            }
            val _tmpEndDate: String?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null
            } else {
              _tmpEndDate = _cursor.getString(_cursorIndexOfEndDate)
            }
            val _tmpEndTime: String?
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmpEndTime = null
            } else {
              _tmpEndTime = _cursor.getString(_cursorIndexOfEndTime)
            }
            val _tmpLat: Double?
            if (_cursor.isNull(_cursorIndexOfLat)) {
              _tmpLat = null
            } else {
              _tmpLat = _cursor.getDouble(_cursorIndexOfLat)
            }
            val _tmpLng: Double?
            if (_cursor.isNull(_cursorIndexOfLng)) {
              _tmpLng = null
            } else {
              _tmpLng = _cursor.getDouble(_cursorIndexOfLng)
            }
            _item =
                ItineraryItemEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpDay,_tmpTitle,_tmpDate,_tmpTime,_tmpLocation,_tmpNotes,_tmpCategory,_tmpAttachment,_tmpAssignedTo,_tmpEndDate,_tmpEndTime,_tmpLat,_tmpLng)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun getItineraryById(id: Long): ItineraryItemEntity? {
    val _sql: String = "SELECT * FROM itinerary_items WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<ItineraryItemEntity?> {
      public override fun call(): ItineraryItemEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfDay: Int = getColumnIndexOrThrow(_cursor, "day")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfTime: Int = getColumnIndexOrThrow(_cursor, "time")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfNotes: Int = getColumnIndexOrThrow(_cursor, "notes")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfAttachment: Int = getColumnIndexOrThrow(_cursor, "attachment")
          val _cursorIndexOfAssignedTo: Int = getColumnIndexOrThrow(_cursor, "assignedTo")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfEndTime: Int = getColumnIndexOrThrow(_cursor, "endTime")
          val _cursorIndexOfLat: Int = getColumnIndexOrThrow(_cursor, "lat")
          val _cursorIndexOfLng: Int = getColumnIndexOrThrow(_cursor, "lng")
          val _result: ItineraryItemEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpDay: Int
            _tmpDay = _cursor.getInt(_cursorIndexOfDay)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpTime: String
            _tmpTime = _cursor.getString(_cursorIndexOfTime)
            val _tmpLocation: String?
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            }
            val _tmpNotes: String?
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes)
            }
            val _tmpCategory: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmpAttachment: String?
            if (_cursor.isNull(_cursorIndexOfAttachment)) {
              _tmpAttachment = null
            } else {
              _tmpAttachment = _cursor.getString(_cursorIndexOfAttachment)
            }
            val _tmpAssignedTo: String?
            if (_cursor.isNull(_cursorIndexOfAssignedTo)) {
              _tmpAssignedTo = null
            } else {
              _tmpAssignedTo = _cursor.getString(_cursorIndexOfAssignedTo)
            }
            val _tmpEndDate: String?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null
            } else {
              _tmpEndDate = _cursor.getString(_cursorIndexOfEndDate)
            }
            val _tmpEndTime: String?
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmpEndTime = null
            } else {
              _tmpEndTime = _cursor.getString(_cursorIndexOfEndTime)
            }
            val _tmpLat: Double?
            if (_cursor.isNull(_cursorIndexOfLat)) {
              _tmpLat = null
            } else {
              _tmpLat = _cursor.getDouble(_cursorIndexOfLat)
            }
            val _tmpLng: Double?
            if (_cursor.isNull(_cursorIndexOfLng)) {
              _tmpLng = null
            } else {
              _tmpLng = _cursor.getDouble(_cursorIndexOfLng)
            }
            _result =
                ItineraryItemEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpDay,_tmpTitle,_tmpDate,_tmpTime,_tmpLocation,_tmpNotes,_tmpCategory,_tmpAttachment,_tmpAssignedTo,_tmpEndDate,_tmpEndTime,_tmpLat,_tmpLng)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun observeParticipants(tripId: Long): Flow<List<TripParticipantEntity>> {
    val _sql: String = "SELECT * FROM trip_participants WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trip_participants"), object :
        Callable<List<TripParticipantEntity>> {
      public override fun call(): List<TripParticipantEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfEmail: Int = getColumnIndexOrThrow(_cursor, "email")
          val _cursorIndexOfDisplayName: Int = getColumnIndexOrThrow(_cursor, "displayName")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfRole: Int = getColumnIndexOrThrow(_cursor, "role")
          val _cursorIndexOfJoinedAt: Int = getColumnIndexOrThrow(_cursor, "joinedAt")
          val _result: MutableList<TripParticipantEntity> =
              ArrayList<TripParticipantEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripParticipantEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpEmail: String
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail)
            val _tmpDisplayName: String
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName)
            val _tmpStatus: ParticipationStatus
            _tmpStatus = __ParticipationStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus))
            val _tmpRole: ParticipantRole
            _tmpRole = __ParticipantRole_stringToEnum(_cursor.getString(_cursorIndexOfRole))
            val _tmpJoinedAt: Long
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt)
            _item =
                TripParticipantEntity(_tmpId,_tmpTripId,_tmpUserId,_tmpEmail,_tmpDisplayName,_tmpStatus,_tmpRole,_tmpJoinedAt)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun isUserParticipant(tripId: Long, userId: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM trip_participants WHERE tripId = ? AND userId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    _argIndex = 2
    _statement.bindLong(_argIndex, userId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Int> {
      public override fun call(): Int {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Int
          if (_cursor.moveToFirst()) {
            val _tmp: Int
            _tmp = _cursor.getInt(0)
            _result = _tmp
          } else {
            _result = 0
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun observeMeetingPoints(tripId: Long): Flow<List<TripMeetingPointEntity>> {
    val _sql: String = "SELECT * FROM trip_meeting_points WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trip_meeting_points"), object :
        Callable<List<TripMeetingPointEntity>> {
      public override fun call(): List<TripMeetingPointEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfLabel: Int = getColumnIndexOrThrow(_cursor, "label")
          val _cursorIndexOfLat: Int = getColumnIndexOrThrow(_cursor, "lat")
          val _cursorIndexOfLng: Int = getColumnIndexOrThrow(_cursor, "lng")
          val _result: MutableList<TripMeetingPointEntity> =
              ArrayList<TripMeetingPointEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripMeetingPointEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpLabel: String
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel)
            val _tmpLat: Double?
            if (_cursor.isNull(_cursorIndexOfLat)) {
              _tmpLat = null
            } else {
              _tmpLat = _cursor.getDouble(_cursorIndexOfLat)
            }
            val _tmpLng: Double?
            if (_cursor.isNull(_cursorIndexOfLng)) {
              _tmpLng = null
            } else {
              _tmpLng = _cursor.getDouble(_cursorIndexOfLng)
            }
            _item = TripMeetingPointEntity(_tmpId,_tmpTripId,_tmpLabel,_tmpLat,_tmpLng)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeArchivedTripsForUser(userId: Long): Flow<List<TripEntity>> {
    val _sql: String = """
        |
        |        SELECT t.* FROM trips t
        |        INNER JOIN trip_participants p ON p.tripId = t.id
        |        WHERE p.userId = ? AND t.isArchived = 1
        |        ORDER BY t.startDate DESC
        |        
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips", "trip_participants"), object :
        Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun getActiveTrips(): Flow<List<TripEntity>> {
    val _sql: String = "SELECT * FROM trips WHERE isArchived = 0"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips"), object :
        Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun getArchivedTrips(): Flow<List<TripEntity>> {
    val _sql: String = "SELECT * FROM trips WHERE isArchived = 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips"), object :
        Callable<List<TripEntity>> {
      public override fun call(): List<TripEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfOrganizerId: Int = getColumnIndexOrThrow(_cursor, "organizerId")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfVisibility: Int = getColumnIndexOrThrow(_cursor, "visibility")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfMaxParticipants: Int = getColumnIndexOrThrow(_cursor, "maxParticipants")
          val _cursorIndexOfCostSharing: Int = getColumnIndexOrThrow(_cursor, "costSharing")
          val _cursorIndexOfPaymentDeadline: Int = getColumnIndexOrThrow(_cursor, "paymentDeadline")
          val _cursorIndexOfWaitlistEnabled: Int = getColumnIndexOrThrow(_cursor, "waitlistEnabled")
          val _cursorIndexOfBudget: Int = getColumnIndexOrThrow(_cursor, "budget")
          val _cursorIndexOfBudgetDisplay: Int = getColumnIndexOrThrow(_cursor, "budgetDisplay")
          val _cursorIndexOfDescription: Int = getColumnIndexOrThrow(_cursor, "description")
          val _cursorIndexOfCoverImgUrl: Int = getColumnIndexOrThrow(_cursor, "coverImgUrl")
          val _cursorIndexOfIsArchived: Int = getColumnIndexOrThrow(_cursor, "isArchived")
          val _result: MutableList<TripEntity> = ArrayList<TripEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            val _tmpOrganizerId: String
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId)
            val _tmpCategory: TripCategory
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCategory)
            }
            val _tmp_1: TripCategory? = __converters.catFrom(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.TripCategory', but it was NULL.")
            } else {
              _tmpCategory = _tmp_1
            }
            val _tmpVisibility: Visibility
            val _tmp_2: String?
            if (_cursor.isNull(_cursorIndexOfVisibility)) {
              _tmp_2 = null
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfVisibility)
            }
            val _tmp_3: Visibility? = __converters.visFrom(_tmp_2)
            if (_tmp_3 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.Visibility', but it was NULL.")
            } else {
              _tmpVisibility = _tmp_3
            }
            val _tmpStartDate: LocalDate?
            val _tmp_4: Long?
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmp_4 = null
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfStartDate)
            }
            _tmpStartDate = __converters.fromEpochDay(_tmp_4)
            val _tmpEndDate: LocalDate?
            val _tmp_5: Long?
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_5 = null
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfEndDate)
            }
            _tmpEndDate = __converters.fromEpochDay(_tmp_5)
            val _tmpMaxParticipants: Int
            _tmpMaxParticipants = _cursor.getInt(_cursorIndexOfMaxParticipants)
            val _tmpCostSharing: Boolean
            val _tmp_6: Int
            _tmp_6 = _cursor.getInt(_cursorIndexOfCostSharing)
            _tmpCostSharing = _tmp_6 != 0
            val _tmpPaymentDeadline: LocalDate?
            val _tmp_7: Long?
            if (_cursor.isNull(_cursorIndexOfPaymentDeadline)) {
              _tmp_7 = null
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfPaymentDeadline)
            }
            _tmpPaymentDeadline = __converters.fromEpochDay(_tmp_7)
            val _tmpWaitlistEnabled: Boolean
            val _tmp_8: Int
            _tmp_8 = _cursor.getInt(_cursorIndexOfWaitlistEnabled)
            _tmpWaitlistEnabled = _tmp_8 != 0
            val _tmpBudget: Double?
            if (_cursor.isNull(_cursorIndexOfBudget)) {
              _tmpBudget = null
            } else {
              _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget)
            }
            val _tmpBudgetDisplay: String?
            if (_cursor.isNull(_cursorIndexOfBudgetDisplay)) {
              _tmpBudgetDisplay = null
            } else {
              _tmpBudgetDisplay = _cursor.getString(_cursorIndexOfBudgetDisplay)
            }
            val _tmpDescription: String
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription)
            val _tmpCoverImgUrl: String?
            if (_cursor.isNull(_cursorIndexOfCoverImgUrl)) {
              _tmpCoverImgUrl = null
            } else {
              _tmpCoverImgUrl = _cursor.getString(_cursorIndexOfCoverImgUrl)
            }
            val _tmpIsArchived: Boolean
            val _tmp_9: Int
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsArchived)
            _tmpIsArchived = _tmp_9 != 0
            _item =
                TripEntity(_tmpId,_tmpName,_tmpFirebaseId,_tmpOrganizerId,_tmpCategory,_tmpVisibility,_tmpStartDate,_tmpEndDate,_tmpMaxParticipants,_tmpCostSharing,_tmpPaymentDeadline,_tmpWaitlistEnabled,_tmpBudget,_tmpBudgetDisplay,_tmpDescription,_tmpCoverImgUrl,_tmpIsArchived)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun markArchived(ids: List<Long>): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stringBuilder: StringBuilder = newStringBuilder()
      _stringBuilder.append("UPDATE trips SET isArchived = 1 WHERE id IN (")
      val _inputSize: Int = ids.size
      appendPlaceholders(_stringBuilder, _inputSize)
      _stringBuilder.append(")")
      val _sql: String = _stringBuilder.toString()
      val _stmt: SupportSQLiteStatement = __db.compileStatement(_sql)
      var _argIndex: Int = 1
      for (_item: Long in ids) {
        _stmt.bindLong(_argIndex, _item)
        _argIndex++
      }
      __db.beginTransaction()
      try {
        _stmt.executeUpdateDelete()
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  private fun __StopType_enumToString(_value: StopType): String = when (_value) {
    StopType.START -> "START"
    StopType.END -> "END"
    StopType.STOP -> "STOP"
  }

  private fun __PaymentMethod_enumToString(_value: PaymentMethod): String = when (_value) {
    PaymentMethod.EWALLET -> "EWALLET"
    PaymentMethod.CASH -> "CASH"
    PaymentMethod.BANK_TRANSFER -> "BANK_TRANSFER"
  }

  private fun __InviteStatus_enumToString(_value: InviteStatus): String = when (_value) {
    InviteStatus.INVITED -> "INVITED"
    InviteStatus.ACCEPTED -> "ACCEPTED"
    InviteStatus.DECLINED -> "DECLINED"
  }

  private fun __ParticipationStatus_enumToString(_value: ParticipationStatus): String = when
      (_value) {
    ParticipationStatus.JOINED -> "JOINED"
    ParticipationStatus.INVITED -> "INVITED"
    ParticipationStatus.PENDING -> "PENDING"
    ParticipationStatus.LEFT -> "LEFT"
  }

  private fun __ParticipantRole_enumToString(_value: ParticipantRole): String = when (_value) {
    ParticipantRole.OWNER -> "OWNER"
    ParticipantRole.MEMBER -> "MEMBER"
  }

  private fun __ParticipationStatus_stringToEnum(_value: String): ParticipationStatus = when
      (_value) {
    "JOINED" -> ParticipationStatus.JOINED
    "INVITED" -> ParticipationStatus.INVITED
    "PENDING" -> ParticipationStatus.PENDING
    "LEFT" -> ParticipationStatus.LEFT
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __ParticipantRole_stringToEnum(_value: String): ParticipantRole = when (_value) {
    "OWNER" -> ParticipantRole.OWNER
    "MEMBER" -> ParticipantRole.MEMBER
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __StopType_stringToEnum(_value: String): StopType = when (_value) {
    "START" -> StopType.START
    "END" -> StopType.END
    "STOP" -> StopType.STOP
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private
      fun __fetchRelationshiprouteStopsAscomExampleTripshareDataModelRouteStopEntity(_map: LongSparseArray<ArrayList<RouteStopEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshiprouteStopsAscomExampleTripshareDataModelRouteStopEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`type`,`label`,`lat`,`lng`,`orderInRoute`,`startDate`,`endDate`,`nights` FROM `route_stops` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfType: Int = 2
      val _cursorIndexOfLabel: Int = 3
      val _cursorIndexOfLat: Int = 4
      val _cursorIndexOfLng: Int = 5
      val _cursorIndexOfOrderInRoute: Int = 6
      val _cursorIndexOfStartDate: Int = 7
      val _cursorIndexOfEndDate: Int = 8
      val _cursorIndexOfNights: Int = 9
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<RouteStopEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: RouteStopEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpType: StopType
          _tmpType = __StopType_stringToEnum(_cursor.getString(_cursorIndexOfType))
          val _tmpLabel: String
          _tmpLabel = _cursor.getString(_cursorIndexOfLabel)
          val _tmpLat: Double?
          if (_cursor.isNull(_cursorIndexOfLat)) {
            _tmpLat = null
          } else {
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat)
          }
          val _tmpLng: Double?
          if (_cursor.isNull(_cursorIndexOfLng)) {
            _tmpLng = null
          } else {
            _tmpLng = _cursor.getDouble(_cursorIndexOfLng)
          }
          val _tmpOrderInRoute: Int
          _tmpOrderInRoute = _cursor.getInt(_cursorIndexOfOrderInRoute)
          val _tmpStartDate: String?
          if (_cursor.isNull(_cursorIndexOfStartDate)) {
            _tmpStartDate = null
          } else {
            _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate)
          }
          val _tmpEndDate: String?
          if (_cursor.isNull(_cursorIndexOfEndDate)) {
            _tmpEndDate = null
          } else {
            _tmpEndDate = _cursor.getString(_cursorIndexOfEndDate)
          }
          val _tmpNights: Int
          _tmpNights = _cursor.getInt(_cursorIndexOfNights)
          _item_1 =
              RouteStopEntity(_tmpId,_tmpTripId,_tmpType,_tmpLabel,_tmpLat,_tmpLng,_tmpOrderInRoute,_tmpStartDate,_tmpEndDate,_tmpNights)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private
      fun __fetchRelationshiptripParticipantsAscomExampleTripshareDataModelTripParticipantEntity(_map: LongSparseArray<ArrayList<TripParticipantEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshiptripParticipantsAscomExampleTripshareDataModelTripParticipantEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`userId`,`email`,`displayName`,`status`,`role`,`joinedAt` FROM `trip_participants` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfUserId: Int = 2
      val _cursorIndexOfEmail: Int = 3
      val _cursorIndexOfDisplayName: Int = 4
      val _cursorIndexOfStatus: Int = 5
      val _cursorIndexOfRole: Int = 6
      val _cursorIndexOfJoinedAt: Int = 7
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<TripParticipantEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: TripParticipantEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpUserId: Long
          _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
          val _tmpEmail: String
          _tmpEmail = _cursor.getString(_cursorIndexOfEmail)
          val _tmpDisplayName: String
          _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName)
          val _tmpStatus: ParticipationStatus
          _tmpStatus = __ParticipationStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus))
          val _tmpRole: ParticipantRole
          _tmpRole = __ParticipantRole_stringToEnum(_cursor.getString(_cursorIndexOfRole))
          val _tmpJoinedAt: Long
          _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt)
          _item_1 =
              TripParticipantEntity(_tmpId,_tmpTripId,_tmpUserId,_tmpEmail,_tmpDisplayName,_tmpStatus,_tmpRole,_tmpJoinedAt)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private
      fun __fetchRelationshiptripMeetingPointsAscomExampleTripshareDataModelTripMeetingPointEntity(_map: LongSparseArray<ArrayList<TripMeetingPointEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshiptripMeetingPointsAscomExampleTripshareDataModelTripMeetingPointEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`label`,`lat`,`lng` FROM `trip_meeting_points` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfLabel: Int = 2
      val _cursorIndexOfLat: Int = 3
      val _cursorIndexOfLng: Int = 4
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<TripMeetingPointEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: TripMeetingPointEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpLabel: String
          _tmpLabel = _cursor.getString(_cursorIndexOfLabel)
          val _tmpLat: Double?
          if (_cursor.isNull(_cursorIndexOfLat)) {
            _tmpLat = null
          } else {
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat)
          }
          val _tmpLng: Double?
          if (_cursor.isNull(_cursorIndexOfLng)) {
            _tmpLng = null
          } else {
            _tmpLng = _cursor.getDouble(_cursorIndexOfLng)
          }
          _item_1 = TripMeetingPointEntity(_tmpId,_tmpTripId,_tmpLabel,_tmpLat,_tmpLng)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private
      fun __fetchRelationshipitineraryItemsAscomExampleTripshareDataModelItineraryItemEntity(_map: LongSparseArray<ArrayList<ItineraryItemEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshipitineraryItemsAscomExampleTripshareDataModelItineraryItemEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`firebaseId`,`day`,`title`,`date`,`time`,`location`,`notes`,`category`,`attachment`,`assignedTo`,`endDate`,`endTime`,`lat`,`lng` FROM `itinerary_items` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfFirebaseId: Int = 2
      val _cursorIndexOfDay: Int = 3
      val _cursorIndexOfTitle: Int = 4
      val _cursorIndexOfDate: Int = 5
      val _cursorIndexOfTime: Int = 6
      val _cursorIndexOfLocation: Int = 7
      val _cursorIndexOfNotes: Int = 8
      val _cursorIndexOfCategory: Int = 9
      val _cursorIndexOfAttachment: Int = 10
      val _cursorIndexOfAssignedTo: Int = 11
      val _cursorIndexOfEndDate: Int = 12
      val _cursorIndexOfEndTime: Int = 13
      val _cursorIndexOfLat: Int = 14
      val _cursorIndexOfLng: Int = 15
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<ItineraryItemEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: ItineraryItemEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpFirebaseId: String?
          if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
            _tmpFirebaseId = null
          } else {
            _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
          }
          val _tmpDay: Int
          _tmpDay = _cursor.getInt(_cursorIndexOfDay)
          val _tmpTitle: String
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
          val _tmpDate: String
          _tmpDate = _cursor.getString(_cursorIndexOfDate)
          val _tmpTime: String
          _tmpTime = _cursor.getString(_cursorIndexOfTime)
          val _tmpLocation: String?
          if (_cursor.isNull(_cursorIndexOfLocation)) {
            _tmpLocation = null
          } else {
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
          }
          val _tmpNotes: String?
          if (_cursor.isNull(_cursorIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes)
          }
          val _tmpCategory: String?
          if (_cursor.isNull(_cursorIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
          }
          val _tmpAttachment: String?
          if (_cursor.isNull(_cursorIndexOfAttachment)) {
            _tmpAttachment = null
          } else {
            _tmpAttachment = _cursor.getString(_cursorIndexOfAttachment)
          }
          val _tmpAssignedTo: String?
          if (_cursor.isNull(_cursorIndexOfAssignedTo)) {
            _tmpAssignedTo = null
          } else {
            _tmpAssignedTo = _cursor.getString(_cursorIndexOfAssignedTo)
          }
          val _tmpEndDate: String?
          if (_cursor.isNull(_cursorIndexOfEndDate)) {
            _tmpEndDate = null
          } else {
            _tmpEndDate = _cursor.getString(_cursorIndexOfEndDate)
          }
          val _tmpEndTime: String?
          if (_cursor.isNull(_cursorIndexOfEndTime)) {
            _tmpEndTime = null
          } else {
            _tmpEndTime = _cursor.getString(_cursorIndexOfEndTime)
          }
          val _tmpLat: Double?
          if (_cursor.isNull(_cursorIndexOfLat)) {
            _tmpLat = null
          } else {
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat)
          }
          val _tmpLng: Double?
          if (_cursor.isNull(_cursorIndexOfLng)) {
            _tmpLng = null
          } else {
            _tmpLng = _cursor.getDouble(_cursorIndexOfLng)
          }
          _item_1 =
              ItineraryItemEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpDay,_tmpTitle,_tmpDate,_tmpTime,_tmpLocation,_tmpNotes,_tmpCategory,_tmpAttachment,_tmpAssignedTo,_tmpEndDate,_tmpEndTime,_tmpLat,_tmpLng)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private fun __PaymentMethod_stringToEnum(_value: String): PaymentMethod = when (_value) {
    "EWALLET" -> PaymentMethod.EWALLET
    "CASH" -> PaymentMethod.CASH
    "BANK_TRANSFER" -> PaymentMethod.BANK_TRANSFER
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private
      fun __fetchRelationshiptripPaymentMethodsAscomExampleTripshareDataModelTripPaymentMethodEntity(_map: LongSparseArray<ArrayList<TripPaymentMethodEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshiptripPaymentMethodsAscomExampleTripshareDataModelTripPaymentMethodEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`method` FROM `trip_payment_methods` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfMethod: Int = 2
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<TripPaymentMethodEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: TripPaymentMethodEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpMethod: PaymentMethod
          _tmpMethod = __PaymentMethod_stringToEnum(_cursor.getString(_cursorIndexOfMethod))
          _item_1 = TripPaymentMethodEntity(_tmpId,_tmpTripId,_tmpMethod)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  private fun __InviteStatus_stringToEnum(_value: String): InviteStatus = when (_value) {
    "INVITED" -> InviteStatus.INVITED
    "ACCEPTED" -> InviteStatus.ACCEPTED
    "DECLINED" -> InviteStatus.DECLINED
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private
      fun __fetchRelationshipparticipantInvitesAscomExampleTripshareDataModelParticipantInviteEntity(_map: LongSparseArray<ArrayList<ParticipantInviteEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, true) {
        __fetchRelationshipparticipantInvitesAscomExampleTripshareDataModelParticipantInviteEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`tripId`,`identifier`,`status` FROM `participant_invites` WHERE `tripId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "tripId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfTripId: Int = 1
      val _cursorIndexOfIdentifier: Int = 2
      val _cursorIndexOfStatus: Int = 3
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        val _tmpRelation: ArrayList<ParticipantInviteEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: ParticipantInviteEntity
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpTripId: Long
          _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
          val _tmpIdentifier: String
          _tmpIdentifier = _cursor.getString(_cursorIndexOfIdentifier)
          val _tmpStatus: InviteStatus
          _tmpStatus = __InviteStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus))
          _item_1 = ParticipantInviteEntity(_tmpId,_tmpTripId,_tmpIdentifier,_tmpStatus)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
