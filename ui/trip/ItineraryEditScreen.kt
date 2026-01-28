// ui/trip/ItineraryEditScreen.kt
package com.example.tripshare.ui.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tripshare.data.model.ItineraryItemEntity
import com.example.tripshare.data.repo.ItineraryRepository
import com.example.tripshare.data.repo.TripRepository
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryEditScreen(
    tripId: Long,
    itemId: Long,                          // 0L = create new, >0 = edit existing
    repo: ItineraryRepository,
    tripRepo: TripRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // ---------------- State ----------------
    var title by rememberSaveable { mutableStateOf("") }
    var dateText by rememberSaveable { mutableStateOf("") }       // "yyyy-MM-dd"
    var timeText by rememberSaveable { mutableStateOf("") }       // "HH:mm" or range if you prefer
    var locationText by rememberSaveable { mutableStateOf("") }
    var notesText by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var attachment by rememberSaveable { mutableStateOf("") }
    var assignedTo by rememberSaveable { mutableStateOf("") }

    // Trip date constraints
    var tripStartDateText by rememberSaveable { mutableStateOf("") }   // yyyy-MM-dd or ""
    var tripEndDateText by rememberSaveable { mutableStateOf("") }     // optional; leave "" if not set
    var computedDay by rememberSaveable { mutableStateOf(1) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val zoneId = remember { ZoneId.systemDefault() }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    fun parseLocalDateOrNull(s: String): LocalDate? =
        runCatching { LocalDate.parse(s, dateFormatter) }.getOrNull()

    fun toEpochMillisOrNull(s: String): Long? = parseLocalDateOrNull(s)?.atStartOfDay(zoneId)
        ?.toInstant()?.toEpochMilli()

    fun parseHourMinuteOrNull(s: String): Pair<Int, Int>? = runCatching {
        val t = LocalTime.parse(s, timeFormatter)
        t.hour to t.minute
    }.getOrNull()

    fun recomputeDay() {
        val start = parseLocalDateOrNull(tripStartDateText)
        val picked = parseLocalDateOrNull(dateText)
        computedDay = if (start != null && picked != null) {
            val days = Duration.between(start.atStartOfDay(), picked.atStartOfDay()).toDays().toInt()
            max(1, days + 1)
        } else 1
    }

    // ------ load trip + item ------


    // keep day tied to date
    LaunchedEffect(dateText, tripStartDateText) { recomputeDay() }

    // ------ pickers state ------
    val initialDateMillis = remember(dateText) { toEpochMillisOrNull(dateText) }
    val tripStartMillis = remember(tripStartDateText) { toEpochMillisOrNull(tripStartDateText) }
    val tripEndMillis = remember(tripEndDateText) { toEpochMillisOrNull(tripEndDateText) }

    val selectableDates = remember(tripStartMillis, tripEndMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val min = tripStartMillis
                val max = tripEndMillis
                return when {
                    min != null && max != null -> utcTimeMillis in min..max
                    min != null -> utcTimeMillis >= min
                    max != null -> utcTimeMillis <= max
                    else -> true
                }
            }
            override fun isSelectableYear(year: Int) = true
        }
    }

    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        selectableDates = selectableDates
        // if your compose version supports it, you can also pass initialDisplayedMonthMillis
    )

    val (initHour, initMinute) = remember(timeText) { parseHourMinuteOrNull(timeText) ?: (9 to 0) }
    val timeState = rememberTimePickerState(
        initialHour = initHour,
        initialMinute = initMinute,
        is24Hour = true
    )

    val isEditing = itemId != 0L
    val canSave = title.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Activity" else "Add Activity") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    scope.launch {
                        val entity = ItineraryItemEntity(
                            id = if (isEditing) itemId else 0L,   // Room will assign on insert when 0
                            tripId = tripId,
                            day = computedDay,
                            title = title.trim(),
                            date = dateText.trim(),
                            time = timeText.trim(),
                            location = locationText.trim().ifBlank { null },
                            notes = notesText.trim().ifBlank { null },
                            category = category.trim().ifBlank { null },
                            attachment = attachment.trim().ifBlank { null },
                            assignedTo = assignedTo.trim().ifBlank { null },
                            lat = null,   // 👈 add this
                            lng = null
                        )

                        onBack()
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(if (isEditing) "Save Changes" else "Add Activity")
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Activity Title*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Date
            OutlinedTextField(
                value = dateText,
                onValueChange = { /* read-only, picked via dialog */ },
                readOnly = true,
                label = {
                    val suffix =
                        if (tripStartDateText.isNotBlank())
                            " (Day $computedDay • Trip starts $tripStartDateText)"
                        else
                            " (Day $computedDay)"
                    Text("Date$suffix")
                },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Time
            OutlinedTextField(
                value = timeText,
                onValueChange = { /* read-only, picked via dialog */ },
                readOnly = true,
                label = { Text("Time (24h)") },
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(Icons.Default.Schedule, contentDescription = "Pick time")
                    }
                },
                placeholder = { Text("HH:mm") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = locationText,
                onValueChange = { locationText = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = attachment,
                onValueChange = { attachment = it },
                label = { Text("Attachment Link / File") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = assignedTo,
                onValueChange = { assignedTo = it },
                label = { Text("Assigned To") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }
    }

    // --------- Date Picker Dialog ----------
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = dateState.selectedDateMillis
                    if (millis != null) {
                        val picked = Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
                        dateText = picked.format(dateFormatter)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    // --------- Time Picker Dialog ----------
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val h = timeState.hour.coerceIn(0, 23)
                    val m = timeState.minute.coerceIn(0, 59)
                    timeText = String.format("%02d:%02d", h, m)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            title = { Text("Select time") },
            text = { TimePicker(state = timeState) }
        )
    }
}
