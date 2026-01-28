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
import com.example.tripshare.`data`.model.RouteStopEntity
import com.example.tripshare.`data`.model.StopType
import java.lang.Class
import java.lang.IllegalArgumentException
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
public class RouteDao_Impl(
  __db: RoomDatabase,
) : RouteDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfRouteStopEntity: EntityInsertionAdapter<RouteStopEntity>

  private val __insertionAdapterOfRouteStopEntity_1: EntityInsertionAdapter<RouteStopEntity>

  private val __deletionAdapterOfRouteStopEntity: EntityDeletionOrUpdateAdapter<RouteStopEntity>

  private val __updateAdapterOfRouteStopEntity: EntityDeletionOrUpdateAdapter<RouteStopEntity>

  private val __preparedStmtOfIncrementNights: SharedSQLiteStatement

  private val __preparedStmtOfSetDates: SharedSQLiteStatement

  private val __preparedStmtOfDeleteByTripId: SharedSQLiteStatement
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
    this.__deletionAdapterOfRouteStopEntity = object :
        EntityDeletionOrUpdateAdapter<RouteStopEntity>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `route_stops` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: RouteStopEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfRouteStopEntity = object :
        EntityDeletionOrUpdateAdapter<RouteStopEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `route_stops` SET `id` = ?,`tripId` = ?,`type` = ?,`label` = ?,`lat` = ?,`lng` = ?,`orderInRoute` = ?,`startDate` = ?,`endDate` = ?,`nights` = ? WHERE `id` = ?"

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
        statement.bindLong(11, entity.id)
      }
    }
    this.__preparedStmtOfIncrementNights = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE route_stops SET nights = nights + ? WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfSetDates = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE route_stops SET startDate = ?, endDate = ? WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteByTripId = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM route_stops WHERE tripId = ?"
        return _query
      }
    }
  }

  public override suspend fun upsert(stop: RouteStopEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfRouteStopEntity.insertAndReturnId(stop)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertAll(stops: List<RouteStopEntity>): Unit =
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

  public override suspend fun delete(stop: RouteStopEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfRouteStopEntity.handle(stop)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun update(stop: RouteStopEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfRouteStopEntity.handle(stop)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun incrementNights(stopId: Long, delta: Int): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfIncrementNights.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, delta.toLong())
      _argIndex = 2
      _stmt.bindLong(_argIndex, stopId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfIncrementNights.release(_stmt)
      }
    }
  })

  public override suspend fun setDates(
    stopId: Long,
    start: String?,
    end: String?,
  ): Unit = CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSetDates.acquire()
      var _argIndex: Int = 1
      if (start == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindString(_argIndex, start)
      }
      _argIndex = 2
      if (end == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindString(_argIndex, end)
      }
      _argIndex = 3
      _stmt.bindLong(_argIndex, stopId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSetDates.release(_stmt)
      }
    }
  })

  public override suspend fun deleteByTripId(tripId: Long): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteByTripId.acquire()
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
        __preparedStmtOfDeleteByTripId.release(_stmt)
      }
    }
  })

  public override fun observeStops(tripId: Long): Flow<List<RouteStopEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM route_stops
        |        WHERE tripId = ?
        |        ORDER BY orderInRoute ASC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("route_stops"), object :
        Callable<List<RouteStopEntity>> {
      public override fun call(): List<RouteStopEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfType: Int = getColumnIndexOrThrow(_cursor, "type")
          val _cursorIndexOfLabel: Int = getColumnIndexOrThrow(_cursor, "label")
          val _cursorIndexOfLat: Int = getColumnIndexOrThrow(_cursor, "lat")
          val _cursorIndexOfLng: Int = getColumnIndexOrThrow(_cursor, "lng")
          val _cursorIndexOfOrderInRoute: Int = getColumnIndexOrThrow(_cursor, "orderInRoute")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfNights: Int = getColumnIndexOrThrow(_cursor, "nights")
          val _result: MutableList<RouteStopEntity> = ArrayList<RouteStopEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: RouteStopEntity
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
            _item =
                RouteStopEntity(_tmpId,_tmpTripId,_tmpType,_tmpLabel,_tmpLat,_tmpLng,_tmpOrderInRoute,_tmpStartDate,_tmpEndDate,_tmpNights)
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

  public override suspend fun getByTripId(tripId: Long): List<RouteStopEntity> {
    val _sql: String = "SELECT * FROM route_stops WHERE tripId = ? ORDER BY orderInRoute ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<RouteStopEntity>> {
      public override fun call(): List<RouteStopEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfType: Int = getColumnIndexOrThrow(_cursor, "type")
          val _cursorIndexOfLabel: Int = getColumnIndexOrThrow(_cursor, "label")
          val _cursorIndexOfLat: Int = getColumnIndexOrThrow(_cursor, "lat")
          val _cursorIndexOfLng: Int = getColumnIndexOrThrow(_cursor, "lng")
          val _cursorIndexOfOrderInRoute: Int = getColumnIndexOrThrow(_cursor, "orderInRoute")
          val _cursorIndexOfStartDate: Int = getColumnIndexOrThrow(_cursor, "startDate")
          val _cursorIndexOfEndDate: Int = getColumnIndexOrThrow(_cursor, "endDate")
          val _cursorIndexOfNights: Int = getColumnIndexOrThrow(_cursor, "nights")
          val _result: MutableList<RouteStopEntity> = ArrayList<RouteStopEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: RouteStopEntity
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
            _item =
                RouteStopEntity(_tmpId,_tmpTripId,_tmpType,_tmpLabel,_tmpLat,_tmpLng,_tmpOrderInRoute,_tmpStartDate,_tmpEndDate,_tmpNights)
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

  private fun __StopType_enumToString(_value: StopType): String = when (_value) {
    StopType.START -> "START"
    StopType.END -> "END"
    StopType.STOP -> "STOP"
  }

  private fun __StopType_stringToEnum(_value: String): StopType = when (_value) {
    "START" -> StopType.START
    "END" -> StopType.END
    "STOP" -> StopType.STOP
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
