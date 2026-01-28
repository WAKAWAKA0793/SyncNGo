package com.example.tripshare.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tripshare.data.model.CostSplitEntity
import com.example.tripshare.data.model.CostSummary
import com.example.tripshare.data.model.ExpensePaymentEntity
import com.example.tripshare.data.model.PaymentJoinRow
import com.example.tripshare.data.model.SettlementTransferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpensePaymentDao {
    // ✅ FIX: Cleaned up the ORDER BY syntax (removed hanging comma)
    @Query("""
    SELECT 
        p.expensePaymentId AS expensePaymentId,
        p.tripId AS tripId,
        p.payerUserId AS payerUserId,
        p.payeeUserId AS payeeUserId,
        p.title AS title,
        p.amount AS amount,
        p.category AS category,
        p.paymentStatus AS paymentStatus,
        p.paidAtMillis AS paidAtMillis,
        p.dueAtMillis AS dueAtMillis,
        COALESCE(u.id, p.payerUserId) AS payerId,        -- Fallback to payment's payerId
        COALESCE(u.name, 'Unknown') AS payerName,        -- Fallback to 'Unknown'
        u.profilePhoto AS payerPhoto
    FROM expense_payments p
    LEFT JOIN users u ON u.id = p.payerUserId
    WHERE p.tripId = :tripId
    ORDER BY 
        COALESCE(p.dueAtMillis, p.paidAtMillis, p.expensePaymentId) DESC,
        p.expensePaymentId DESC
""")
    fun observePaymentsWithPayer(tripId: Long): Flow<List<PaymentJoinRow>>

    // ✅ ADD: Direct lookup for Repository efficiency
    @Query("SELECT * FROM expense_payments WHERE expensePaymentId = :id LIMIT 1")
    suspend fun getPaymentById(id: Long): ExpensePaymentEntity?

    @Query("""
        SELECT * FROM expense_payments 
        WHERE tripId = :tripId 
          AND firebaseId IS NULL 
          AND title = :title 
          AND amount = :amount 
        LIMIT 1
    """)
    suspend fun findPendingPayment(tripId: Long, title: String, amount: Double): ExpensePaymentEntity?

    @Query("SELECT * FROM expense_payments WHERE firebaseId = :fid LIMIT 1")
    suspend fun getPaymentByFirebaseId(fid: String): ExpensePaymentEntity?

    @Query("SELECT * FROM expense_payments WHERE tripId = :tripId AND itineraryPlanId IS NOT NULL")
    fun observePlanLinkedPayments(tripId: Long): Flow<List<ExpensePaymentEntity>>

    @Query("UPDATE expense_payments SET dueAtMillis = :dueAtMillis WHERE expensePaymentId = :expensePaymentId")
    suspend fun updateDueDate(expensePaymentId: Long, dueAtMillis: Long?)

    @Query("SELECT budget FROM trips WHERE id = :tripId LIMIT 1")
    fun observeBudget(tripId: Long): Flow<Double>

    @Query("UPDATE trips SET budget = :amount WHERE id = :tripId")
    suspend fun updateBudget(tripId: Long, amount: Double)

    @Query("SELECT * FROM expense_payments WHERE tripId = :tripId ORDER BY expensePaymentId DESC")
    fun observePayments(tripId: Long): Flow<List<ExpensePaymentEntity>>

    @Insert
    suspend fun insertPayment(entity: ExpensePaymentEntity): Long

    @Update
    suspend fun updatePayment(payment: ExpensePaymentEntity)

    @Delete
    suspend fun deletePayment(payment: ExpensePaymentEntity)

    @Query("""
    SELECT 
        cs.userId AS payeeUserId,
        SUM(cs.amountOwed) AS totalOwed,
        CASE 
            WHEN SUM(CASE WHEN p.paymentStatus = 'PAID' THEN 1 ELSE 0 END) = COUNT(*) THEN 'Paid'
            WHEN SUM(CASE WHEN p.paymentStatus = 'PAY_NOW' THEN 1 ELSE 0 END) > 0 THEN 'Pay Now'
            ELSE 'Pending'
        END AS status
    FROM cost_split cs
    JOIN expense_payments p 
        ON p.expensePaymentId = cs.expensePaymentId
    WHERE p.tripId = :tripId
    GROUP BY cs.userId
""")
    fun observeCostSummary(tripId: Long): Flow<List<CostSummary>>
}

@Dao
interface CostSplitDao {
    @Query("SELECT * FROM cost_split WHERE tripId = :tripId")
    fun observeSplits(tripId: Long): Flow<List<CostSplitEntity>>

    @Insert
    suspend fun insertSplits(splits: List<CostSplitEntity>)

    @Update
    suspend fun updateSplit(split: CostSplitEntity)

    @Query("DELETE FROM cost_split WHERE expensePaymentId = :expenseId")
    suspend fun deleteSplitsForExpense(expenseId: Long)
}

@Dao
interface SettlementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(entity: SettlementTransferEntity): Long

    @Query("SELECT * FROM settlement_transfers WHERE firebaseId = :fid LIMIT 1")
    suspend fun getSettlementByFirebaseId(fid: String): SettlementTransferEntity?

    @Query("""
        SELECT * FROM settlement_transfers 
        WHERE tripId = :tripId 
        ORDER BY createdAtMillis DESC
    """)
    fun observeTransfersForTrip(tripId: Long): Flow<List<SettlementTransferEntity>>
}