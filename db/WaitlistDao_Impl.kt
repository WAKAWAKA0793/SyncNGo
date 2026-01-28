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
import com.example.tripshare.`data`.model.WaitlistEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class WaitlistDao_Impl(
  __db: RoomDatabase,
) : WaitlistDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfWaitlistEntity: EntityInsertionAdapter<WaitlistEntity>

  private val __deletionAdapterOfWaitlistEntity: EntityDeletionOrUpdateAdapter<WaitlistEntity>

  private val __updateAdapterOfWaitlistEntity: EntityDeletionOrUpdateAdapter<WaitlistEntity>

  private val __preparedStmtOfDeleteByUserId: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfWaitlistEntity = object : EntityInsertionAdapter<WaitlistEntity>(__db)
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `waitlist` (`id`,`tripId`,`firebaseId`,`tripName`,`location`,`date`,`position`,`alertsEnabled`,`tripImageUrl`,`userId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

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
    this.__deletionAdapterOfWaitlistEntity = object :
        EntityDeletionOrUpdateAdapter<WaitlistEntity>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `waitlist` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: WaitlistEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfWaitlistEntity = object :
        EntityDeletionOrUpdateAdapter<WaitlistEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `waitlist` SET `id` = ?,`tripId` = ?,`firebaseId` = ?,`tripName` = ?,`location` = ?,`date` = ?,`position` = ?,`alertsEnabled` = ?,`tripImageUrl` = ?,`userId` = ? WHERE `id` = ?"

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
        statement.bindLong(11, entity.id)
      }
    }
    this.__preparedStmtOfDeleteByUserId = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM waitlist WHERE tripId = ? AND userId = ?"
        return _query
      }
    }
  }

  public override suspend fun insert(entry: WaitlistEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfWaitlistEntity.insert(entry)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun delete(entry: WaitlistEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfWaitlistEntity.handle(entry)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun update(entry: WaitlistEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfWaitlistEntity.handle(entry)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteByUserId(tripId: Long, userId: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteByUserId.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, tripId)
      _argIndex = 2
      _stmt.bindLong(_argIndex, userId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteByUserId.release(_stmt)
      }
    }
  })

  public override fun observeForUser(userId: Long): Flow<List<WaitlistEntity>> {
    val _sql: String = """
        |
        |    SELECT * FROM waitlist
        |    WHERE userId = ?
        |    ORDER BY id DESC
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("waitlist"), object :
        Callable<List<WaitlistEntity>> {
      public override fun call(): List<WaitlistEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripName: Int = getColumnIndexOrThrow(_cursor, "tripName")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfPosition: Int = getColumnIndexOrThrow(_cursor, "position")
          val _cursorIndexOfAlertsEnabled: Int = getColumnIndexOrThrow(_cursor, "alertsEnabled")
          val _cursorIndexOfTripImageUrl: Int = getColumnIndexOrThrow(_cursor, "tripImageUrl")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _result: MutableList<WaitlistEntity> = ArrayList<WaitlistEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: WaitlistEntity
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
            val _tmpTripName: String
            _tmpTripName = _cursor.getString(_cursorIndexOfTripName)
            val _tmpLocation: String
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpPosition: Int
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition)
            val _tmpAlertsEnabled: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfAlertsEnabled)
            _tmpAlertsEnabled = _tmp != 0
            val _tmpTripImageUrl: String?
            if (_cursor.isNull(_cursorIndexOfTripImageUrl)) {
              _tmpTripImageUrl = null
            } else {
              _tmpTripImageUrl = _cursor.getString(_cursorIndexOfTripImageUrl)
            }
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            _item =
                WaitlistEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpTripName,_tmpLocation,_tmpDate,_tmpPosition,_tmpAlertsEnabled,_tmpTripImageUrl,_tmpUserId)
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

  public override suspend fun getByFirebaseId(fid: String): WaitlistEntity? {
    val _sql: String = "SELECT * FROM waitlist WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<WaitlistEntity?> {
      public override fun call(): WaitlistEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripName: Int = getColumnIndexOrThrow(_cursor, "tripName")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfPosition: Int = getColumnIndexOrThrow(_cursor, "position")
          val _cursorIndexOfAlertsEnabled: Int = getColumnIndexOrThrow(_cursor, "alertsEnabled")
          val _cursorIndexOfTripImageUrl: Int = getColumnIndexOrThrow(_cursor, "tripImageUrl")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _result: WaitlistEntity?
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
            val _tmpTripName: String
            _tmpTripName = _cursor.getString(_cursorIndexOfTripName)
            val _tmpLocation: String
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpPosition: Int
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition)
            val _tmpAlertsEnabled: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfAlertsEnabled)
            _tmpAlertsEnabled = _tmp != 0
            val _tmpTripImageUrl: String?
            if (_cursor.isNull(_cursorIndexOfTripImageUrl)) {
              _tmpTripImageUrl = null
            } else {
              _tmpTripImageUrl = _cursor.getString(_cursorIndexOfTripImageUrl)
            }
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            _result =
                WaitlistEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpTripName,_tmpLocation,_tmpDate,_tmpPosition,_tmpAlertsEnabled,_tmpTripImageUrl,_tmpUserId)
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

  public override suspend fun getForTrip(tripId: Long): List<WaitlistEntity> {
    val _sql: String = "SELECT * FROM waitlist WHERE tripId = ? ORDER BY position ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<WaitlistEntity>> {
      public override fun call(): List<WaitlistEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripName: Int = getColumnIndexOrThrow(_cursor, "tripName")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfPosition: Int = getColumnIndexOrThrow(_cursor, "position")
          val _cursorIndexOfAlertsEnabled: Int = getColumnIndexOrThrow(_cursor, "alertsEnabled")
          val _cursorIndexOfTripImageUrl: Int = getColumnIndexOrThrow(_cursor, "tripImageUrl")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _result: MutableList<WaitlistEntity> = ArrayList<WaitlistEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: WaitlistEntity
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
            val _tmpTripName: String
            _tmpTripName = _cursor.getString(_cursorIndexOfTripName)
            val _tmpLocation: String
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpPosition: Int
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition)
            val _tmpAlertsEnabled: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfAlertsEnabled)
            _tmpAlertsEnabled = _tmp != 0
            val _tmpTripImageUrl: String?
            if (_cursor.isNull(_cursorIndexOfTripImageUrl)) {
              _tmpTripImageUrl = null
            } else {
              _tmpTripImageUrl = _cursor.getString(_cursorIndexOfTripImageUrl)
            }
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            _item =
                WaitlistEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpTripName,_tmpLocation,_tmpDate,_tmpPosition,_tmpAlertsEnabled,_tmpTripImageUrl,_tmpUserId)
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

  public override suspend fun getMaxParticipants(tripId: Long): Int {
    val _sql: String = "SELECT maxParticipants FROM trips WHERE id = ?"
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
            _result = _cursor.getInt(0)
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

  public override fun observeForTrip(tripId: Long): Flow<List<WaitlistEntity>> {
    val _sql: String = "SELECT * FROM waitlist WHERE tripId = ? ORDER BY position ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("waitlist"), object :
        Callable<List<WaitlistEntity>> {
      public override fun call(): List<WaitlistEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripName: Int = getColumnIndexOrThrow(_cursor, "tripName")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfPosition: Int = getColumnIndexOrThrow(_cursor, "position")
          val _cursorIndexOfAlertsEnabled: Int = getColumnIndexOrThrow(_cursor, "alertsEnabled")
          val _cursorIndexOfTripImageUrl: Int = getColumnIndexOrThrow(_cursor, "tripImageUrl")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _result: MutableList<WaitlistEntity> = ArrayList<WaitlistEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: WaitlistEntity
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
            val _tmpTripName: String
            _tmpTripName = _cursor.getString(_cursorIndexOfTripName)
            val _tmpLocation: String
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpPosition: Int
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition)
            val _tmpAlertsEnabled: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfAlertsEnabled)
            _tmpAlertsEnabled = _tmp != 0
            val _tmpTripImageUrl: String?
            if (_cursor.isNull(_cursorIndexOfTripImageUrl)) {
              _tmpTripImageUrl = null
            } else {
              _tmpTripImageUrl = _cursor.getString(_cursorIndexOfTripImageUrl)
            }
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            _item =
                WaitlistEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpTripName,_tmpLocation,_tmpDate,_tmpPosition,_tmpAlertsEnabled,_tmpTripImageUrl,_tmpUserId)
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

  public override suspend fun findByUser(tripId: Long, userId: Long): WaitlistEntity? {
    val _sql: String = "SELECT * FROM waitlist WHERE tripId = ? AND userId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    _argIndex = 2
    _statement.bindLong(_argIndex, userId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<WaitlistEntity?> {
      public override fun call(): WaitlistEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripName: Int = getColumnIndexOrThrow(_cursor, "tripName")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfPosition: Int = getColumnIndexOrThrow(_cursor, "position")
          val _cursorIndexOfAlertsEnabled: Int = getColumnIndexOrThrow(_cursor, "alertsEnabled")
          val _cursorIndexOfTripImageUrl: Int = getColumnIndexOrThrow(_cursor, "tripImageUrl")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _result: WaitlistEntity?
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
            val _tmpTripName: String
            _tmpTripName = _cursor.getString(_cursorIndexOfTripName)
            val _tmpLocation: String
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpPosition: Int
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition)
            val _tmpAlertsEnabled: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfAlertsEnabled)
            _tmpAlertsEnabled = _tmp != 0
            val _tmpTripImageUrl: String?
            if (_cursor.isNull(_cursorIndexOfTripImageUrl)) {
              _tmpTripImageUrl = null
            } else {
              _tmpTripImageUrl = _cursor.getString(_cursorIndexOfTripImageUrl)
            }
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            _result =
                WaitlistEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpTripName,_tmpLocation,_tmpDate,_tmpPosition,_tmpAlertsEnabled,_tmpTripImageUrl,_tmpUserId)
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

  public override suspend fun countForTrip(tripId: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM waitlist WHERE tripId = ?"
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

  public override fun observeWaitlist(tripId: Long): Flow<List<WaitlistEntity>> {
    val _sql: String = "SELECT * FROM waitlist WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("waitlist"), object :
        Callable<List<WaitlistEntity>> {
      public override fun call(): List<WaitlistEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfTripName: Int = getColumnIndexOrThrow(_cursor, "tripName")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_cursor, "date")
          val _cursorIndexOfPosition: Int = getColumnIndexOrThrow(_cursor, "position")
          val _cursorIndexOfAlertsEnabled: Int = getColumnIndexOrThrow(_cursor, "alertsEnabled")
          val _cursorIndexOfTripImageUrl: Int = getColumnIndexOrThrow(_cursor, "tripImageUrl")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _result: MutableList<WaitlistEntity> = ArrayList<WaitlistEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: WaitlistEntity
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
            val _tmpTripName: String
            _tmpTripName = _cursor.getString(_cursorIndexOfTripName)
            val _tmpLocation: String
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
            val _tmpDate: String
            _tmpDate = _cursor.getString(_cursorIndexOfDate)
            val _tmpPosition: Int
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition)
            val _tmpAlertsEnabled: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfAlertsEnabled)
            _tmpAlertsEnabled = _tmp != 0
            val _tmpTripImageUrl: String?
            if (_cursor.isNull(_cursorIndexOfTripImageUrl)) {
              _tmpTripImageUrl = null
            } else {
              _tmpTripImageUrl = _cursor.getString(_cursorIndexOfTripImageUrl)
            }
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            _item =
                WaitlistEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpTripName,_tmpLocation,_tmpDate,_tmpPosition,_tmpAlertsEnabled,_tmpTripImageUrl,_tmpUserId)
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
