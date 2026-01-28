package com.example.tripshare.`data`.db

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.SavedChecklistEntity
import com.example.tripshare.`data`.model.SavedChecklistItemEntity
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
public class SavedListDao_Impl(
  __db: RoomDatabase,
) : SavedListDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfSavedChecklistEntity: EntityInsertionAdapter<SavedChecklistEntity>

  private val __insertionAdapterOfSavedChecklistEntity_1:
      EntityInsertionAdapter<SavedChecklistEntity>

  private val __insertionAdapterOfSavedChecklistItemEntity:
      EntityInsertionAdapter<SavedChecklistItemEntity>

  private val __preparedStmtOfDelete: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfSavedChecklistEntity = object :
        EntityInsertionAdapter<SavedChecklistEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `saved_checklists` (`id`,`userId`,`name`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: SavedChecklistEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.userId)
        statement.bindString(3, entity.name)
      }
    }
    this.__insertionAdapterOfSavedChecklistEntity_1 = object :
        EntityInsertionAdapter<SavedChecklistEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `saved_checklists` (`id`,`userId`,`name`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: SavedChecklistEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.userId)
        statement.bindString(3, entity.name)
      }
    }
    this.__insertionAdapterOfSavedChecklistItemEntity = object :
        EntityInsertionAdapter<SavedChecklistItemEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `saved_checklist_items` (`id`,`checklistId`,`title`,`quantity`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: SavedChecklistItemEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.checklistId)
        statement.bindString(3, entity.title)
        statement.bindLong(4, entity.quantity.toLong())
      }
    }
    this.__preparedStmtOfDelete = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM saved_checklists WHERE id = ?"
        return _query
      }
    }
  }

  public override suspend fun insert(checklist: SavedChecklistEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfSavedChecklistEntity.insertAndReturnId(checklist)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertList(list: SavedChecklistEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfSavedChecklistEntity_1.insertAndReturnId(list)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertItems(items: List<SavedChecklistItemEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfSavedChecklistItemEntity.insert(items)
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

  public override fun observeSavedLists(userId: Long): Flow<List<SavedChecklistEntity>> {
    val _sql: String = "SELECT * FROM saved_checklists WHERE userId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("saved_checklists"), object :
        Callable<List<SavedChecklistEntity>> {
      public override fun call(): List<SavedChecklistEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _result: MutableList<SavedChecklistEntity> =
              ArrayList<SavedChecklistEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: SavedChecklistEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            _item = SavedChecklistEntity(_tmpId,_tmpUserId,_tmpName)
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

  public override suspend fun getItems(listId: Long): List<SavedChecklistItemEntity> {
    val _sql: String = "SELECT * FROM saved_checklist_items WHERE checklistId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, listId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object :
        Callable<List<SavedChecklistItemEntity>> {
      public override fun call(): List<SavedChecklistItemEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfChecklistId: Int = getColumnIndexOrThrow(_cursor, "checklistId")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfQuantity: Int = getColumnIndexOrThrow(_cursor, "quantity")
          val _result: MutableList<SavedChecklistItemEntity> =
              ArrayList<SavedChecklistItemEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: SavedChecklistItemEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpChecklistId: Long
            _tmpChecklistId = _cursor.getLong(_cursorIndexOfChecklistId)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpQuantity: Int
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity)
            _item = SavedChecklistItemEntity(_tmpId,_tmpChecklistId,_tmpTitle,_tmpQuantity)
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

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
