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
import com.example.tripshare.`data`.model.EmergencyContactEntity
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
public class EmergencyContactDao_Impl(
  __db: RoomDatabase,
) : EmergencyContactDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfEmergencyContactEntity:
      EntityInsertionAdapter<EmergencyContactEntity>

  private val __insertionAdapterOfEmergencyContactEntity_1:
      EntityInsertionAdapter<EmergencyContactEntity>

  private val __deletionAdapterOfEmergencyContactEntity:
      EntityDeletionOrUpdateAdapter<EmergencyContactEntity>

  private val __updateAdapterOfEmergencyContactEntity:
      EntityDeletionOrUpdateAdapter<EmergencyContactEntity>

  private val __preparedStmtOfDeleteByUserId: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfEmergencyContactEntity = object :
        EntityInsertionAdapter<EmergencyContactEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `emergency_contacts` (`id`,`userId`,`name`,`relationship`,`phone`,`firebaseId`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: EmergencyContactEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.userId)
        statement.bindString(3, entity.name)
        statement.bindString(4, entity.relationship)
        statement.bindString(5, entity.phone)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpFirebaseId)
        }
      }
    }
    this.__insertionAdapterOfEmergencyContactEntity_1 = object :
        EntityInsertionAdapter<EmergencyContactEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `emergency_contacts` (`id`,`userId`,`name`,`relationship`,`phone`,`firebaseId`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: EmergencyContactEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.userId)
        statement.bindString(3, entity.name)
        statement.bindString(4, entity.relationship)
        statement.bindString(5, entity.phone)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpFirebaseId)
        }
      }
    }
    this.__deletionAdapterOfEmergencyContactEntity = object :
        EntityDeletionOrUpdateAdapter<EmergencyContactEntity>(__db) {
      protected override fun createQuery(): String =
          "DELETE FROM `emergency_contacts` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: EmergencyContactEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfEmergencyContactEntity = object :
        EntityDeletionOrUpdateAdapter<EmergencyContactEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `emergency_contacts` SET `id` = ?,`userId` = ?,`name` = ?,`relationship` = ?,`phone` = ?,`firebaseId` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: EmergencyContactEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.userId)
        statement.bindString(3, entity.name)
        statement.bindString(4, entity.relationship)
        statement.bindString(5, entity.phone)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpFirebaseId)
        }
        statement.bindLong(7, entity.id)
      }
    }
    this.__preparedStmtOfDeleteByUserId = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM emergency_contacts WHERE userId=?"
        return _query
      }
    }
  }

  public override suspend fun insertAll(list: List<EmergencyContactEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfEmergencyContactEntity.insert(list)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insert(contact: EmergencyContactEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfEmergencyContactEntity_1.insertAndReturnId(contact)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun delete(contact: EmergencyContactEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfEmergencyContactEntity.handle(contact)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun update(contact: EmergencyContactEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfEmergencyContactEntity.handle(contact)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteByUserId(userId: Long): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteByUserId.acquire()
      var _argIndex: Int = 1
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

  public override fun observeByUserId(userId: Long): Flow<List<EmergencyContactEntity>> {
    val _sql: String = "SELECT * FROM emergency_contacts WHERE userId=?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("emergency_contacts"), object :
        Callable<List<EmergencyContactEntity>> {
      public override fun call(): List<EmergencyContactEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfRelationship: Int = getColumnIndexOrThrow(_cursor, "relationship")
          val _cursorIndexOfPhone: Int = getColumnIndexOrThrow(_cursor, "phone")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _result: MutableList<EmergencyContactEntity> =
              ArrayList<EmergencyContactEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: EmergencyContactEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpRelationship: String
            _tmpRelationship = _cursor.getString(_cursorIndexOfRelationship)
            val _tmpPhone: String
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            _item =
                EmergencyContactEntity(_tmpId,_tmpUserId,_tmpName,_tmpRelationship,_tmpPhone,_tmpFirebaseId)
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

  public override suspend fun getByUserId(userId: Long): List<EmergencyContactEntity> {
    val _sql: String = "SELECT * FROM emergency_contacts WHERE userId=?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<EmergencyContactEntity>>
        {
      public override fun call(): List<EmergencyContactEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfRelationship: Int = getColumnIndexOrThrow(_cursor, "relationship")
          val _cursorIndexOfPhone: Int = getColumnIndexOrThrow(_cursor, "phone")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _result: MutableList<EmergencyContactEntity> =
              ArrayList<EmergencyContactEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: EmergencyContactEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpRelationship: String
            _tmpRelationship = _cursor.getString(_cursorIndexOfRelationship)
            val _tmpPhone: String
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            _item =
                EmergencyContactEntity(_tmpId,_tmpUserId,_tmpName,_tmpRelationship,_tmpPhone,_tmpFirebaseId)
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

  public override suspend fun getByFirebaseId(fid: String): EmergencyContactEntity? {
    val _sql: String = "SELECT * FROM emergency_contacts WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<EmergencyContactEntity?> {
      public override fun call(): EmergencyContactEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfRelationship: Int = getColumnIndexOrThrow(_cursor, "relationship")
          val _cursorIndexOfPhone: Int = getColumnIndexOrThrow(_cursor, "phone")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _result: EmergencyContactEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpRelationship: String
            _tmpRelationship = _cursor.getString(_cursorIndexOfRelationship)
            val _tmpPhone: String
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone)
            val _tmpFirebaseId: String?
            if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
              _tmpFirebaseId = null
            } else {
              _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
            }
            _result =
                EmergencyContactEntity(_tmpId,_tmpUserId,_tmpName,_tmpRelationship,_tmpPhone,_tmpFirebaseId)
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
