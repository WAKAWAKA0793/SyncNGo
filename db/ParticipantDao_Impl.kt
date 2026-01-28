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
import com.example.tripshare.`data`.model.ParticipantRole
import com.example.tripshare.`data`.model.ParticipationStatus
import com.example.tripshare.`data`.model.TripParticipantEntity
import com.example.tripshare.`data`.model.UserEntity
import com.example.tripshare.`data`.model.VerificationMethod
import java.lang.Class
import java.lang.IllegalArgumentException
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
public class ParticipantDao_Impl(
  __db: RoomDatabase,
) : ParticipantDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfTripParticipantEntity:
      EntityInsertionAdapter<TripParticipantEntity>

  private val __preparedStmtOfDeleteParticipant: SharedSQLiteStatement

  private val __preparedStmtOfDeleteParticipantByEmail: SharedSQLiteStatement

  private val __preparedStmtOfDeleteAllParticipantsForTrip: SharedSQLiteStatement

  private val __converters: Converters = Converters()
  init {
    this.__db = __db
    this.__insertionAdapterOfTripParticipantEntity = object :
        EntityInsertionAdapter<TripParticipantEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `trip_participants` (`id`,`tripId`,`userId`,`email`,`displayName`,`status`,`role`,`joinedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: TripParticipantEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        statement.bindLong(3, entity.userId)
        statement.bindString(4, entity.email)
        statement.bindString(5, entity.displayName)
        statement.bindString(6, __ParticipationStatus_enumToString(entity.status))
        statement.bindString(7, __ParticipantRole_enumToString(entity.role))
        statement.bindLong(8, entity.joinedAt)
      }
    }
    this.__preparedStmtOfDeleteParticipant = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM trip_participants WHERE id = ? AND tripId = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteParticipantByEmail = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM trip_participants WHERE email = ? AND tripId = ?"
        return _query
      }
    }
    this.__preparedStmtOfDeleteAllParticipantsForTrip = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM trip_participants WHERE tripId = ?"
        return _query
      }
    }
  }

  public override suspend fun upsert(participant: TripParticipantEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfTripParticipantEntity.insert(participant)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun upsertAll(participants: List<TripParticipantEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfTripParticipantEntity.insert(participants)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun insertParticipant(participant: TripParticipantEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfTripParticipantEntity.insertAndReturnId(participant)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteParticipant(tripId: Long, participantId: Long): Int =
      CoroutinesRoom.execute(__db, true, object : Callable<Int> {
    public override fun call(): Int {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteParticipant.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, participantId)
      _argIndex = 2
      _stmt.bindLong(_argIndex, tripId)
      try {
        __db.beginTransaction()
        try {
          val _result: Int = _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
          return _result
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteParticipant.release(_stmt)
      }
    }
  })

  public override suspend fun deleteParticipantByEmail(tripId: Long, email: String): Int =
      CoroutinesRoom.execute(__db, true, object : Callable<Int> {
    public override fun call(): Int {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteParticipantByEmail.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, email)
      _argIndex = 2
      _stmt.bindLong(_argIndex, tripId)
      try {
        __db.beginTransaction()
        try {
          val _result: Int = _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
          return _result
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteParticipantByEmail.release(_stmt)
      }
    }
  })

  public override suspend fun deleteAllParticipantsForTrip(tripId: Long): Int =
      CoroutinesRoom.execute(__db, true, object : Callable<Int> {
    public override fun call(): Int {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteAllParticipantsForTrip.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, tripId)
      try {
        __db.beginTransaction()
        try {
          val _result: Int = _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
          return _result
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteAllParticipantsForTrip.release(_stmt)
      }
    }
  })

  public override fun observeParticipantsForTrip(tripId: Long): Flow<List<TripParticipantEntity>> {
    val _sql: String =
        "SELECT * FROM trip_participants WHERE tripId = ? ORDER BY displayName COLLATE NOCASE ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trip_participants"), object :
        Callable<List<TripParticipantEntity>> {
      public override fun call(): List<TripParticipantEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfEmail: Int = getColumnIndexOrThrow(_cursor, "email")
          val _cursorIndexOfDisplayName: Int = getColumnIndexOrThrow(_cursor, "displayName")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfRole: Int = getColumnIndexOrThrow(_cursor, "role")
          val _cursorIndexOfJoinedAt: Int = getColumnIndexOrThrow(_cursor, "joinedAt")
          val _result: MutableList<TripParticipantEntity> =
              ArrayList<TripParticipantEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripParticipantEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpEmail: String
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail)
            val _tmpDisplayName: String
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName)
            val _tmpStatus: ParticipationStatus
            _tmpStatus = __ParticipationStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus))
            val _tmpRole: ParticipantRole
            _tmpRole = __ParticipantRole_stringToEnum(_cursor.getString(_cursorIndexOfRole))
            val _tmpJoinedAt: Long
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt)
            _item =
                TripParticipantEntity(_tmpId,_tmpTripId,_tmpUserId,_tmpEmail,_tmpDisplayName,_tmpStatus,_tmpRole,_tmpJoinedAt)
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

  public override suspend fun getParticipantsForTrip(tripId: Long): List<TripParticipantEntity> {
    val _sql: String =
        "SELECT * FROM trip_participants WHERE tripId = ? ORDER BY displayName COLLATE NOCASE ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<TripParticipantEntity>>
        {
      public override fun call(): List<TripParticipantEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfEmail: Int = getColumnIndexOrThrow(_cursor, "email")
          val _cursorIndexOfDisplayName: Int = getColumnIndexOrThrow(_cursor, "displayName")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfRole: Int = getColumnIndexOrThrow(_cursor, "role")
          val _cursorIndexOfJoinedAt: Int = getColumnIndexOrThrow(_cursor, "joinedAt")
          val _result: MutableList<TripParticipantEntity> =
              ArrayList<TripParticipantEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripParticipantEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpEmail: String
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail)
            val _tmpDisplayName: String
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName)
            val _tmpStatus: ParticipationStatus
            _tmpStatus = __ParticipationStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus))
            val _tmpRole: ParticipantRole
            _tmpRole = __ParticipantRole_stringToEnum(_cursor.getString(_cursorIndexOfRole))
            val _tmpJoinedAt: Long
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt)
            _item =
                TripParticipantEntity(_tmpId,_tmpTripId,_tmpUserId,_tmpEmail,_tmpDisplayName,_tmpStatus,_tmpRole,_tmpJoinedAt)
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

  public override fun observeUsersForTrip(tripId: Long): Flow<List<UserEntity>> {
    val _sql: String = """
        |
        |        SELECT u.*
        |        FROM users AS u
        |        INNER JOIN trip_participants AS tp
        |            ON tp.userId = u.id
        |        WHERE tp.tripId = ?
        |        ORDER BY u.name COLLATE NOCASE
        |        
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("users", "trip_participants"), object :
        Callable<List<UserEntity>> {
      public override fun call(): List<UserEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfEmail: Int = getColumnIndexOrThrow(_cursor, "email")
          val _cursorIndexOfPasswordHash: Int = getColumnIndexOrThrow(_cursor, "passwordHash")
          val _cursorIndexOfIcNumber: Int = getColumnIndexOrThrow(_cursor, "icNumber")
          val _cursorIndexOfVerificationMethod: Int = getColumnIndexOrThrow(_cursor,
              "verificationMethod")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phoneNumber")
          val _cursorIndexOfVerifiedEmail: Int = getColumnIndexOrThrow(_cursor, "verifiedEmail")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfBio: Int = getColumnIndexOrThrow(_cursor, "bio")
          val _cursorIndexOfProfilePhoto: Int = getColumnIndexOrThrow(_cursor, "profilePhoto")
          val _cursorIndexOfVerified: Int = getColumnIndexOrThrow(_cursor, "verified")
          val _cursorIndexOfTripsCompleted: Int = getColumnIndexOrThrow(_cursor, "tripsCompleted")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _result: MutableList<UserEntity> = ArrayList<UserEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: UserEntity
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
            _item =
                UserEntity(_tmpId,_tmpFirebaseId,_tmpName,_tmpEmail,_tmpPasswordHash,_tmpIcNumber,_tmpVerificationMethod,_tmpPhoneNumber,_tmpVerifiedEmail,_tmpLocation,_tmpBio,_tmpProfilePhoto,_tmpVerified,_tmpTripsCompleted,_tmpCreatedAt)
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

  public override suspend fun getUsersForTripOnce(tripId: Long): List<UserEntity> {
    val _sql: String = """
        |
        |        SELECT u.*
        |        FROM users AS u
        |        INNER JOIN trip_participants AS tp
        |            ON tp.userId = u.id
        |        WHERE tp.tripId = ?
        |        ORDER BY u.name COLLATE NOCASE
        |        
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<UserEntity>> {
      public override fun call(): List<UserEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfEmail: Int = getColumnIndexOrThrow(_cursor, "email")
          val _cursorIndexOfPasswordHash: Int = getColumnIndexOrThrow(_cursor, "passwordHash")
          val _cursorIndexOfIcNumber: Int = getColumnIndexOrThrow(_cursor, "icNumber")
          val _cursorIndexOfVerificationMethod: Int = getColumnIndexOrThrow(_cursor,
              "verificationMethod")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phoneNumber")
          val _cursorIndexOfVerifiedEmail: Int = getColumnIndexOrThrow(_cursor, "verifiedEmail")
          val _cursorIndexOfLocation: Int = getColumnIndexOrThrow(_cursor, "location")
          val _cursorIndexOfBio: Int = getColumnIndexOrThrow(_cursor, "bio")
          val _cursorIndexOfProfilePhoto: Int = getColumnIndexOrThrow(_cursor, "profilePhoto")
          val _cursorIndexOfVerified: Int = getColumnIndexOrThrow(_cursor, "verified")
          val _cursorIndexOfTripsCompleted: Int = getColumnIndexOrThrow(_cursor, "tripsCompleted")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _result: MutableList<UserEntity> = ArrayList<UserEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: UserEntity
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
            _item =
                UserEntity(_tmpId,_tmpFirebaseId,_tmpName,_tmpEmail,_tmpPasswordHash,_tmpIcNumber,_tmpVerificationMethod,_tmpPhoneNumber,_tmpVerifiedEmail,_tmpLocation,_tmpBio,_tmpProfilePhoto,_tmpVerified,_tmpTripsCompleted,_tmpCreatedAt)
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

  public override suspend fun countMembership(
    tripId: Long,
    userA: Long,
    userB: Long,
  ): Int {
    val _sql: String = """
        |
        |        SELECT COUNT(*) FROM trip_participants 
        |        WHERE tripId = ? AND userId IN (?, ?)
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 3)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    _argIndex = 2
    _statement.bindLong(_argIndex, userA)
    _argIndex = 3
    _statement.bindLong(_argIndex, userB)
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

  public override suspend fun getByTrip(tripId: Long): List<TripParticipantEntity> {
    val _sql: String = "SELECT * FROM trip_participants WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<TripParticipantEntity>>
        {
      public override fun call(): List<TripParticipantEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfEmail: Int = getColumnIndexOrThrow(_cursor, "email")
          val _cursorIndexOfDisplayName: Int = getColumnIndexOrThrow(_cursor, "displayName")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfRole: Int = getColumnIndexOrThrow(_cursor, "role")
          val _cursorIndexOfJoinedAt: Int = getColumnIndexOrThrow(_cursor, "joinedAt")
          val _result: MutableList<TripParticipantEntity> =
              ArrayList<TripParticipantEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TripParticipantEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpEmail: String
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail)
            val _tmpDisplayName: String
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName)
            val _tmpStatus: ParticipationStatus
            _tmpStatus = __ParticipationStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus))
            val _tmpRole: ParticipantRole
            _tmpRole = __ParticipantRole_stringToEnum(_cursor.getString(_cursorIndexOfRole))
            val _tmpJoinedAt: Long
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt)
            _item =
                TripParticipantEntity(_tmpId,_tmpTripId,_tmpUserId,_tmpEmail,_tmpDisplayName,_tmpStatus,_tmpRole,_tmpJoinedAt)
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

  private fun __ParticipationStatus_enumToString(_value: ParticipationStatus): String = when
      (_value) {
    ParticipationStatus.JOINED -> "JOINED"
    ParticipationStatus.INVITED -> "INVITED"
    ParticipationStatus.PENDING -> "PENDING"
    ParticipationStatus.LEFT -> "LEFT"
  }

  private fun __ParticipantRole_enumToString(_value: ParticipantRole): String = when (_value) {
    ParticipantRole.OWNER -> "OWNER"
    ParticipantRole.MEMBER -> "MEMBER"
  }

  private fun __ParticipationStatus_stringToEnum(_value: String): ParticipationStatus = when
      (_value) {
    "JOINED" -> ParticipationStatus.JOINED
    "INVITED" -> ParticipationStatus.INVITED
    "PENDING" -> ParticipationStatus.PENDING
    "LEFT" -> ParticipationStatus.LEFT
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __ParticipantRole_stringToEnum(_value: String): ParticipantRole = when (_value) {
    "OWNER" -> ParticipantRole.OWNER
    "MEMBER" -> ParticipantRole.MEMBER
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
