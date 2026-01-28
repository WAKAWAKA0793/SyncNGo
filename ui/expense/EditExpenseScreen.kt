package com.example.tripshare.ui.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.rounded.CallSplit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tripshare.data.model.PaymentStatus
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(
    expenseId: Long,
    vm: ExpenseViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onOpenSplitBill: () -> Unit = {},
) {
    val loadedExpense: ExpensePaymentUi? by vm.observeExpense(expenseId)
        .collectAsState(initial = null)

    val participants by vm.participants.collectAsState()
    val draftAmount by vm.draftAmount.collectAsState()
    val draftSplits by vm.draftSplitsForNextExpense.collectAsState()
    val draftMode by vm.draftSplitMode.collectAsState()
    val currentPayer by vm.payerUserFlow.collectAsState()

    var amountText by rememberSaveable(expenseId) { mutableStateOf("") }
    var currencyText by rememberSaveable(expenseId) { mutableStateOf("MYR") }

    var description by rememberSaveable("editexpense_description_$expenseId") { mutableStateOf("") }
    var isPaid by rememberSaveable("editexpense_isPaid_$expenseId") { mutableStateOf(true) }
    var category by rememberSaveable("editexpense_category_$expenseId") { mutableStateOf("Food") }
    var date by rememberSaveable("editexpense_date_$expenseId") { mutableStateOf(LocalDate.now()) }

    var splitLabel by remember { mutableStateOf("Equally") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    var isReturningFromSplit by rememberSaveable(expenseId) { mutableStateOf(false) }
    var hasSeededFromDb by rememberSaveable(expenseId) { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    fun modeToLabel(mode: SplitMode): String = when (mode) {
        SplitMode.EQUALLY -> "Equally"
        SplitMode.PARTS -> "As Parts"
        SplitMode.AMOUNTS -> "As Amounts"
    }

    LaunchedEffect(loadedExpense, participants) {
        if (!hasSeededFromDb && loadedExpense != null && participants.isNotEmpty()) {
            val e = loadedExpense!!

            if (isReturningFromSplit) {
                val draftVal = vm.draftAmount.value
                amountText = if (draftVal > 0) "%.2f".format(draftVal) else ""
                splitLabel = modeToLabel(vm.draftSplitMode.value)
                isReturningFromSplit = false
            } else {
                vm.loadSplitsForEditing(expenseId)
                amountText = if (e.amount > 0) "%.2f".format(e.amount) else ""
            }

            currencyText = "MYR"
            description = e.title
            isPaid = e.paymentStatus == PaymentStatus.PAID
            category = e.category.ifBlank { "Other" }
            date = e.paidOn ?: e.effectiveOn

            val existingPayer = participants.find { it.id == e.payerId }
            vm.setPayerUser(existingPayer)

            hasSeededFromDb = true
        }
    }

    LaunchedEffect(isReturningFromSplit, draftAmount, draftSplits, draftMode) {
        if (isReturningFromSplit) {
            if (draftAmount > 0) amountText = "%.2f".format(draftAmount)
            splitLabel = modeToLabel(draftMode)
            isReturningFromSplit = false
        }
    }

    LaunchedEffect(draftMode) {
        splitLabel = modeToLabel(draftMode)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        // 👇 ADD THIS VALIDATOR
        selectableDates = remember {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val dateToCheck = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()
                    val today = LocalDate.now(ZoneId.of("UTC"))
                    return !dateToCheck.isBefore(today)
                }
            }
        }
    )

    val isFormValid =
        amountText.toDoubleOrNull() != null &&
                (amountText.toDoubleOrNull() ?: 0.0) > 0.0 &&
                description.isNotBlank()

    val cs = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cs.surface)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Close",
                        tint = cs.onSurface
                    )
                }

                Text(
                    "Edit Expense",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = cs.onSurface
                )

                TextButton(
                    onClick = {
                        if (!isFormValid) return@TextButton

                        vm.updatePayment(
                            expenseId = expenseId,
                            title = description.ifBlank { "Expense" },
                            amount = amountText.toDoubleOrNull() ?: 0.0,
                            category = category,
                            status = if (isPaid) PaymentStatus.PAID else PaymentStatus.PENDING,
                            paidOn = date,
                            payerUserId = currentPayer?.id
                        )

                        vm.saveSplitsForExistingExpense(
                            expenseId,
                            vm.draftSplitsForNextExpense.value
                        )

                        onSaved()
                    },
                    enabled = isFormValid
                ) {
                    Text(
                        "Save",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isFormValid) cs.primary else cs.onSurfaceVariant
                    )
                }
            }
        },
        containerColor = cs.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /* --- 1. BIG AMOUNT INPUT --- */
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Amount",
                    style = MaterialTheme.typography.labelMedium,
                    color = cs.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currencyText,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = cs.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Box(contentAlignment = Alignment.CenterStart) {
                        if (amountText.isEmpty()) {
                            Text(
                                text = "0.00",
                                style = TextStyle(
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = cs.onSurfaceVariant.copy(alpha = 0.45f)
                                )
                            )
                        }

                        BasicTextField(
                            value = amountText,
                            onValueChange = { newValue ->
                                if (newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                    amountText = newValue
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = cs.onSurface,
                                textAlign = TextAlign.Start
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            cursorBrush = SolidColor(cs.primary),
                            modifier = Modifier.width(IntrinsicSize.Min)
                        )
                    }
                }
            }

            /* --- 2. DESCRIPTION & CATEGORY --- */
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cs.surface,
                tonalElevation = 2.dp
            ) {
                Column(Modifier.padding(vertical = 4.dp)) {

                    // Category row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryPicker = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = cs.surfaceVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    getCategoryEmoji(category),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = cs.onSurface
                                )
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyLarge,
                            color = cs.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = cs.onSurfaceVariant
                        )
                    }

                    DividerThin(padding = 16.dp)

                    // Description row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = cs.onSurfaceVariant
                        )
                        Spacer(Modifier.width(16.dp))

                        Box(Modifier.weight(1f)) {
                            if (description.isEmpty()) {
                                Text(
                                    "What is this for?",
                                    color = cs.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            BasicTextField(
                                value = description,
                                onValueChange = { description = it },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = cs.onSurface),
                                cursorBrush = SolidColor(cs.primary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            /* --- 3. SETTINGS CARD --- */
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cs.surface,
                tonalElevation = 2.dp
            ) {
                Column(Modifier.padding(vertical = 4.dp)) {

                    // Status
                    ModernSettingRow(
                        icon = if (isPaid) "✔" else "⏱",
                        iconColor = if (isPaid) cs.primary else cs.tertiary,
                        label = "Status",
                        value = if (isPaid) "Paid" else "Pending",
                        onClick = { isPaid = !isPaid }
                    )

                    DividerThin(padding = 56.dp)

                    // Split
                    ModernSettingRow(
                        iconVector = Icons.Rounded.CallSplit,
                        label = "Split",
                        value = splitLabel,
                        onClick = {
                            vm.setDraftAmountFromText(amountText)
                            vm.setDraftCurrency(currencyText)
                            isReturningFromSplit = true
                            onOpenSplitBill()
                        }
                    )

                    DividerThin(padding = 56.dp)

                    // Date
                    ModernSettingRow(
                        iconVector = Icons.Default.CalendarToday,
                        label = "Date",
                        value = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                        onClick = { showDatePicker = true }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Delete
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.error,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDeleteConfirm = true   // 👈 open dialog
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Delete expense",
                        color = MaterialTheme.colorScheme.onError,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }
    }
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = {
                Text(
                    text = "Delete expense?",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text("This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        vm.deletePayment(expenseId)
                        onBack()
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    /* DATE PICKER */
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    /* CATEGORY PICKER */
    if (showCategoryPicker) {
        Dialog(onDismissRequest = { showCategoryPicker = false }) {
            CategoryPickerDialog(
                current = category,
                onSelect = { chosen ->
                    category = chosen
                    showCategoryPicker = false
                },
                onDismiss = { showCategoryPicker = false }
            )
        }
    }
}

@Composable
private fun DividerThin(padding: androidx.compose.ui.unit.Dp = 0.dp) {
    val cs = MaterialTheme.colorScheme
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = padding)
            .height(1.dp)
            .background(cs.outlineVariant)
    )
}

@Composable
private fun CategoryPickerDialog(
    current: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        color = cs.surface,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = cs.onSurface
            )

            CategoryOption("Food", current, onSelect)
            CategoryOption("Hotel", current, onSelect)
            CategoryOption("Transport", current, onSelect)
            CategoryOption("Activity", current, onSelect)
            CategoryOption("Drinks", current, onSelect)
            CategoryOption("Shopping", current, onSelect)
            CategoryOption("Car rent", current, onSelect)
            CategoryOption("Other", current, onSelect)

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun CategoryOption(
    label: String,
    current: String,
    onSelect: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(label) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(getCategoryEmoji(label))
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                color = cs.onSurface,
                fontWeight = if (label == current) FontWeight.SemiBold else FontWeight.Normal
            )
        }

        if (label == current) {
            Text(
                "✓",
                color = cs.primary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun getCategoryEmoji(category: String): String {
    return when (category) {
        "Food" -> "🍔"
        "Hotel" -> "🏨"
        "Transport" -> "🚗"
        "Activity" -> "🎟️"
        "Drinks" -> "🍹"
        "Shopping" -> "🛍️"
        "Car rent" -> "🚙"
        else -> "🏷️"
    }
}
