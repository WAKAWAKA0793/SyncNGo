package com.example.tripshare.`data`.db

import android.database.Cursor
import androidx.room.CoroutinesRoom
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.LocalNotificationEntity
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
public class LocalNotificationDao_Impl(
  __db: RoomDatabase,
) : LocalNotificationDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfLocalNotificationEntity:
      EntityInsertionAdapter<LocalNotificationEntity>

  private val __deletionAdapterOfLocalNotificationEntity:
      EntityDeletionOrUpdateAdapter<LocalNotificationEntity>

  private val __preparedStmtOfMarkRead: SharedSQLiteStatement

  private val __preparedStmtOfClearAll: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfLocalNotificationEntity = object :
        EntityInsertionAdapter<LocalNotificationEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `local_notifications` (`id`,`recipientId`,`type`,`title`,`body`,`relatedId`,`timestamp`,`isRead`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: LocalNotificationEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.recipientId)
        statement.bindString(3, entity.type)
        statement.bindString(4, entity.title)
        statement.bindString(5, entity.body)
        val _tmpRelatedId: Long? = entity.relatedId
        if (_tmpRelatedId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpRelatedId)
        }
        statement.bindLong(7, entity.timestamp)
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(8, _tmp.toLong())
      }
    }
    this.__deletionAdapterOfLocalNotificationEntity = object :
        EntityDeletionOrUpdateAdapter<LocalNotificationEntity>(__db) {
      protected override fun createQuery(): String =
          "DELETE FROM `local_notifications` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: LocalNotificationEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__preparedStmtOfMarkRead = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE local_notifications SET isRead = 1 WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfClearAll = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM local_notifications"
        return _query
      }
    }
  }

  public override suspend fun insert(n: LocalNotificationEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfLocalNotificationEntity.insert(n)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun delete(n: LocalNotificationEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfLocalNotificationEntity.handle(n)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun markRead(id: Long): Unit = CoroutinesRoom.execute(__db, true, object :
      Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfMarkRead.acquire()
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
        __preparedStmtOfMarkRead.release(_stmt)
      }
    }
  })

  public override suspend fun clearAll(): Unit = CoroutinesRoom.execute(__db, true, object :
      Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfClearAll.acquire()
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfClearAll.release(_stmt)
      }
    }
  })

  public override fun observeForUser(userId: Long): Flow<List<LocalNotificationEntity>> {
    val _sql: String =
        "SELECT * FROM local_notifications WHERE recipientId = ? ORDER BY timestamp DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("local_notifications"), object :
        Callable<List<LocalNotificationEntity>> {
      public override fun call(): List<LocalNotificationEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfRecipientId: Int = getColumnIndexOrThrow(_cursor, "recipientId")
          val _cursorIndexOfType: Int = getColumnIndexOrThrow(_cursor, "type")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfBody: Int = getColumnIndexOrThrow(_cursor, "body")
          val _cursorIndexOfRelatedId: Int = getColumnIndexOrThrow(_cursor, "relatedId")
          val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_cursor, "timestamp")
          val _cursorIndexOfIsRead: Int = getColumnIndexOrThrow(_cursor, "isRead")
          val _result: MutableList<LocalNotificationEntity> =
              ArrayList<LocalNotificationEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: LocalNotificationEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpRecipientId: Long
            _tmpRecipientId = _cursor.getLong(_cursorIndexOfRecipientId)
            val _tmpType: String
            _tmpType = _cursor.getString(_cursorIndexOfType)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpBody: String
            _tmpBody = _cursor.getString(_cursorIndexOfBody)
            val _tmpRelatedId: Long?
            if (_cursor.isNull(_cursorIndexOfRelatedId)) {
              _tmpRelatedId = null
            } else {
              _tmpRelatedId = _cursor.getLong(_cursorIndexOfRelatedId)
            }
            val _tmpTimestamp: Long
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp)
            val _tmpIsRead: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsRead)
            _tmpIsRead = _tmp != 0
            _item =
                LocalNotificationEntity(_tmpId,_tmpRecipientId,_tmpType,_tmpTitle,_tmpBody,_tmpRelatedId,_tmpTimestamp,_tmpIsRead)
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
