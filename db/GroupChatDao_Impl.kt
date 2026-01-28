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
import com.example.tripshare.`data`.model.GroupMessageEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
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
public class GroupChatDao_Impl(
  __db: RoomDatabase,
) : GroupChatDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfGroupMessageEntity: EntityInsertionAdapter<GroupMessageEntity>

  private val __updateAdapterOfGroupMessageEntity: EntityDeletionOrUpdateAdapter<GroupMessageEntity>

  private val __preparedStmtOfClearMessagesForTrip: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfGroupMessageEntity = object :
        EntityInsertionAdapter<GroupMessageEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `group_messages` (`id`,`tripId`,`firebaseId`,`senderId`,`senderName`,`senderAvatar`,`content`,`type`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: GroupMessageEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindLong(4, entity.senderId)
        statement.bindString(5, entity.senderName)
        val _tmpSenderAvatar: String? = entity.senderAvatar
        if (_tmpSenderAvatar == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpSenderAvatar)
        }
        statement.bindString(7, entity.content)
        statement.bindString(8, entity.type)
        statement.bindLong(9, entity.timestamp)
      }
    }
    this.__updateAdapterOfGroupMessageEntity = object :
        EntityDeletionOrUpdateAdapter<GroupMessageEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `group_messages` SET `id` = ?,`tripId` = ?,`firebaseId` = ?,`senderId` = ?,`senderName` = ?,`senderAvatar` = ?,`content` = ?,`type` = ?,`timestamp` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: GroupMessageEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindLong(4, entity.senderId)
        statement.bindString(5, entity.senderName)
        val _tmpSenderAvatar: String? = entity.senderAvatar
        if (_tmpSenderAvatar == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpSenderAvatar)
        }
        statement.bindString(7, entity.content)
        statement.bindString(8, entity.type)
        statement.bindLong(9, entity.timestamp)
        statement.bindLong(10, entity.id)
      }
    }
    this.__preparedStmtOfClearMessagesForTrip = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM group_messages WHERE tripId = ?"
        return _query
      }
    }
  }

  public override suspend fun insertMessage(msg: GroupMessageEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfGroupMessageEntity.insertAndReturnId(msg)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateMessage(msg: GroupMessageEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfGroupMessageEntity.handle(msg)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun clearMessagesForTrip(tripId: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfClearMessagesForTrip.acquire()
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
        __preparedStmtOfClearMessagesForTrip.release(_stmt)
      }
    }
  })

  public override fun getMessagesForTrip(tripId: Long): Flow<List<GroupMessageEntity>> {
    val _sql: String = "SELECT * FROM group_messages WHERE tripId = ? ORDER BY timestamp ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("group_messages"), object :
        Callable<List<GroupMessageEntity>> {
      public override fun call(): List<GroupMessageEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfSenderId: Int = getColumnIndexOrThrow(_cursor, "senderId")
          val _cursorIndexOfSenderName: Int = getColumnIndexOrThrow(_cursor, "senderName")
          val _cursorIndexOfSenderAvatar: Int = getColumnIndexOrThrow(_cursor, "senderAvatar")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfType: Int = getColumnIndexOrThrow(_cursor, "type")
          val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_cursor, "timestamp")
          val _result: MutableList<GroupMessageEntity> =
              ArrayList<GroupMessageEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: GroupMessageEntity
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
            val _tmpSenderId: Long
            _tmpSenderId = _cursor.getLong(_cursorIndexOfSenderId)
            val _tmpSenderName: String
            _tmpSenderName = _cursor.getString(_cursorIndexOfSenderName)
            val _tmpSenderAvatar: String?
            if (_cursor.isNull(_cursorIndexOfSenderAvatar)) {
              _tmpSenderAvatar = null
            } else {
              _tmpSenderAvatar = _cursor.getString(_cursorIndexOfSenderAvatar)
            }
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpType: String
            _tmpType = _cursor.getString(_cursorIndexOfType)
            val _tmpTimestamp: Long
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp)
            _item =
                GroupMessageEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpSenderId,_tmpSenderName,_tmpSenderAvatar,_tmpContent,_tmpType,_tmpTimestamp)
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

  public override suspend fun getMessageByFirebaseId(fid: String): GroupMessageEntity? {
    val _sql: String = "SELECT * FROM group_messages WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<GroupMessageEntity?> {
      public override fun call(): GroupMessageEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfSenderId: Int = getColumnIndexOrThrow(_cursor, "senderId")
          val _cursorIndexOfSenderName: Int = getColumnIndexOrThrow(_cursor, "senderName")
          val _cursorIndexOfSenderAvatar: Int = getColumnIndexOrThrow(_cursor, "senderAvatar")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfType: Int = getColumnIndexOrThrow(_cursor, "type")
          val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_cursor, "timestamp")
          val _result: GroupMessageEntity?
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
            val _tmpSenderId: Long
            _tmpSenderId = _cursor.getLong(_cursorIndexOfSenderId)
            val _tmpSenderName: String
            _tmpSenderName = _cursor.getString(_cursorIndexOfSenderName)
            val _tmpSenderAvatar: String?
            if (_cursor.isNull(_cursorIndexOfSenderAvatar)) {
              _tmpSenderAvatar = null
            } else {
              _tmpSenderAvatar = _cursor.getString(_cursorIndexOfSenderAvatar)
            }
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpType: String
            _tmpType = _cursor.getString(_cursorIndexOfType)
            val _tmpTimestamp: Long
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp)
            _result =
                GroupMessageEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpSenderId,_tmpSenderName,_tmpSenderAvatar,_tmpContent,_tmpType,_tmpTimestamp)
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

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
