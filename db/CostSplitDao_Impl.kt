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
import com.example.tripshare.`data`.model.CostSplitEntity
import com.example.tripshare.`data`.model.PaymentStatus
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Double
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
public class CostSplitDao_Impl(
  __db: RoomDatabase,
) : CostSplitDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfCostSplitEntity: EntityInsertionAdapter<CostSplitEntity>

  private val __converters: Converters = Converters()

  private val __updateAdapterOfCostSplitEntity: EntityDeletionOrUpdateAdapter<CostSplitEntity>

  private val __preparedStmtOfDeleteSplitsForExpense: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfCostSplitEntity = object :
        EntityInsertionAdapter<CostSplitEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `cost_split` (`id`,`tripId`,`firebaseId`,`expensePaymentId`,`userId`,`amountOwed`,`status`,`splitMode`,`shareCount`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: CostSplitEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindLong(4, entity.expensePaymentId)
        statement.bindLong(5, entity.userId)
        statement.bindDouble(6, entity.amountOwed)
        val _tmp: String = __converters.fromPaymentStatus(entity.status)
        statement.bindString(7, _tmp)
        statement.bindLong(8, entity.splitMode.toLong())
        statement.bindLong(9, entity.shareCount.toLong())
      }
    }
    this.__updateAdapterOfCostSplitEntity = object :
        EntityDeletionOrUpdateAdapter<CostSplitEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `cost_split` SET `id` = ?,`tripId` = ?,`firebaseId` = ?,`expensePaymentId` = ?,`userId` = ?,`amountOwed` = ?,`status` = ?,`splitMode` = ?,`shareCount` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: CostSplitEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindLong(4, entity.expensePaymentId)
        statement.bindLong(5, entity.userId)
        statement.bindDouble(6, entity.amountOwed)
        val _tmp: String = __converters.fromPaymentStatus(entity.status)
        statement.bindString(7, _tmp)
        statement.bindLong(8, entity.splitMode.toLong())
        statement.bindLong(9, entity.shareCount.toLong())
        statement.bindLong(10, entity.id)
      }
    }
    this.__preparedStmtOfDeleteSplitsForExpense = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM cost_split WHERE expensePaymentId = ?"
        return _query
      }
    }
  }

  public override suspend fun insertSplits(splits: List<CostSplitEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfCostSplitEntity.insert(splits)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateSplit(split: CostSplitEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfCostSplitEntity.handle(split)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteSplitsForExpense(expenseId: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfDeleteSplitsForExpense.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, expenseId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfDeleteSplitsForExpense.release(_stmt)
      }
    }
  })

  public override fun observeSplits(tripId: Long): Flow<List<CostSplitEntity>> {
    val _sql: String = "SELECT * FROM cost_split WHERE tripId = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("cost_split"), object :
        Callable<List<CostSplitEntity>> {
      public override fun call(): List<CostSplitEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfExpensePaymentId: Int = getColumnIndexOrThrow(_cursor,
              "expensePaymentId")
          val _cursorIndexOfUserId: Int = getColumnIndexOrThrow(_cursor, "userId")
          val _cursorIndexOfAmountOwed: Int = getColumnIndexOrThrow(_cursor, "amountOwed")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfSplitMode: Int = getColumnIndexOrThrow(_cursor, "splitMode")
          val _cursorIndexOfShareCount: Int = getColumnIndexOrThrow(_cursor, "shareCount")
          val _result: MutableList<CostSplitEntity> = ArrayList<CostSplitEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CostSplitEntity
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
            val _tmpExpensePaymentId: Long
            _tmpExpensePaymentId = _cursor.getLong(_cursorIndexOfExpensePaymentId)
            val _tmpUserId: Long
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId)
            val _tmpAmountOwed: Double
            _tmpAmountOwed = _cursor.getDouble(_cursorIndexOfAmountOwed)
            val _tmpStatus: PaymentStatus
            val _tmp: String
            _tmp = _cursor.getString(_cursorIndexOfStatus)
            _tmpStatus = __converters.toPaymentStatus(_tmp)
            val _tmpSplitMode: Int
            _tmpSplitMode = _cursor.getInt(_cursorIndexOfSplitMode)
            val _tmpShareCount: Int
            _tmpShareCount = _cursor.getInt(_cursorIndexOfShareCount)
            _item =
                CostSplitEntity(_tmpId,_tmpTripId,_tmpFirebaseId,_tmpExpensePaymentId,_tmpUserId,_tmpAmountOwed,_tmpStatus,_tmpSplitMode,_tmpShareCount)
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
