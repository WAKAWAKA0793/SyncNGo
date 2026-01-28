// ui/expense/SplitBillScreen.kt
package com.example.tripshare.ui.expense

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tripshare.data.model.UserEntity
import kotlin.math.abs
import kotlin.math.max

enum class SplitMode { EQUALLY, PARTS, AMOUNTS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillScreen(
    vm: ExpenseViewModel,
    totalAmount: Double,
    currency: String,
    participants: List<UserEntity>,
    initialShares: Map<Long, Int> = emptyMap(),
    payerUser: UserEntity?,
    onPickPayerDone: (UserEntity?) -> Unit,
    expenseId: Long? = null, // pass only when editing
    onDone: (label: String, splits: List<SplitShare>, payer: UserEntity?) -> Unit,
    onBack: () -> Unit
) {
    val draftSplits by vm.draftSplitsForNextExpense.collectAsState()
    val draftMode by vm.draftSplitMode.collectAsState()

    /* ---------------- State ---------------- */
    var currentPayer by remember { mutableStateOf(payerUser) }
    var showPayerSheet by remember { mutableStateOf(false) }

    var mode by remember { mutableStateOf(SplitMode.EQUALLY) }
    var showModeMenu by remember { mutableStateOf(false) }

    // who is included in this split
    val selected = remember(participants) {
        mutableStateMapOf<Long, Boolean>().apply {
            participants.forEach { this[it.id] = true }
        }
    }

    // parts counters per user (for PARTS)
    val counts = remember(participants) {
        mutableStateMapOf<Long, Int>().apply {
            // base default
            participants.forEach { this[it.id] = 1 }

            // initial shares from caller (optional)
            if (initialShares.isNotEmpty()) {
                initialShares.forEach { (uid, c) -> this[uid] = c }
            }
        }
    }

    // manual amounts per user (for AMOUNTS)
    val owedOverrides = remember(participants) {
        mutableStateMapOf<Long, String>().apply {
            participants.forEach { this[it.id] = "" }
        }
    }

    var hasInitialized by remember { mutableStateOf(false) }

    /**
     * INIT: Use VM Drafts as source of truth (mode + splits).
     * - NO MORE guessing mode from amounts
     * - Restore:
     *    - selected users
     *    - PARTS counts
     *    - AMOUNTS textfields
     */
    LaunchedEffect(draftSplits, draftMode, participants) {
        if (!hasInitialized && participants.isNotEmpty()) {
            mode = draftMode

            // reset selection
            participants.forEach { selected[it.id] = false }

            if (draftSplits.isNotEmpty()) {
                draftSplits.forEach { split ->
                    selected[split.userId] = true

                    // restore counts for PARTS
                    counts[split.userId] = split.count

                    // restore amount overrides for AMOUNTS
                    if (split.amountOwed > 0) {
                        owedOverrides[split.userId] = String.format("%.2f", split.amountOwed)
                    }
                }
            } else {
                // no draft splits => default select all
                participants.forEach { selected[it.id] = true }
            }

            // ensure payer is selected
            currentPayer?.id?.let { payerId -> selected[payerId] = true }

            hasInitialized = true
        }
    }

    /* ---------------- Helpers ---------------- */
    fun selectedIds(): List<Long> =
        participants.map { it.id }.filter { selected[it] == true }

    fun selectedCount(): Int = selectedIds().size

    fun amountFor(userId: Long): Double {
        return when (mode) {
            SplitMode.EQUALLY -> {
                if (selected[userId] != true) 0.0
                else totalAmount / selectedCount().coerceAtLeast(1)
            }

            SplitMode.PARTS -> {
                if (selected[userId] != true) 0.0
                else {
                    val totalShares = counts
                        .filter { (uid, _) -> selected[uid] == true }
                        .values
                        .sum()
                        .coerceAtLeast(1)

                    val perShare = totalAmount / totalShares
                    val c = counts[userId] ?: 0
                    perShare * c
                }
            }

            SplitMode.AMOUNTS -> {
                if (selected[userId] != true) 0.0
                else (owedOverrides[userId]?.toDoubleOrNull() ?: 0.0)
            }
        }
    }

    fun netForUserAfterPayer(userId: Long): Double {
        if (selected[userId] != true) return 0.0
        val payerId = currentPayer?.id
        val raw = amountFor(userId)
        return if (payerId != null && userId == payerId) {
            val others = participants
                .filter { it.id != payerId && selected[it.id] == true }
                .sumOf { amountFor(it.id) }
            -others
        } else raw
    }

    fun normalizeManualAmounts() {
        if (mode != SplitMode.AMOUNTS) return
        val entered = participants
            .filter { selected[it.id] == true }
            .associate { u -> u.id to (owedOverrides[u.id]?.toDoubleOrNull() ?: 0.0) }

        val sum = entered.values.sum()
        if (sum == 0.0) return

        val factor = totalAmount / sum
        entered.forEach { (uid, v) ->
            owedOverrides[uid] = String.format("%.2f", v * factor)
        }

        // clear non-selected
        participants.filter { selected[it.id] != true }.forEach { owedOverrides[it.id] = "" }
    }

    fun pillLabel(): String {
        val base = when (mode) {
            SplitMode.EQUALLY -> "Equally"
            SplitMode.PARTS -> {
                if (participants.isNotEmpty()) {
                    val first = counts[participants.first().id] ?: 0
                    val allSame = participants
                        .filter { selected[it.id] == true }
                        .all { (counts[it.id] ?: 0) == first }
                    if (first > 0 && allSame) "Everyone ${first}x" else "As Parts"
                } else "As Parts"
            }

            SplitMode.AMOUNTS -> "As Amounts"
        }
        return "$base (${selectedCount()} selected)"
    }

    fun resetSplit() {
        if (participants.isEmpty()) return
        val firstId = participants.first().id
        counts.keys.forEach { uid -> counts[uid] = if (uid == firstId) 1 else 0 }
        owedOverrides.keys.forEach { uid -> owedOverrides[uid] = "" }
        participants.forEach { selected[it.id] = true }
        mode = SplitMode.PARTS
        vm.setDraftSplitMode(SplitMode.PARTS)
    }

    fun everyone1x() {
        counts.keys.forEach { counts[it] = 1 }
        owedOverrides.keys.forEach { owedOverrides[it] = "" }
        mode = SplitMode.PARTS
        vm.setDraftSplitMode(SplitMode.PARTS)
    }

    fun toggleSelectAll() {
        val allSelected = participants.all { selected[it.id] == true }
        if (allSelected) {
            participants.forEach { u ->
                selected[u.id] = (u.id == currentPayer?.id)
            }
        } else {
            participants.forEach { selected[it.id] = true }
        }
    }

    fun toggleOne(uid: Long) {
        if (uid == currentPayer?.id) return // never deselect payer
        selected[uid] = selected[uid] != true
    }

    /* ---------------- UI ---------------- */

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        "Split the bill",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (mode == SplitMode.AMOUNTS) normalizeManualAmounts()

                            val label = pillLabel()

                            val splits = participants.map { u ->
                                SplitShare(
                                    userId = u.id,
                                    count = (counts[u.id] ?: 0).takeIf { selected[u.id] == true } ?: 0,
                                    amountOwed = amountFor(u.id)
                                )
                            }.filter { selected[it.userId] == true }

                            val newTotal = splits.sumOf { it.amountOwed }
                            vm.setDraftAmountFromText(newTotal.toString())

                            // ✅ Persist mode into VM draft
                            vm.setDraftSplitMode(mode)

                            // update drafts
                            vm.updateSplitLabel(label)
                            vm.updateSplitFromShares(splits)
                            vm.setPayerUser(currentPayer)
                            vm.setDraftSplitsForNextExpense(splits)

                            onDone(label, splits, currentPayer)
                        }
                    ) {
                        Text("Done", color = Color(0xFFFFA000), fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        },
        bottomBar = {
            BottomBarSplit(
                splitLabel = pillLabel(),
                onReset = { resetSplit() },
                onEveryone1x = { everyone1x() }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            /* Total header + Split mode chip */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "$currency ${String.format("%.2f", totalAmount)}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Total amount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ModeChip(
                    text = when (mode) {
                        SplitMode.EQUALLY -> "Equally"
                        SplitMode.PARTS -> "As Parts"
                        SplitMode.AMOUNTS -> "As Amounts"
                    },
                    onClick = { showModeMenu = true }
                )
            }

            if (showModeMenu) {
                ModalBottomSheet(onDismissRequest = { showModeMenu = false }) {
                    Column(Modifier.padding(vertical = 8.dp)) {

                        ModeItem("Equally", selected = mode == SplitMode.EQUALLY) {
                            mode = SplitMode.EQUALLY
                            vm.setDraftSplitMode(SplitMode.EQUALLY)
                            counts.keys.forEach { counts[it] = 1 }
                            owedOverrides.keys.forEach { owedOverrides[it] = "" }
                            showModeMenu = false
                        }

                        ModeItem("As Parts", selected = mode == SplitMode.PARTS) {
                            mode = SplitMode.PARTS
                            vm.setDraftSplitMode(SplitMode.PARTS)
                            owedOverrides.keys.forEach { owedOverrides[it] = "" }
                            // keep counts
                            showModeMenu = false
                        }

                        ModeItem("As Amounts", selected = mode == SplitMode.AMOUNTS) {
                            mode = SplitMode.AMOUNTS
                            vm.setDraftSplitMode(SplitMode.AMOUNTS)
                            counts.keys.forEach { counts[it] = 0 }
                            // keep owedOverrides as-is
                            showModeMenu = false
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /* Paid by row */
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .heightIn(min = 48.dp)
                        .clickable { showPayerSheet = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Paid by", style = MaterialTheme.typography.bodyLarge)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            currentPayer?.name ?: "Select",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
            }

            /* Select all */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val allSelected = participants.all { selected[it.id] == true }
                    Checkbox(
                        checked = allSelected,
                        onCheckedChange = { toggleSelectAll() }
                    )
                    Text("Select all", style = MaterialTheme.typography.bodyLarge)
                }
                Text(
                    "${selectedCount()} selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)

            /* People list */
            LazyColumn(Modifier.fillMaxSize()) {
                itemsIndexed(participants, key = { index, user -> "${user.id}#$index" }) { _, user ->
                    val uid = user.id
                    val readOnlyAmount = String.format("%.2f", amountFor(uid))

                    val netAmount = netForUserAfterPayer(uid)
                    val owedDisplayLabel = buildString {
                        if (netAmount < 0) append("-")
                        append(currency).append(" ")
                        append(String.format("%.2f", abs(netAmount)))
                    }

                    ParticipantSplitRow(
                        name = user.name,
                        checked = selected[uid] == true,
                        onCheckedChange = { toggleOne(uid) },
                        countText = (counts[uid] ?: 0).toString(),
                        onCountMinus = {
                            if (mode == SplitMode.PARTS && selected[uid] == true) {
                                counts[uid] = max(0, (counts[uid] ?: 0) - 1)
                                owedOverrides[uid] = ""
                            }
                        },
                        onCountPlus = {
                            if (mode == SplitMode.PARTS && selected[uid] == true) {
                                counts[uid] = (counts[uid] ?: 0) + 1
                                owedOverrides[uid] = ""
                            }
                        },
                        onCountTyped = { newCountText ->
                            if (mode == SplitMode.PARTS && selected[uid] == true) {
                                val n = newCountText.toIntOrNull()
                                if (n != null && n >= 0) {
                                    counts[uid] = n
                                    owedOverrides[uid] = ""
                                }
                            }
                        },
                        currency = currency,
                        owedText = when (mode) {
                            SplitMode.AMOUNTS ->
                                if (selected[uid] == true) (owedOverrides[uid] ?: "") else "0.00"
                            else -> readOnlyAmount
                        },
                        onOwedTyped = { new ->
                            if (mode == SplitMode.AMOUNTS && selected[uid] == true) {
                                owedOverrides[uid] = new
                            }
                        },
                        owedDisplayLabel = owedDisplayLabel,
                        showParts = mode == SplitMode.PARTS,
                        enableAmountField = (mode == SplitMode.AMOUNTS && selected[uid] == true)
                    )

                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }

    /* Payer sheet */
    if (showPayerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPayerSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            LazyColumn {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Who paid?",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        IconButton(onClick = { showPayerSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }
                itemsIndexed(participants, key = { index, u -> "${u.id}#payer#$index" }) { _, u ->
                    PayerRow(
                        user = u,
                        isSelected = (u.id == currentPayer?.id),
                        onSelect = {
                            currentPayer = u
                            selected[u.id] = true // payer must be selected
                            onPickPayerDone(u)
                            showPayerSheet = false
                        }
                    )
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

/* ---------------------------------------------------------- */
/* Bottom bar                                                 */
/* ---------------------------------------------------------- */

@Composable
private fun BottomBarSplit(
    splitLabel: String,
    onReset: () -> Unit,
    onEveryone1x: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    RoundedCornerShape(50)
                )
                .clickable { onReset() }
        ) {
            Text(
                "Reset",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .border(2.dp, Color(0xFFFFA000), RoundedCornerShape(50))
                .clickable { onEveryone1x() }
        ) {
            Text(
                splitLabel,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFFFFA000)
            )
        }
    }
}

@Composable
private fun ParticipantSplitRow(
    name: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    countText: String,
    onCountMinus: () -> Unit,
    onCountPlus: () -> Unit,
    onCountTyped: (String) -> Unit,
    currency: String,
    owedText: String,
    onOwedTyped: (String) -> Unit,
    owedDisplayLabel: String,
    showParts: Boolean,
    enableAmountField: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked, onCheckedChange = { onCheckedChange() })

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = owedDisplayLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    currency,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (enableAmountField) {
                    OutlinedTextField(
                        value = owedText,
                        onValueChange = onOwedTyped,
                        singleLine = true,
                        modifier = Modifier.width(96.dp).height(48.dp),
                        textStyle = MaterialTheme.typography.titleMedium,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Text(
                        owedText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (showParts) {
            Spacer(Modifier.height(8.dp))
            Surface(
                color = Color(0xFFFFF9E8),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFFFC928))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "–",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.clickable { onCountMinus() }
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            countText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            "x",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Text(
                        "+",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.clickable { onCountPlus() }
                    )
                }
            }
        }
    }
}

@Composable
private fun PayerRow(
    user: UserEntity,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                val initials = buildString {
                    val parts = user.name.split(" ")
                    append(parts.getOrNull(0)?.firstOrNull()?.uppercaseChar() ?: 'U')
                    append(parts.getOrNull(1)?.firstOrNull()?.uppercaseChar() ?: "")
                }
                Text(initials, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                user.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "@user${user.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (isSelected) {
            Text(
                "✓",
                color = Color(0xFF00C27A),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun ModeItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
        if (selected) {
            Text("✓", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ModeChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
