package com.example.tripshare.ui.trip

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.db.AppDatabase
import com.example.tripshare.data.model.ItineraryItemEntity
import com.example.tripshare.data.model.RatingSummary
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.TripMeetingPointEntity
import com.example.tripshare.data.model.TripParticipantEntity
import com.example.tripshare.data.model.Visibility
import com.example.tripshare.data.model.WaitlistEntity
import com.example.tripshare.ui.notifications.RequestNotificationPermissionIfNeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// Define local accent colors if not in Theme
private val Amber500 = Color(0xFFFFC107)
private val Orange500 = Color(0xFFFF9800)

private fun openGoogleMaps(label: String?, lat: Double?, lng: Double?, ctx: android.content.Context) {
    val uri = when {
        lat != null && lng != null -> {
            Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(label ?: "Starting point")})")
        }
        !label.isNullOrBlank() -> {
            Uri.parse("geo:0,0?q=${Uri.encode(label)}")
        }
        else -> return
    }
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    if (intent.resolveActivity(ctx.packageManager) != null) {
        ctx.startActivity(intent)
    } else {
        ctx.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}

@Composable
fun TripDetailsScreen(
    vm: TripDetailsViewModel,
    onBack: () -> Unit,
    onJoined: (TripEntity) -> Unit,
    onWaitlist: (WaitlistEntity) -> Unit,
    onOpenProfile: (Long) -> Unit,
    onEditTrip: (Long) -> Unit,
    onCopyItinerary: (List<ItineraryItemEntity>) -> Unit
) {
    val ctx = LocalContext.current
    val db = remember { AppDatabase.get(ctx) }
    val scope = rememberCoroutineScope()

    RequestNotificationPermissionIfNeeded(
        onGranted = { },
        onDenied = { }
    )

    // 1. Collect the UI Model (initially null)
    val uiModel = vm.tripUiModel.collectAsState(initial = null).value

    val joining = vm.joining.collectAsState().value
    val hasRequested by vm.hasRequested.collectAsState()
    val currentUserIdState = AuthPrefs.getUserId(ctx).collectAsState(initial = -1L)
    val currentUserId = currentUserIdState.value
    val rating by vm.ratingSummary.collectAsState(initial = null)

    val organizerLocalIdState = remember { mutableStateOf<Long?>(null) }

    // Try to load organizer ID once model is loaded
    LaunchedEffect(uiModel?.trip?.organizerId) {
        val organizerFid = uiModel?.trip?.organizerId ?: return@LaunchedEffect
        // Run safely on IO
        withContext(Dispatchers.IO) {
            val organizerUser = db.userDao().findByFirebaseId(organizerFid)
            organizerLocalIdState.value = organizerUser?.id
            organizerUser?.id?.let { vm.loadOrganizerRating(it) }
        }
    }

    LaunchedEffect(Unit) {
        val userId = AuthPrefs.getUserId(ctx).firstOrNull() ?: -1L
        if (userId != -1L) vm.refreshRequestState(userId)
    }

    // 🟢 Handle Loading State
    if (uiModel == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
        return
    }

    // 🟢 Data is loaded, render the content
    uiModel.let { model ->
        val isPrivate = remember(model.trip.visibility) { model.trip.visibility == Visibility.Private }
        val currentUserFirebaseIdState = remember { mutableStateOf<String?>(null) }
        val currentUserLocalId = currentUserIdState.value

        LaunchedEffect(currentUserLocalId) {
            currentUserLocalId?.let { uid ->
                if (uid != -1L) {
                    withContext(Dispatchers.IO) {
                        currentUserFirebaseIdState.value =
                            db.userDao().getUserById(uid)?.firebaseId
                    }
                }
            }
        }

        val isHost = model.trip.organizerId == (currentUserFirebaseIdState.value ?: "")
        val isParticipant = remember(model.participants, currentUserLocalId) {
            model.participants.any { it.userId == currentUserLocalId }
        }

        TripDetailsContent(
            trip = model.trip,
            participants = model.participants,
            itinerary = model.itinerary,
            rating = rating,
            organizerName = model.organizerRating?.organizerName,
            organizerAvatarUrl = model.organizerRating?.avatarUrl,
            meetingPoints = model.meetingPoints,
            joining = joining,
            isPrivate = isPrivate,
            hasRequested = hasRequested,
            isHost = isHost,
            isParticipant = isParticipant,
            onBack = onBack,
            onEditAction = { onEditTrip(model.trip.id) },
            onCopyItinerary = onCopyItinerary,
            onPrimaryAction = {
                // ✅ UPDATED: Robust Click Handler with Feedback
                scope.launch {
                    val userId = AuthPrefs.getUserId(ctx).firstOrNull() ?: -1L
                    if (userId == -1L) {
                        Toast.makeText(ctx, "You are not logged in.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Run DB Lookups on IO thread to prevent main thread blocking/crashes
                    val (user, ownerIdResolved) = withContext(Dispatchers.IO) {
                        val u = db.userDao().getUserById(userId)
                        // Try to find owner again if state was null
                        val owner = organizerLocalIdState.value
                            ?: db.userDao().findByFirebaseId(model.trip.organizerId)?.id
                        Pair(u, owner)
                    }

                    if (user == null) {
                        Toast.makeText(ctx, "Error: User profile not found locally.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    if (isPrivate) {
                        // For Private trips, we MUST have the owner's ID
                        if (ownerIdResolved == null) {
                            Toast.makeText(ctx, "Organizer data syncing... please wait.", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        if (!hasRequested) {
                            vm.requestToJoin(
                                userId = user.id,
                                tripId = model.trip.id,
                                ownerId = ownerIdResolved,
                                email = user.email,
                                displayName = user.name.ifBlank { user.email },
                                onRequested = {
                                    Toast.makeText(ctx, "Request sent!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(ctx, "Request already sent.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Public Trip -> Direct Join (or Waitlist if full)
                        vm.joinTrip(
                            userId = user.id,
                            email = user.email,
                            displayName = user.name.ifBlank { user.email },
                            onJoined = {
                                Toast.makeText(ctx, "Joined successfully!", Toast.LENGTH_SHORT).show()
                                onJoined(model.trip)
                            },
                            onWaitlist = { wait ->
                                Toast.makeText(ctx, "Added to waitlist.", Toast.LENGTH_SHORT).show()
                                onWaitlist(wait)
                            }
                        )
                    }
                }
            },
            onOpenProfile = onOpenProfile
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TripDetailsContent(
    trip: TripEntity,
    participants: List<TripParticipantEntity>,
    isHost: Boolean,
    isParticipant: Boolean,
    onEditAction: () -> Unit,
    meetingPoints: List<TripMeetingPointEntity>,
    itinerary: List<ItineraryItemEntity>,
    rating: RatingSummary?,
    organizerName: String?,
    organizerAvatarUrl: String?,
    joining: Boolean,
    isPrivate: Boolean,
    hasRequested: Boolean,
    onBack: () -> Unit,
    onPrimaryAction: () -> Unit,
    onOpenProfile: (Long) -> Unit,
    onCopyItinerary: (List<ItineraryItemEntity>) -> Unit
) {
    val scroll = rememberScrollState()
    val ctx = LocalContext.current
    val startPoint = remember(meetingPoints) { meetingPoints.firstOrNull() }

    var showAboutPopup by remember { mutableStateOf(false) }
    val canViewItinerary = !isPrivate || isHost || isParticipant

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = trip.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (isHost) {
                        IconButton(onClick = onEditAction) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Only show button if NOT already a participant (and not host)
            if (!isParticipant && !isHost) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(Modifier.padding(16.dp)) {
                        val isFull = trip.maxParticipants <= participants.size

                        // Button is enabled if NOT joining AND (if private, not yet requested)
                        val buttonEnabled = !joining && !(isPrivate && hasRequested)

                        val primaryColor = MaterialTheme.colorScheme.primary
                        val disabledColor = MaterialTheme.colorScheme.outline

                        val (label, containerColor) = when {
                            joining -> "Processing..." to primaryColor
                            isPrivate && hasRequested -> "Request Sent" to disabledColor
                            isPrivate -> "Request to Join" to primaryColor
                            isFull -> "Join Waitlist (${participants.size}/${trip.maxParticipants})" to Orange500
                            else -> "Join Trip" to primaryColor
                        }

                        Button(
                            onClick = onPrimaryAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = buttonEnabled,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = containerColor,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            if (joining) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(12.dp))
                            }
                            Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (isParticipant) {
                // Optional: Show "You are joined" indicator
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "You are a participant",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .verticalScroll(scroll)
        ) {
            // 2. HERO IMAGE with Gradient
            Box(Modifier.height(280.dp).fillMaxWidth()) {
                val coverModel = trip.coverImgUrl ?: "https://picsum.photos/seed/${trip.id}/900/500"
                AsyncImage(
                    model = coverModel,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                startY = 100f
                            )
                        )
                )

                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Text(
                        trip.category.name.replace('_', ' ').uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        trip.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    val organizerKey = trip.organizerId.ifBlank { trip.id.toString() }

                    // Organizer Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = organizerAvatarUrl ?: "https://i.pravatar.cc/50?u=$organizerKey",
                            contentDescription = "Organizer",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Hosted by ${organizerName ?: "Unknown"}",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Amber500,
                                    modifier = Modifier.size(14.dp)
                                )

                                Text(
                                    text = when {
                                        rating == null -> " Loading..."
                                        rating.reviewCount == 0 -> " No reviews yet"
                                        else -> " %.1f (%d)".format(
                                            rating.averageRating,
                                            rating.reviewCount
                                        )
                                    },
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {

                // 3. INFO GRID
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    InfoCard(
                        icon = Icons.Default.CalendarToday,
                        label = "Date",
                        value = formatDateRangeWithDays(trip.startDate, trip.endDate),
                        valueMaxLines = 3,
                        height = 110,
                        modifier = Modifier.weight(1f)
                    )


                    InfoCard(
                        icon = Icons.Default.AttachMoney,
                        label = "Budget",
                        value = trip.budgetDisplay ?: (trip.budget?.let { "RM ${it.toInt()}" } ?: "N/A"),
                        height = 110,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoCard(
                        icon = Icons.Default.Group,
                        label = "Spots",
                        value = "${trip.maxParticipants - participants.size} Left",
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        icon = if(isPrivate) Icons.Default.Lock else Icons.Default.Public,
                        label = "Access",
                        value = trip.visibility.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                // 4. DESCRIPTION & ITINERARY
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "About the Trip",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            trip.description.ifBlank { "No description provided." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )

                        HorizontalDivider(
                            Modifier.padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Itinerary",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (canViewItinerary) {
                                TextButton(onClick = { showAboutPopup = true }) {
                                    Text("View All", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        if (canViewItinerary) {
                            itinerary.take(2).forEach { item ->
                                Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Circle,
                                        null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(8.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        item.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Join to view itinerary",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 5. LOCATION CARD
                if (startPoint != null) {
                    ElevatedCard(
                        onClick = { openGoogleMaps(startPoint.label, startPoint.lat, startPoint.lng, ctx) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(48.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Place,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Meeting Point",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    startPoint.label ?: "Unknown",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Icon(
                                Icons.Default.ChevronRight,
                                null,
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                // 6. PARTICIPANTS
                Column {
                    Text(
                        "Participants",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        participants.forEach { p ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .clickable { onOpenProfile(p.userId) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        p.displayName.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    p.displayName.split(" ").first(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (participants.isEmpty()) {
                            Text(
                                "Be the first to join!",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(Modifier.height(80.dp)) // Extra space for bottom bar
            }
        }
    }

    if (showAboutPopup) {
        AboutItineraryDialog(
            trip = trip,
            itinerary = itinerary,
            canCopy = true,
            onCopy = { onCopyItinerary(itinerary) },
            onDismiss = { showAboutPopup = false }
        )
    }
}

// --- Modern Component: Info Card ---
@Composable
fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueMaxLines: Int = 1,
    height: Int = 90
) {
    Surface(
        modifier = modifier.height(height.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Icon(
                icon,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = valueMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// --- Helper Functions ---
fun formatBudgetSimple(budget: String?): String {
    return budget.takeIf { !it.isNullOrBlank() } ?: "N/A"
}

// --- Updated Dialog ---
@Composable
private fun AboutItineraryDialog(
    trip: TripEntity,
    itinerary: List<ItineraryItemEntity>,
    canCopy: Boolean,
    onCopy: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFmt = remember { DateTimeFormatter.ofPattern("EEE, MMM d", java.util.Locale.getDefault()) }

    fun parseItemDate(item: ItineraryItemEntity): LocalDate? {
        val raw = item.date.trim()
        return runCatching { LocalDate.parse(raw) }.getOrNull()
    }

    fun computedDay(item: ItineraryItemEntity): Int {
        val start = trip.startDate ?: return item.day
        val itemDate = parseItemDate(item) ?: return item.day
        return (ChronoUnit.DAYS.between(start, itemDate).toInt() + 1).coerceAtLeast(1)
    }

    val sorted = remember(itinerary, trip.startDate) {
        itinerary.sortedWith(compareBy({ parseItemDate(it) ?: LocalDate.MAX }, { computedDay(it) }))
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Itinerary",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                HorizontalDivider(
                    Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Column(
                    Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (sorted.isEmpty()) {
                        Text(
                            "No itinerary details yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        sorted.forEach { item ->
                            Row(Modifier.fillMaxWidth()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(50.dp)) {
                                    Text(
                                        "DAY",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "${computedDay(item)}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(
                                        item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    parseItemDate(item)?.let {
                                        Text(
                                            it.format(dateFmt),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (canCopy && sorted.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { onCopy(); onDismiss() },
                        modifier = Modifier.fillMaxWidth(),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Copy to New Trip")
                    }
                }
            }
        }
    }
}

private fun formatDateRangeWithDays(
    start: LocalDate?,
    end: LocalDate?
): String {
    if (start == null && end == null) return "TBD"

    val fmt = DateTimeFormatter.ofPattern("MMM d, yyyy", java.util.Locale.getDefault())

    return when {
        start != null && end != null -> {
            val days = ChronoUnit.DAYS.between(start, end) + 1 // inclusive
            "${start.format(fmt)} – ${end.format(fmt)} ($days days)"
        }
        start != null -> "From ${start.format(fmt)}"
        end != null -> "Until ${end.format(fmt)}"
        else -> "TBD"
    }
}