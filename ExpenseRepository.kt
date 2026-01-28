package com.example.tripshare.data.repo

import android.util.Log
import com.example.tripshare.data.db.CostSplitDao
import com.example.tripshare.data.db.ExpensePaymentDao
import com.example.tripshare.data.db.SettlementDao
import com.example.tripshare.data.db.TripDao
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.model.CostSplitEntity
import com.example.tripshare.data.model.ExpensePaymentEntity
import com.example.tripshare.data.model.PaymentStatus
import com.example.tripshare.data.model.PaymentWithPayer
import com.example.tripshare.data.model.SettlementTransferEntity
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.UserEntity
import com.example.tripshare.ui.expense.SplitMode
import com.example.tripshare.ui.expense.SplitShare
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ExpenseRepository(
    private val paymentDao: ExpensePaymentDao,
    private val splitDao: CostSplitDao,
    private val tripDao: TripDao,
    private val userDao: UserDao,
    private val settlementDao: SettlementDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun startSync(tripId: Long, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val trip = tripDao.getTripById(tripId)
            val fid = trip?.firebaseId ?: return@launch

            // A. Sync Expenses & Splits
            firestore.collection("trips").document(fid).collection("expenses")
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener

                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshots.documents) {
                            val firebaseId = doc.id
                            val title = doc.getString("title") ?: "Expense"
                            val amount = doc.getDouble("amount") ?: 0.0
                            val category = doc.getString("category") ?: "General"
                            val payerUid = doc.getString("payerUid")

                            val statusStr = doc.getString("status") ?: "PENDING"
                            val paymentStatus = try {
                                PaymentStatus.valueOf(statusStr)
                            } catch (e: Exception) {
                                PaymentStatus.PENDING
                            }

                            val paidAt = doc.getLong("paidAt")

                            // Resolve Payer
                            val payerUser = payerUid?.let { userDao.findByFirebaseId(it) }
                            val payerId = payerUser?.id ?: 0L

                            // ✅ FIX: DUPLICATE PREVENTION LOGIC
                            // 1. Try finding by Firebase ID
                            var existing = paymentDao.getPaymentByFirebaseId(firebaseId)

                            // 2. If not found, try finding a local "Ghost" duplicate (Same title/amount, no Firebase ID)
                            if (existing == null) {
                                existing = paymentDao.findPendingPayment(tripId, title, amount)
                            }

                            val finalId: Long

                            if (existing != null) {
                                val updateEntity = existing.copy(
                                    firebaseId = firebaseId, // Ensure FID is linked
                                    tripId = tripId,
                                    payerUserId = payerId,
                                    payeeUserId = payerId,
                                    title = title,
                                    amount = amount,
                                    category = category,
                                    paymentStatus = paymentStatus,
                                    paidAtMillis = paidAt
                                )
                                paymentDao.updatePayment(updateEntity)
                                finalId = existing.expensePaymentId
                            } else {
                                val newEntity = ExpensePaymentEntity(
                                    expensePaymentId = 0L,
                                    firebaseId = firebaseId,
                                    tripId = tripId,
                                    payerUserId = payerId,
                                    payeeUserId = payerId,
                                    title = title,
                                    amount = amount,
                                    category = category,
                                    paymentStatus = paymentStatus,
                                    paidAtMillis = paidAt
                                )
                                finalId = paymentDao.insertPayment(newEntity)
                            }

                            // Sync Nested Splits
                            val splitsList = doc.get("splits") as? List<Map<String, Any>>
                            if (splitsList != null) {
                                // Clear old splits for this expense to prevent duplicates
                                splitDao.deleteSplitsForExpense(finalId)

                                val splitEntities = splitsList.mapNotNull { splitMap ->
                                    val uid = splitMap["userId"] as? String
                                    val owed = (splitMap["amount"] as? Number)?.toDouble() ?: 0.0
                                    val localU = uid?.let { userDao.findByFirebaseId(it) }

                                    if (localU != null) {
                                        CostSplitEntity(
                                            tripId = tripId,
                                            expensePaymentId = finalId,
                                            userId = localU.id,
                                            amountOwed = owed,
                                            status = PaymentStatus.PENDING,
                                            splitMode = 0,
                                            shareCount = 1
                                        )
                                    } else null
                                }
                                splitDao.insertSplits(splitEntities)
                            }
                        }
                    }
                }

            // B. Sync Settlements (kept as is)
            firestore.collection("trips").document(fid).collection("settlements")
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener
                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshots.documents) {
                            val settlementFid = doc.id
                            val amount = doc.getDouble("amount") ?: 0.0
                            val payerUid = doc.getString("payerUid")
                            val receiverUid = doc.getString("receiverUid")

                            val payer = payerUid?.let { userDao.findByFirebaseId(it) }
                            val receiver = receiverUid?.let { userDao.findByFirebaseId(it) }

                            if (payer != null && receiver != null) {
                                val existing = settlementDao.getSettlementByFirebaseId(settlementFid)
                                if (existing == null) { // Only insert if new
                                    val ent = SettlementTransferEntity(
                                        id = 0L,
                                        firebaseId = settlementFid,
                                        tripId = tripId,
                                        payerUserId = payer.id,
                                        receiverUserId = receiver.id,
                                        amount = amount,
                                        currency = doc.getString("currency") ?: "MYR",
                                        createdAtMillis = doc.getLong("timestamp") ?: System.currentTimeMillis()
                                    )
                                    settlementDao.insertTransfer(ent)
                                }
                            }
                        }
                    }
                }
        }
    }

    /* ─────────────────────────── UPLOAD HELPERS ─────────────────────────── */

    private suspend fun uploadExpense(payment: ExpensePaymentEntity) {
        val trip = tripDao.getTripById(payment.tripId) ?: return
        val fid = trip.firebaseId

        if (fid == null) {
            Log.e("Repo", "Cannot upload expense: Trip has no Firebase ID")
            return
        }

        val payer = userDao.getUserById(payment.payerUserId)
        val payerUid = payer?.firebaseId ?: ""

        // Generate ID if missing
        val docRef = if (payment.firebaseId != null) {
            firestore.collection("trips").document(fid).collection("expenses").document(payment.firebaseId)
        } else {
            firestore.collection("trips").document(fid).collection("expenses").document()
        }

        // IMPORTANT: Update local DB with the new Cloud ID so future updates/deletes work
        if (payment.firebaseId == null) {
            paymentDao.updatePayment(payment.copy(firebaseId = docRef.id))
        }

        val data = hashMapOf(
            "title" to payment.title,
            "amount" to payment.amount,
            "category" to payment.category,
            "payerUid" to payerUid,
            "status" to payment.paymentStatus.name,
            "paidAt" to payment.paidAtMillis,
            "updatedAt" to System.currentTimeMillis()
        )

        try {
            docRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun deleteExpenseFromCloud(payment: ExpensePaymentEntity) {
        val trip = tripDao.getTripById(payment.tripId) ?: return
        val tripFid = trip.firebaseId ?: return
        val paymentFid = payment.firebaseId ?: return

        try {
            firestore.collection("trips").document(tripFid)
                .collection("expenses").document(paymentFid)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun uploadSplits(tripId: Long, expenseId: Long) {
        val trip = tripDao.getTripById(tripId) ?: return
        val fid = trip.firebaseId ?: return

        // Fetch payment to get its Firebase ID
        val payment = paymentDao.getPaymentById(expenseId) ?: return
        val paymentFid = payment.firebaseId ?: return

        // Fetch splits
        val splits = splitDao.observeSplits(tripId).firstOrNull()?.filter { it.expensePaymentId == expenseId } ?: return

        val splitMaps = splits.mapNotNull { split ->
            val u = userDao.getUserById(split.userId)
            if (u?.firebaseId != null) {
                mapOf(
                    "userId" to u.firebaseId,
                    "amount" to split.amountOwed
                )
            } else null
        }

        try {
            firestore.collection("trips").document(fid)
                .collection("expenses").document(paymentFid)
                .update("splits", splitMaps)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* ─────────────────────────── PUBLIC METHODS ─────────────────────────── */

    fun observePaymentsForTripWithPayer(tripId: Long) = paymentDao.observePaymentsWithPayer(tripId).map { rows ->
        rows.map { row ->
            PaymentWithPayer(
                payment = ExpensePaymentEntity(
                    expensePaymentId = row.expensePaymentId,
                    firebaseId = null, // simplified
                    tripId = row.tripId,
                    payerUserId = row.payerUserId,
                    payeeUserId = row.payeeUserId,
                    title = row.title,
                    amount = row.amount,
                    category = row.category,
                    paymentStatus = row.paymentStatus,
                    paidAtMillis = row.paidAtMillis,
                    dueAtMillis = row.dueAtMillis
                ),
                payer = UserEntity(id = row.payerId, name = row.payerName, profilePhoto = row.payerPhoto),
                payerUserId = row.payerUserId
            )
        }
    }

    fun observePayments(tripId: Long): Flow<List<ExpensePaymentEntity>> =
        paymentDao.observePayments(tripId)

    fun observeCostSummary(tripId: Long) =
        paymentDao.observeCostSummary(tripId)

    suspend fun addPayment(payment: ExpensePaymentEntity): Long {
        val id = paymentDao.insertPayment(payment)
        // 🚀 Trigger Cloud Upload
        uploadExpense(payment.copy(expensePaymentId = id))
        return id
    }

    suspend fun updatePayment(payment: ExpensePaymentEntity) {
        paymentDao.updatePayment(payment)
        uploadExpense(payment)
    }

    // ✅ UPDATED: Delete Local AND Cloud
    suspend fun deletePayment(payment: ExpensePaymentEntity) {
        // 1. Delete from cloud first (or fire and forget)
        deleteExpenseFromCloud(payment)
        // 2. Delete locally
        paymentDao.deletePayment(payment)
    }

    /* ─────────────────────────── Splits & Others ─────────────────────────── */

    fun observeSplits(tripId: Long): Flow<List<CostSplitEntity>> =
        splitDao.observeSplits(tripId)

    suspend fun replaceSplitsForExpense(
        expenseId: Long,
        tripId: Long,
        splits: List<SplitShare>,
        splitMode: SplitMode
    ) {
        // ✅ remove duplicates (same userId)
        val uniqueSplits = splits.distinctBy { it.userId }

        splitDao.deleteSplitsForExpense(expenseId)

        val rows = uniqueSplits.map { s ->
            CostSplitEntity(
                tripId = tripId,
                expensePaymentId = expenseId,
                userId = s.userId,
                amountOwed = s.amountOwed,
                status = PaymentStatus.PENDING,
                splitMode = splitMode.ordinal,
                shareCount = s.count
            )
        }

        splitDao.insertSplits(rows)

        uploadSplits(tripId, expenseId)
    }

    suspend fun updateSplit(split: CostSplitEntity) = splitDao.updateSplit(split)

    fun observeSettlementTransfers(tripId: Long) = settlementDao.observeTransfersForTrip(tripId)

    suspend fun addSettlementTransfer(
        tripId: Long,
        payerUserId: Long,
        receiverUserId: Long,
        amount: Double,
        currency: String
    ): Long {
        val localId = settlementDao.insertTransfer(
            SettlementTransferEntity(
                tripId = tripId,
                payerUserId = payerUserId,
                receiverUserId = receiverUserId,
                amount = amount,
                currency = currency
            )
        )

        val trip = tripDao.getTripById(tripId)
        val payer = userDao.getUserById(payerUserId)
        val receiver = userDao.getUserById(receiverUserId)

        if (trip?.firebaseId != null && payer?.firebaseId != null && receiver?.firebaseId != null) {
            val ref = firestore.collection("trips").document(trip.firebaseId).collection("settlements").document()
            val map = hashMapOf(
                "amount" to amount,
                "currency" to currency,
                "payerUid" to payer.firebaseId,
                "receiverUid" to receiver.firebaseId,
                "timestamp" to System.currentTimeMillis()
            )
            ref.set(map)
        }
        return localId
    }

    fun observeBudget(tripId: Long): Flow<Double> = paymentDao.observeBudget(tripId)
    suspend fun setBudget(tripId: Long, amount: Double) = paymentDao.updateBudget(tripId, amount)

    fun observeParticipants(tripId: Long): Flow<List<UserEntity>> = userDao.observeParticipants(tripId)
    suspend fun getTripById(tripId: Long): TripEntity? = tripDao.getTripById(tripId)
    suspend fun updateDueDate(expensePaymentId: Long, dueAtMillis: Long?) = paymentDao.updateDueDate(expensePaymentId, dueAtMillis)
    fun observePlanLinkedPayments(tripId: Long): Flow<List<ExpensePaymentEntity>> = paymentDao.observePlanLinkedPayments(tripId)
}