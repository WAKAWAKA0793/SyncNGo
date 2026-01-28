@file:OptIn(
    ExperimentalMaterial3Api::class,
    com.google.maps.android.compose.MapsComposeExperimentalApi::class,
    ExperimentalLayoutApi::class
)

package com.example.tripshare.ui.trip

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.tripshare.data.model.ExpensePaymentEntity
import com.example.tripshare.data.model.TripEntity
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.max

data class CommentUi(
    val id: Long,
    val authorName: String,
    val text: String,
    val timestamp: String,
    val authorAvatarUrl: String? = null
)

data class RouteStopUi(
    val id: Long,
    val name: String,
    val start: LocalDate,
    val end: LocalDate,
    val nights: Int,
    val lat: Double,
    val lng: Double,
    val distanceFromPrevKm: Int? = null
)

private fun extOrFallback(icon: ImageVector?): ImageVector = icon ?: Icons.Default.Event

enum class PlanType(val label: String, val icon: ImageVector) {
    Activity("Activity", extOrFallback(Icons.Default.DirectionsWalk)),
    Flight("Flight", Icons.Default.Flight),
    Lodging("Lodging", Icons.Default.Hotel),
    CarRental("Car Rental", Icons.Default.DirectionsCar),
    Cruise("Cruise", extOrFallback(Icons.Default.DirectionsBoat)),
    Rail("Rail", extOrFallback(Icons.Default.Train)),
    Restaurant("Restaurant", extOrFallback(Icons.Default.Restaurant)),
}

data class PlanEntryUi(
    val id: Long,
    val date: LocalDate,
    val time: LocalTime?,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null, // Stores Notes/Confirmation
    val type: PlanType,
    val lat: Double? = null,
    val lng: Double? = null,
    val endDate: LocalDate? = null,
    val endTime: LocalTime? = null,

)

/* -------------------------------------------------------------------------- */
/* TOP-LEVEL SCREEN                                                           */
/* -------------------------------------------------------------------------- */

@Composable
fun ItineraryPlannerScreen(
    tripTitle: String,
    dateRangeLabel: String,
    stops: List<RouteStopUi>,
    plans: List<PlanEntryUi>,
    tripStart: LocalDate,
    tripEnd: LocalDate,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onOpenStop: (Long) -> Unit,
    onAddPlanType: (PlanType) -> Unit,
    onSelectPlan: (Long) -> Unit, // Add this
    currentComments: List<CommentUi>, // Add this
    onPostComment: (Long, String) -> Unit ,
    onDeletePlan: (Long) -> Unit,
    planExpenseMap: Map<Long, List<ExpensePaymentEntity>>,
    onViewExpense: (Long) -> Unit,
    onAddExpenseFromPlan: (PlanEntryUi) -> Unit,
    onEditPlan: (Long) -> Unit, // ✅ NEW: Edit callback
    availableTrips: List<TripEntity>,
    onCopyToTrip: (TripEntity) -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    var selectedPlanId by remember { mutableStateOf<Long?>(null) }
    var showCopyDialog by remember { mutableStateOf(false) }

    if (showCopyDialog) {
        CopyTargetDialog(
            trips = availableTrips,
            onDismiss = { showCopyDialog = false },
            onSelectTrip = { targetTrip ->
                onCopyToTrip(targetTrip)
                showCopyDialog = false
            }
        )
    }
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(tripTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { showCopyDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Itinerary"
                        )
                    }
                }
            )
        },

        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetDragHandle = { SheetHandle() },
        sheetPeekHeight = 220.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContent = {
            PlannerSheet(
                dateRangeLabel = dateRangeLabel,
                stops = stops,
                plans = plans,
                planExpenseMap = planExpenseMap,
                tripStart = tripStart,
                tripEnd = tripEnd,
                onOpenStop = onOpenStop,
                onAddPlanType = onAddPlanType,
                comments = currentComments, // 👈 Pass real data
                onPostComment = onPostComment,
                onDeletePlan = onDeletePlan,
                onSelectPlan = { id ->
                    selectedPlanId = id      // UI selection
                    onSelectPlan(id)         // 🔴 ViewModel.loadComments(id)
                },
                onAddExpenseFromPlan = onAddExpenseFromPlan,
                onViewExpense = onViewExpense,
                onEditPlan = onEditPlan // ✅ Pass through
            )
        },
        content = { innerPadding ->
            MapHeader(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                stops = stops,
                plans = plans,
                selectedPlanId = selectedPlanId
            )
        }
    )
}

// ... (MapHeader remains the same) ...
@Composable
private fun MapHeader(
    modifier: Modifier = Modifier,
    stops: List<RouteStopUi>,
    plans: List<PlanEntryUi>,
    selectedPlanId: Long?
) {
    // Determine where to focus the camera
    val selectedPlan = remember(plans, selectedPlanId) {
        plans.firstOrNull { it.id == selectedPlanId && it.lat != null && it.lng != null }
    }

    val stopPositions = remember(stops) { stops.map { LatLng(it.lat, it.lng) } }

    val anchor = selectedPlan?.let { LatLng(it.lat!!, it.lng!!) }
        ?: stopPositions.firstOrNull()
        ?: plans.firstOrNull { it.lat != null && it.lng != null }?.let { LatLng(it.lat!!, it.lng!!) }
        ?: LatLng(3.1390, 101.6869) // Default fallback

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(anchor, 10f)
    }

    // Animate camera if selection changes
    LaunchedEffect(anchor) {
        cameraState.animate(
            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(anchor, 12f)
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraState,
        uiSettings = MapUiSettings(zoomControlsEnabled = true)
    ) {
        // 1. Draw Route Stops (The main road trip path)
        stops.forEachIndexed { idx, stop ->
            Marker(
                state = com.google.maps.android.compose.MarkerState(
                    position = LatLng(stop.lat, stop.lng)
                ),
                title = "${idx + 1}. ${stop.name}",
                // You can add a custom icon here for stops if you want
            )
        }

        // 2. Draw ALL Itinerary Plans (Flights, Hotels, etc.)
        plans.forEach { plan ->
            if (plan.lat != null && plan.lng != null) {
                val isSelected = plan.id == selectedPlanId

                Marker(
                    state = com.google.maps.android.compose.MarkerState(
                        position = LatLng(plan.lat, plan.lng)
                    ),
                    title = plan.title,
                    snippet = plan.subtitle,
                    // Optional: Make selected pin distinct color
                    icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                        if (plan.type == PlanType.Flight) com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE
                        else if (isSelected) com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
                        else com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET
                    )
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/* BOTTOM SHEET CONTENT                                                       */
/* -------------------------------------------------------------------------- */
@Composable
private fun PlannerSheet(
    dateRangeLabel: String,
    stops: List<RouteStopUi>,
    plans: List<PlanEntryUi>,
    onOpenStop: (Long) -> Unit,
    tripStart: LocalDate,
    tripEnd: LocalDate,
    planExpenseMap: Map<Long, List<ExpensePaymentEntity>>,
    onViewExpense: (Long) -> Unit,
    onAddPlanType: (PlanType) -> Unit,
    onDeletePlan: (Long) -> Unit,
    comments: List<CommentUi>, // 👈 Receive real list
    onPostComment: (Long, String) -> Unit,
    onSelectPlan: (Long) -> Unit,
    onAddExpenseFromPlan: (PlanEntryUi) -> Unit,
    onEditPlan: (Long) -> Unit // ✅ NEW Param
) {
    var detailPlan by remember { mutableStateOf<PlanEntryUi?>(null) }

    val nightsPlanned = remember(plans, tripStart, tripEnd) {
        plans
            .map { it.date }
            .filter { !it.isBefore(tripStart) && !it.isAfter(tripEnd) }
            .toSet()
            .size
    }

    val nightsTarget = max(ChronoUnit.DAYS.between(tripStart, tripEnd).toInt(), 1)
    val dateFmt = remember { DateTimeFormatter.ofPattern("d MMM yyyy") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(dateRangeLabel, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlannedBadge("$nightsPlanned/$nightsTarget")
                    Spacer(Modifier.width(8.dp))
                    Text("Nights planned", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Divider()

        PlanTabContent(
            plans = plans,
            onOpen = { id ->
                onSelectPlan(id)
                detailPlan = plans.firstOrNull { it.id == id }
            },
            onDelete = { id -> onDeletePlan(id) },
            onAddTypeChosen = onAddPlanType
        )

        if (stops.isNotEmpty()) {
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
            Text(
                "Stops",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(stops, key = { _, s -> "stop-${s.id}" }) { index, stop ->
                    SwipeableStopRow(
                        stop = stop,
                        index = index + 1,
                        dateFormatter = dateFmt,
                        onOpen = { onOpenStop(stop.id) },
                        onDelete = {}
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    detailPlan?.let { plan ->
        val linkedExpenses = planExpenseMap[plan.id] ?: emptyList()


        PlanDetailsDialog(
            plan = plan,
            linkedExpenses = linkedExpenses,
            comments = comments,// <--- PASS COMMENTS HERE
            onDismiss = { detailPlan = null },
            onAddExpenseFromPlan = { pickedPlan ->
                onAddExpenseFromPlan(pickedPlan)
                detailPlan = null
            },
            onPostComment = { text ->
                onPostComment(plan.id, text) // 👈 Send real post request
            },
            onViewExpense = onViewExpense,
            onEditPlan = {
                detailPlan = null
                onEditPlan(it)
            },
        )
    }
}

@Composable
fun PlanDetailsDialog(
    plan: PlanEntryUi,
    linkedExpenses: List<ExpensePaymentEntity>,
    comments: List<CommentUi> = emptyList(),
    onDismiss: () -> Unit,
    onAddExpenseFromPlan: (PlanEntryUi) -> Unit,
    onViewExpense: (Long) -> Unit,
    onEditPlan: (Long) -> Unit,
    onPostComment: (String) -> Unit
) {
    val dateFmt = remember { DateTimeFormatter.ofPattern("EEE, MMM d, yyyy") }
    val timeFmt = remember { DateTimeFormatter.ofPattern("h:mm a") }
    val scrollState = rememberScrollState()

    // State to control the full comment popup
    var showAllCommentsDialog by remember { mutableStateOf(false) }

    // Logic: Get only the last 2 comments (assuming list is chronological)
    val previewComments = remember(comments) {
        comments.takeLast(2)
    }

    if (showAllCommentsDialog) {
        AllCommentsDialog(
            comments = comments,
            onDismiss = { showAllCommentsDialog = false },
            onPostComment = onPostComment
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                // --- Header Section ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(plan.type.label) },
                        icon = {
                            Icon(
                                plan.type.icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = null
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!plan.subtitle.isNullOrBlank()) {
                    Text(
                        text = plan.subtitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // --- Details Section ---
                DetailRow(icon = Icons.Outlined.DateRange, label = "Time") {
                    Column {
                        Text("Start: ${plan.date.format(dateFmt)} at ${plan.time?.format(timeFmt) ?: "All Day"}")
                        if (plan.endDate != null) {
                            Text("End: ${plan.endDate.format(dateFmt)} at ${plan.endTime?.format(timeFmt) ?: ""}")
                        }
                    }
                }

                if (!plan.description.isNullOrBlank()) {
                    Spacer(Modifier.height(16.dp))
                    // You can add a description row here if needed
                    Text(plan.description, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(16.dp))

                // --- Expense Section (Existing Logic) ---
                if (linkedExpenses.isNotEmpty()) {
                    // ... render expenses ...
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Comments", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    if (comments.isNotEmpty()) {
                        Text(
                            "View all (${comments.size})",
                            modifier = Modifier
                                .clickable { showAllCommentsDialog = true }
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (comments.isEmpty()) {
                    Text("No comments yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    // Fake input to trigger dialog
                    OutlinedButton(
                        onClick = { showAllCommentsDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add a comment...")
                    }
                } else {
                    // Show only last 2
                    comments.takeLast(2).forEach { comment ->
                        CommentRow(comment = comment)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showAllCommentsDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add a comment...")
                    }
                }
                Spacer(Modifier.height(24.dp))

                // --- Action Buttons ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onAddExpenseFromPlan(plan) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Add Expense")
                    }

                    Button(
                        onClick = { onEditPlan(plan.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Edit")
                    }
                }
            }
        }
    }
}

@Composable
fun AllCommentsDialog(
    comments: List<CommentUi>,
    onDismiss: () -> Unit,
    onPostComment: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Auto-scroll to bottom when a new comment is added
    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            listState.animateScrollToItem(comments.size - 1)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f) // Use 85% of screen height
        ) {
            Column(Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Comments (${comments.size})",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                HorizontalDivider()

                // Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(comments) { comment ->
                        CommentRow(comment = comment)
                    }
                }

                HorizontalDivider()

                // Input Field
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Write a comment...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                onPostComment(newCommentText)
                                newCommentText = "" // Clear input on send
                            }
                        },
                        enabled = newCommentText.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (newCommentText.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }
        }
    }
}
// ... (DetailRow, PlannedBadge, RouteRowCompact, SheetHandle, PlanTabContent, etc. remain unchanged) ...
@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    content: @Composable () -> Unit
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 2.dp).size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        content()
    }
}

@Composable
private fun PlannedBadge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = CircleShape
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Hotel, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun RouteRowCompact(
    index: Int,
    stop: RouteStopUi,
    onOpen: () -> Unit,
    dateFormatter: DateTimeFormatter
) {
    val dateStr = "${stop.start.format(dateFormatter)} - ${stop.end.format(dateFormatter)}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(index.toString(), style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    stop.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stop.nights.toString(), style = MaterialTheme.typography.titleMedium)
                Text(
                    if (stop.nights == 1) "night" else "nights",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun SheetHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(4.dp)
                .width(48.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        )
    }
}

@Composable
private fun PlanTabContent(
    plans: List<PlanEntryUi>,
    onOpen: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onAddTypeChosen: (PlanType) -> Unit
) {
    val plansByDate = remember(plans) { plans.groupBy { it.date } }
    val sortedDates = remember(plansByDate) { plansByDate.keys.sorted() }
    var addMenuExpanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Plans",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            FilledTonalButton(
                onClick = { addMenuExpanded = true },
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add a Plan")
            }
        }
        Divider()
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            sortedDates.forEach { day ->
                val itemsForDay = plansByDate[day].orEmpty()
                val sortedItemsForDay = itemsForDay.sortedWith(
                    compareBy<PlanEntryUi> { it.time ?: LocalTime.MIDNIGHT }
                )
                item(key = "header-$day") {
                    DateHeader(date = day)
                }
                itemsIndexed(
                    sortedItemsForDay,
                    key = { _, p -> "plan-${p.id}" }
                ) { _, plan ->
                    TimelineItem(
                        plan = plan,
                        onOpen = { onOpen(plan.id) },
                        onDelete = { onDelete(plan.id) }
                    )
                }
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }
    if (addMenuExpanded) {
        AddPlanTypeMenu(
            onDismiss = { addMenuExpanded = false },
            onPick = {
                addMenuExpanded = false
                onAddTypeChosen(it)
            }
        )
    }
}
@Composable
fun ExpensePreviewDialog(
    details: SelectedExpenseDetails,
    onDismiss: () -> Unit,
    onEdit: (Long) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                /* ───── Header ───── */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Expense details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.clickable { onDismiss() },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                /* ───── Summary ───── */
                ExpensePreviewRow(label = "Title", value = details.title)
                ExpensePreviewRow(label = "Category", value = details.category)
                ExpensePreviewRow(
                    label = "Amount",
                    value = "MYR ${String.format("%.2f", details.amount)}",
                    emphasize = true
                )

                // Show Payer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Paid by",
                        modifier = Modifier.width(90.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PayerAvatarSmall(name = details.payerName, photoUrl = details.payerPhotoUrl)
                        Spacer(Modifier.width(8.dp))
                        Text(details.payerName, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                /* ───── Split section ───── */
                Text(
                    "Split breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (details.splits.isEmpty()) {
                    Text(
                        "No split information available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        details.splits.forEach { s ->
                            SplitPreviewRow(
                                name = s.name,
                                photoUrl = s.photoUrl,
                                amount = s.amount
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total owed", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "MYR ${String.format("%.2f", details.splits.sumOf { it.amount })}",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                /* ───── Actions ───── */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss
                    ) { Text("Close") }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onEdit(details.paymentId) }
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Edit")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpensePreviewRow(
    label: String,
    value: String,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            label,
            modifier = Modifier.width(90.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SplitPreviewRow(
    name: String,
    photoUrl: String?,
    amount: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PayerAvatarSmall(name = name, photoUrl = photoUrl)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Owes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            "MYR ${String.format("%.2f", amount)}",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PayerAvatarSmall(
    name: String,
    photoUrl: String?,
) {
    val initials = remember(name) {
        val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (parts.isEmpty()) "U" else {
            val f = parts[0].first().uppercaseChar()
            val s = parts.getOrNull(1)?.firstOrNull()?.uppercaseChar()
            if (s != null) "$f$s" else "$f"
        }
    }

    if (!photoUrl.isNullOrBlank()) {
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            modifier = Modifier.size(32.dp) // Smaller than BudgetScreen version
        ) {
            Image(
                painter = rememberAsyncImagePainter(photoUrl),
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(32.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    initials,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
@Composable
private fun DateHeader(date: LocalDate) {
    val fmt = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            date.format(fmt),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun TimelineItem(
    plan: PlanEntryUi,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
            }
            false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        content = {
            Row(Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(72.dp)
                ) {
                    Text(
                        text = plan.time?.let { it.format(DateTimeFormatter.ofPattern("h:mm a")) } ?: "",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.height(56.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                plan.type.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Card(
                    onClick = onOpen,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            plan.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!plan.subtitle.isNullOrBlank()) {
                            Text(
                                plan.subtitle!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun AddPlanTypeMenu(
    onDismiss: () -> Unit,
    onPick: (PlanType) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Add a Plan", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                val items = remember {
                    listOf(
                        PlanType.Activity,
                        PlanType.Flight,
                        PlanType.Lodging,
                        PlanType.CarRental,
                        PlanType.Cruise,
                        PlanType.Rail,
                        PlanType.Restaurant,
                    )
                }
                FlowRow(
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items.forEach { t ->
                        PlanTypeChip(
                            type = t,
                            onClick = { onPick(t) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanTypeChip(
    type: PlanType,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(type.icon, contentDescription = null)
            Spacer(Modifier.width(10.dp))
            Text(type.label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun CopyTargetDialog(
    trips: List<TripEntity>,
    onDismiss: () -> Unit,
    onSelectTrip: (TripEntity) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Copy Itinerary To...",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (trips.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No other trips found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(trips) { trip ->
                            Card(
                                onClick = { onSelectTrip(trip) },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(trip.name, style = MaterialTheme.typography.titleMedium)
                                    trip.startDate?.let { date ->
                                        Text(
                                            "Starts: ${date.format(java.time.format.DateTimeFormatter.ISO_DATE)}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun ItineraryPlannerRoute(
    vm: ItineraryPlannerViewModel,
    onBack: () -> Unit,
    onNavigateToExpense: (Long) -> Unit, // This is now "Edit"
    onAddExpenseFromPlan: (PlanEntryUi) -> Unit,
    onAdd: () -> Unit,
    onOpenStop: (Long) -> Unit,
    onNavigateToEditPlan: (Long) -> Unit
) {
    val plans by vm.plans.collectAsState()
    val planExpenseMap by vm.planExpenseMap.collectAsState()
    val availableTrips by vm.availableTargetTrips.collectAsState()
    val copyStatus by vm.copyStatus.collectAsState()
    val selectedExpenseDetails by vm.selectedExpense.collectAsState() // <--- NEW
    val comments by vm.currentPlanComments.collectAsState()
    val ctx = LocalContext.current

    LaunchedEffect(copyStatus) {
        copyStatus?.let {
            Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
            vm.clearCopyStatus()
        }
    }

    val tripStart = vm.tripStart ?: LocalDate.now()
    val tripEnd = vm.tripEnd ?: tripStart.plusDays(2)
    val stops: List<RouteStopUi> = emptyList()

    // 1. Show the Expense Dialog if state exists
    selectedExpenseDetails?.let { details ->
        ExpensePreviewDialog(
            details = details,
            onDismiss = { vm.clearSelectedExpense() },
            onEdit = { id ->
                vm.clearSelectedExpense()
                onNavigateToExpense(id)
            }
        )
    }

    ItineraryPlannerScreen(
        tripTitle = vm.tripName ?: "Trip #${vm.tripId}",
        dateRangeLabel = "${tripStart} – ${tripEnd}",
        stops = stops,
        plans = plans,
        planExpenseMap = planExpenseMap,
        onViewExpense = { id -> vm.selectExpenseForPreview(id) }, // <--- CHANGED: View details locally
        tripStart = tripStart,
        tripEnd = tripEnd,
        onBack = onBack,
        onAdd = onAdd,
        onOpenStop = onOpenStop,
        onAddPlanType = { type -> vm.addQuickPlan(type) },
        onDeletePlan = { id -> vm.deletePlan(id) },
        onSelectPlan = { id ->
            vm.loadComments(id) // 👈 2. Trigger loading when plan is clicked
        },
        currentComments = comments, // 👈 3. Pass comments down
        onPostComment = { planId, text ->
            vm.postComment(planId, text) // 👈 4. Handle posting
        },
        onAddExpenseFromPlan = onAddExpenseFromPlan,
        availableTrips = availableTrips,
        onCopyToTrip = { targetTrip -> vm.copyItineraryToTrip(targetTrip) },
        onEditPlan = onNavigateToEditPlan
    )
}
@Composable
private fun CommentRow(comment: CommentUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Reusing your existing PayerAvatarSmall composable
        PayerAvatarSmall(name = comment.authorName, photoUrl = comment.authorAvatarUrl)

        Spacer(Modifier.width(12.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = comment.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
@Composable
private fun SwipeableStopRow(
    stop: RouteStopUi,
    index: Int,
    dateFormatter: DateTimeFormatter,
    onOpen: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete?.invoke()
            }
            false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete stop",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        content = {
            RouteRowCompact(
                index = index,
                stop = stop,
                onOpen = onOpen,
                dateFormatter = dateFormatter
            )
        }
    )
}

private fun planTypeToExpenseCategory(type: PlanType): String {
    return when (type) {
        PlanType.Restaurant -> "Food"
        PlanType.Lodging -> "Hotel"
        PlanType.Activity -> "Activity"
        PlanType.Flight,
        PlanType.CarRental,
        PlanType.Rail,
        PlanType.Cruise -> "Transport"
    }
}