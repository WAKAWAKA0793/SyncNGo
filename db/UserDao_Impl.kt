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
import com.example.tripshare.`data`.model.UserEntity
import com.example.tripshare.`data`.model.VerificationMethod
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
public class UserDao_Impl(
  __db: RoomDatabase,
) : UserDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfUserEntity: EntityInsertionAdapter<UserEntity>

  private val __converters: Converters = Converters()

  private val __deletionAdapterOfUserEntity: EntityDeletionOrUpdateAdapter<UserEntity>

  private val __updateAdapterOfUserEntity: EntityDeletionOrUpdateAdapter<UserEntity>

  private val __preparedStmtOfUpdateAvatarUrl: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfUserEntity = object : EntityInsertionAdapter<UserEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `users` (`id`,`firebaseId`,`name`,`email`,`passwordHash`,`icNumber`,`verificationMethod`,`phoneNumber`,`verifiedEmail`,`location`,`bio`,`profilePhoto`,`verified`,`tripsCompleted`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: UserEntity) {
        statement.bindLong(1, entity.id)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(2)
        } else {
          statement.bindString(2, _tmpFirebaseId)
        }
        statement.bindString(3, entity.name)
        statement.bindString(4, entity.email)
        statement.bindString(5, entity.passwordHash)
        val _tmpIcNumber: String? = entity.icNumber
        if (_tmpIcNumber == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpIcNumber)
        }
        val _tmp: String = __converters.fromVerification(entity.verificationMethod)
        statement.bindString(7, _tmp)
        val _tmpPhoneNumber: String? = entity.phoneNumber
        if (_tmpPhoneNumber == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmpPhoneNumber)
        }
        val _tmpVerifiedEmail: String? = entity.verifiedEmail
        if (_tmpVerifiedEmail == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpVerifiedEmail)
        }
        statement.bindString(10, entity.location)
        statement.bindString(11, entity.bio)
        val _tmpProfilePhoto: String? = entity.profilePhoto
        if (_tmpProfilePhoto == null) {
          statement.bindNull(12)
        } else {
          statement.bindString(12, _tmpProfilePhoto)
        }
        val _tmp_1: Int = if (entity.verified) 1 else 0
        statement.bindLong(13, _tmp_1.toLong())
        statement.bindLong(14, entity.tripsCompleted.toLong())
        statement.bindLong(15, entity.createdAt)
      }
    }
    this.__deletionAdapterOfUserEntity = object : EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `users` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: UserEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfUserEntity = object : EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `users` SET `id` = ?,`firebaseId` = ?,`name` = ?,`email` = ?,`passwordHash` = ?,`icNumber` = ?,`verificationMethod` = ?,`phoneNumber` = ?,`verifiedEmail` = ?,`location` = ?,`bio` = ?,`profilePhoto` = ?,`verified` = ?,`tripsCompleted` = ?,`createdAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: UserEntity) {
        statement.bindLong(1, entity.id)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(2)
        } else {
          statement.bindString(2, _tmpFirebaseId)
        }
        statement.bindString(3, entity.name)
        statement.bindString(4, entity.email)
        statement.bindString(5, entity.passwordHash)
        val _tmpIcNumber: String? = entity.icNumber
        if (_tmpIcNumber == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpIcNumber)
        }
        val _tmp: String = __converters.fromVerification(entity.verificationMethod)
        statement.bindString(7, _tmp)
        val _tmpPhoneNumber: String? = entity.phoneNumber
        if (_tmpPhoneNumber == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmpPhoneNumber)
        }
        val _tmpVerifiedEmail: String? = entity.verifiedEmail
        if (_tmpVerifiedEmail == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpVerifiedEmail)
        }
        statement.bindString(10, entity.location)
        statement.bindString(11, entity.bio)
        val _tmpProfilePhoto: String? = entity.profilePhoto
        if (_tmpProfilePhoto == null) {
          statement.bindNull(12)
        } else {
          statement.bindString(12, _tmpProfilePhoto)
        }
        val _tmp_1: Int = if (entity.verified) 1 else 0
        statement.bindLong(13, _tmp_1.toLong())
        statement.bindLong(14, entity.tripsCompleted.toLong())
        statement.bindLong(15, entity.createdAt)
        statement.bindLong(16, entity.id)
      }
    }
    this.__preparedStmtOfUpdateAvatarUrl = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE users SET profilePhoto = ? WHERE id = ?"
        return _query
      }
    }
  }

  public override suspend fun insertUser(user: UserEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfUserEntity.insertAndReturnId(user)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteUser(user: UserEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfUserEntity.handle(user)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateUser(user: UserEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfUserEntity.handle(user)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateAvatarUrl(userId: Long, url: String): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUpdateAvatarUrl.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, url)
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
        __preparedStmtOfUpdateAvatarUrl.release(_stmt)
      }
    }
  })

  public override suspend fun findUserIdByExactName(name: String): Long? {
    val _sql: String = "SELECT id FROM users WHERE LOWER(name) = LOWER(?) LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, name)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Long?> {
      public override fun call(): Long? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Long?
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null
            } else {
              _result = _cursor.getLong(0)
            }
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

  public override suspend fun getUserById(userId: Long): UserEntity? {
    val _sql: String = "SELECT * FROM users WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<UserEntity?> {
      public override fun call(): UserEntity? {
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
          val _result: UserEntity?
          if (_cursor.moveToFirst()) {
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
            _result =
                UserEntity(_tmpId,_tmpFirebaseId,_tmpName,_tmpEmail,_tmpPasswordHash,_tmpIcNumber,_tmpVerificationMethod,_tmpPhoneNumber,_tmpVerifiedEmail,_tmpLocation,_tmpBio,_tmpProfilePhoto,_tmpVerified,_tmpTripsCompleted,_tmpCreatedAt)
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

  public override fun observeUser(): Flow<UserEntity?> {
    val _sql: String = "SELECT * FROM users LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("users"), object : Callable<UserEntity?> {
      public override fun call(): UserEntity? {
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
          val _result: UserEntity?
          if (_cursor.moveToFirst()) {
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
            _result =
                UserEntity(_tmpId,_tmpFirebaseId,_tmpName,_tmpEmail,_tmpPasswordHash,_tmpIcNumber,_tmpVerificationMethod,_tmpPhoneNumber,_tmpVerifiedEmail,_tmpLocation,_tmpBio,_tmpProfilePhoto,_tmpVerified,_tmpTripsCompleted,_tmpCreatedAt)
          } else {
            _result = null
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

  public override fun observeUserById(userId: Long): Flow<UserEntity?> {
    val _sql: String = "SELECT * FROM users WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("users"), object : Callable<UserEntity?> {
      public override fun call(): UserEntity? {
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
          val _result: UserEntity?
          if (_cursor.moveToFirst()) {
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
            _result =
                UserEntity(_tmpId,_tmpFirebaseId,_tmpName,_tmpEmail,_tmpPasswordHash,_tmpIcNumber,_tmpVerificationMethod,_tmpPhoneNumber,_tmpVerifiedEmail,_tmpLocation,_tmpBio,_tmpProfilePhoto,_tmpVerified,_tmpTripsCompleted,_tmpCreatedAt)
          } else {
            _result = null
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

  public override suspend fun getUsername(userId: Long): String? {
    val _sql: String = "SELECT name FROM users WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<String?> {
      public override fun call(): String? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: String?
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null
            } else {
              _result = _cursor.getString(0)
            }
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

  public override fun observeUserName(id: Long): Flow<String?> {
    val _sql: String = "SELECT name FROM users WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("users"), object : Callable<String?> {
      public override fun call(): String? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: String?
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null
            } else {
              _result = _cursor.getString(0)
            }
          } else {
            _result = null
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

  public override suspend fun findByEmail(email: String): UserEntity? {
    val _sql: String = "SELECT * FROM users WHERE email = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, email)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<UserEntity?> {
      public override fun call(): UserEntity? {
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
          val _result: UserEntity?
          if (_cursor.moveToFirst()) {
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
            _result =
                UserEntity(_tmpId,_tmpFirebaseId,_tmpName,_tmpEmail,_tmpPasswordHash,_tmpIcNumber,_tmpVerificationMethod,_tmpPhoneNumber,_tmpVerifiedEmail,_tmpLocation,_tmpBio,_tmpProfilePhoto,_tmpVerified,_tmpTripsCompleted,_tmpCreatedAt)
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

  public override suspend fun findById(id: Long): UserEntity? {
    val _sql: String = "SELECT * FROM users WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<UserEntity?> {
      public override fun call(): UserEntity? {
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
          val _result: UserEntity?
          if (_cursor.moveToFirst()) {
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
            _result =
                UserEntity(_tmpId,_tmpFirebaseId,_tmpName,_tmpEmail,_tmpPasswordHash,_tmpIcNumber,_tmpVerificationMethod,_tmpPhoneNumber,_tmpVerifiedEmail,_tmpLocation,_tmpBio,_tmpProfilePhoto,_tmpVerified,_tmpTripsCompleted,_tmpCreatedAt)
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

  public override suspend fun emailExists(email: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM users WHERE email = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, email)
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

  public override suspend fun findByFirebaseId(fid: String): UserEntity? {
    val _sql: String = "SELECT * FROM users WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<UserEntity?> {
      public override fun call(): UserEntity? {
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
          val _result: UserEntity?
          if (_cursor.moveToFirst()) {
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
            _result =
                UserEntity(_tmpId,_tmpFirebaseId,_tmpName,_tmpEmail,_tmpPasswordHash,_tmpIcNumber,_tmpVerificationMethod,_tmpPhoneNumber,_tmpVerifiedEmail,_tmpLocation,_tmpBio,_tmpProfilePhoto,_tmpVerified,_tmpTripsCompleted,_tmpCreatedAt)
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

  public override suspend fun getUserIdsForTrip(tripId: Long): List<Long> {
    val _sql: String = "SELECT DISTINCT userId FROM trip_participants WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<Long>> {
      public override fun call(): List<Long> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: MutableList<Long> = ArrayList<Long>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: Long
            _item = _cursor.getLong(0)
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

  public override fun observeParticipants(tripId: Long): Flow<List<UserEntity>> {
    val _sql: String = """
        |
        |        SELECT u.* FROM users u
        |        INNER JOIN trip_participants tp ON u.id = tp.userId
        |        WHERE tp.tripId = ?
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

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
