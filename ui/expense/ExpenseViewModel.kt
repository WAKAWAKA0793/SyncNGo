package com.example.tripshare.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.model.CostSummary
import com.example.tripshare.data.model.ExpensePaymentEntity
import com.example.tripshare.data.model.PaymentStatus
import com.example.tripshare.data.model.PaymentWithPayer
import com.example.tripshare.data.model.SettlementTransferEntity
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.UserEntity
import com.example.tripshare.data.repo.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/* ---------- tiny date helpers ---------- */
fun LocalDate.toEpochMilliAtStartOfDay(): Long =
    this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun millisToLocalDate(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()

/* ---------- UI models & Enums ---------- */

data class SplitShare(val userId: Long, val count: Int, val amountOwed: Double)

data class SplitDetailUi(
    val userId: Long,
    val name: String,
    val photoUrl: String?,
    val amount: Double
)

sealed class BalanceHistoryItem {
    abstract val date: LocalDate
    abstract val timestamp: Long

    data class ExpensePaid(
        override val date: LocalDate,
        override val timestamp: Long,
        val paymentId: Long,
        val title: String,
        val amount: Double // Positive (Credit)
    ) : BalanceHistoryItem()

    data class ExpenseShare(
        override val date: LocalDate,
        override val timestamp: Long,
        val paymentId: Long,
        val title: String,
        val amountOwed: Double // Negative (Debt)
    ) : BalanceHistoryItem()

    data class SettlementSent(
        override val date: LocalDate,
        override val timestamp: Long,
        val receiverName: String,
        val amount: Double
    ) : BalanceHistoryItem()

    data class SettlementReceived(
        override val date: LocalDate,
        override val timestamp: Long,
        val senderName: String,
        val amount: Double
    ) : BalanceHistoryItem()
}

data class BalancePersonUi(
    val id: Long,
    val avatarUrl: String?,
    val initials: String?,
    val name: String,
    val fairShare: Double,
    val paid: Double,
    val balance: Double,   // >0 receive, <0 pay
    val isYou: Boolean
)

class ExpenseViewModel(
    private val repo: ExpenseRepository,
    private val tripId: Long,
    private val currentUserId: Long
) : ViewModel() {

    /* --------- READ: base streams from repo --------- */
    private fun r2(x: Double): Double = kotlin.math.round(x * 100.0) / 100.0
    init {
        viewModelScope.launch {
            val trip: TripEntity? = repo.getTripById(tripId)
            _tripName.value = trip?.name ?: "Trip"

            // ✅ START SYNC HERE
            repo.startSync(tripId, viewModelScope)
        }
    }
    val participants: StateFlow<List<UserEntity>> =
        repo.observeParticipants(tripId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val paymentsFlow: StateFlow<List<ExpensePaymentEntity>> =
        repo.observePayments(tripId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val costSummaryFlow: StateFlow<List<CostSummary>> =
        repo.observeCostSummary(tripId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val settlementsFlow: StateFlow<List<SettlementTransferEntity>> =
        repo.observeSettlementTransfers(tripId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /* --------- DRAFTS: Split mode (SOURCE OF TRUTH) --------- */
    private val _draftSplitMode = MutableStateFlow(SplitMode.EQUALLY)
    val draftSplitMode: StateFlow<SplitMode> = _draftSplitMode
    fun setDraftSplitMode(mode: SplitMode) { _draftSplitMode.value = mode }

    /* --------- LIST OF EXPENSES UI --------- */
    private fun mapToUi(row: PaymentWithPayer): ExpensePaymentUi {
        val p = row.payment
        val u = row.payer

        val paidOn = p.paidAtMillis?.let { millisToLocalDate(it) }
        val dueOn = p.dueAtMillis?.let { millisToLocalDate(it) }

        val effective = when (p.paymentStatus) {
            PaymentStatus.PAID -> paidOn
            else -> dueOn
        } ?: (paidOn ?: dueOn ?: LocalDate.now())

        return ExpensePaymentUi(
            expensePaymentId = p.expensePaymentId,
            title = p.title,
            amount = p.amount,
            paymentStatus = p.paymentStatus,
            category = p.category,
            effectiveOn = effective,
            dueOn = dueOn,
            paidOn = if (p.paymentStatus == PaymentStatus.PAID) paidOn else null,
            payerName = u.name,
            payerId = u.id,
            payerPhotoUrl = u.profilePhoto
        )
    }

    /**
     * EDIT: Load splits from DB into drafts.
     * Requires DB columns:
     * - cost_split.splitMode (Int ordinal)
     * - cost_split.shareCount (Int)
     */
    fun loadSplitsForEditing(expenseId: Long) {
        viewModelScope.launch {
            val allSplits = repo.observeSplits(tripId).firstOrNull() ?: return@launch
            val relevant = allSplits.filter { it.expensePaymentId == expenseId }
            if (relevant.isEmpty()) return@launch

            // Restore mode (ordinal stored in DB)
            val ordinal = relevant.first().splitMode
            val restoredMode =
                SplitMode.values().getOrNull(ordinal) ?: SplitMode.EQUALLY
            _draftSplitMode.value = restoredMode

            // Restore splits (counts + amounts)
            _draftSplitsForNextExpense.value = relevant.map {
                SplitShare(
                    userId = it.userId,
                    count = it.shareCount,
                    amountOwed = it.amountOwed
                )
            }
        }
    }

    // You can keep this, but EDIT screen should not rely on it anymore.
    fun computeSplitLabelFromDraft(): String {
        val splits = _draftSplitsForNextExpense.value
        if (splits.isEmpty()) return "Not set"
        val uniqueAmounts = splits.map { it.amountOwed }.distinct()
        return if (uniqueAmounts.size == 1) "Equal" else "Custom"
    }

    fun observeSplitsForExpense(expenseId: Long): Flow<List<SplitDetailUi>> {
        return combine(repo.observeSplits(tripId), participants) { allSplits, users ->
            val relevantSplits = allSplits.filter { it.expensePaymentId == expenseId }
            relevantSplits.mapNotNull { split ->
                val user = users.find { it.id == split.userId } ?: return@mapNotNull null
                SplitDetailUi(
                    userId = user.id,
                    name = user.name,
                    photoUrl = user.profilePhoto,
                    amount = split.amountOwed
                )
            }
        }
    }

    fun updateExpenseAmountAndPayer(
        expenseId: Long,
        newAmount: Double,
        payerUserId: Long?
    ) {
        viewModelScope.launch {
            val current = repo.observePayments(tripId)
                .map { list -> list.firstOrNull { it.expensePaymentId == expenseId } }
                .firstOrNull() ?: return@launch

            repo.updatePayment(
                current.copy(
                    amount = newAmount,
                    payerUserId = payerUserId ?: current.payerUserId,
                    payeeUserId = payerUserId ?: current.payeeUserId
                )
            )
        }
    }

    val expenses: StateFlow<List<ExpensePaymentUi>> =
        repo.observePaymentsForTripWithPayer(tripId)
            .map { rows -> rows.map { mapToUi(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getExpensesForTrip(targetTripId: Long): Flow<List<ExpensePaymentUi>> {
        return repo.observePaymentsForTripWithPayer(targetTripId)
            .map { rows -> rows.map { mapToUi(it) } }
    }

    /* --------- BUDGET / TOTALS --------- */

    val budget: StateFlow<Double> =
        repo.observeBudget(tripId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val totalSpent: StateFlow<Double> =
        repo.observePayments(tripId)
            .map { list -> list.sumOf { p -> p.amount } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val budgetRemaining: StateFlow<Double> =
        combine(budget, totalSpent) { b, spent -> b - spent }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    /* --------- BALANCES (PURELY DERIVED FROM DB) --------- */

    private fun initials(name: String): String {
        val parts = name.trim().split(" ")
        val f = parts.getOrNull(0)?.firstOrNull()?.uppercaseChar() ?: 'U'
        val s = parts.getOrNull(1)?.firstOrNull()?.uppercaseChar() ?: ' '
        return ("$f$s").trim()
    }

    val balancePeopleFlow: StateFlow<List<BalancePersonUi>> =
        combine(participants, paymentsFlow, costSummaryFlow, settlementsFlow)
        { users, payments, summaries, transfers ->

            val paidByUser: Map<Long, Double> =
                payments.groupBy { it.payerUserId }
                    .mapValues { (_, list) -> list.sumOf { it.amount } }

            val fairByUser: Map<Long, Double> =
                summaries.associate { cs -> cs.payeeUserId to cs.totalOwed }

            val map: MutableMap<Long, BalancePersonUi> =
                users.associate { u ->
                    val paid = r2(paidByUser[u.id] ?: 0.0)
                    val fair = r2(fairByUser[u.id] ?: 0.0)
                    u.id to BalancePersonUi(
                        id = u.id,
                        avatarUrl = u.profilePhoto,
                        initials = initials(u.name),
                        name = u.name,
                        fairShare = fair,
                        paid = paid,
                        balance = r2(paid - fair),
                        isYou = (u.id == currentUserId)
                    )
                }.toMutableMap()

            transfers.forEach { t ->
                map[t.payerUserId]?.let { cur ->
                    map[t.payerUserId] = cur.copy(balance = r2(cur.balance + t.amount))
                }
                map[t.receiverUserId]?.let { cur ->
                    map[t.receiverUserId] = cur.copy(balance = r2(cur.balance - t.amount))
                }
            }

            map.values.sortedBy { it.name.lowercase() }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addManualPendingExpense(
        tripId: Long,
        title: String,
        amount: Double,
        category: String = "Expense",
        dueDate: LocalDate,
        remark: String?
    ) = viewModelScope.launch {
        // ✅ FIX: Robust Payer Selection
        val validParticipants = participants.value
        val payerId = payerUserFlow.value?.id
            ?: validParticipants.find { it.id == currentUserId }?.id // Default to You
            ?: validParticipants.firstOrNull()?.id                   // Default to First in list
            ?: currentUserId                                         // Fallback to ID

        repo.addPayment(
            ExpensePaymentEntity(
                tripId = tripId,
                payerUserId = payerId,
                payeeUserId = payerId,
                title = title,
                amount = amount,
                category = category,
                paymentStatus = PaymentStatus.PENDING,
                paidAtMillis = null,
                dueAtMillis = dueDate.toEpochMilliAtStartOfDay()
            )
        )
    }


    fun applySettlement(
        tripId: Long,
        payerId: Long,
        receiverId: Long,
        amount: Double,
        currency: String
    ) = viewModelScope.launch {
        repo.addSettlementTransfer(
            tripId = tripId,
            payerUserId = payerId,
            receiverUserId = receiverId,
            amount = amount,
            currency = currency
        )
    }

    val yourTotalCostFlow: StateFlow<Double> =
        balancePeopleFlow
            .map { list -> list.firstOrNull { it.isYou }?.fairShare ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val yourNetBalanceFlow: StateFlow<Double> =
        balancePeopleFlow
            .map { list -> list.firstOrNull { it.isYou }?.balance ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    /* --------- BALANCE DETAILS LOGIC --------- */

    fun getBalanceHistoryForUser(targetUserId: Long): Flow<List<BalanceHistoryItem>> {
        return combine(
            repo.observePayments(tripId),
            repo.observeSplits(tripId),
            repo.observeSettlementTransfers(tripId),
            participants
        ) { payments, splits, transfers, users ->
            val history = mutableListOf<BalanceHistoryItem>()
            val userMap = users.associateBy { it.id }

            payments.filter { it.payerUserId == targetUserId }.forEach { p ->
                val time = p.paidAtMillis ?: p.dueAtMillis ?: System.currentTimeMillis()
                history.add(
                    BalanceHistoryItem.ExpensePaid(
                        date = millisToLocalDate(time),
                        timestamp = time,
                        paymentId = p.expensePaymentId,
                        title = p.title,
                        amount = p.amount
                    )
                )
            }

            val paymentMap = payments.associateBy { it.expensePaymentId }
            splits.filter { it.userId == targetUserId }.forEach { s ->
                val linkedPayment = paymentMap[s.expensePaymentId]
                if (linkedPayment != null) {
                    val time = linkedPayment.paidAtMillis ?: linkedPayment.dueAtMillis ?: System.currentTimeMillis()
                    history.add(
                        BalanceHistoryItem.ExpenseShare(
                            date = millisToLocalDate(time),
                            timestamp = time,
                            paymentId = linkedPayment.expensePaymentId,
                            title = linkedPayment.title,
                            amountOwed = s.amountOwed
                        )
                    )
                }
            }

            transfers.filter { it.payerUserId == targetUserId }.forEach { t ->
                val receiver = userMap[t.receiverUserId]?.name ?: "Unknown"
                history.add(
                    BalanceHistoryItem.SettlementSent(
                        date = millisToLocalDate(t.createdAtMillis),
                        timestamp = t.createdAtMillis,
                        receiverName = receiver,
                        amount = t.amount
                    )
                )
            }

            transfers.filter { it.receiverUserId == targetUserId }.forEach { t ->
                val sender = userMap[t.payerUserId]?.name ?: "Unknown"
                history.add(
                    BalanceHistoryItem.SettlementReceived(
                        date = millisToLocalDate(t.createdAtMillis),
                        timestamp = t.createdAtMillis,
                        senderName = sender,
                        amount = t.amount
                    )
                )
            }

            history.sortedByDescending { it.timestamp }
        }
    }

    fun setExpenseReminder(expensePaymentId: Long, dueDate: LocalDate) {
        viewModelScope.launch {
            repo.updateDueDate(
                expensePaymentId = expensePaymentId,
                dueAtMillis = dueDate.toEpochMilliAtStartOfDay()
            )
        }
    }

    fun observeExpenseUi(expensePaymentId: Long): Flow<ExpensePaymentUi?> {
        return repo.observePaymentsForTripWithPayer(tripId)
            .map { rows -> rows.firstOrNull { it.payment.expensePaymentId == expensePaymentId }?.let { mapToUi(it) } }
    }

    /* --------- SINGLE EXPENSE EDIT --------- */

    fun observeExpense(expenseId: Long): Flow<ExpensePaymentUi?> =
        repo.observePaymentsForTripWithPayer(tripId).map { rows ->
            rows.firstOrNull { it.payment.expensePaymentId == expenseId }?.let { row ->
                val p = row.payment
                val u = row.payer

                val paidOn = p.paidAtMillis?.let { millisToLocalDate(it) }
                val dueOn = p.dueAtMillis?.let { millisToLocalDate(it) }

                val effective = when (p.paymentStatus) {
                    PaymentStatus.PAID -> paidOn
                    else -> dueOn
                } ?: (paidOn ?: dueOn ?: LocalDate.now())

                ExpensePaymentUi(
                    expensePaymentId = p.expensePaymentId,
                    title = p.title,
                    amount = p.amount,
                    paymentStatus = p.paymentStatus,
                    category = p.category,
                    effectiveOn = effective,
                    dueOn = dueOn,
                    paidOn = paidOn,
                    payerName = u.name,
                    payerId = u.id,
                    payerPhotoUrl = u.profilePhoto
                )
            }
        }

    fun updatePayment(
        expenseId: Long,
        title: String,
        amount: Double,
        category: String,
        status: PaymentStatus,
        paidOn: LocalDate,
        payerUserId: Long? = null
    ) {
        viewModelScope.launch {
            val current = repo.observePayments(tripId)
                .map { list -> list.firstOrNull { it.expensePaymentId == expenseId } }
                .firstOrNull()

            current?.let {
                val finalPayerId = payerUserId ?: it.payerUserId

                repo.updatePayment(
                    it.copy(
                        title = title,
                        amount = amount,
                        category = category,
                        paymentStatus = status,
                        paidAtMillis = paidOn.toEpochMilliAtStartOfDay(),
                        payerUserId = finalPayerId,
                        payeeUserId = finalPayerId
                    )
                )
            }
        }
    }

    fun deletePayment(expenseId: Long) {
        viewModelScope.launch {
            val current = repo.observePayments(tripId)
                .map { list -> list.firstOrNull { it.expensePaymentId == expenseId } }
                .firstOrNull()
            current?.let { repo.deletePayment(it) }
        }
    }

    /* --------- WRITE HELPERS --------- */

    fun addPayment(
        payerUserId: Long,
        payeeUserId: Long,
        title: String,
        amount: Double,
        category: String,
        status: PaymentStatus,
        paidOn: LocalDate
    ) = viewModelScope.launch {
        repo.addPayment(
            ExpensePaymentEntity(
                tripId = tripId,
                payerUserId = payerUserId,
                payeeUserId = payeeUserId,
                title = title,
                amount = amount,
                category = category,
                paymentStatus = PaymentStatus.PENDING,
                paidAtMillis = paidOn.toEpochMilliAtStartOfDay()
            )
        )
    }

    fun updateExpensePayer(expenseId: Long, newPayerUserId: Long) {
        viewModelScope.launch {
            val current = repo.observePayments(tripId)
                .map { list -> list.firstOrNull { it.expensePaymentId == expenseId } }
                .firstOrNull()

            current?.let {
                repo.updatePayment(
                    it.copy(
                        payerUserId = newPayerUserId,
                        payeeUserId = newPayerUserId
                    )
                )
            }
        }
    }

    // ✅ FIXED: Now robustly handles null Payer and empty Splits
    // This was the function causing your "no stored" issue
    fun saveNewExpenseWithSplits(
        title: String,
        amount: Double,
        category: String,
        itineraryPlanId: Long? = null,
        status: PaymentStatus,
        paidOn: LocalDate
    ) = viewModelScope.launch {

        val validParticipants = participants.value
        if (validParticipants.isEmpty()) return@launch

        // 1️⃣ Resolve payer: Manual → You → First participant
        val payer = payerUserFlow.value
            ?: validParticipants.find { it.id == currentUserId }
            ?: validParticipants.first()

        // 2️⃣ Resolve splits: Manual → Default equal split
        var splits = draftSplitsForNextExpense.value

        if (splits.isEmpty()) {
            val share = amount / validParticipants.size
            splits = validParticipants.map {
                SplitShare(
                    userId = it.id,
                    count = 1,
                    amountOwed = share
                )
            }
        }

        // 3️⃣ Create expense
        val newExpenseId = repo.addPayment(
            ExpensePaymentEntity(
                tripId = tripId,
                payerUserId = payer.id,
                payeeUserId = payer.id,
                title = title.ifBlank { "Expense" },
                amount = amount,
                category = category,
                itineraryPlanId = itineraryPlanId,
                paymentStatus = status,
                paidAtMillis = paidOn.toEpochMilliAtStartOfDay()
            )
        )

        // 4️⃣ Save splits (REPLACE — not append)
        repo.replaceSplitsForExpense(
            expenseId = newExpenseId,
            tripId = tripId,
            splits = splits.distinctBy { it.userId }, // 🔒 safety
            splitMode = draftSplitMode.value
        )

        // 5️⃣ ✅ CLEAR DRAFT STATE (IMPORTANT)
        _draftSplitsForNextExpense.value = emptyList()
        _currentSharesMap.value = emptyMap()
        _draftSplitMode.value = SplitMode.EQUALLY
    }

    fun saveSplitsForExistingExpense(expenseId: Long, splits: List<SplitShare>) =
        viewModelScope.launch {
            repo.replaceSplitsForExpense(
                expenseId = expenseId,
                tripId = tripId,
                splits = splits,
                splitMode = draftSplitMode.value
            )
        }

    fun updateBudget(amount: Double) {
        if (tripId <= 0L) return
        viewModelScope.launch { repo.setBudget(tripId, amount) }
    }

    /* --------- DRAFTS (for AddExpense / SplitBill UI) --------- */

    private val _draftAmount = MutableStateFlow(0.0)
    val draftAmount: StateFlow<Double> = _draftAmount
    fun setDraftAmountFromText(raw: String) { _draftAmount.value = raw.toDoubleOrNull() ?: 0.0 }

    private val _draftCurrency = MutableStateFlow("MYR")
    val draftCurrency: StateFlow<String> = _draftCurrency
    fun setDraftCurrency(cur: String) { _draftCurrency.value = cur }

    private val _payerUser = MutableStateFlow<UserEntity?>(null)
    val payerUserFlow: StateFlow<UserEntity?> = _payerUser
    fun setPayerUser(user: UserEntity?) { _payerUser.value = user }

    private val _currentSharesMap = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val currentSharesMapFlow: StateFlow<Map<Long, Int>> = _currentSharesMap
    fun setShareCount(userId: Long, newCount: Int) {
        _currentSharesMap.update { old ->
            val next: MutableMap<Long, Int> = old.toMutableMap()
            next[userId] = newCount.coerceAtLeast(0)
            next
        }
    }
    fun incrementShare(userId: Long) =
        setShareCount(userId, (currentSharesMapFlow.value[userId] ?: 0) + 1)

    fun decrementShare(userId: Long) =
        setShareCount(userId, (currentSharesMapFlow.value[userId] ?: 0) - 1)

    fun resetShares() { _currentSharesMap.value = emptyMap() }

    private val _splitLabel = MutableStateFlow("Everyone 1x")
    val splitLabel: StateFlow<String> = _splitLabel
    fun updateSplitLabel(lbl: String) { _splitLabel.value = lbl }

    private val _draftSplitsForNextExpense = MutableStateFlow<List<SplitShare>>(emptyList())
    val draftSplitsForNextExpense: StateFlow<List<SplitShare>> = _draftSplitsForNextExpense
    fun setDraftSplitsForNextExpense(splits: List<SplitShare>) { _draftSplitsForNextExpense.value = splits }

    fun updateSplitFromShares(splits: List<SplitShare>) {
        val map: Map<Long, Int> = splits.associate { it.userId to it.count }
        _currentSharesMap.value = map
    }

    /* --------- Trip name --------- */

    private val _tripName = MutableStateFlow("Trip")
    val tripNameFlow: StateFlow<String> = _tripName

    init {
        viewModelScope.launch {
            val trip: TripEntity? = repo.getTripById(tripId)
            _tripName.value = trip?.name ?: "Trip"
        }
    }

    companion object {
        fun provideFactory(
            repo: ExpenseRepository,
            tripId: Long,
            currentUserId: Long
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExpenseViewModel(repo, tripId, currentUserId) as T
                }
            }
        }
    }
}