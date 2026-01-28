package com.example.tripshare.`data`.db

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.ItineraryItemEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
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
public class ItineraryDao_Impl(
  __db: RoomDatabase,
) : ItineraryDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfItineraryItemEntity: EntityInsertionAdapter<ItineraryItemEntity>

  private val __deletionAdapterOfItineraryItemEntity:
      EntityDeletionOrUpdateAdapter<ItineraryItemEntity>

  private val __updateAdapterOfItineraryItemEntity:
      EntityDeletionOrUpdateAdapter<ItineraryItemEntity>

  private val __preparedStmtOfDeleteById: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfItineraryItemEntity = object :
        EntityInsertionAdapter<ItineraryItemEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `itinerary_items` (`id`,`tripId`,`firebaseId`,`day`,`title`,`date`,`time`,`location`,`notes`,`category`,`attachment`,`assignedTo`,`endDate`,`endTime`,`lat`,`lng`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

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
    this.__deletionAdapterOfItineraryItemEntity = object :
        EntityDeletionOrUpdateAdapter<ItineraryItemEntity>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `itinerary_items` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ItineraryItemEntity) {
        statement.bindLong(1, entity.id)
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
    this.__preparedStmtOfDeleteById = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM itinerary_items WHERE id = ?"
        return _query
      }
    }
  }

  public override suspend fun insert(item: ItineraryItemEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
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

  public override suspend fun delete(item: ItineraryItemEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
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

  public override suspend fun update(item: ItineraryItemEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
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

  public override suspend fun deleteById(id: Long): Unit = CoroutinesRoom.execute(__db, true, object
      : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteById.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, id)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteById.release(_stmt)
      }
    }
  })

  public override fun observeItemsForTrip(tripId: Long): Flow<List<ItineraryItemEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM itinerary_items
        |        WHERE tripId = ?
        |        ORDER BY date ASC, time ASC
        |    
        """.trimMargin()
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

  public override suspend fun getByFirebaseId(fid: String): ItineraryItemEntity? {
    val _sql: String = "SELECT * FROM itinerary_items WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
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

  public override fun observePlans(tripId: Long): Flow<List<ItineraryItemEntity>> {
    val _sql: String = "SELECT * FROM itinerary_items WHERE tripId = ? ORDER BY date, time"
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

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
