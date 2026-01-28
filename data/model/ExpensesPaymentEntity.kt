package com.example.tripshare.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


enum class PaymentStatus {
    PAID,
    PENDING,
    PAY_NOW;

    fun label(): String {
        return when (this) {
            PAID -> "Paid"
            PENDING -> "Pending"
            PAY_NOW -> "Pay Now"
        }
    }
}

@Entity(tableName = "expense_payments")
data class ExpensePaymentEntity(
    @PrimaryKey(autoGenerate = true) val expensePaymentId: Long = 0L,
    val tripId: Long,
    val firebaseId: String? = null,
    val payerUserId: Long,
    val payeeUserId: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val receiptImage: String? = null,
    val paymentStatus: PaymentStatus,
    val paidAtMillis: Long? = null,
    val dueAtMillis: Long? = null,
    val itineraryPlanId: Long? = null
)
@Entity(tableName = "settlement_transfers")
data class SettlementTransferEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tripId: Long,
    val firebaseId: String? = null,
    val payerUserId: Long,      // debtor
    val receiverUserId: Long,   // creditor
    val amount: Double,
    val currency: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "cost_split")
data class CostSplitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val firebaseId: String? = null,
    val expensePaymentId: Long,
    val userId: Long,
    val amountOwed: Double,
    val status: PaymentStatus,
    val splitMode: Int,
    val shareCount: Int
)


data class CostSummary(
    val payeeUserId: Long,
    val totalOwed: Double,
    val status: String
)

data class PaymentWithPayer(
    val payment: ExpensePaymentEntity,
    val payer: UserEntity,
    val payerUserId: Long
)

data class PaymentJoinRow(
    // from ExpensePaymentEntity
    val expensePaymentId: Long,
    val tripId: Long,
    val payerUserId: Long,
    val payeeUserId: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val paymentStatus: PaymentStatus,
    val paidAtMillis: Long?,
    val dueAtMillis: Long?,

    val payerId: Long,
    val payerName: String,
    val payerPhoto: String?
)