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
import com.example.tripshare.`data`.model.Message
import com.example.tripshare.`data`.model.MessageStatus
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
public class MessageDao_Impl(
  __db: RoomDatabase,
) : MessageDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfMessage: EntityInsertionAdapter<Message>

  private val __converters: Converters = Converters()

  private val __insertionAdapterOfMessage_1: EntityInsertionAdapter<Message>

  private val __updateAdapterOfMessage: EntityDeletionOrUpdateAdapter<Message>

  private val __preparedStmtOfUpdateStatus: SharedSQLiteStatement

  private val __preparedStmtOfDeleteMessagesForChat: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfMessage = object : EntityInsertionAdapter<Message>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `messages` (`id`,`chatId`,`senderId`,`content`,`createdAt`,`status`,`edited`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: Message) {
        statement.bindString(1, entity.id)
        statement.bindString(2, entity.chatId)
        statement.bindString(3, entity.senderId)
        statement.bindString(4, entity.content)
        statement.bindLong(5, entity.createdAt)
        val _tmp: String? = __converters.statusToString(entity.status)
        if (_tmp == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmp)
        }
        val _tmp_1: Int = if (entity.edited) 1 else 0
        statement.bindLong(7, _tmp_1.toLong())
      }
    }
    this.__insertionAdapterOfMessage_1 = object : EntityInsertionAdapter<Message>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `messages` (`id`,`chatId`,`senderId`,`content`,`createdAt`,`status`,`edited`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: Message) {
        statement.bindString(1, entity.id)
        statement.bindString(2, entity.chatId)
        statement.bindString(3, entity.senderId)
        statement.bindString(4, entity.content)
        statement.bindLong(5, entity.createdAt)
        val _tmp: String? = __converters.statusToString(entity.status)
        if (_tmp == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmp)
        }
        val _tmp_1: Int = if (entity.edited) 1 else 0
        statement.bindLong(7, _tmp_1.toLong())
      }
    }
    this.__updateAdapterOfMessage = object : EntityDeletionOrUpdateAdapter<Message>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `messages` SET `id` = ?,`chatId` = ?,`senderId` = ?,`content` = ?,`createdAt` = ?,`status` = ?,`edited` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: Message) {
        statement.bindString(1, entity.id)
        statement.bindString(2, entity.chatId)
        statement.bindString(3, entity.senderId)
        statement.bindString(4, entity.content)
        statement.bindLong(5, entity.createdAt)
        val _tmp: String? = __converters.statusToString(entity.status)
        if (_tmp == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmp)
        }
        val _tmp_1: Int = if (entity.edited) 1 else 0
        statement.bindLong(7, _tmp_1.toLong())
        statement.bindString(8, entity.id)
      }
    }
    this.__preparedStmtOfUpdateStatus = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE messages SET status = ? WHERE id = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteMessagesForChat = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM messages WHERE chatId = ?"
        return _query
      }
    }
  }

  public override suspend fun insertMessage(message: Message): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfMessage.insert(message)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertMessages(messages: List<Message>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfMessage_1.insert(messages)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateMessage(message: Message): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfMessage.handle(message)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateStatus(messageId: String, status: MessageStatus): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUpdateStatus.acquire()
      var _argIndex: Int = 1
      val _tmp: String? = __converters.statusToString(status)
      if (_tmp == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindString(_argIndex, _tmp)
      }
      _argIndex = 2
      _stmt.bindString(_argIndex, messageId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfUpdateStatus.release(_stmt)
      }
    }
  })

  public override suspend fun deleteMessagesForChat(chatId: String): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteMessagesForChat.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, chatId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteMessagesForChat.release(_stmt)
      }
    }
  })

  public override suspend fun getMessagesByStatus(status: MessageStatus): List<Message> {
    val _sql: String = "SELECT * FROM messages WHERE status = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    val _tmp: String? = __converters.statusToString(status)
    if (_tmp == null) {
      _statement.bindNull(_argIndex)
    } else {
      _statement.bindString(_argIndex, _tmp)
    }
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<Message>> {
      public override fun call(): List<Message> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfChatId: Int = getColumnIndexOrThrow(_cursor, "chatId")
          val _cursorIndexOfSenderId: Int = getColumnIndexOrThrow(_cursor, "senderId")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfEdited: Int = getColumnIndexOrThrow(_cursor, "edited")
          val _result: MutableList<Message> = ArrayList<Message>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: Message
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpChatId: String
            _tmpChatId = _cursor.getString(_cursorIndexOfChatId)
            val _tmpSenderId: String
            _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId)
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpStatus: MessageStatus
            val _tmp_1: String?
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_1 = null
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfStatus)
            }
            val _tmp_2: MessageStatus? = __converters.stringToStatus(_tmp_1)
            if (_tmp_2 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.MessageStatus', but it was NULL.")
            } else {
              _tmpStatus = _tmp_2
            }
            val _tmpEdited: Boolean
            val _tmp_3: Int
            _tmp_3 = _cursor.getInt(_cursorIndexOfEdited)
            _tmpEdited = _tmp_3 != 0
            _item =
                Message(_tmpId,_tmpChatId,_tmpSenderId,_tmpContent,_tmpCreatedAt,_tmpStatus,_tmpEdited)
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

  public override fun observeMessagesForChat(chatId: String): Flow<List<Message>> {
    val _sql: String = "SELECT * FROM messages WHERE chatId = ? ORDER BY createdAt ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, chatId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("messages"), object :
        Callable<List<Message>> {
      public override fun call(): List<Message> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfChatId: Int = getColumnIndexOrThrow(_cursor, "chatId")
          val _cursorIndexOfSenderId: Int = getColumnIndexOrThrow(_cursor, "senderId")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfEdited: Int = getColumnIndexOrThrow(_cursor, "edited")
          val _result: MutableList<Message> = ArrayList<Message>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: Message
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpChatId: String
            _tmpChatId = _cursor.getString(_cursorIndexOfChatId)
            val _tmpSenderId: String
            _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId)
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpStatus: MessageStatus
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus)
            }
            val _tmp_1: MessageStatus? = __converters.stringToStatus(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.MessageStatus', but it was NULL.")
            } else {
              _tmpStatus = _tmp_1
            }
            val _tmpEdited: Boolean
            val _tmp_2: Int
            _tmp_2 = _cursor.getInt(_cursorIndexOfEdited)
            _tmpEdited = _tmp_2 != 0
            _item =
                Message(_tmpId,_tmpChatId,_tmpSenderId,_tmpContent,_tmpCreatedAt,_tmpStatus,_tmpEdited)
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

  public override suspend fun getMessageById(messageId: String): Message? {
    val _sql: String = "SELECT * FROM messages WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, messageId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Message?> {
      public override fun call(): Message? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfChatId: Int = getColumnIndexOrThrow(_cursor, "chatId")
          val _cursorIndexOfSenderId: Int = getColumnIndexOrThrow(_cursor, "senderId")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfEdited: Int = getColumnIndexOrThrow(_cursor, "edited")
          val _result: Message?
          if (_cursor.moveToFirst()) {
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpChatId: String
            _tmpChatId = _cursor.getString(_cursorIndexOfChatId)
            val _tmpSenderId: String
            _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId)
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpStatus: MessageStatus
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus)
            }
            val _tmp_1: MessageStatus? = __converters.stringToStatus(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.MessageStatus', but it was NULL.")
            } else {
              _tmpStatus = _tmp_1
            }
            val _tmpEdited: Boolean
            val _tmp_2: Int
            _tmp_2 = _cursor.getInt(_cursorIndexOfEdited)
            _tmpEdited = _tmp_2 != 0
            _result =
                Message(_tmpId,_tmpChatId,_tmpSenderId,_tmpContent,_tmpCreatedAt,_tmpStatus,_tmpEdited)
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

  public override suspend fun getMessagesForChatOnce(chatId: String): List<Message> {
    val _sql: String = "SELECT * FROM messages WHERE chatId = ? ORDER BY createdAt ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, chatId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<Message>> {
      public override fun call(): List<Message> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfChatId: Int = getColumnIndexOrThrow(_cursor, "chatId")
          val _cursorIndexOfSenderId: Int = getColumnIndexOrThrow(_cursor, "senderId")
          val _cursorIndexOfContent: Int = getColumnIndexOrThrow(_cursor, "content")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfEdited: Int = getColumnIndexOrThrow(_cursor, "edited")
          val _result: MutableList<Message> = ArrayList<Message>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: Message
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpChatId: String
            _tmpChatId = _cursor.getString(_cursorIndexOfChatId)
            val _tmpSenderId: String
            _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId)
            val _tmpContent: String
            _tmpContent = _cursor.getString(_cursorIndexOfContent)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpStatus: MessageStatus
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus)
            }
            val _tmp_1: MessageStatus? = __converters.stringToStatus(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.tripshare.`data`.model.MessageStatus', but it was NULL.")
            } else {
              _tmpStatus = _tmp_1
            }
            val _tmpEdited: Boolean
            val _tmp_2: Int
            _tmp_2 = _cursor.getInt(_cursorIndexOfEdited)
            _tmpEdited = _tmp_2 != 0
            _item =
                Message(_tmpId,_tmpChatId,_tmpSenderId,_tmpContent,_tmpCreatedAt,_tmpStatus,_tmpEdited)
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

  public override suspend fun countUnread(chatId: String, readStatus: MessageStatus): Int {
    val _sql: String = "SELECT COUNT(*) FROM messages WHERE chatId = ? AND status != ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, chatId)
    _argIndex = 2
    val _tmp: String? = __converters.statusToString(readStatus)
    if (_tmp == null) {
      _statement.bindNull(_argIndex)
    } else {
      _statement.bindString(_argIndex, _tmp)
    }
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Int> {
      public override fun call(): Int {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Int
          if (_cursor.moveToFirst()) {
            val _tmp_1: Int
            _tmp_1 = _cursor.getInt(0)
            _result = _tmp_1
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
