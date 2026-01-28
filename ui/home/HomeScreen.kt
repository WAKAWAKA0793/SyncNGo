package com.example.tripshare.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tripshare.R
import com.example.tripshare.data.model.TripCategory
import com.example.tripshare.ui.TripBottomBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/* ---------- sort ---------- */
private enum class DiscoverSort(val label: String) {
    EARLIEST("Earliest start"),
    MOST_TRAVELERS("Most travelers")
}

/* ---------- TripUi date helpers (requires TripUi.startDateIso / endDateIso) ---------- */
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
        else -> dateRange // fallback if you keep a precomputed string
    }
}

/* ------------------------------------------------------------------------------------ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: HomeViewModel,
    userDisplayName: String? = null,
    onMessagesClick: () -> Unit = {},
    onTripClick: (Long) -> Unit,
    onJoinNow: () -> Unit = {},
    onNotificationsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNavClick: (String) -> Unit,
    onLogout: () -> Unit = {},
    userHasJoinedTrip: Boolean = false
) {
    val ui by vm.uiState.collectAsState()
    var selectedSort by rememberSaveable { mutableStateOf(DiscoverSort.EARLIEST) }
    var showSortSheet by remember { mutableStateOf(false) }
    val selectedCategory by vm.selectedCategory.collectAsState()

    // ✅ FIXED: Filter out full trips AND apply sorting
    val discoverSorted = remember(ui.discover, selectedSort) {
        // 1. Filter: Hide trips that are full
        val availableTrips = ui.discover.filter { trip -> trip.travelers < trip.maxTravelers }

        // 2. Sort: Apply the selected sort order
        when (selectedSort) {
            DiscoverSort.EARLIEST -> availableTrips.sortedBy { it.startDateIso }
            DiscoverSort.MOST_TRAVELERS -> availableTrips.sortedByDescending { it.travelers }
        }
    }

    val upcomingActive = ui.upcoming.filter { it.endLocalDateOrNull() == null || !it.endLocalDateOrNull()!!.isBefore(LocalDate.now()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onNotificationsClick) { Icon(Icons.Default.Notifications, null) }
                    IconButton(onClick = onMessagesClick) { Icon(Icons.Default.Message, null) }
                }
            )
        },
        bottomBar = {
            TripBottomBar(currentRoute = "home", onNavClick = onNavClick)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Welcome Header
            if (userDisplayName != null) {
                item {
                    Text("Welcome, $userDisplayName!", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    HomeSearchBar(onClick = onSearchClick)
                    Spacer(Modifier.height(8.dp))
                }
            }

            // 2. MY TRIPS SECTION
            if (upcomingActive.isNotEmpty()) {
                item {
                    HomeSectionHeader(title = "My Upcoming Trips")
                }
                item {
                    HomeTripHorizontalCarousel(
                        trips = upcomingActive,
                        onTripClick = { trip -> onTripClick(trip.id) }
                    )
                }
            } else {
                item { HomeHeroJoinBlock(onJoinNow) }
            }

            // 3. DISCOVER SECTION
            item {
                Spacer(Modifier.height(8.dp))
                HomeSectionHeader(
                    title = "Discover",
                    subtitle = "Find your next adventure",
                    showSort = true,
                    onSortClick = { showSortSheet = true }
                )
            }

            item {
                CategoryFilterRow(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        vm.toggleCategory(category)
                    }
                )
            }


            // 4. Discover List
            // ✅ FIXED: Use 'discoverSorted' instead of 'ui.discover'
            items(discoverSorted) { trip ->
                HomeDiscoverTripCard(trip = trip, onClick = { onTripClick(trip.id) })
            }

            // Optional: Show a message if no trips are found after filtering
            if (discoverSorted.isEmpty() && ui.discover.isNotEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No available trips at the moment.", color = Color.Gray)
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        HomeSortBottomSheet(
            current = selectedSort,
            onPick = { picked ->
                selectedSort = picked
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }
}

/* ---------- UI pieces (renamed to avoid clashes) ---------- */

@Composable
private fun HomeSectionHeader(
    title: String,
    subtitle: String? = null,
    showSort: Boolean = false,
    onSortClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (showSort) {
            TextButton(onClick = onSortClick) { Text("Sort by") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeSortBottomSheet(
    current: DiscoverSort,
    onPick: (DiscoverSort) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Sort trips", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            HomeSortOptionRow(
                title = DiscoverSort.EARLIEST.label,
                selected = current == DiscoverSort.EARLIEST,
                onClick = { onPick(DiscoverSort.EARLIEST) }
            )
            HomeSortOptionRow(
                title = DiscoverSort.MOST_TRAVELERS.label,
                selected = current == DiscoverSort.MOST_TRAVELERS,
                onClick = { onPick(DiscoverSort.MOST_TRAVELERS) }
            )
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Cancel") }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HomeSortOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 14.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun HomeSmallTripCard(
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
private fun TripUi.budgetTextForCard(): String {
    budgetDisplay?.takeIf { it.isNotBlank() }?.let { return it }
    return "N/A"
}

@Composable
private fun HomeDiscoverTripCard(
    trip: TripUi,
    onClick: () -> Unit
) {
    val isFull = trip.travelers >= trip.maxTravelers

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column {

            Box {
                AsyncImage(
                    model = trip.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    error = painterResource(R.drawable.ic_launcher_foreground)
                )

                if (isFull) {
                    Surface(
                        color = Color.Red,
                        shape = RoundedCornerShape(bottomEnd = 12.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "FULL",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {

                Text(
                    text = trip.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                HomeTripDetailRow(
                    icon = Icons.Default.CalendarToday,
                    text = trip.prettyDateRange()
                )

                Spacer(Modifier.height(4.dp))

                HomeTripDetailRow(
                    icon = Icons.Default.AttachMoney,
                    text = trip.budgetTextForCard()
                )

                Spacer(Modifier.height(8.dp))

                if (trip.category.isNotBlank()) {
                    HomeTripDetailRow(
                        icon = Icons.Default.Category,
                        text = trip.category
                    )
                    Spacer(Modifier.height(6.dp))
                }

                HomeTripDetailRow(
                    icon = Icons.Default.People,
                    text = "${trip.travelers} / ${trip.maxTravelers} travelers"
                )
            }
        }
    }
}

@Composable
private fun HomeTripDetailRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HomeHeroJoinBlock(onJoinNow: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.ic_empty_trip),
            contentDescription = "No trips yet",
            modifier = Modifier
                .height(120.dp)
                .padding(8.dp)
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onJoinNow,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Join a trip now")
        }
    }
}

@Composable
private fun HomeSearchBar(
    onClick: () -> Unit,
    placeholder: String = "Search by budget, category, location..."
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterRow(
    selectedCategory: TripCategory?,
    onCategorySelected: (TripCategory) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(TripCategory.values()) { category ->
            val isSelected = (category == selectedCategory)

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = { Text(category.label) },
                leadingIcon = if (isSelected) {
                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
fun HomeTripHorizontalCarousel(
    trips: List<TripUi>,
    modifier: Modifier = Modifier,
    itemWidthDp: Int = 240,
    itemHeightDp: Int = 160,
    itemSpacingDp: Int = 12,
    edgePaddingDp: Int = 16,
    badgeProvider: (TripUi) -> String? = { null },
    badgeColorProvider: ((TripUi) -> Color)? = null,
    onTripClick: (TripUi) -> Unit
) {
    val listState = rememberLazyListState()
    val fling = rememberSnapFlingBehavior(lazyListState = listState)

    LazyRow(
        state = listState,
        flingBehavior = fling,
        contentPadding = PaddingValues(horizontal = edgePaddingDp.dp),
        horizontalArrangement = Arrangement.spacedBy(itemSpacingDp.dp),
        modifier = modifier.height(itemHeightDp.dp)
    ) {
        items(trips, key = { it.id }) { trip ->
            HomeSmallTripCard(
                trip = trip,
                badge = badgeProvider(trip),
                modifier = Modifier
                    .width(itemWidthDp.dp)
                    .height(itemHeightDp.dp),
                onClick = { onTripClick(trip) }
            )
        }
    }
}