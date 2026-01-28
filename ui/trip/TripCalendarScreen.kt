package com.example.tripshare.ui.trip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.tripshare.data.model.CalendarEventEntity
import com.example.tripshare.data.model.PaymentStatus
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.ui.expense.ExpensePaymentUi
import com.example.tripshare.ui.expense.ExpenseViewModel
import com.example.tripshare.ui.notifications.NotificationViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private fun isPendingStatus(status: PaymentStatus): Boolean {
    return status == PaymentStatus.PENDING || status == PaymentStatus.PAY_NOW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCalendarScreen(
    calendarVm: TripCalendarViewModel,
    expenseVm: ExpenseViewModel,
    notifVm: NotificationViewModel,
    tripId: Long,
    currentUserId: Long,
    onBack: () -> Unit,
    onChecklistClick: () -> Unit = {},
    onAddEvent: (LocalDate) -> Unit = {},
    onGoToTrip: (Long) -> Unit,
    onOpenExpense: (tripId: Long, expensePaymentId: Long) -> Unit
) {
    var displayedTripId by remember(tripId) { mutableStateOf(tripId) }

    val trip by calendarVm.observeTrip(displayedTripId).collectAsState(initial = null)
    val allTrips by calendarVm.activeTrips
        .map { list -> list.distinctBy { it.id } } // <--- ADD THIS
        .collectAsState(initial = emptyList())
    val events by calendarVm.events(displayedTripId).collectAsState(initial = emptyList())
    val notes by calendarVm.notes(displayedTripId).collectAsState(initial = emptyList())

    // --- CHANGED: Observe expenses from ALL active trips, not just the displayed one ---
    // We map each expense to its TripID so we can navigate to the correct trip later.
    val globalExpenses by produceState<List<Pair<Long, ExpensePaymentUi>>>(initialValue = emptyList(), key1 = allTrips) {
        if (allTrips.isEmpty()) {
            value = emptyList()
        } else {
            // Create a list of flows, one for each trip
            val flows = allTrips.map { tripEntity ->
                expenseVm.getExpensesForTrip(tripEntity.id)
                    .map { expenses -> expenses.map { expense -> tripEntity.id to expense } }
            }
            // Combine them into a single list
            combine(flows) { arrayOfLists ->
                arrayOfLists.flatMap { it }
            }.collect { mergedList ->
                value = mergedList
            }
        }
    }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val monthTitleFmt = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }
    val dayTitleFmt = remember { DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy") }

    val nowMonth = remember { YearMonth.now() }
    val state = rememberCalendarState(
        startMonth = remember { nowMonth.minusMonths(12) },
        endMonth = remember { nowMonth.plusMonths(12) },
        firstVisibleMonth = nowMonth,
        firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    )
    val scope = rememberCoroutineScope()

    // --- Bottom Sheet State ---
    val addReminderSheetState = rememberModalBottomSheetState()
    var showAddReminderSheet by remember(selectedDate) { mutableStateOf(false) }

    val addExpenseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddExpenseSheet by remember(selectedDate) { mutableStateOf(false) }

    val dayExpenses = remember(globalExpenses, selectedDate) {
        globalExpenses
            .filter { (_, exp) ->
                val isEffectiveDate = exp.effectiveOn == selectedDate
                val isDueDate = exp.dueOn == selectedDate

                // SHOW IF:
                // 1. It happened today (Effective Date)
                // 2. OR It is due today AND is still Pending (Hide due date if already paid)
                isEffectiveDate || (isDueDate && isPendingStatus(exp.paymentStatus))
            }
            .distinctBy { it.second.expensePaymentId }
    }


    // Merge Events Logic
    val dayEvents = remember(events, selectedDate, trip, dayExpenses) {
        val dbEvents = events.filter { it.date == selectedDate.toString() }
        val syntheticEvents = mutableListOf<CalendarEventEntity>()

        trip?.let { t ->
            if (t.startDate != null && t.startDate == selectedDate) {
                val alreadyHasStart = dbEvents.any { it.title.contains("Trip Starts", ignoreCase = true) }
                if (!alreadyHasStart) {
                    syntheticEvents.add(
                        CalendarEventEntity(
                            id = -1L,
                            tripId = displayedTripId,
                            title = "Trip Starts: ${t.name}",
                            date = selectedDate.toString(),
                            time = "08:00",
                            location = null,
                            category = "travel"
                        )
                    )
                }
            }
            if (t.endDate != null && t.endDate == selectedDate) {
                val alreadyHasEnd = dbEvents.any { it.title.contains("Trip Ends", ignoreCase = true) }
                if (!alreadyHasEnd) {
                    syntheticEvents.add(
                        CalendarEventEntity(
                            id = -2L,
                            tripId = displayedTripId,
                            title = "Trip Ends: ${t.name}",
                            date = selectedDate.toString(),
                            time = "18:00",
                            location = null,
                            category = "travel"
                        )
                    )
                }
            }
        }

        // Pending expenses become “reminder” events (for this selected day)
        val pendingExpenseEvents = dayExpenses
            .filter { (_, exp) -> isPendingStatus(exp.paymentStatus) }
            .map { (tId, exp) ->
                CalendarEventEntity(
                    id = -1_000_000L - exp.expensePaymentId,
                    tripId = tId, // Use the correct trip ID for the event
                    title = "PAYMENT DUE: ${exp.title}",
                    date = selectedDate.toString(),
                    time = "09:00",
                    location = "RM %.2f".format(exp.amount),
                    category = "Payment Due"
                )
            }

        pendingExpenseEvents + syntheticEvents + dbEvents
    }

    val tripsOnDate = remember(allTrips, selectedDate) {
        allTrips.filter { t ->
            val start = t.startDate
            val end = t.endDate
            if (start != null && end != null) !selectedDate.isBefore(start) && !selectedDate.isAfter(end) else false
        }
    }

    val dayNote = notes.firstOrNull { it.date == selectedDate.toString() }
    var reminders by remember(selectedDate, dayNote) {
        mutableStateOf(parseReminders(dayNote?.note.orEmpty()))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Calendar") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { /* notifications */ }) {
                        Icon(Icons.Default.Notifications, null)
                    }
                }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.firstVisibleMonth.yearMonth.atDay(1).format(monthTitleFmt),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            scope.launch { state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.minusMonths(1)) }
                        }) { Icon(Icons.Default.ChevronLeft, null) }
                        IconButton(onClick = {
                            scope.launch { state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.plusMonths(1)) }
                        }) { Icon(Icons.Default.ChevronRight, null) }
                    }
                }
            }

            // Calendar
            item {
                HorizontalCalendar(
                    state = state,
                    dayContent = { day ->
                        val isSelected = day.date == selectedDate
                        val isTripDay = trip?.let { t ->
                            if (t.startDate != null && t.endDate != null) {
                                !day.date.isBefore(t.startDate) && !day.date.isAfter(t.endDate)
                            } else false
                        } ?: false

                        val cellColor = when {
                            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            isTripDay -> MaterialTheme.colorScheme.secondaryContainer
                            else -> Color.Transparent
                        }
                        val textColor = when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isTripDay -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        // Filter global expenses for this specific day (dots logic)
                        val dayExps = globalExpenses.filter { (_, exp) ->
                            val due = exp.dueOn
                            (exp.effectiveOn == day.date) || (due != null && due == day.date)
                        }

                        val hasPendingPayment = dayExps.any { (_, exp) -> isPendingStatus(exp.paymentStatus) }
                        val hasExpenses = dayExps.isNotEmpty()

                        val hasEvents = events.any { it.date == day.date.toString() }
                        val hasNotes = notes.any { it.date == day.date.toString() && it.note.isNotBlank() }
                        val hasTripOnThisDay = allTrips.any { t ->
                            t.startDate != null && t.endDate != null &&
                                    !day.date.isBefore(t.startDate) && !day.date.isAfter(t.endDate)
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(cellColor)
                                .clickable { selectedDate = day.date },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected || isTripDay) FontWeight.Bold else FontWeight.Normal,
                                    color = textColor
                                )
                                if (hasExpenses || hasEvents || hasNotes || hasTripOnThisDay) {
                                    Spacer(Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    hasPendingPayment -> MaterialTheme.colorScheme.error
                                                    hasTripOnThisDay -> MaterialTheme.colorScheme.tertiary
                                                    isSelected -> MaterialTheme.colorScheme.primary
                                                    else -> MaterialTheme.colorScheme.secondary
                                                }
                                            )
                                    )
                                }
                            }
                        }
                    },
                    monthHeader = { }
                )
            }

            // Selected Day Header
            item {
                var showAddMenu by remember { mutableStateOf(false) }
                SectionTitle(
                    text = selectedDate.format(dayTitleFmt),
                    trailing = {
                        Box {
                            IconButton(onClick = { showAddMenu = true }) {
                                Icon(Icons.Default.Add, "Add")
                            }
                            DropdownMenu(
                                expanded = showAddMenu,
                                onDismissRequest = { showAddMenu = false }
                            ) {
                                DropdownMenuItem(text = { Text("Add note reminder") }, onClick = {
                                    showAddMenu = false
                                    showAddReminderSheet = true
                                })
                                DropdownMenuItem(text = { Text("Add expense reminder") }, onClick = {
                                    showAddMenu = false
                                    showAddExpenseSheet = true
                                })
                            }
                        }
                    }
                )
            }

            // Trips on this day
            if (tripsOnDate.isNotEmpty()) {
                item { SectionTitle("Trips on this day") }
                items(tripsOnDate) { t ->
                    TripNavigationCard(
                        tripName = t.name,
                        startDate = t.startDate?.toString() ?: "?",
                        endDate = t.endDate?.toString() ?: "?",
                        onClick = { onGoToTrip(t.id) }
                    )
                }
            }


            // Expenses
            item { SectionTitle("Payment & Expenses") }

            if (dayExpenses.isEmpty()) {
                item {
                    Text(
                        "No expenses recorded on this day.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                item {
                    val totalForDay = dayExpenses.sumOf { it.second.amount }
                    Text(
                        text = "Total for this day: RM %.2f".format(totalForDay),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                }

                items(dayExpenses, key = { (_, exp) -> "${exp.expensePaymentId}-payment" }) { (tId, exp) ->
                    val isPending = isPendingStatus(exp.paymentStatus)
                    val isDueDate = exp.dueOn == selectedDate

                    val isPaymentDueCard = isPending && isDueDate

                    EventCard(
                        title = if (isPaymentDueCard) "PAYMENT DUE: ${exp.title}" else exp.title,
                        subtitle = buildString {
                            append("Amount: RM %.2f".format(exp.amount))
                            if (isPending) append(" (Unpaid)")
                            if (!isDueDate && exp.dueOn != null) append("\nDue on: ${exp.dueOn}")
                        },
                        category = if (isPending) "Payment Due" else exp.category.ifBlank { "Expense" },
                        onClick = if (isPaymentDueCard) {
                            {
                                // --- CHANGED: Use the TripID associated with this expense ---
                                onOpenExpense(tId, exp.expensePaymentId)
                            }
                        } else null
                    )
                }
            }


            // Reminders
            item { SectionTitle("Daily Notes & Reminders") }
            if (reminders.isEmpty()) {
                item { Text("No reminders for this day.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(
                    items = reminders.sortedBy { timeToMinutes(it.time) },
                    key = { it.time.orEmpty() + it.text }
                ) { item ->
                    ReminderRow(
                        item = item,
                        onChange = { updatedItem ->

                            // ✅ cancel old scheduled reminder first (old item)
                            notifVm.cancelCalendarReminder(
                                tripId = displayedTripId,
                                date = selectedDate,
                                time24 = item.time,
                                text = item.text
                            )

                            val updatedList = reminders.map { if (it == item) updatedItem else it }
                            reminders = updatedList
                            calendarVm.saveNote(displayedTripId, selectedDate, encodeReminders(updatedList))

                            // ✅ if not done, schedule new one; if done, keep cancelled
                            if (!updatedItem.done) {
                                notifVm.scheduleCalendarReminder(
                                    tripId = displayedTripId,
                                    date = selectedDate,
                                    time24 = updatedItem.time,
                                    text = updatedItem.text
                                )
                            }
                        },
                        onDelete = {
                            // ✅ cancel reminder notification before removing it
                            notifVm.cancelCalendarReminder(
                                tripId = displayedTripId,
                                date = selectedDate,
                                time24 = item.time,
                                text = item.text
                            )

                            val updatedList = reminders.filterNot { it == item }
                            reminders = updatedList
                            calendarVm.saveNote(displayedTripId, selectedDate, encodeReminders(updatedList))
                        }

                    )
                }
            }

            item { Spacer(Modifier.height(64.dp)) }
        }

        // --- Bottom Sheets ---
        if (showAddReminderSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddReminderSheet = false },
                sheetState = addReminderSheetState
            ) {
                AddReminderSheetContent(
                    onAddReminder = { time, text ->
                        val newItem = ReminderItem(time?.trim()?.ifBlank { null }, text.trim(), false)
                        val updatedList = reminders + newItem
                        reminders = updatedList
                        calendarVm.saveNote(displayedTripId, selectedDate, encodeReminders(updatedList))
// ✅ schedule system notification 1 hour before
                        notifVm.scheduleCalendarReminder(
                            tripId = displayedTripId,
                            date = selectedDate,
                            time24 = newItem.time,
                            text = newItem.text,
                            remindBeforeMs = 60 * 60 * 1000L
                        )

                        scope.launch { addReminderSheetState.hide() }.invokeOnCompletion {
                            if (!addReminderSheetState.isVisible) showAddReminderSheet = false
                        }
                    },
                    onCancel = {
                        scope.launch { addReminderSheetState.hide() }.invokeOnCompletion {
                            if (!addReminderSheetState.isVisible) showAddReminderSheet = false
                        }
                    }
                )
            }
        }

        if (showAddExpenseSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddExpenseSheet = false },
                sheetState = addExpenseSheetState
            ) {
                AddExpenseReminderSheet(
                    selectedDate = selectedDate,
                    allTrips = allTrips,
                    currentTripId = displayedTripId,
                    expenseVm = expenseVm,
                    onConfirm = { expensePaymentId, expenseTitle, selectedTripId ->

                        // 1) Save Due Date
                        expenseVm.setExpenseReminder(
                            expensePaymentId = expensePaymentId,
                            dueDate = selectedDate
                        )

                        // 2) Schedule Notification
                        val dueMillis = selectedDate.atTime(9, 0)
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()

                        if (dueMillis > System.currentTimeMillis()) {
                            notifVm.schedulePaymentReminder(
                                invoiceId = expensePaymentId,
                                currentUserId = currentUserId, // user who must pay
                                dueAtMillis = dueMillis,
                                title = "Payment Due: $expenseTitle"
                            )
                        }

                        showAddExpenseSheet = false
                    },
                    onCancel = { showAddExpenseSheet = false }
                )
            }
        }
    }
}
/* =========== ADD EXPENSE REMINDER SHEET (Unchanged) =========== */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseReminderSheet(
    selectedDate: LocalDate,
    allTrips: List<TripEntity>,
    currentTripId: Long,
    expenseVm: ExpenseViewModel,
    onConfirm: (expensePaymentId: Long, expenseTitle: String, selectedTripId: Long) -> Unit,
    onCancel: () -> Unit
) {
    // 1. Manage Selected Trip
    var selectedTripId by remember(currentTripId) { mutableStateOf(currentTripId) }
    var tripDropdownExpanded by remember { mutableStateOf(false) }

    val selectedTripName = allTrips.find { it.id == selectedTripId }?.name ?: "Select your trip"

    // 2. Fetch Pending Expenses for the SELECTED trip dynamically
    val pendingExpenses by produceState<List<ExpensePaymentUi>>(initialValue = emptyList(), key1 = selectedTripId) {
        expenseVm.getExpensesForTrip(selectedTripId).collect { fullList ->
            value = fullList.filter { isPendingStatus(it.paymentStatus) }
        }
    }
    var selectedPending by remember { mutableStateOf<ExpensePaymentUi?>(null) }
    var pendingExpanded by remember { mutableStateOf(false) }

    // Reset selected expense if trip changes
    LaunchedEffect(selectedTripId) { selectedPending = null }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Set Expense Reminder", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text("Due Date: $selectedDate", color = MaterialTheme.colorScheme.onSurfaceVariant)

        // --- Trip Selector ---
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedTripName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Trip") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { tripDropdownExpanded = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            // Overlay clickable box
            Box(
                Modifier
                    .matchParentSize()
                    .clickable { tripDropdownExpanded = true }
            )

            DropdownMenu(
                expanded = tripDropdownExpanded,
                onDismissRequest = { tripDropdownExpanded = false }
            ) {
                allTrips.forEach { trip ->
                    DropdownMenuItem(
                        text = { Text(trip.name) },
                        onClick = {
                            selectedTripId = trip.id
                            tripDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // --- Pending Expense Picker ---
        Box(modifier = Modifier.fillMaxWidth()) {
            val displayText = selectedPending?.let {
                "${it.title} (RM %.2f)".format(it.amount)
            } ?: if (pendingExpenses.isEmpty()) "No pending expenses in this trip" else "Select pending expense"

            OutlinedTextField(
                value = displayText,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Pending Expense") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Box(
                Modifier
                    .matchParentSize()
                    .clickable(enabled = pendingExpenses.isNotEmpty()) { pendingExpanded = true }
            )

            DropdownMenu(
                expanded = pendingExpanded,
                onDismissRequest = { pendingExpanded = false }
            ) {
                if (pendingExpenses.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No pending expenses") },
                        onClick = { pendingExpanded = false },
                        enabled = false
                    )
                } else {
                    pendingExpenses.forEach { p ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(p.title, fontWeight = FontWeight.Bold)
                                    Text("RM %.2f • %s".format(p.amount, p.payerName), style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = {
                                selectedPending = p
                                pendingExpanded = false
                            }
                        )
                    }
                }
            }
        }


        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) { Text("Cancel") }
            Spacer(Modifier.width(8.dp))
            Button(
                enabled = selectedPending != null,
                onClick = {
                    val p = selectedPending ?: return@Button
                    onConfirm(p.expensePaymentId, p.title, selectedTripId)
                }
            ) { Text("Save Reminder") }
        }
    }
}

/* ===================== Notes Sheet (unchanged) ===================== */

@Composable
private fun AddReminderSheetContent(
    onAddReminder: (time: String?, text: String) -> Unit,
    onCancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var showTimePicker by remember { mutableStateOf(false) }

// Display text (nice 12h) but save value as "HH:mm"
    val timeDisplay = remember(time) { time24To12(time) }
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Add a new reminder",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            label = { Text("What do you need to remember?") },
            placeholder = { Text("e.g., Check out of hotel") },
            maxLines = 5
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = timeDisplay,
                onValueChange = {},
                readOnly = true,
                enabled = false, // ✅ important (prevents it eating clicks)
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Time (Optional)") },
                placeholder = { Text("Set time") },
                leadingIcon = { Icon(Icons.Default.Notifications, null) },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // ✅ overlay that ALWAYS receives the tap
            Box(
                Modifier
                    .matchParentSize()
                    .clickable { showTimePicker = true }
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) { Text("Cancel") }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (text.isNotBlank()) onAddReminder(time.trim().ifBlank { null }, text)
                },
                enabled = text.isNotBlank()
            ) { Text("Save") }
        }
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
        if (showTimePicker) {
            WheelTimePickerDialog(
                initialTime24 = time.takeIf { it.isNotBlank() } ?: "09:00",
                onDismiss = { showTimePicker = false },
                onConfirm = { pickedTime24 ->
                    time = pickedTime24 // ✅ this will update field display
                    showTimePicker = false
                }
            )
        }

    }
}

/* ===================== Helpers + UI components (unchanged) ===================== */

@Composable
private fun SquareCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Transparent)
            .clickable { onCheckedChange(!checked) }
            .border(
                BorderStroke(
                    width = 2.dp,
                    color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                ),
                RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ReminderRow(
    item: ReminderItem,
    onChange: (ReminderItem) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editTime by remember { mutableStateOf(item.time.orEmpty()) }
    var editText by remember { mutableStateOf(item.text) }

    if (isEditing) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = editTime,
                    onValueChange = { editTime = it },
                    modifier = Modifier.width(90.dp),
                    label = { Text("Time") }
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    onChange(item.copy(time = editTime.trim().ifBlank { null }, text = editText.trim()))
                    isEditing = false
                }) { Icon(Icons.Default.Check, "Save") }
                IconButton(onClick = {
                    isEditing = false
                    editTime = item.time.orEmpty()
                    editText = item.text
                }) { Icon(Icons.Default.Close, "Cancel") }
            }
        }
    } else {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SquareCheckBox(checked = item.done, onCheckedChange = { checked -> onChange(item.copy(done = checked)) })
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    item.time?.takeIf { it.isNotBlank() }?.let { t ->
                        Text(t, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        item.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (item.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { isEditing = true }) { Icon(Icons.Default.Edit, "Edit") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete") }
            }
        }
    }
}

private data class ReminderItem(val time: String?, val text: String, val done: Boolean)

private fun parseReminders(raw: String): List<ReminderItem> {
    if (raw.isBlank()) return emptyList()
    return raw.lines().mapNotNull { line ->
        var trimmed = line.trim()
        if (trimmed.isBlank()) return@mapNotNull null

        var time: String? = null
        val firstToken = trimmed.substringBefore(' ')
        if (firstToken.matches(Regex("""\d{1,2}:\d{2}"""))) {
            time = firstToken
            trimmed = trimmed.substringAfter(' ').trim()
        }

        var done = false
        when {
            trimmed.startsWith("[x] ", ignoreCase = true) -> { done = true; trimmed = trimmed.drop(4) }
            trimmed.startsWith("[ ] ") -> { done = false; trimmed = trimmed.drop(4) }
        }

        if (trimmed.isBlank()) null else ReminderItem(time, trimmed, done)
    }
}

private fun encodeReminders(reminders: List<ReminderItem>) =
    reminders.joinToString("\n") { "${it.time?.plus(" ") ?: ""}${if (it.done) "[x]" else "[ ]"} ${it.text}" }

private fun timeToMinutes(time: String?): Int {
    if (time.isNullOrBlank()) return Int.MAX_VALUE
    val parts = time.split(":")
    if (parts.size != 2) return Int.MAX_VALUE
    return (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
}

@Composable
fun TripNavigationCard(tripName: String, startDate: String, endDate: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.primaryContainer,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Luggage, null, tint = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    tripName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "$startDate - $endDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(Icons.Default.ChevronRight, "Go to trip", tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
private fun SectionTitle(text: String, trailing: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        trailing?.invoke()
    }
}

@Composable
private fun EventCard(
    title: String,
    subtitle: String,
    category: String,
    onClick: (() -> Unit)? = null
) {
    val stroke = when (category.lowercase()) {
        "payment due" -> MaterialTheme.colorScheme.error
        "travel", "flight" -> Color(0xFF3F51B5)
        "activity" -> Color(0xFF673AB7)
        "social" -> Color(0xFF009688)
        "food" -> Color(0xFFE65100)
        "hotel" -> Color(0xFF1E88E5)
        "transport" -> Color(0xFF5E35B1)
        "shopping" -> Color(0xFFD81B60)
        else -> MaterialTheme.colorScheme.primary
    }

    val iconText = when (category.lowercase()) {
        "payment due" -> "❗"
        "travel", "flight" -> "✈️"
        "activity" -> "🎉"
        "social" -> "👋"
        "food" -> "🍕"
        "hotel" -> "🏨"
        "transport" -> "🚗"
        "shopping" -> "🛍️"
        else -> "◎"
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .let { m -> if (onClick != null) m.clickable { onClick() } else m },
        border = BorderStroke(1.dp, stroke.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(34.dp).clip(CircleShape).background(stroke.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) { Text(iconText, color = stroke) }

                Spacer(Modifier.width(12.dp))

                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = if (category.lowercase() == "payment due") stroke else Color.Unspecified
                )

                Spacer(Modifier.weight(1f))
                IconButton(onClick = { /* menu */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                }
            }

            if (subtitle.isNotBlank()) {
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            AssistChip(
                onClick = { },
                label = { Text(category.replaceFirstChar { it.uppercaseChar() }) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = stroke.copy(alpha = 0.10f),
                    labelColor = stroke
                )
            )
        }
    }
}
@Composable
private fun WheelTimePickerDialog(
    initialTime24: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    // Parse "HH:mm"
    val (initH, initM) = runCatching {
        val parts = initialTime24.split(":")
        (parts.getOrNull(0)?.toIntOrNull() ?: 9) to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
    }.getOrDefault(9 to 0)

    val initIsPm = initH >= 12
    val initHour12 = ((initH % 12).let { if (it == 0) 12 else it })

    var hour12 by remember { mutableStateOf(initHour12.coerceIn(1, 12)) }
    var minute by remember { mutableStateOf(initM.coerceIn(0, 59)) }
    var isPm by remember { mutableStateOf(initIsPm) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Time", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

                Text(
                    text = "%d:%02d %s".format(hour12, minute, if (isPm) "pm" else "am"),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelPicker(
                        items = (1..12).map { it.toString() },
                        initialIndex = (hour12 - 1).coerceIn(0, 11),
                        width = 90.dp,
                        onSelectedIndex = { idx -> hour12 = idx + 1 }
                    )
                    Text(":", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(horizontal = 6.dp))
                    WheelPicker(
                        items = (0..59).map { "%02d".format(it) },
                        initialIndex = minute.coerceIn(0, 59),
                        width = 90.dp,
                        onSelectedIndex = { idx -> minute = idx }
                    )
                    WheelPicker(
                        items = listOf("am", "pm"),
                        initialIndex = if (isPm) 1 else 0,
                        width = 80.dp,
                        onSelectedIndex = { idx -> isPm = (idx == 1) }
                    )
                }

                Text("Presets", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PresetChip(label = "9 am", selected = !isPm && hour12 == 9 && minute == 0) {
                        hour12 = 9; minute = 0; isPm = false
                    }
                    PresetChip(label = "12 pm", selected = isPm && hour12 == 12 && minute == 0) {
                        hour12 = 12; minute = 0; isPm = true
                    }
                    PresetChip(label = "4 pm", selected = isPm && hour12 == 4 && minute == 0) {
                        hour12 = 4; minute = 0; isPm = true
                    }
                    PresetChip(label = "6 pm", selected = isPm && hour12 == 6 && minute == 0) {
                        hour12 = 6; minute = 0; isPm = true
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        val hour24 = hour12To24(hour12, isPm)
                        onConfirm("%02d:%02d".format(hour24, minute))
                    }) { Text("Done") }
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors()
    )
}

/**
 * Simple iOS-like wheel using LazyColumn + snapping.
 */
@Composable
private fun WheelPicker(
    items: List<String>,
    initialIndex: Int,
    width: Dp,
    onSelectedIndex: (Int) -> Unit
) {
    val visibleCount = 5 // odd number => center item
    val itemHeight = 42.dp
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex.coerceIn(0, items.lastIndex))
    val fling = rememberSnapFlingBehavior(lazyListState = listState)

    // When scrolling stops, choose the centered item
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerOffset = (visibleCount - 1) / 2
            val centered = listState.firstVisibleItemIndex + centerOffset
            onSelectedIndex(centered.coerceIn(0, items.lastIndex))

        }
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(itemHeight * visibleCount)
    ) {
        // The list
        LazyColumn(
            state = listState,
            flingBehavior = fling,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = itemHeight * ((visibleCount - 1) / 2))
        ) {
            items(items.size) { idx ->
                val label = items[idx]
                val isSelected = idx == listState.firstVisibleItemIndex
                Text(
                    text = label,
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = if (isSelected) MaterialTheme.typography.titleLarge
                    else MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // Center highlight (subtle)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
        )
    }
}

private fun hour12To24(hour12: Int, isPm: Boolean): Int {
    val h = hour12.coerceIn(1, 12)
    return when {
        isPm && h != 12 -> h + 12
        !isPm && h == 12 -> 0
        else -> h
    }
}

private fun time24To12(time24: String): String {
    val parts = time24.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull() ?: return time24
    val m = parts.getOrNull(1)?.toIntOrNull() ?: return time24
    val isPm = h >= 12
    val h12 = ((h % 12).let { if (it == 0) 12 else it })
    return "%d:%02d %s".format(h12, m, if (isPm) "pm" else "am")
}