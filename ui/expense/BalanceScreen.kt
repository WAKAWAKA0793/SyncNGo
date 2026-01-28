package com.example.tripshare.ui.expense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlin.math.abs
import kotlin.math.min

/* ---------- helpers ---------- */

private fun money(amount: Double, currency: String) =
    "$currency ${String.format("%.2f", amount)}"

enum class BudgetTab { Expenses, Balance }

/* ---------- Settlement model & matcher ---------- */

data class SettlementUi(
    val fromUserId: Long,
    val fromName: String,
    val toUserId: Long,
    val toName: String,
    val amount: Double,
    val fromIsYou: Boolean,
    val toIsYou: Boolean
)

/**
 * Turn net balances into minimal transfers.
 * Input: BalancePersonUi (balance > 0 means RECEIVE; < 0 means PAY).
 */
private fun buildSettlements(
    people: List<BalancePersonUi>
): List<SettlementUi> {
    if (people.isEmpty()) return emptyList()

    val receivers = people
        .filter { it.balance > 1e-6 }
        .map { it.copy(balance = it.balance) }
        .toMutableList()

    val payers = people
        .filter { it.balance < -1e-6 }
        .map { it.copy(balance = it.balance) }
        .toMutableList()

    receivers.sortByDescending { it.balance }
    payers.sortBy { it.balance }

    val out = mutableListOf<SettlementUi>()
    var i = 0
    var j = 0
    while (i < payers.size && j < receivers.size) {
        val payer = payers[i]
        val recv = receivers[j]
        val payAbs = -payer.balance
        val recAbs = recv.balance
        val amt = min(payAbs, recAbs)

        if (amt > 1e-6) {
            out += SettlementUi(
                fromUserId = payer.id,
                fromName = payer.name,
                toUserId = recv.id,
                toName = recv.name,
                amount = amt,
                fromIsYou = payer.isYou,
                toIsYou = recv.isYou
            )
        }

        payers[i] = payer.copy(balance = payer.balance + amt)
        receivers[j] = recv.copy(balance = recv.balance - amt)

        if (payers[i].balance >= -1e-6) i++
        if (receivers[j].balance <= 1e-6) j++
    }
    return out
}

/* ---------- SCREEN ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    tripId: Long,
    vm: ExpenseViewModel,
    currencyCode: String,
    onBackClick: () -> Unit,
    onSettleClick: () -> Unit,     // optional persistence hook
    onNavExpenses: () -> Unit,
    onNavInsights: () -> Unit,
    onNavAddExpense: () -> Unit,
    onNavBalance: () -> Unit,
    onNavExport: () -> Unit,
    onPersonClick: (Long) -> Unit,
) {
    val vmPeople by vm.balancePeopleFlow.collectAsState()

    // Editable local copy of balances (for instant UI feedback)
    val peopleState = remember(vmPeople) {
        vmPeople.map { it.copy() }.toMutableStateList()
    }

    // Build settlements fresh each recomposition so the section disappears when done
    val settlements = buildSettlements(peopleState)

    var selectedSettlement by remember { mutableStateOf<SettlementUi?>(null) }
    val epsilon = 1e-6

    // Apply settlement to local balances (UI)
    fun applySettlementLocally(s: SettlementUi) {
        val payerIdx = peopleState.indexOfFirst { it.id == s.fromUserId }
        val recvIdx  = peopleState.indexOfFirst { it.id == s.toUserId }
        if (payerIdx == -1 || recvIdx == -1) return

        val payer = peopleState[payerIdx]
        val recv  = peopleState[recvIdx]

        val newPayerBal = payer.balance + s.amount   // payer moves toward 0 (less negative)
        val newRecvBal  = recv.balance - s.amount    // receiver moves toward 0 (less positive)

        peopleState[payerIdx] = payer.copy(balance = if (abs(newPayerBal) < epsilon) 0.0 else newPayerBal)
        peopleState[recvIdx]  = recv.copy(balance  = if (abs(newRecvBal)  < epsilon) 0.0 else newRecvBal)
    }

    val bg = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val accent = Color(0xFFFFA000)
    val fabYellow = Color(0xFFFFC928)

    // NEW: Scaffold wraps the screen to provide the TopAppBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Balance",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to expenses"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bg,
                    titleContentColor = onSurface,
                    navigationIconContentColor = onSurface
                )
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(innerPadding) // NEW: Apply Scaffold padding here
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 110.dp),
                // REMOVED: contentPadding = WindowInsets.statusBars.asPaddingValues()
                // (Scaffold handles top inset now)
            ) {
                if (settlements.isNotEmpty()) {
                    item {
                        Text(
                            "Suggested settlements",
                            color = onSurface,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                    items(settlements) { s ->
                        SettlementRow(
                            s = s,
                            currencyCode = currencyCode,
                            onClick = { selectedSettlement = s },
                            cardColor = surface,
                            youBadgeBg = Color(0xFFF1F5F9),
                            youBadgeText = Color(0xFF475569),
                            settledTint = Color(0xFFEAF7F1),
                            textColor = onSurface,
                            subTextColor = onSurfaceVariant
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                    item { Spacer(Modifier.height(10.dp)) }
                }

                item {
                    Text(
                        "Details",
                        color = onSurface,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }

                items(items = peopleState, key = { it.id }) { person ->
                    PersonBalanceRow(
                        person = person,
                        currencyCode = currencyCode,
                        cardColor = surface,
                        avatarBg = Color(0xFFE9F0F2),
                        initialsColor = Color(0xFF45606A),
                        positiveColor = Color(0xFF0F9D58),   // green
                        negativeColor = Color(0xFFD93025),   // red
                        neutralColor = onSurfaceVariant,
                        textColor = onSurface,
                        onClick = { onPersonClick(person.id) }
                    )
                    Spacer(Modifier.height(8.dp))
                }

                item { Spacer(Modifier.height(24.dp)) }
            }

            // Bottom bar (light style)
            BudgetBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                activeTab = BudgetTab.Balance,
                onFabClick = onNavAddExpense,
                onNavExpenses = onNavExpenses,
                onNavInsights = onNavInsights,
                onNavBalance = onNavBalance,
                onNavExport = onNavExport,
                barColor = surface,
                activeColor = accent,
                inactiveColor = Color(0xFF8A8F93),
                fabColor = fabYellow,
                fabIconColor = Color(0xFF003D37)
            )

            // Dialog: confirm settlement -> persist + local UI
            selectedSettlement?.let { s ->
                AlertDialog(
                    onDismissRequest = { selectedSettlement = null },
                    title = { Text("Settle this now?") },
                    text = {
                        Text(
                            if (s.fromIsYou) "You pay ${money(s.amount, currencyCode)} to ${s.toName}."
                            else if (s.toIsYou) "${s.fromName} pays you ${money(s.amount, currencyCode)}."
                            else "${s.fromName} pays ${s.toName} ${money(s.amount, currencyCode)}."
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            // persist
                            vm.applySettlement(
                                tripId = tripId,
                                payerId = s.fromUserId,
                                receiverId = s.toUserId,
                                amount = s.amount,
                                currency = currencyCode
                            )
                            // local instant feedback (optional but nice)
                            applySettlementLocally(s)

                            selectedSettlement = null
                            onSettleClick()
                        }) { Text("Mark as settled") }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedSettlement = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
/* ---------- Settlement row (bright) ---------- */

@Composable
private fun SettlementRow(
    s: SettlementUi,
    currencyCode: String,
    onClick: () -> Unit,
    cardColor: Color,
    youBadgeBg: Color,
    youBadgeText: Color,
    settledTint: Color,
    textColor: Color,
    subTextColor: Color
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = cardColor,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (s.fromIsYou) "You" else s.fromName,
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    if (s.fromIsYou) {
                        Spacer(Modifier.width(6.dp))
                        Surface(color = youBadgeBg, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                "Me",
                                color = youBadgeText,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text("  →  ", color = subTextColor)
                    Text(
                        if (s.toIsYou) "You" else s.toName,
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    if (s.toIsYou) {
                        Spacer(Modifier.width(6.dp))
                        Surface(color = youBadgeBg, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                "Me",
                                color = youBadgeText,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    if (s.fromIsYou) "You need to pay"
                    else if (s.toIsYou) "You will receive"
                    else "Payment",
                    color = subTextColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                money(s.amount, currencyCode),
                color = textColor,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

/* ---------- Person row (bright) ---------- */

@Composable
fun PersonBalanceRow(
    person: BalancePersonUi,
    currencyCode: String,
    cardColor: Color,
    avatarBg: Color,
    initialsColor: Color,
    positiveColor: Color,
    negativeColor: Color,
    neutralColor: Color,
    textColor: Color,
    onClick: () -> Unit = {}
) {
    val absBalance = abs(person.balance)
    val rightNumberText = buildString {
        when {
            person.balance > 0 -> append("+")
            person.balance < 0 -> append("-")
        }
        append(money(absBalance, currencyCode))
    }
    val rightColor = when {
        person.balance > 0 -> positiveColor
        person.balance < 0 -> negativeColor
        else -> neutralColor
    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = cardColor,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (!person.avatarUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(person.avatarUrl),
                        contentDescription = null,
                        modifier = Modifier.size(44.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(shape = CircleShape, color = avatarBg, modifier = Modifier.size(44.dp)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(person.initials ?: "?", color = initialsColor, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            person.name,
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (person.isYou) {
                            Spacer(Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFF1F5F9)) {
                                Text(
                                    "Me",
                                    color = Color(0xFF475569),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    val line = when {
                        person.balance > 0 -> "Will receive ${money(absBalance, currencyCode)}"
                        person.balance < 0 -> "Needs to pay ${money(absBalance, currencyCode)}"
                        else -> "Settled"
                    }
                    val lineColor = when {
                        person.balance > 0 -> positiveColor
                        person.balance < 0 -> negativeColor
                        else -> neutralColor
                    }
                    Text(line, color = lineColor, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Text(
                rightNumberText,
                color = rightColor,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

/* ---------- Bottom bar (bright) ---------- */

@Composable
fun BudgetBottomBar(
    modifier: Modifier = Modifier,
    activeTab: BudgetTab,
    onFabClick: () -> Unit,
    onNavExpenses: () -> Unit,
    onNavInsights: () -> Unit,
    onNavBalance: () -> Unit,
    onNavExport: () -> Unit,
    barColor: Color = MaterialTheme.colorScheme.surface,
    activeColor: Color = Color(0xFFFFA000),
    inactiveColor: Color = Color(0xFF8A8F93),
    fabColor: Color = Color(0xFFFFC928),
    fabIconColor: Color = Color(0xFF003D37)
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        shape = RoundedCornerShape(28.dp),
        color = barColor,
        tonalElevation = 2.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .heightIn(min = 72.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).clickable { onNavExpenses() }
            ) {
                Text(
                    "Expenses",
                    color = if (activeTab == BudgetTab.Expenses) activeColor else inactiveColor,
                    fontWeight = if (activeTab == BudgetTab.Expenses) FontWeight.SemiBold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(fabColor)
                    .clickable { onFabClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add expense", tint = fabIconColor, modifier = Modifier.size(32.dp))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).clickable { onNavBalance() }
            ) {
                Text(
                    "Balance",
                    color = if (activeTab == BudgetTab.Balance) activeColor else inactiveColor,
                    fontWeight = if (activeTab == BudgetTab.Balance) FontWeight.SemiBold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
