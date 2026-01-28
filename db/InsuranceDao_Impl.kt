package com.example.tripshare.`data`.db

import android.database.Cursor
import androidx.room.CoroutinesRoom
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.InsurancePolicyEntity
import java.lang.Class
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class InsuranceDao_Impl(
  __db: RoomDatabase,
) : InsuranceDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfInsurancePolicyEntity:
      EntityInsertionAdapter<InsurancePolicyEntity>

  private val __preparedStmtOfDeletePolicy: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfInsurancePolicyEntity = object :
        EntityInsertionAdapter<InsurancePolicyEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `insurance_policies` (`id`,`userId`,`providerName`,`policyNumber`,`emergencyPhone`,`claimUrl`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: InsurancePolicyEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.userId)
        statement.bindString(3, entity.providerName)
        statement.bindString(4, entity.policyNumber)
        statement.bindString(5, entity.emergencyPhone)
        statement.bindString(6, entity.claimUrl)
      }
    }
    this.__preparedStmtOfDeletePolicy = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM insurance_policies WHERE userId = ?"
        return _query
      }
    }
  }

  public override suspend fun insertPolicy(policy: InsurancePolicyEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfInsurancePolicyEntity.insert(policy)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deletePolicy(userId: Long): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeletePolicy.acquire()
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
        __preparedStmtOfDeletePolicy.release(_stmt)
      }
    }
  })

  public override fun observePolicy(userId: Long): Flow<InsurancePolicyEntity?> {
    val _sql: String = "SELECT * FROM insurance_policies WHERE userId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, userId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("insurance_policies"), object :
        Callable<InsurancePolicyEntity?> {
      public override fun call(): InsurancePolicyEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfProviderName: Int = getColumnIndexOrThrow(_cursor, "providerName")
          val _cursorIndexOfPolicyNumber: Int = getColumnIndexOrThrow(_cursor, "policyNumber")
          val _cursorIndexOfEmergencyPhone: Int = getColumnIndexOrThrow(_cursor, "emergencyPhone")
          val _cursorIndexOfClaimUrl: Int = getColumnIndexOrThrow(_cursor, "claimUrl")
          val _result: InsurancePolicyEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpProviderName: String
            _tmpProviderName = _cursor.getString(_cursorIndexOfProviderName)
            val _tmpPolicyNumber: String
            _tmpPolicyNumber = _cursor.getString(_cursorIndexOfPolicyNumber)
            val _tmpEmergencyPhone: String
            _tmpEmergencyPhone = _cursor.getString(_cursorIndexOfEmergencyPhone)
            val _tmpClaimUrl: String
            _tmpClaimUrl = _cursor.getString(_cursorIndexOfClaimUrl)
            _result =
                InsurancePolicyEntity(_tmpId,_tmpUserId,_tmpProviderName,_tmpPolicyNumber,_tmpEmergencyPhone,_tmpClaimUrl)
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

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
