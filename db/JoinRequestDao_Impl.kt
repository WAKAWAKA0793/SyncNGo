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
import com.example.tripshare.`data`.model.JoinRequestEntity
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
public class JoinRequestDao_Impl(
  __db: RoomDatabase,
) : JoinRequestDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfJoinRequestEntity: EntityInsertionAdapter<JoinRequestEntity>

  private val __updateAdapterOfJoinRequestEntity: EntityDeletionOrUpdateAdapter<JoinRequestEntity>

  private val __preparedStmtOfDelete: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfJoinRequestEntity = object :
        EntityInsertionAdapter<JoinRequestEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `join_requests` (`id`,`tripId`,`userId`,`createdAt`,`status`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: JoinRequestEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindLong(3, entity.userId)
        statement.bindLong(4, entity.createdAt)
        statement.bindString(5, entity.status)
      }
    }
    this.__updateAdapterOfJoinRequestEntity = object :
        EntityDeletionOrUpdateAdapter<JoinRequestEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `join_requests` SET `id` = ?,`tripId` = ?,`userId` = ?,`createdAt` = ?,`status` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: JoinRequestEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindLong(3, entity.userId)
        statement.bindLong(4, entity.createdAt)
        statement.bindString(5, entity.status)
        statement.bindLong(6, entity.id)
      }
    }
    this.__preparedStmtOfDelete = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM join_requests WHERE id = ?"
        return _query
      }
    }
  }

  public override suspend fun insert(req: JoinRequestEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfJoinRequestEntity.insertAndReturnId(req)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun update(req: JoinRequestEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfJoinRequestEntity.handle(req)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun delete(id: Long): Unit = CoroutinesRoom.execute(__db, true, object :
      Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDelete.acquire()
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
        __preparedStmtOfDelete.release(_stmt)
      }
    }
  })

  public override fun observePending(tripId: Long): Flow<List<JoinRequestEntity>> {
    val _sql: String =
        "SELECT * FROM join_requests WHERE tripId = ? AND status = 'PENDING' ORDER BY createdAt ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("join_requests"), object :
        Callable<List<JoinRequestEntity>> {
      public override fun call(): List<JoinRequestEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _result: MutableList<JoinRequestEntity> =
              ArrayList<JoinRequestEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: JoinRequestEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpStatus: String
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus)
            _item = JoinRequestEntity(_tmpId,_tmpTripId,_tmpUserId,_tmpCreatedAt,_tmpStatus)
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

  public override suspend fun exists(tripId: Long, userId: Long): Boolean {
    val _sql: String = """
        |
        |        SELECT EXISTS(
        |            SELECT 1 FROM join_requests 
        |            WHERE tripId = ? AND userId = ? 
        |            LIMIT 1
        |        )
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    _argIndex = 2
    _statement.bindLong(_argIndex, userId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Boolean> {
      public override fun call(): Boolean {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Boolean
          if (_cursor.moveToFirst()) {
            val _tmp: Int
            _tmp = _cursor.getInt(0)
            _result = _tmp != 0
          } else {
            _result = false
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun observeByTrip(tripId: Long): Flow<List<JoinRequestEntity>> {
    val _sql: String = "SELECT * FROM join_requests WHERE tripId=?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("join_requests"), object :
        Callable<List<JoinRequestEntity>> {
      public override fun call(): List<JoinRequestEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _result: MutableList<JoinRequestEntity> =
              ArrayList<JoinRequestEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: JoinRequestEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpStatus: String
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus)
            _item = JoinRequestEntity(_tmpId,_tmpTripId,_tmpUserId,_tmpCreatedAt,_tmpStatus)
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
