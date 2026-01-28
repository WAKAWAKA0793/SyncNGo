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
import com.example.tripshare.`data`.model.ChatRoom
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
public class ChatRoomDao_Impl(
  __db: RoomDatabase,
) : ChatRoomDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfChatRoom: EntityInsertionAdapter<ChatRoom>

  private val __updateAdapterOfChatRoom: EntityDeletionOrUpdateAdapter<ChatRoom>

  private val __preparedStmtOfDeleteRoom: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfChatRoom = object : EntityInsertionAdapter<ChatRoom>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `chat_rooms` (`id`,`title`,`userAId`,`userBId`,`lastMessage`,`updatedAt`,`isPrivate`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ChatRoom) {
        statement.bindString(1, entity.id)
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(2)
        } else {
          statement.bindString(2, _tmpTitle)
        }
        statement.bindString(3, entity.userAId)
        statement.bindString(4, entity.userBId)
        val _tmpLastMessage: String? = entity.lastMessage
        if (_tmpLastMessage == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmpLastMessage)
        }
        statement.bindLong(6, entity.updatedAt)
        val _tmp: Int = if (entity.isPrivate) 1 else 0
        statement.bindLong(7, _tmp.toLong())
      }
    }
    this.__updateAdapterOfChatRoom = object : EntityDeletionOrUpdateAdapter<ChatRoom>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `chat_rooms` SET `id` = ?,`title` = ?,`userAId` = ?,`userBId` = ?,`lastMessage` = ?,`updatedAt` = ?,`isPrivate` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ChatRoom) {
        statement.bindString(1, entity.id)
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(2)
        } else {
          statement.bindString(2, _tmpTitle)
        }
        statement.bindString(3, entity.userAId)
        statement.bindString(4, entity.userBId)
        val _tmpLastMessage: String? = entity.lastMessage
        if (_tmpLastMessage == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmpLastMessage)
        }
        statement.bindLong(6, entity.updatedAt)
        val _tmp: Int = if (entity.isPrivate) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindString(8, entity.id)
      }
    }
    this.__preparedStmtOfDeleteRoom = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM chat_rooms WHERE id = ?"
        return _query
      }
    }
  }

  public override suspend fun insertRoom(room: ChatRoom): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfChatRoom.insert(room)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateRoom(room: ChatRoom): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfChatRoom.handle(room)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteRoom(roomId: String): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteRoom.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, roomId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteRoom.release(_stmt)
      }
    }
  })

  public override suspend fun getRoomById(id: String): ChatRoom? {
    val _sql: String = "SELECT * FROM chat_rooms WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<ChatRoom?> {
      public override fun call(): ChatRoom? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfUserAId: Int = getColumnIndexOrThrow(_cursor, "userAId")
          val _cursorIndexOfUserBId: Int = getColumnIndexOrThrow(_cursor, "userBId")
          val _cursorIndexOfLastMessage: Int = getColumnIndexOrThrow(_cursor, "lastMessage")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updatedAt")
          val _cursorIndexOfIsPrivate: Int = getColumnIndexOrThrow(_cursor, "isPrivate")
          val _result: ChatRoom?
          if (_cursor.moveToFirst()) {
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpTitle: String?
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            }
            val _tmpUserAId: String
            _tmpUserAId = _cursor.getString(_cursorIndexOfUserAId)
            val _tmpUserBId: String
            _tmpUserBId = _cursor.getString(_cursorIndexOfUserBId)
            val _tmpLastMessage: String?
            if (_cursor.isNull(_cursorIndexOfLastMessage)) {
              _tmpLastMessage = null
            } else {
              _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage)
            }
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpIsPrivate: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsPrivate)
            _tmpIsPrivate = _tmp != 0
            _result =
                ChatRoom(_tmpId,_tmpTitle,_tmpUserAId,_tmpUserBId,_tmpLastMessage,_tmpUpdatedAt,_tmpIsPrivate)
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

  public override fun observeRoomsForUser(userId: String): Flow<List<ChatRoom>> {
    val _sql: String =
        "SELECT * FROM chat_rooms WHERE (userAId = ? OR userBId = ?) ORDER BY updatedAt DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, userId)
    _argIndex = 2
    _statement.bindString(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("chat_rooms"), object :
        Callable<List<ChatRoom>> {
      public override fun call(): List<ChatRoom> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfUserAId: Int = getColumnIndexOrThrow(_cursor, "userAId")
          val _cursorIndexOfUserBId: Int = getColumnIndexOrThrow(_cursor, "userBId")
          val _cursorIndexOfLastMessage: Int = getColumnIndexOrThrow(_cursor, "lastMessage")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updatedAt")
          val _cursorIndexOfIsPrivate: Int = getColumnIndexOrThrow(_cursor, "isPrivate")
          val _result: MutableList<ChatRoom> = ArrayList<ChatRoom>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: ChatRoom
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpTitle: String?
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            }
            val _tmpUserAId: String
            _tmpUserAId = _cursor.getString(_cursorIndexOfUserAId)
            val _tmpUserBId: String
            _tmpUserBId = _cursor.getString(_cursorIndexOfUserBId)
            val _tmpLastMessage: String?
            if (_cursor.isNull(_cursorIndexOfLastMessage)) {
              _tmpLastMessage = null
            } else {
              _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage)
            }
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpIsPrivate: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsPrivate)
            _tmpIsPrivate = _tmp != 0
            _item =
                ChatRoom(_tmpId,_tmpTitle,_tmpUserAId,_tmpUserBId,_tmpLastMessage,_tmpUpdatedAt,_tmpIsPrivate)
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

  public override suspend fun isUserMember(roomId: String, userId: String): Int {
    val _sql: String =
        "SELECT COUNT(1) FROM chat_rooms WHERE id = ? AND (userAId = ? OR userBId = ?)"
    val _statement: RoomSQLiteQuery = acquire(_sql, 3)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, roomId)
    _argIndex = 2
    _statement.bindString(_argIndex, userId)
    _argIndex = 3
    _statement.bindString(_argIndex, userId)
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

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
