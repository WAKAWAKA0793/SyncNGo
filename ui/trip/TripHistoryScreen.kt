package com.example.tripshare.ui.trip

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tripshare.R
import com.example.tripshare.ui.home.TripStatus
import com.example.tripshare.ui.home.TripUi
import com.example.tripshare.ui.home.toUi // ✅ Import the mapper
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

// ---------------- Helper Functions (Local to this screen) ----------------
private val isoFmt: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
private val monthHeaderFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
private val dateRangeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

private fun TripUi.startLocalDateOrNull(): LocalDate? = try {
    startDateIso?.let { LocalDate.parse(it, isoFmt) }
} catch (_: DateTimeParseException) { null }

private fun TripUi.endLocalDateOrNull(): LocalDate? = try {
    endDateIso?.let { LocalDate.parse(it, isoFmt) }
} catch (_: DateTimeParseException) { null }

private fun TripUi.prettyDateRange(): String {
    val s = startLocalDateOrNull()
    val e = endLocalDateOrNull()
    return when {
        s != null && e != null -> "${s.format(dateRangeFmt)} — ${e.format(dateRangeFmt)}"
        s != null -> "Starts ${s.format(dateRangeFmt)}"
        e != null -> "Ends ${e.format(dateRangeFmt)}"
        else -> dateRange
    }
}

// ---------------- Main Screen ----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripHistoryScreen(
    vm: TripHistoryViewModel,
    onBack: () -> Unit,
    onTripClick: (Long) -> Unit
) {
    val pastEntities by vm.pastTrips.collectAsState(initial = emptyList())
    val zone = remember { ZoneId.systemDefault() }
    val today = remember { LocalDate.now(zone) }

    // 1. Map Entity -> TripUi & Group by Month
    val pastGrouped = remember(pastEntities) {
        pastEntities
            .map { it.toUi(TripStatus.PAST) } // ✅ Fix: Convert Entity to UI model
            .sortedByDescending { it.endLocalDateOrNull() ?: LocalDate.MIN }
            .groupBy { trip ->
                trip.endLocalDateOrNull()?.format(monthHeaderFmt) ?: "Unknown Date"
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip History",fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            if (pastEntities.isEmpty()) {
                item {
                    Text(
                        "No past trips found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            pastGrouped.forEach { (month, tripsInMonth) ->
                item {
                    Text(
                        text = month,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(tripsInMonth.chunked(2)) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { t ->
                            // Calculate "Recently archived" logic
                            val end = t.endLocalDateOrNull()
                            val daysSinceEnd = end?.let {
                                ChronoUnit.DAYS.between(it, today).toInt()
                            } ?: Int.MAX_VALUE
                            val recentlyArchived = daysSinceEnd in 0..14

                            HistoryTripCard(
                                trip = t,
                                badge = if (recentlyArchived) "Recently archived" else null,
                                badgeColor = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(170.dp),
                                onClick = { onTripClick(t.id) }
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// ---------------- Local Card Component ----------------
// Replicated locally because HomeSmallTripCard is private in HomeScreen.kt
@Composable
private fun HistoryTripCard(
    trip: TripUi,
    badge: String? = null,
    badgeColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val locationAndCategory = listOfNotNull(
        trip.location.takeIf { it.isNotBlank() },
        trip.category.takeIf { it.isNotBlank() }
    ).joinToString(" • ")

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = trip.imageUrl,
                contentDescription = "${trip.title} image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                Column(Modifier.padding(10.dp)) {
                    Text(
                        text = trip.title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (locationAndCategory.isNotBlank()) {
                        Text(
                            text = locationAndCategory,
                            color = Color.White.copy(alpha = 0.92f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = trip.prettyDateRange(),
                        color = Color.White.copy(alpha = 0.92f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (badge != null) {
                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}