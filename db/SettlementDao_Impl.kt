package com.example.tripshare.`data`.db

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.tripshare.`data`.model.SettlementTransferEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class SettlementDao_Impl(
  __db: RoomDatabase,
) : SettlementDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfSettlementTransferEntity:
      EntityInsertionAdapter<SettlementTransferEntity>
  init {
    this.__db = __db
    this.__insertionAdapterOfSettlementTransferEntity = object :
        EntityInsertionAdapter<SettlementTransferEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `settlement_transfers` (`id`,`tripId`,`firebaseId`,`payerUserId`,`receiverUserId`,`amount`,`currency`,`createdAtMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: SettlementTransferEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindLong(4, entity.payerUserId)
        statement.bindLong(5, entity.receiverUserId)
        statement.bindDouble(6, entity.amount)
        statement.bindString(7, entity.currency)
        statement.bindLong(8, entity.createdAtMillis)
      }
    }
  }

  public override suspend fun insertTransfer(entity: SettlementTransferEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfSettlementTransferEntity.insertAndReturnId(entity)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun getSettlementByFirebaseId(fid: String): SettlementTransferEntity? {
    val _sql: String = "SELECT * FROM settlement_transfers WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<SettlementTransferEntity?> {
      public override fun call(): SettlementTransferEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfPayerUserId: Int = getColumnIndexOrThrow(_cursor, "payerUserId")
          val _cursorIndexOfReceiverUserId: Int = getColumnIndexOrThrow(_cursor, "receiverUserId")
          val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_cursor, "amount")
          val _cursorIndexOfCurrency: Int = getColumnIndexOrThrow(_cursor, "currency")
          val _cursorIndexOfCreatedAtMillis: Int = getColumnIndexOrThrow(_cursor, "createdAtMillis")
          val _result: SettlementTransferEntity?
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
            val _tmpPayerUserId: Long
            _tmpPayerUserId = _cursor.getLong(_cursorIndexOfPayerUserId)
            val _tmpReceiverUserId: Long
            _tmpReceiverUserId = _cursor.getLong(_cursorIndexOfReceiverUserId)
            val _tmpAmount: Double
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount)
            val _tmpCurrency: String
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency)
            val _tmpCreatedAtMillis: Long
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis)
            _result =
                SettlementTransferEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpPayerUserId,_tmpReceiverUserId,_tmpAmount,_tmpCurrency,_tmpCreatedAtMillis)
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

  public override fun observeTransfersForTrip(tripId: Long): Flow<List<SettlementTransferEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM settlement_transfers 
        |        WHERE tripId = ? 
        |        ORDER BY createdAtMillis DESC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("settlement_transfers"), object :
        Callable<List<SettlementTransferEntity>> {
      public override fun call(): List<SettlementTransferEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfPayerUserId: Int = getColumnIndexOrThrow(_cursor, "payerUserId")
          val _cursorIndexOfReceiverUserId: Int = getColumnIndexOrThrow(_cursor, "receiverUserId")
          val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_cursor, "amount")
          val _cursorIndexOfCurrency: Int = getColumnIndexOrThrow(_cursor, "currency")
          val _cursorIndexOfCreatedAtMillis: Int = getColumnIndexOrThrow(_cursor, "createdAtMillis")
          val _result: MutableList<SettlementTransferEntity> =
              ArrayList<SettlementTransferEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: SettlementTransferEntity
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
            val _tmpPayerUserId: Long
            _tmpPayerUserId = _cursor.getLong(_cursorIndexOfPayerUserId)
            val _tmpReceiverUserId: Long
            _tmpReceiverUserId = _cursor.getLong(_cursorIndexOfReceiverUserId)
            val _tmpAmount: Double
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount)
            val _tmpCurrency: String
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency)
            val _tmpCreatedAtMillis: Long
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis)
            _item =
                SettlementTransferEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpPayerUserId,_tmpReceiverUserId,_tmpAmount,_tmpCurrency,_tmpCreatedAtMillis)
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
