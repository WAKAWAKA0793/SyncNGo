// ui/search/SearchScreen.kt
package com.example.tripshare.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates // 👈 1. IMPORT THIS
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tripshare.data.model.TripCategory
import com.example.tripshare.ui.home.HomeViewModel
import com.example.tripshare.ui.home.TripUi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    vm: HomeViewModel,
    onBack: () -> Unit,
    onAvatarClick: () -> Unit,
    onTripClick: (Long) -> Unit
) {
    val trips by vm.allTrips.collectAsState()
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<TripCategory?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // ✅ 2. UPDATE THIS BLOCK: Define validator to disable past dates
    val dateRangeState = rememberDateRangePickerState(
        selectableDates = remember {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // Convert the picker's date to LocalDate
                    val dateToCheck = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()

                    // Get Today's date in UTC
                    val today = LocalDate.now(ZoneId.of("UTC"))

                    // Allow only if date is NOT before today
                    return !dateToCheck.isBefore(today)
                }
            }
        }
    )

    // ✅ Cached filtered list: re-run only when dependencies change
    val filteredTrips = remember(
        trips,
        query,
        selectedCategory,
        dateRangeState.selectedStartDateMillis,
        dateRangeState.selectedEndDateMillis
    ) {
        trips.filter { trip ->
            val q = query.trim()

            // ✅ 1) Text Query Match (Title / Location / Category / Budget)
            val matchesQuery = q.isBlank() ||
                    trip.title.contains(q, ignoreCase = true) ||
                    trip.location.contains(q, ignoreCase = true) ||
                    trip.category.contains(q, ignoreCase = true) ||
                    (trip.budgetDisplay?.contains(q, ignoreCase = true) == true)

            // ✅ 2) Category Chip Match
            val matchesCategory =
                selectedCategory == null ||
                        trip.category.equals(selectedCategory!!.label, ignoreCase = true)

            // ✅ 3) Date Range Match
            val matchesDate = run {
                val startMillis = dateRangeState.selectedStartDateMillis
                val endMillis = dateRangeState.selectedEndDateMillis

                if (startMillis != null && endMillis != null) {
                    // Convert Picker Millis (UTC) to LocalDate
                    val rangeStart = Instant.ofEpochMilli(startMillis)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()
                    val rangeEnd = Instant.ofEpochMilli(endMillis)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()

                    // Parse Trip Dates (ISO string from TripUi)
                    val tripStart = try { trip.startDateIso?.let { LocalDate.parse(it) } } catch (e: Exception) { null }
                    val tripEnd = try { trip.endDateIso?.let { LocalDate.parse(it) } } catch (e: Exception) { null }

                    if (tripStart != null && tripEnd != null) {
                        // Trip overlaps with selected range
                        !(tripEnd.isBefore(rangeStart) || tripStart.isAfter(rangeEnd))
                    } else true
                } else true
            }

            matchesQuery && matchesCategory && matchesDate
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Groups") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search by title, location, category, budget...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // ✅ Date chips row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { showDatePicker = true },
                    label = {
                        val s = dateRangeState.selectedStartDateMillis
                        val e = dateRangeState.selectedEndDateMillis
                        Text(if (s != null && e != null) "${formatDate(s)} → ${formatDate(e)}" else "Choose Dates")
                    }
                )
                if (dateRangeState.selectedStartDateMillis != null) {
                    AssistChip(
                        onClick = { dateRangeState.setSelection(null, null) },
                        label = { Text("Clear Dates") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Featured Groups", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTrips, key = { it.id }) { trip ->
                    SearchTripCard(trip, onClick = { onTripClick(trip.id) })
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DateRangePicker(state = dateRangeState)
        }
    }
}

// ... (Rest of the file remains unchanged: SearchTripCard, formatDate)
@Composable
private fun SearchTripCard(trip: TripUi, onClick: () -> Unit) {
    val isFull = trip.travelers >= trip.maxTravelers

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(Modifier.height(160.dp)) {
                AsyncImage(
                    model = trip.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (isFull) {
                    Surface(
                        color = Color.Red,
                        shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "FULL",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Column(Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        trip.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${trip.travelers}/${trip.maxTravelers}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isFull) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val budgetText = trip.budgetDisplay?.takeIf { it.isNotBlank() } ?: "N/A"
                Text(
                    "${trip.category} • Budget: $budgetText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    trip.dateRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(Modifier.height(4.dp))
                if (trip.description.isNotBlank()) {
                    Text(
                        trip.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatDate(millis: Long): String {
    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("dd MMM"))
}