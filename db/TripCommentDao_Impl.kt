package com.example.tripshare.`data`.db

import android.database.Cursor
import androidx.collection.LongSparseArray
import androidx.room.CoroutinesRoom
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.newStringBuilder
import androidx.room.util.query
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.TripCommentEntity
import com.example.tripshare.`data`.model.TripCommentWithAuthor
import com.example.tripshare.`data`.model.UserEntity
import com.example.tripshare.`data`.model.VerificationMethod
import java.lang.Class
import java.lang.StringBuilder
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
public class TripCommentDao_Impl(
  __db: RoomDatabase,
) : TripCommentDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfTripCommentEntity: EntityInsertionAdapter<TripCommentEntity>

  private val __preparedStmtOfDelete: SharedSQLiteStatement

  private val __converters: Converters = Converters()
  init {
    this.__db = __db
    this.__insertionAdapterOfTripCommentEntity = object :
        EntityInsertionAdapter<TripCommentEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `trip_comments` (`id`,`planId`,`userId`,`text`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: TripCommentEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.planId)
        statement.bindLong(3, entity.userId)
        statement.bindString(4, entity.text)
        statement.bindLong(5, entity.timestamp)
      }
    }
    this.__preparedStmtOfDelete = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM trip_comments WHERE id = ?"
        return _query
      }
    }
  }

  public override suspend fun insert(comment: TripCommentEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfTripCommentEntity.insert(comment)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun delete(commentId: Long): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDelete.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, commentId)
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

  public override fun observeCommentsForPlan(planId: Long): Flow<List<TripCommentWithAuthor>> {
    val _sql: String = """
        |
        |        SELECT * FROM trip_comments
        |        WHERE planId = ?
        |        ORDER BY timestamp ASC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, planId)
    return CoroutinesRoom.createFlow(__db, true, arrayOf("users", "trip_comments"), object :
        Callable<List<TripCommentWithAuthor>> {
      public override fun call(): List<TripCommentWithAuthor> {
        __db.beginTransaction()
        try {
          val _cursor: Cursor = query(__db, _statement, true, null)
          try {
            val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
            val _cursorIndexOfPlanId: Int = getColumnIndexOrThrow(_cursor, "planId")
            val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
            val _cursorIndexOfText: Int = getColumnIndexOrThrow(_cursor, "text")
            val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_cursor, "timestamp")
            val _collectionAuthor: LongSparseArray<UserEntity?> = LongSparseArray<UserEntity?>()
            while (_cursor.moveToNext()) {
              val _tmpKey: Long
              _tmpKey = _cursor.getLong(_cursorIndexOfUserId)
              _collectionAuthor.put(_tmpKey, null)
            }
            _cursor.moveToPosition(-1)
            __fetchRelationshipusersAscomExampleTripshareDataModelUserEntity(_collectionAuthor)
            val _result: MutableList<TripCommentWithAuthor> =
                ArrayList<TripCommentWithAuthor>(_cursor.getCount())
            while (_cursor.moveToNext()) {
              val _item: TripCommentWithAuthor
              val _tmpComment: TripCommentEntity
              val _tmpId: Long
              _tmpId = _cursor.getLong(_cursorIndexOfId)
              val _tmpPlanId: Long
              _tmpPlanId = _cursor.getLong(_cursorIndexOfPlanId)
              val _tmpUserId: Long
              _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
              val _tmpText: String
              _tmpText = _cursor.getString(_cursorIndexOfText)
              val _tmpTimestamp: Long
              _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp)
              _tmpComment = TripCommentEntity(_tmpId,_tmpPlanId,_tmpUserId,_tmpText,_tmpTimestamp)
              val _tmpAuthor: UserEntity?
              val _tmpKey_1: Long
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfUserId)
              _tmpAuthor = _collectionAuthor.get(_tmpKey_1)
              _item = TripCommentWithAuthor(_tmpComment,_tmpAuthor)
              _result.add(_item)
            }
            __db.setTransactionSuccessful()
            return _result
          } finally {
            _cursor.close()
          }
        } finally {
          __db.endTransaction()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  private
      fun __fetchRelationshipusersAscomExampleTripshareDataModelUserEntity(_map: LongSparseArray<UserEntity?>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      recursiveFetchLongSparseArray(_map, false) {
        __fetchRelationshipusersAscomExampleTripshareDataModelUserEntity(it)
      }
      return
    }
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("SELECT `id`,`firebaseId`,`name`,`email`,`passwordHash`,`icNumber`,`verificationMethod`,`phoneNumber`,`verifiedEmail`,`location`,`bio`,`profilePhoto`,`verified`,`tripsCompleted`,`createdAt` FROM `users` WHERE `id` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _stmt: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    val _cursor: Cursor = query(__db, _stmt, false, null)
    try {
      val _itemKeyIndex: Int = getColumnIndex(_cursor, "id")
      if (_itemKeyIndex == -1) {
        return
      }
      val _cursorIndexOfId: Int = 0
      val _cursorIndexOfFirebaseId: Int = 1
      val _cursorIndexOfName: Int = 2
      val _cursorIndexOfEmail: Int = 3
      val _cursorIndexOfPasswordHash: Int = 4
      val _cursorIndexOfIcNumber: Int = 5
      val _cursorIndexOfVerificationMethod: Int = 6
      val _cursorIndexOfPhoneNumber: Int = 7
      val _cursorIndexOfVerifiedEmail: Int = 8
      val _cursorIndexOfLocation: Int = 9
      val _cursorIndexOfBio: Int = 10
      val _cursorIndexOfProfilePhoto: Int = 11
      val _cursorIndexOfVerified: Int = 12
      val _cursorIndexOfTripsCompleted: Int = 13
      val _cursorIndexOfCreatedAt: Int = 14
      while (_cursor.moveToNext()) {
        val _tmpKey: Long
        _tmpKey = _cursor.getLong(_itemKeyIndex)
        if (_map.containsKey(_tmpKey)) {
          val _item_1: UserEntity?
          val _tmpId: Long
          _tmpId = _cursor.getLong(_cursorIndexOfId)
          val _tmpFirebaseId: String?
          if (_cursor.isNull(_cursorIndexOfFirebaseId)) {
            _tmpFirebaseId = null
          } else {
            _tmpFirebaseId = _cursor.getString(_cursorIndexOfFirebaseId)
          }
          val _tmpName: String
          _tmpName = _cursor.getString(_cursorIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _cursor.getString(_cursorIndexOfEmail)
          val _tmpPasswordHash: String
          _tmpPasswordHash = _cursor.getString(_cursorIndexOfPasswordHash)
          val _tmpIcNumber: String?
          if (_cursor.isNull(_cursorIndexOfIcNumber)) {
            _tmpIcNumber = null
          } else {
            _tmpIcNumber = _cursor.getString(_cursorIndexOfIcNumber)
          }
          val _tmpVerificationMethod: VerificationMethod
          val _tmp: String
          _tmp = _cursor.getString(_cursorIndexOfVerificationMethod)
          _tmpVerificationMethod = __converters.toVerification(_tmp)
          val _tmpPhoneNumber: String?
          if (_cursor.isNull(_cursorIndexOfPhoneNumber)) {
            _tmpPhoneNumber = null
          } else {
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
          }
          val _tmpVerifiedEmail: String?
          if (_cursor.isNull(_cursorIndexOfVerifiedEmail)) {
            _tmpVerifiedEmail = null
          } else {
            _tmpVerifiedEmail = _cursor.getString(_cursorIndexOfVerifiedEmail)
          }
          val _tmpLocation: String
          _tmpLocation = _cursor.getString(_cursorIndexOfLocation)
          val _tmpBio: String
          _tmpBio = _cursor.getString(_cursorIndexOfBio)
          val _tmpProfilePhoto: String?
          if (_cursor.isNull(_cursorIndexOfProfilePhoto)) {
            _tmpProfilePhoto = null
          } else {
            _tmpProfilePhoto = _cursor.getString(_cursorIndexOfProfilePhoto)
          }
          val _tmpVerified: Boolean
          val _tmp_1: Int
          _tmp_1 = _cursor.getInt(_cursorIndexOfVerified)
          _tmpVerified = _tmp_1 != 0
          val _tmpTripsCompleted: Int
          _tmpTripsCompleted = _cursor.getInt(_cursorIndexOfTripsCompleted)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
          _item_1 =
              UserEntity(_tmpId,_tmpFirebaseId,_tmpName,_tmpEmail,_tmpPasswordHash,_tmpIcNumber,_tmpVerificationMethod,_tmpPhoneNumber,_tmpVerifiedEmail,_tmpLocation,_tmpBio,_tmpProfilePhoto,_tmpVerified,_tmpTripsCompleted,_tmpCreatedAt)
          _map.put(_tmpKey, _item_1)
        }
      }
    } finally {
      _cursor.close()
    }
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
