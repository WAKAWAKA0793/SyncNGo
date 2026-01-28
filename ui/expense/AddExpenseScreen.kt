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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tripshare.data.model.PaymentStatus
import com.example.tripshare.ui.notifications.NotificationViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    vm: ExpenseViewModel,
    onBack: () -> Unit,
    onDone: () -> Unit = {},
    onOpenSplitBill: () -> Unit = {},
    onPickDate: (LocalDate) -> Unit = {},
    onScheduleReminder: (String, LocalDate) -> Unit = { _, _ -> },
    description: String,
    onDescriptionChange: (String) -> Unit,
    isPaid: Boolean,
    onIsPaidChange: (Boolean) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    date: LocalDate,
) {
    val participants by vm.participants.collectAsState()
    val payerUser by vm.payerUserFlow.collectAsState()
    val effectivePayer = payerUser ?: participants.firstOrNull()
    val effectivePayee = participants.getOrNull(1) ?: effectivePayer

    val vmAmount by vm.draftAmount.collectAsState()
    val vmCurrency by vm.draftCurrency.collectAsState()
    val splitLabel by vm.splitLabel.collectAsState()

    // Smart Input Logic: Start empty to allow typing immediately.
    var amountText by remember {
        mutableStateOf(if (vmAmount > 0.0) "%.2f".format(vmAmount) else "")
    }

    var currencyText by remember { mutableStateOf(vmCurrency.ifBlank { "MYR" }) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

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

    LaunchedEffect(amountText) {
        vm.setDraftAmountFromText(amountText)
    }

    val isFormValid =
        amountText.toDoubleOrNull() != null &&
                amountText.toDouble() > 0 &&
                description.isNotBlank() &&
                effectivePayer != null &&
                effectivePayee != null

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface) // Theme Surface
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface // Theme OnSurface
                    )
                }
                Text(
                    "New Expense",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = {
                        if (!isFormValid) return@TextButton

                        if (!isPaid) {
                            onScheduleReminder(description.ifBlank { "Unpaid Expense" }, date)
                        }
                        onDone()
                    },
                    enabled = isFormValid
                ) {
                    Text(
                        "Save",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        // Use Theme Primary for action, or disabled color
                        color = if (isFormValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background // Theme Background (Grey50/Grey900)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currencyText,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Box(contentAlignment = Alignment.CenterStart) {
                        if (amountText.isEmpty()) {
                            Text(
                                text = "0.00",
                                style = TextStyle(
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline // Placeholder color
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
                                // Use Primary color for the main value to match theme
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Start
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            // Cursor matches text
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.width(IntrinsicSize.Min)
                        )
                    }
                }
            }

            /* --- 2. DESCRIPTION & CATEGORY CARD --- */
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(Modifier.padding(vertical = 4.dp)) {
                    // Category Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryPicker = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            // Light container for icon
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(getCategoryEmoji(category), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowBack, // Placeholder icon
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DividerThin(padding = 16.dp)

                    // Description Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(16.dp))

                        Box(Modifier.weight(1f)) {
                            if (description.isEmpty()) {
                                Text("What is this for?", color = MaterialTheme.colorScheme.outline)
                            }
                            BasicTextField(
                                value = description,
                                onValueChange = onDescriptionChange,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            /* --- 3. SETTINGS CARD (Split, Date, Paid By) --- */
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(Modifier.padding(vertical = 4.dp)) {

                    // Paid By / Status
                    // Note: Keeping semantic colors for "Paid" (Green) vs "Pending" (Yellow) as they are logic-specific,
                    // but falling back to Primary for "Paid" is also an option if strict theming is desired.
                    // For now, using hardcoded semantic colors for clarity, but could use theme attributes if preferred.
                    val paidColor = Color(0xFF00A27A) // Success Green
                    val pendingColor = Color(0xFFFFC107) // Warning Yellow

                    ModernSettingRow(
                        icon = if (isPaid) "✔" else "⏱",
                        iconColor = if(isPaid) paidColor else pendingColor,
                        label = "Status",
                        value = if (isPaid) "Paid" else "Pending",
                        onClick = { onIsPaidChange(!isPaid) }
                    )

                    DividerThin(padding = 56.dp)

                    // Split Bill
                    ModernSettingRow(
                        iconVector = Icons.Rounded.CallSplit,
                        label = "Split",
                        value = splitLabel,
                        onClick = {
                            vm.setDraftAmountFromText(amountText)
                            vm.setDraftCurrency(currencyText)
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
        }
    }

    // --- Dialogs ---

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val picked = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        onPickDate(picked)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showCategoryPicker) {
        Dialog(onDismissRequest = { showCategoryPicker = false }) {
            CategoryPickerDialog(
                current = category,
                onSelect = { chosen ->
                    onCategoryChange(chosen)
                    showCategoryPicker = false
                },
                onDismiss = { showCategoryPicker = false }
            )
        }
    }
}


/* ----- Modern Helper Components ----- */

@Composable
fun ModernSettingRow(
    icon: String? = null,
    iconVector: ImageVector? = null,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = iconColor.copy(alpha = 0.1f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (iconVector != null) {
                    Icon(
                        iconVector,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (icon != null) {
                    Text(icon, fontSize = 18.sp, color = iconColor)
                }
            }
        }

        Spacer(Modifier.width(16.dp))

        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary, // Highlight value with Primary color
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}


@Composable
private fun DividerThin(padding: androidx.compose.ui.unit.Dp = 0.dp) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = padding)
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant) // Theme divider
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


// ... AddExpenseFlow wrapper and getCategoryEmoji function remain unchanged ...
// (I will include them here for completeness if you copy-paste the whole file)

/* ----- Flow wrapper (UPDATED) ----- */

@Composable
fun AddExpenseFlow(
    tripId: Long,
    vm: ExpenseViewModel,
    notifVm: NotificationViewModel,
    onBack: () -> Unit,
    linkedPlanId: Long? = null,
    onAfterSave: () -> Unit,
    initialDescription: String = "",
    initialDate: LocalDate = LocalDate.now(),
    initialCategory: String = "Food",
    initialIsPaid: Boolean = true,
) {
    var showSplit by remember { mutableStateOf(false) }

    val amount by vm.draftAmount.collectAsState()
    val currency by vm.draftCurrency.collectAsState()
    val people by vm.participants.collectAsState()
    val shares by vm.currentSharesMapFlow.collectAsState()
    val payer by vm.payerUserFlow.collectAsState()
    val tripName by vm.tripNameFlow.collectAsState()

    var description by rememberSaveable("addexpense_description") { mutableStateOf(initialDescription) }
    var isPaid by rememberSaveable("addexpense_isPaid") { mutableStateOf(initialIsPaid) }
    var category by rememberSaveable("addexpense_category") { mutableStateOf(initialCategory) }
    var date by rememberSaveable("addexpense_date") { mutableStateOf(initialDate) }

    LaunchedEffect(initialDescription, initialDate, initialCategory, initialIsPaid) {
        if (description.isBlank() && initialDescription.isNotBlank()) description = initialDescription
        if (category.isBlank()) category = initialCategory
        date = initialDate
        isPaid = initialIsPaid
    }

    val draftSplits by vm.draftSplitsForNextExpense.collectAsState()

    if (!showSplit) {
        AddExpenseScreen(
            vm = vm,
            onBack = onBack,
            onDone = {
                val payerId = vm.payerUserFlow.value?.id ?: -1L
                val payerName = vm.payerUserFlow.value?.name ?: "Someone"

                val finalSplits = if (draftSplits.isNotEmpty()) {
                    draftSplits
                } else {
                    val count = people.size
                    if (count > 0) {
                        val share = amount / count
                        people.map { SplitShare(it.id, 1, share) }
                    } else emptyList()
                }
                vm.saveNewExpenseWithSplits(
                    title = description.ifBlank { "Expense" },
                    amount = amount,
                    category = category,
                    status = if (isPaid) PaymentStatus.PAID else PaymentStatus.PENDING,
                    paidOn = date,
                    itineraryPlanId = linkedPlanId
                )

                notifVm.notifyBillSplit(
                    tripId = tripId,
                    tripName = tripName,
                    expenseTitle = description.ifBlank { "Expense" },
                    payerId = payerId,
                    payerName = payerName,
                    splits = finalSplits,
                    currency = currency.ifBlank { "MYR" }
                )

                onAfterSave()

            },

            onOpenSplitBill = {
                vm.setDraftAmountFromText(amount.toString())
                vm.setDraftCurrency(currency)
                showSplit = true
            },
            onPickDate = { picked -> date = picked },
            onScheduleReminder = { title, reminderDate ->
            },
            description = description,
            onDescriptionChange = { description = it },
            isPaid = isPaid,
            onIsPaidChange = { isPaid = it },
            category = category,
            onCategoryChange = { category = it },
            date = date,
        )
    } else {
        SplitBillScreen(
            vm = vm,
            totalAmount = amount,
            currency = currency,
            participants = people,
            initialShares = shares,
            payerUser = payer,
            onPickPayerDone = { vm.setPayerUser(it) },
            onDone = { newLabel, splits, payerFinal ->
                vm.updateSplitLabel(newLabel)
                vm.updateSplitFromShares(splits)
                vm.setPayerUser(payerFinal)
                vm.setDraftSplitsForNextExpense(splits)
                showSplit = false
            },
            onBack = { showSplit = false }
        )
    }
}
