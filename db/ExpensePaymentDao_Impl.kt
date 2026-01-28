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
import com.example.tripshare.`data`.model.CostSummary
import com.example.tripshare.`data`.model.ExpensePaymentEntity
import com.example.tripshare.`data`.model.PaymentJoinRow
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
public class ExpensePaymentDao_Impl(
  __db: RoomDatabase,
) : ExpensePaymentDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfExpensePaymentEntity: EntityInsertionAdapter<ExpensePaymentEntity>

  private val __converters: Converters = Converters()

  private val __deletionAdapterOfExpensePaymentEntity:
      EntityDeletionOrUpdateAdapter<ExpensePaymentEntity>

  private val __updateAdapterOfExpensePaymentEntity:
      EntityDeletionOrUpdateAdapter<ExpensePaymentEntity>

  private val __preparedStmtOfUpdateDueDate: SharedSQLiteStatement

  private val __preparedStmtOfUpdateBudget: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfExpensePaymentEntity = object :
        EntityInsertionAdapter<ExpensePaymentEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `expense_payments` (`expensePaymentId`,`tripId`,`firebaseId`,`payerUserId`,`payeeUserId`,`title`,`amount`,`category`,`receiptImage`,`paymentStatus`,`paidAtMillis`,`dueAtMillis`,`itineraryPlanId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ExpensePaymentEntity) {
        statement.bindLong(1, entity.expensePaymentId)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindLong(4, entity.payerUserId)
        statement.bindLong(5, entity.payeeUserId)
        statement.bindString(6, entity.title)
        statement.bindDouble(7, entity.amount)
        statement.bindString(8, entity.category)
        val _tmpReceiptImage: String? = entity.receiptImage
        if (_tmpReceiptImage == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpReceiptImage)
        }
        val _tmp: String = __converters.fromPaymentStatus(entity.paymentStatus)
        statement.bindString(10, _tmp)
        val _tmpPaidAtMillis: Long? = entity.paidAtMillis
        if (_tmpPaidAtMillis == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpPaidAtMillis)
        }
        val _tmpDueAtMillis: Long? = entity.dueAtMillis
        if (_tmpDueAtMillis == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpDueAtMillis)
        }
        val _tmpItineraryPlanId: Long? = entity.itineraryPlanId
        if (_tmpItineraryPlanId == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpItineraryPlanId)
        }
      }
    }
    this.__deletionAdapterOfExpensePaymentEntity = object :
        EntityDeletionOrUpdateAdapter<ExpensePaymentEntity>(__db) {
      protected override fun createQuery(): String =
          "DELETE FROM `expense_payments` WHERE `expensePaymentId` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ExpensePaymentEntity) {
        statement.bindLong(1, entity.expensePaymentId)
      }
    }
    this.__updateAdapterOfExpensePaymentEntity = object :
        EntityDeletionOrUpdateAdapter<ExpensePaymentEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `expense_payments` SET `expensePaymentId` = ?,`tripId` = ?,`firebaseId` = ?,`payerUserId` = ?,`payeeUserId` = ?,`title` = ?,`amount` = ?,`category` = ?,`receiptImage` = ?,`paymentStatus` = ?,`paidAtMillis` = ?,`dueAtMillis` = ?,`itineraryPlanId` = ? WHERE `expensePaymentId` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ExpensePaymentEntity) {
        statement.bindLong(1, entity.expensePaymentId)
        statement.bindLong(2, entity.tripId)
        val _tmpFirebaseId: String? = entity.firebaseId
        if (_tmpFirebaseId == null) {
          statement.bindNull(3)
        } else {
          statement.bindString(3, _tmpFirebaseId)
        }
        statement.bindLong(4, entity.payerUserId)
        statement.bindLong(5, entity.payeeUserId)
        statement.bindString(6, entity.title)
        statement.bindDouble(7, entity.amount)
        statement.bindString(8, entity.category)
        val _tmpReceiptImage: String? = entity.receiptImage
        if (_tmpReceiptImage == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpReceiptImage)
        }
        val _tmp: String = __converters.fromPaymentStatus(entity.paymentStatus)
        statement.bindString(10, _tmp)
        val _tmpPaidAtMillis: Long? = entity.paidAtMillis
        if (_tmpPaidAtMillis == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpPaidAtMillis)
        }
        val _tmpDueAtMillis: Long? = entity.dueAtMillis
        if (_tmpDueAtMillis == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpDueAtMillis)
        }
        val _tmpItineraryPlanId: Long? = entity.itineraryPlanId
        if (_tmpItineraryPlanId == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpItineraryPlanId)
        }
        statement.bindLong(14, entity.expensePaymentId)
      }
    }
    this.__preparedStmtOfUpdateDueDate = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String =
            "UPDATE expense_payments SET dueAtMillis = ? WHERE expensePaymentId = ?"
        return _query
      }
    }
    this.__preparedStmtOfUpdateBudget = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE trips SET budget = ? WHERE id = ?"
        return _query
      }
    }
  }

  public override suspend fun insertPayment(entity: ExpensePaymentEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfExpensePaymentEntity.insertAndReturnId(entity)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deletePayment(payment: ExpensePaymentEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfExpensePaymentEntity.handle(payment)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updatePayment(payment: ExpensePaymentEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfExpensePaymentEntity.handle(payment)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateDueDate(expensePaymentId: Long, dueAtMillis: Long?): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUpdateDueDate.acquire()
      var _argIndex: Int = 1
      if (dueAtMillis == null) {
        _stmt.bindNull(_argIndex)
      } else {
        _stmt.bindLong(_argIndex, dueAtMillis)
      }
      _argIndex = 2
      _stmt.bindLong(_argIndex, expensePaymentId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfUpdateDueDate.release(_stmt)
      }
    }
  })

  public override suspend fun updateBudget(tripId: Long, amount: Double): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUpdateBudget.acquire()
      var _argIndex: Int = 1
      _stmt.bindDouble(_argIndex, amount)
      _argIndex = 2
      _stmt.bindLong(_argIndex, tripId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfUpdateBudget.release(_stmt)
      }
    }
  })

  public override fun observePaymentsWithPayer(tripId: Long): Flow<List<PaymentJoinRow>> {
    val _sql: String = """
        |
        |    SELECT 
        |        p.expensePaymentId AS expensePaymentId,
        |        p.tripId AS tripId,
        |        p.payerUserId AS payerUserId,
        |        p.payeeUserId AS payeeUserId,
        |        p.title AS title,
        |        p.amount AS amount,
        |        p.category AS category,
        |        p.paymentStatus AS paymentStatus,
        |        p.paidAtMillis AS paidAtMillis,
        |        p.dueAtMillis AS dueAtMillis,
        |        COALESCE(u.id, p.payerUserId) AS payerId,        -- Fallback to payment's payerId
        |        COALESCE(u.name, 'Unknown') AS payerName,        -- Fallback to 'Unknown'
        |        u.profilePhoto AS payerPhoto
        |    FROM expense_payments p
        |    LEFT JOIN users u ON u.id = p.payerUserId
        |    WHERE p.tripId = ?
        |    ORDER BY 
        |        COALESCE(p.dueAtMillis, p.paidAtMillis, p.expensePaymentId) DESC,
        |        p.expensePaymentId DESC
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("expense_payments", "users"), object :
        Callable<List<PaymentJoinRow>> {
      public override fun call(): List<PaymentJoinRow> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfExpensePaymentId: Int = 0
          val _cursorIndexOfTripId: Int = 1
          val _cursorIndexOfPayerUserId: Int = 2
          val _cursorIndexOfPayeeUserId: Int = 3
          val _cursorIndexOfTitle: Int = 4
          val _cursorIndexOfAmount: Int = 5
          val _cursorIndexOfCategory: Int = 6
          val _cursorIndexOfPaymentStatus: Int = 7
          val _cursorIndexOfPaidAtMillis: Int = 8
          val _cursorIndexOfDueAtMillis: Int = 9
          val _cursorIndexOfPayerId: Int = 10
          val _cursorIndexOfPayerName: Int = 11
          val _cursorIndexOfPayerPhoto: Int = 12
          val _result: MutableList<PaymentJoinRow> = ArrayList<PaymentJoinRow>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: PaymentJoinRow
            val _tmpExpensePaymentId: Long
            _tmpExpensePaymentId = _cursor.getLong(_cursorIndexOfExpensePaymentId)
            val _tmpTripId: Long
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId)
            val _tmpPayerUserId: Long
            _tmpPayerUserId = _cursor.getLong(_cursorIndexOfPayerUserId)
            val _tmpPayeeUserId: Long
            _tmpPayeeUserId = _cursor.getLong(_cursorIndexOfPayeeUserId)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpAmount: Double
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpPaymentStatus: PaymentStatus
            val _tmp: String
            _tmp = _cursor.getString(_cursorIndexOfPaymentStatus)
            _tmpPaymentStatus = __converters.toPaymentStatus(_tmp)
            val _tmpPaidAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfPaidAtMillis)) {
              _tmpPaidAtMillis = null
            } else {
              _tmpPaidAtMillis = _cursor.getLong(_cursorIndexOfPaidAtMillis)
            }
            val _tmpDueAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfDueAtMillis)) {
              _tmpDueAtMillis = null
            } else {
              _tmpDueAtMillis = _cursor.getLong(_cursorIndexOfDueAtMillis)
            }
            val _tmpPayerId: Long
            _tmpPayerId = _cursor.getLong(_cursorIndexOfPayerId)
            val _tmpPayerName: String
            _tmpPayerName = _cursor.getString(_cursorIndexOfPayerName)
            val _tmpPayerPhoto: String?
            if (_cursor.isNull(_cursorIndexOfPayerPhoto)) {
              _tmpPayerPhoto = null
            } else {
              _tmpPayerPhoto = _cursor.getString(_cursorIndexOfPayerPhoto)
            }
            _item =
                PaymentJoinRow(_tmpExpensePaymentId,_tmpTripId,_tmpPayerUserId,_tmpPayeeUserId,_tmpTitle,_tmpAmount,_tmpCategory,_tmpPaymentStatus,_tmpPaidAtMillis,_tmpDueAtMillis,_tmpPayerId,_tmpPayerName,_tmpPayerPhoto)
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

  public override suspend fun getPaymentById(id: Long): ExpensePaymentEntity? {
    val _sql: String = "SELECT * FROM expense_payments WHERE expensePaymentId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<ExpensePaymentEntity?> {
      public override fun call(): ExpensePaymentEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfExpensePaymentId: Int = getColumnIndexOrThrow(_cursor,
              "expensePaymentId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfPayerUserId: Int = getColumnIndexOrThrow(_cursor, "payerUserId")
          val _cursorIndexOfPayeeUserId: Int = getColumnIndexOrThrow(_cursor, "payeeUserId")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_cursor, "amount")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfReceiptImage: Int = getColumnIndexOrThrow(_cursor, "receiptImage")
          val _cursorIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_cursor, "paymentStatus")
          val _cursorIndexOfPaidAtMillis: Int = getColumnIndexOrThrow(_cursor, "paidAtMillis")
          val _cursorIndexOfDueAtMillis: Int = getColumnIndexOrThrow(_cursor, "dueAtMillis")
          val _cursorIndexOfItineraryPlanId: Int = getColumnIndexOrThrow(_cursor, "itineraryPlanId")
          val _result: ExpensePaymentEntity?
          if (_cursor.moveToFirst()) {
            val _tmpExpensePaymentId: Long
            _tmpExpensePaymentId = _cursor.getLong(_cursorIndexOfExpensePaymentId)
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
            val _tmpPayeeUserId: Long
            _tmpPayeeUserId = _cursor.getLong(_cursorIndexOfPayeeUserId)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpAmount: Double
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpReceiptImage: String?
            if (_cursor.isNull(_cursorIndexOfReceiptImage)) {
              _tmpReceiptImage = null
            } else {
              _tmpReceiptImage = _cursor.getString(_cursorIndexOfReceiptImage)
            }
            val _tmpPaymentStatus: PaymentStatus
            val _tmp: String
            _tmp = _cursor.getString(_cursorIndexOfPaymentStatus)
            _tmpPaymentStatus = __converters.toPaymentStatus(_tmp)
            val _tmpPaidAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfPaidAtMillis)) {
              _tmpPaidAtMillis = null
            } else {
              _tmpPaidAtMillis = _cursor.getLong(_cursorIndexOfPaidAtMillis)
            }
            val _tmpDueAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfDueAtMillis)) {
              _tmpDueAtMillis = null
            } else {
              _tmpDueAtMillis = _cursor.getLong(_cursorIndexOfDueAtMillis)
            }
            val _tmpItineraryPlanId: Long?
            if (_cursor.isNull(_cursorIndexOfItineraryPlanId)) {
              _tmpItineraryPlanId = null
            } else {
              _tmpItineraryPlanId = _cursor.getLong(_cursorIndexOfItineraryPlanId)
            }
            _result =
                ExpensePaymentEntity(_tmpExpensePaymentId,_tmpTripId,_tmpFirebaseId,_tmpPayerUserId,_tmpPayeeUserId,_tmpTitle,_tmpAmount,_tmpCategory,_tmpReceiptImage,_tmpPaymentStatus,_tmpPaidAtMillis,_tmpDueAtMillis,_tmpItineraryPlanId)
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

  public override suspend fun findPendingPayment(
    tripId: Long,
    title: String,
    amount: Double,
  ): ExpensePaymentEntity? {
    val _sql: String = """
        |
        |        SELECT * FROM expense_payments 
        |        WHERE tripId = ? 
        |          AND firebaseId IS NULL 
        |          AND title = ? 
        |          AND amount = ? 
        |        LIMIT 1
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 3)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    _argIndex = 2
    _statement.bindString(_argIndex, title)
    _argIndex = 3
    _statement.bindDouble(_argIndex, amount)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<ExpensePaymentEntity?> {
      public override fun call(): ExpensePaymentEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfExpensePaymentId: Int = getColumnIndexOrThrow(_cursor,
              "expensePaymentId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfPayerUserId: Int = getColumnIndexOrThrow(_cursor, "payerUserId")
          val _cursorIndexOfPayeeUserId: Int = getColumnIndexOrThrow(_cursor, "payeeUserId")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_cursor, "amount")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfReceiptImage: Int = getColumnIndexOrThrow(_cursor, "receiptImage")
          val _cursorIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_cursor, "paymentStatus")
          val _cursorIndexOfPaidAtMillis: Int = getColumnIndexOrThrow(_cursor, "paidAtMillis")
          val _cursorIndexOfDueAtMillis: Int = getColumnIndexOrThrow(_cursor, "dueAtMillis")
          val _cursorIndexOfItineraryPlanId: Int = getColumnIndexOrThrow(_cursor, "itineraryPlanId")
          val _result: ExpensePaymentEntity?
          if (_cursor.moveToFirst()) {
            val _tmpExpensePaymentId: Long
            _tmpExpensePaymentId = _cursor.getLong(_cursorIndexOfExpensePaymentId)
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
            val _tmpPayeeUserId: Long
            _tmpPayeeUserId = _cursor.getLong(_cursorIndexOfPayeeUserId)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpAmount: Double
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpReceiptImage: String?
            if (_cursor.isNull(_cursorIndexOfReceiptImage)) {
              _tmpReceiptImage = null
            } else {
              _tmpReceiptImage = _cursor.getString(_cursorIndexOfReceiptImage)
            }
            val _tmpPaymentStatus: PaymentStatus
            val _tmp: String
            _tmp = _cursor.getString(_cursorIndexOfPaymentStatus)
            _tmpPaymentStatus = __converters.toPaymentStatus(_tmp)
            val _tmpPaidAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfPaidAtMillis)) {
              _tmpPaidAtMillis = null
            } else {
              _tmpPaidAtMillis = _cursor.getLong(_cursorIndexOfPaidAtMillis)
            }
            val _tmpDueAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfDueAtMillis)) {
              _tmpDueAtMillis = null
            } else {
              _tmpDueAtMillis = _cursor.getLong(_cursorIndexOfDueAtMillis)
            }
            val _tmpItineraryPlanId: Long?
            if (_cursor.isNull(_cursorIndexOfItineraryPlanId)) {
              _tmpItineraryPlanId = null
            } else {
              _tmpItineraryPlanId = _cursor.getLong(_cursorIndexOfItineraryPlanId)
            }
            _result =
                ExpensePaymentEntity(_tmpExpensePaymentId,_tmpTripId,_tmpFirebaseId,_tmpPayerUserId,_tmpPayeeUserId,_tmpTitle,_tmpAmount,_tmpCategory,_tmpReceiptImage,_tmpPaymentStatus,_tmpPaidAtMillis,_tmpDueAtMillis,_tmpItineraryPlanId)
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

  public override suspend fun getPaymentByFirebaseId(fid: String): ExpensePaymentEntity? {
    val _sql: String = "SELECT * FROM expense_payments WHERE firebaseId = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, fid)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<ExpensePaymentEntity?> {
      public override fun call(): ExpensePaymentEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfExpensePaymentId: Int = getColumnIndexOrThrow(_cursor,
              "expensePaymentId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfPayerUserId: Int = getColumnIndexOrThrow(_cursor, "payerUserId")
          val _cursorIndexOfPayeeUserId: Int = getColumnIndexOrThrow(_cursor, "payeeUserId")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_cursor, "amount")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfReceiptImage: Int = getColumnIndexOrThrow(_cursor, "receiptImage")
          val _cursorIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_cursor, "paymentStatus")
          val _cursorIndexOfPaidAtMillis: Int = getColumnIndexOrThrow(_cursor, "paidAtMillis")
          val _cursorIndexOfDueAtMillis: Int = getColumnIndexOrThrow(_cursor, "dueAtMillis")
          val _cursorIndexOfItineraryPlanId: Int = getColumnIndexOrThrow(_cursor, "itineraryPlanId")
          val _result: ExpensePaymentEntity?
          if (_cursor.moveToFirst()) {
            val _tmpExpensePaymentId: Long
            _tmpExpensePaymentId = _cursor.getLong(_cursorIndexOfExpensePaymentId)
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
            val _tmpPayeeUserId: Long
            _tmpPayeeUserId = _cursor.getLong(_cursorIndexOfPayeeUserId)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpAmount: Double
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpReceiptImage: String?
            if (_cursor.isNull(_cursorIndexOfReceiptImage)) {
              _tmpReceiptImage = null
            } else {
              _tmpReceiptImage = _cursor.getString(_cursorIndexOfReceiptImage)
            }
            val _tmpPaymentStatus: PaymentStatus
            val _tmp: String
            _tmp = _cursor.getString(_cursorIndexOfPaymentStatus)
            _tmpPaymentStatus = __converters.toPaymentStatus(_tmp)
            val _tmpPaidAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfPaidAtMillis)) {
              _tmpPaidAtMillis = null
            } else {
              _tmpPaidAtMillis = _cursor.getLong(_cursorIndexOfPaidAtMillis)
            }
            val _tmpDueAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfDueAtMillis)) {
              _tmpDueAtMillis = null
            } else {
              _tmpDueAtMillis = _cursor.getLong(_cursorIndexOfDueAtMillis)
            }
            val _tmpItineraryPlanId: Long?
            if (_cursor.isNull(_cursorIndexOfItineraryPlanId)) {
              _tmpItineraryPlanId = null
            } else {
              _tmpItineraryPlanId = _cursor.getLong(_cursorIndexOfItineraryPlanId)
            }
            _result =
                ExpensePaymentEntity(_tmpExpensePaymentId,_tmpTripId,_tmpFirebaseId,_tmpPayerUserId,_tmpPayeeUserId,_tmpTitle,_tmpAmount,_tmpCategory,_tmpReceiptImage,_tmpPaymentStatus,_tmpPaidAtMillis,_tmpDueAtMillis,_tmpItineraryPlanId)
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

  public override fun observePlanLinkedPayments(tripId: Long): Flow<List<ExpensePaymentEntity>> {
    val _sql: String =
        "SELECT * FROM expense_payments WHERE tripId = ? AND itineraryPlanId IS NOT NULL"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("expense_payments"), object :
        Callable<List<ExpensePaymentEntity>> {
      public override fun call(): List<ExpensePaymentEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfExpensePaymentId: Int = getColumnIndexOrThrow(_cursor,
              "expensePaymentId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfPayerUserId: Int = getColumnIndexOrThrow(_cursor, "payerUserId")
          val _cursorIndexOfPayeeUserId: Int = getColumnIndexOrThrow(_cursor, "payeeUserId")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_cursor, "amount")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfReceiptImage: Int = getColumnIndexOrThrow(_cursor, "receiptImage")
          val _cursorIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_cursor, "paymentStatus")
          val _cursorIndexOfPaidAtMillis: Int = getColumnIndexOrThrow(_cursor, "paidAtMillis")
          val _cursorIndexOfDueAtMillis: Int = getColumnIndexOrThrow(_cursor, "dueAtMillis")
          val _cursorIndexOfItineraryPlanId: Int = getColumnIndexOrThrow(_cursor, "itineraryPlanId")
          val _result: MutableList<ExpensePaymentEntity> =
              ArrayList<ExpensePaymentEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: ExpensePaymentEntity
            val _tmpExpensePaymentId: Long
            _tmpExpensePaymentId = _cursor.getLong(_cursorIndexOfExpensePaymentId)
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
            val _tmpPayeeUserId: Long
            _tmpPayeeUserId = _cursor.getLong(_cursorIndexOfPayeeUserId)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpAmount: Double
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpReceiptImage: String?
            if (_cursor.isNull(_cursorIndexOfReceiptImage)) {
              _tmpReceiptImage = null
            } else {
              _tmpReceiptImage = _cursor.getString(_cursorIndexOfReceiptImage)
            }
            val _tmpPaymentStatus: PaymentStatus
            val _tmp: String
            _tmp = _cursor.getString(_cursorIndexOfPaymentStatus)
            _tmpPaymentStatus = __converters.toPaymentStatus(_tmp)
            val _tmpPaidAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfPaidAtMillis)) {
              _tmpPaidAtMillis = null
            } else {
              _tmpPaidAtMillis = _cursor.getLong(_cursorIndexOfPaidAtMillis)
            }
            val _tmpDueAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfDueAtMillis)) {
              _tmpDueAtMillis = null
            } else {
              _tmpDueAtMillis = _cursor.getLong(_cursorIndexOfDueAtMillis)
            }
            val _tmpItineraryPlanId: Long?
            if (_cursor.isNull(_cursorIndexOfItineraryPlanId)) {
              _tmpItineraryPlanId = null
            } else {
              _tmpItineraryPlanId = _cursor.getLong(_cursorIndexOfItineraryPlanId)
            }
            _item =
                ExpensePaymentEntity(_tmpExpensePaymentId,_tmpTripId,_tmpFirebaseId,_tmpPayerUserId,_tmpPayeeUserId,_tmpTitle,_tmpAmount,_tmpCategory,_tmpReceiptImage,_tmpPaymentStatus,_tmpPaidAtMillis,_tmpDueAtMillis,_tmpItineraryPlanId)
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

  public override fun observeBudget(tripId: Long): Flow<Double> {
    val _sql: String = "SELECT budget FROM trips WHERE id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("trips"), object : Callable<Double> {
      public override fun call(): Double {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Double
          if (_cursor.moveToFirst()) {
            _result = _cursor.getDouble(0)
          } else {
            _result = 0.0
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

  public override fun observePayments(tripId: Long): Flow<List<ExpensePaymentEntity>> {
    val _sql: String =
        "SELECT * FROM expense_payments WHERE tripId = ? ORDER BY expensePaymentId DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("expense_payments"), object :
        Callable<List<ExpensePaymentEntity>> {
      public override fun call(): List<ExpensePaymentEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfExpensePaymentId: Int = getColumnIndexOrThrow(_cursor,
              "expensePaymentId")
          val _cursorIndexOfTripId: Int = getColumnIndexOrThrow(_cursor, "tripId")
          val _cursorIndexOfFirebaseId: Int = getColumnIndexOrThrow(_cursor, "firebaseId")
          val _cursorIndexOfPayerUserId: Int = getColumnIndexOrThrow(_cursor, "payerUserId")
          val _cursorIndexOfPayeeUserId: Int = getColumnIndexOrThrow(_cursor, "payeeUserId")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_cursor, "amount")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _cursorIndexOfReceiptImage: Int = getColumnIndexOrThrow(_cursor, "receiptImage")
          val _cursorIndexOfPaymentStatus: Int = getColumnIndexOrThrow(_cursor, "paymentStatus")
          val _cursorIndexOfPaidAtMillis: Int = getColumnIndexOrThrow(_cursor, "paidAtMillis")
          val _cursorIndexOfDueAtMillis: Int = getColumnIndexOrThrow(_cursor, "dueAtMillis")
          val _cursorIndexOfItineraryPlanId: Int = getColumnIndexOrThrow(_cursor, "itineraryPlanId")
          val _result: MutableList<ExpensePaymentEntity> =
              ArrayList<ExpensePaymentEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: ExpensePaymentEntity
            val _tmpExpensePaymentId: Long
            _tmpExpensePaymentId = _cursor.getLong(_cursorIndexOfExpensePaymentId)
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
            val _tmpPayeeUserId: Long
            _tmpPayeeUserId = _cursor.getLong(_cursorIndexOfPayeeUserId)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpAmount: Double
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            val _tmpReceiptImage: String?
            if (_cursor.isNull(_cursorIndexOfReceiptImage)) {
              _tmpReceiptImage = null
            } else {
              _tmpReceiptImage = _cursor.getString(_cursorIndexOfReceiptImage)
            }
            val _tmpPaymentStatus: PaymentStatus
            val _tmp: String
            _tmp = _cursor.getString(_cursorIndexOfPaymentStatus)
            _tmpPaymentStatus = __converters.toPaymentStatus(_tmp)
            val _tmpPaidAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfPaidAtMillis)) {
              _tmpPaidAtMillis = null
            } else {
              _tmpPaidAtMillis = _cursor.getLong(_cursorIndexOfPaidAtMillis)
            }
            val _tmpDueAtMillis: Long?
            if (_cursor.isNull(_cursorIndexOfDueAtMillis)) {
              _tmpDueAtMillis = null
            } else {
              _tmpDueAtMillis = _cursor.getLong(_cursorIndexOfDueAtMillis)
            }
            val _tmpItineraryPlanId: Long?
            if (_cursor.isNull(_cursorIndexOfItineraryPlanId)) {
              _tmpItineraryPlanId = null
            } else {
              _tmpItineraryPlanId = _cursor.getLong(_cursorIndexOfItineraryPlanId)
            }
            _item =
                ExpensePaymentEntity(_tmpExpensePaymentId,_tmpTripId,_tmpFirebaseId,_tmpPayerUserId,_tmpPayeeUserId,_tmpTitle,_tmpAmount,_tmpCategory,_tmpReceiptImage,_tmpPaymentStatus,_tmpPaidAtMillis,_tmpDueAtMillis,_tmpItineraryPlanId)
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

  public override fun observeCostSummary(tripId: Long): Flow<List<CostSummary>> {
    val _sql: String = """
        |
        |    SELECT 
        |        cs.userId AS payeeUserId,
        |        SUM(cs.amountOwed) AS totalOwed,
        |        CASE 
        |            WHEN SUM(CASE WHEN p.paymentStatus = 'PAID' THEN 1 ELSE 0 END) = COUNT(*) THEN 'Paid'
        |            WHEN SUM(CASE WHEN p.paymentStatus = 'PAY_NOW' THEN 1 ELSE 0 END) > 0 THEN 'Pay Now'
        |            ELSE 'Pending'
        |        END AS status
        |    FROM cost_split cs
        |    JOIN expense_payments p 
        |        ON p.expensePaymentId = cs.expensePaymentId
        |    WHERE p.tripId = ?
        |    GROUP BY cs.userId
        |""".trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, tripId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("cost_split", "expense_payments"), object
        : Callable<List<CostSummary>> {
      public override fun call(): List<CostSummary> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfPayeeUserId: Int = 0
          val _cursorIndexOfTotalOwed: Int = 1
          val _cursorIndexOfStatus: Int = 2
          val _result: MutableList<CostSummary> = ArrayList<CostSummary>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CostSummary
            val _tmpPayeeUserId: Long
            _tmpPayeeUserId = _cursor.getLong(_cursorIndexOfPayeeUserId)
            val _tmpTotalOwed: Double
            _tmpTotalOwed = _cursor.getDouble(_cursorIndexOfTotalOwed)
            val _tmpStatus: String
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus)
            _item = CostSummary(_tmpPayeeUserId,_tmpTotalOwed,_tmpStatus)
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
