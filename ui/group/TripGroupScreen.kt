package com.example.tripshare.ui.group

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.db.ParticipantDao
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.UserEntity
import com.example.tripshare.data.repo.ChatRepository
import com.example.tripshare.data.repo.TripRepository
import com.example.tripshare.ui.expense.ExpenseViewModel
import com.example.tripshare.ui.notifications.NotificationViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripGroupScreen(
    trip: TripEntity,
    repo: TripRepository,
    chatRepo: ChatRepository,
    participantDao: ParticipantDao,
    voteVm: VoteViewModel,
    expenseVm: ExpenseViewModel,
    onEditTrip: (Long) -> Unit,
    notifVm: NotificationViewModel,
    reviewVm: ReviewViewModel,
    navController: NavController,
    onBack: () -> Unit,
    onOpenExpense: (Long) -> Unit
) {
    val ctx = LocalContext.current
    val currentUserId by AuthPrefs.getUserId(ctx).collectAsState(initial = null)

    val isOwner = remember(trip.organizerId, currentUserId) {
        val me = currentUserId?.toString()
        trip.organizerId == me && me != null
    }

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteTripDialog by remember { mutableStateOf(false) }
    val participants by expenseVm.participants.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Call ViewModel to save it
            // Assuming you added updateTripImage to your TripGroupViewModel as shown in Step 2
            // If strictly using TripRepository passed in params, call a repo function directly or via a VM.
            // Here is how to do it if you add the logic to TripGroupViewModel:

            // NOTE: You need to pass the logic to save.
            // Since TripGroupViewModel in your provided file handles Notifs mostly,
            // you might want to perform the update via the repo directly in a coroutine here
            // OR ideally add the function to the VM.

            coroutineScope.launch {
                val flag = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                ctx.contentResolver.takePersistableUriPermission(uri, flag)
                repo.updateTripImage(trip.id, uri.toString()) // Ensure repo has this method
            }
        }
    }

    // Local, optimistic participants list
    val localParticipants = remember { mutableStateListOf<UserEntity>() }
    LaunchedEffect(participants) {
        localParticipants.clear()
        localParticipants.addAll(participants)
    }

    // review / end-trip state
    var showEndTripDialog by remember { mutableStateOf(false) }
    var showMemberPicker by remember { mutableStateOf(false) }
    var userToReview by remember { mutableStateOf<UserEntity?>(null) }

    // local "is ended" UI state so we can grey out the button immediately after success
    var isTripEnded by remember(trip.id, trip.isArchived) {
        mutableStateOf(trip.isArchived)
    }
    val reviewedUserIds = remember { mutableStateListOf<Long>() }

    // For non-owners: show a one-time popup when opening an already-ended trip
    var showEndedReviewPrompt by remember(isOwner, isTripEnded) {
        mutableStateOf(!isOwner && isTripEnded)
    }
    var showLeaveDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }


    // centralised "end trip" action used by skip + after review
    fun endTrip() {
        coroutineScope.launch {
            try {
                repo.archiveTrip(trip.id)
                isTripEnded = true
                snackbarHostState.showSnackbar("Trip ended. It’s now in Past Trips.")
            } catch (e: Exception) {
                snackbarHostState.showSnackbar(
                    "Failed to end trip: ${e.message ?: "unknown error"}"
                )
            }
        }
    }

    fun leaveTrip() {
        coroutineScope.launch {
            val meId = currentUserId ?: return@launch
            val meUser = localParticipants.find { it.id == meId }

            try {
                var deletedCount = participantDao.deleteParticipant(trip.id, meId)
                if (deletedCount == 0 && meUser?.email != null) {
                    deletedCount = participantDao.deleteParticipantByEmail(trip.id, meUser.email!!)
                }

                if (deletedCount > 0) {
                    localParticipants.removeAll { it.id == meId }
                    notifVm.notifyWaitlistIfSlotAvailable(
                        tripId = trip.id,
                        tripName = trip.name,
                        forceOneSlotOpen = true
                    )
                    snackbarHostState.showSnackbar("You have left the trip.")
                    onBack()
                } else {
                    snackbarHostState.showSnackbar("Error: Could not find your participant record to delete.")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Failed to leave trip: ${e.message}")
            }
        }
    }

    // centralised "open direct chat" for a participant
    fun openDirectChatWith(user: UserEntity) {
        coroutineScope.launch {
            val me = currentUserId
            if (me == null) {
                snackbarHostState.showSnackbar("Please sign in to message participants")
                return@launch
            }
            try {
                val chatId = chatRepo.openDirectChat(me, user.id)
                if (!chatId.isNullOrBlank()) {
                    val encodedChatId = Uri.encode(chatId) // dm:1-2 -> dm%3A1-2
                    navController.navigate("directChat/$encodedChatId")
                } else {
                    snackbarHostState.showSnackbar("Unable to open chat")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Chat error: ${e.message ?: "unknown"}")
            }
        }
    }

    // centralised "remove participant" with optimistic UI + DB rollback on error
    fun removeParticipant(user: UserEntity) {
        if (!isOwner) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Only the trip owner can remove participants")
            }
            return
        }

        val index = localParticipants.indexOfFirst { it.id == user.id }
        if (index == -1) return

        // optimistic remove
        localParticipants.removeAt(index)

        coroutineScope.launch {
            try {
                val deleted = participantDao.deleteParticipant(trip.id, user.id)
                val ok = if (deleted > 0) {
                    true
                } else {
                    val email = user.email
                    if (!email.isNullOrBlank()) {
                        participantDao.deleteParticipantByEmail(trip.id, email) > 0
                    } else false
                }

                if (ok) {
                    snackbarHostState.showSnackbar("${user.name} removed")
                    notifVm.notifyWaitlistIfSlotAvailable(
                        tripId = trip.id,
                        tripName = trip.name,
                        forceOneSlotOpen = true
                    )
                } else {
                    val insertIndex = index.coerceIn(0, localParticipants.size)
                    localParticipants.add(insertIndex, user)
                    snackbarHostState.showSnackbar("Failed to remove ${user.name}")
                }

            } catch (t: Throwable) {
                val insertIndex = index.coerceIn(0, localParticipants.size)
                localParticipants.add(insertIndex, user)
                snackbarHostState.showSnackbar("Failed to remove ${user.name}: ${t.message ?: "error"}")
            }
        }
    }

    fun deleteTripAction() {
        coroutineScope.launch {
            try {
                repo.deleteTrip(trip.id)
                snackbarHostState.showSnackbar("Trip deleted.")
                onBack() // Navigate back to home
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Failed to delete trip: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip.name, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // 3-Dot Menu (Edit/Delete) - Only for Host
                    if (isOwner) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit Trip") },
                                    onClick = {
                                        showMenu = false
                                        onEditTrip(trip.id) // Call the navigation callback
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Edit, contentDescription = null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Trip", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showMenu = false
                                        showDeleteTripDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            val ended = trip.isArchived || isTripEnded
            Surface(tonalElevation = 3.dp) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    if (isOwner) {
                        Button(
                            onClick = { if (!ended) showEndTripDialog = true },
                            enabled = !ended,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (ended) "Trip ended" else "End Trip")
                        }
                    } else {
                        // NON-OWNERS see "Leave Trip"
                        Button(
                            onClick = { showLeaveDialog = true },
                            enabled = !ended,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Leave Trip")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->

        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            /* ---------------------------------------------------------
                1) TRIP HEADER (Image + Dates + Participants)
            --------------------------------------------------------- */
            item {
                Column {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        // Logic: Use custom image if available, else random seed
                        val model = trip.coverImgUrl ?: "https://picsum.photos/seed/${trip.id}/900/300"

                        AsyncImage(
                            model = model,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp), // Slightly taller for better look
                            contentScale = ContentScale.Crop
                        )

                        // ✅ EDIT BUTTON (Only for Owner)
                        if (isOwner) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(40.dp)
                                    .clickable {
                                        // Launch Picker
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Change Cover",
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    Column(Modifier.padding(16.dp)) {
                        TripDateRangeChip(
                            start = trip.startDate?.toString(),
                            end = trip.endDate?.toString()
                        )


                        Spacer(Modifier.height(12.dp))


                        if (isOwner && isTripEnded) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "This trip has ended and is now in Past Trips.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                DashboardGrid(
                    onItineraryClick = { navController.navigate("itineraryPlanner/${trip.id}") },
                    onExpensesClick = { onOpenExpense(trip.id) },
                    onChatClick = {
                        val encodedName = java.net.URLEncoder.encode(trip.name, "utf-8")
                        navController.navigate("groupChat/${trip.id}/$encodedName")
                    },
                    onVoteClick = { navController.navigate("tripVotes/${trip.id}") }
                )
            }

            /* ---------------------------------------------------------
                5) PARTICIPANTS
            --------------------------------------------------------- */
            item {
                ParticipantsHeader(
                    count = localParticipants.size,
                    showInvite = isOwner,
                    onInviteClick = {
                        navController.navigate("joinRequests/${trip.id}")
                    }
                )
            }

            items(localParticipants, key = { it.id }) { user ->
                ImprovedParticipantRow(
                    user = user,
                    onProfile = { navController.navigate("userProfile/${user.id}") },
                    canRemove = isOwner && (user.id != currentUserId),
                    onMessage = { openDirectChatWith(user) },
                    onRemove = { removeParticipant(user) }
                )
            }

        }
    }

    // 1) End Trip confirmation dialog
    if (showEndTripDialog) {
        AlertDialog(
            onDismissRequest = { showEndTripDialog = false },
            title = { Text("End this trip?") },
            text = {
                Text(
                    "Ending this trip will move it to Past Trips for all members. " +
                            "They will still be able to view the trip, but it will no longer appear as an active trip."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEndTripDialog = false
                        showMemberPicker = true        // 👉 go to member picker next
                    }
                ) {
                    Text("Next: Review members")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndTripDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2) Member picker dialog (list of participants)
    if (showMemberPicker) {
        // filter: no self, no already-reviewed
        val meId = currentUserId
        val selectable = localParticipants.filter { u ->
            (meId == null || u.id != meId) && !reviewedUserIds.contains(u.id)
        }

        if (selectable.isEmpty()) {
            // nothing left to review
            showMemberPicker = false
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar("No more members to review.")
            }
        } else {
            MemberPickerDialog(
                participants = selectable,
                onUserSelected = { user ->
                    showMemberPicker = false
                    userToReview = user
                },
                onSkip = {
                    showMemberPicker = false
                    // host can still finish the trip from here
                    endTrip()
                }
            )
        }
    }

    // 3) Review dialog for selected user
    if (userToReview != null) {
        val isHost = isOwner
        val meId = currentUserId

        ReviewUserDialog(
            user = userToReview!!,
            onSubmit = { rating, comment ->
                // ✅ FIXED: Check if meId is not null, then pass it as reviewerId
                if (meId != null) {
                    val targetId = userToReview!!.id
                    reviewVm.submitReview(targetId, meId, rating, comment)

                    if (!reviewedUserIds.contains(targetId)) {
                        reviewedUserIds.add(targetId)
                    }
                }

                userToReview = null

                // Check if there are more members to review
                val remaining = localParticipants.filter { u ->
                    (meId == null || u.id != meId) && !reviewedUserIds.contains(u.id)
                }

                if (remaining.isNotEmpty()) {
                    showMemberPicker = true
                } else {
                    if (isHost && !isTripEnded) {
                        endTrip()
                    }
                }
            },
            onDismiss = {
                userToReview = null
            }
        )
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Leave Trip?") },
            text = { Text("Are you sure you want to leave '${trip.name}'? A spot will open up for someone on the waitlist.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLeaveDialog = false
                        leaveTrip()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Leave")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteTripDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteTripDialog = false },
            title = { Text("Delete Trip") },
            text = { Text("Are you sure you want to permanently delete '${trip.name}'? This action cannot be undone and all data (expenses, chat, itinerary) will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteTripDialog = false
                        deleteTripAction()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteTripDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEndedReviewPrompt && !isOwner) {
        AlertDialog(
            onDismissRequest = { showEndedReviewPrompt = false },
            title = { Text("Trip has ended") },
            text = {
                Text(
                    "This trip has already ended. " +
                            "Would you like to review your fellow travelers?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showEndedReviewPrompt = false
                    showMemberPicker = true   // member picker will filter & may show nothing
                }) {
                    Text("Review members")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndedReviewPrompt = false }) {
                    Text("Maybe later")
                }
            }
        )
    }
}

/* ------------ UI bits ------------- */

@Composable
private fun OverlappingAvatars(
    users: List<UserEntity>,
    avatarSize: Dp,
    overlap: Dp,
    maxVisible: Int,
    onUserClick: (UserEntity) -> Unit
) {
    val visible = users.take(maxVisible)
    val step = avatarSize - overlap
    val width = if (visible.isEmpty()) 0.dp else avatarSize + step * (visible.size - 1)
    Box(modifier = Modifier.width(width)) {
        visible.forEachIndexed { index, u ->
            val url = u.profilePhoto ?: "https://i.pravatar.cc/64?u=${u.id}"
            AsyncImage(
                model = url,
                contentDescription = u.name,
                modifier = Modifier
                    .size(avatarSize)
                    .offset(x = step * index)
                    .clip(CircleShape)
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.surface), CircleShape)
                    .clickable { onUserClick(u) }
            )
        }
        if (users.size > maxVisible) {
            val more = users.size - maxVisible
            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .size(avatarSize)
                    .offset(x = step * (visible.size - 1))
                    .clip(CircleShape),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("+$more", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun TripDateRangeChip(start: String?, end: String?) {
    val startDate = start ?: "?"
    val endDate = end ?: "?"

    Surface(
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                "Trip:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.width(6.dp))

            Text(
                startDate,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text("  →  ")

            Text(
                endDate,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

/** Card showing participants with avatars + invite icon */
@Composable
fun ParticipantsHeader(
    count: Int,
    showInvite: Boolean,
    onInviteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Participants ($count)",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.weight(1f))
        if (showInvite) {
            IconButton(onClick = onInviteClick) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Invite"
                )
            }
        }
    }
}

@Composable
fun DashboardGrid(
    onItineraryClick: () -> Unit,
    onExpensesClick: () -> Unit,
    onChatClick: () -> Unit,
    onVoteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ColorTile(
                title = "Itinerary",
                subtitle = "Plan your days",
                icon = Icons.Default.Notifications,
                modifier = Modifier.weight(1f),
                startColor = Color(0xFFEEF2FF),   // soft indigo
                endColor = Color(0xFFD1FAFF),
                accentColor = Color(0xFF4F46E5),
                onClick = onItineraryClick
            )
            ColorTile(
                title = "Expenses",
                subtitle = "Split & track",
                icon = Icons.Default.AttachMoney,
                modifier = Modifier.weight(1f),
                startColor = Color(0xFFFFF7ED),   // warm orange
                endColor = Color(0xFFFFE4D5),
                accentColor = Color(0xFFEA580C),
                onClick = onExpensesClick
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ColorTile(
                title = "Chat",
                subtitle = "Group messages",
                icon = Icons.Default.Message,
                modifier = Modifier.weight(1f),
                startColor = Color(0xFFE0F7FA),   // teal
                endColor = Color(0xFFCCFBF1),
                accentColor = Color(0xFF0D9488),
                onClick = onChatClick
            )
            ColorTile(
                title = "Vote",
                subtitle = "Trip decisions",
                icon = Icons.Default.Favorite,
                modifier = Modifier.weight(1f),
                startColor = Color(0xFFFDF2FF),
                endColor = Color(0xFFE9D5FF),
                accentColor = Color(0xFFDB2777),
                onClick = {
                    onVoteClick()
                }
            )
        }
    }
}

@Composable
fun ColorTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    startColor: Color,
    endColor: Color,
    accentColor: Color,
    titleColor: Color = Color.Black,
    subtitleColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(startColor, endColor)
                )
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor
            )
        }
    }
}



/** Modern participant row with inline actions + delete confirmation */
// In ui/group/TripGroupScreen.kt

@Composable
fun ImprovedParticipantRow(
    user: UserEntity,
    canRemove: Boolean, // 👈 1. Add this parameter
    onProfile: () -> Unit,
    onMessage: () -> Unit,
    onRemove: () -> Unit
) {
    var confirmVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = user.profilePhoto ?: "https://i.pravatar.cc/96?u=${user.id}",
                contentDescription = null,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(user.name, style = MaterialTheme.typography.bodyLarge)
                if (!user.email.isNullOrBlank()) {
                    Text(user.email!!, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onProfile) {
                Icon(Icons.Default.Person, contentDescription = "Profile")
            }
            IconButton(onClick = onMessage) {
                Icon(Icons.Default.Message, contentDescription = "Message")
            }

            // 👇 2. Only show the Delete button if 'canRemove' is true
            if (canRemove) {
                IconButton(onClick = { confirmVisible = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
            }
        }
    }

    if (confirmVisible) {
        AlertDialog(
            onDismissRequest = { confirmVisible = false },
            title = { Text("Remove participant") },
            text = {
                Text("Are you sure you want to remove \"${user.name}\" from this trip? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmVisible = false
                    onRemove()
                }) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmVisible = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/* ==== Review helper dialogs ==== */

@Composable
private fun MemberPickerDialog(
    participants: List<UserEntity>,
    onUserSelected: (UserEntity) -> Unit,
    onSkip: () -> Unit
) {
   AlertDialog(
        onDismissRequest = onSkip,
        title = { Text("Review trip members") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Choose a traveler to review. You can skip if you don't want to leave a review."
                )
                Spacer(Modifier.height(8.dp))
                participants.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUserSelected(user) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatarPainter =
                            rememberAsyncImagePainter(user.profilePhoto ?: "")
                        if (!user.profilePhoto.isNullOrBlank()) {
                            Image(
                                painter = avatarPainter,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(user.name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSkip) {
                Text("Skip & finish trip")
            }
        }
    )
}

@Composable
private fun ReviewUserDialog(
    user: UserEntity,
    onSubmit: (rating: Int, comment: String) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Review ${user.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("How was your experience traveling with ${user.name}?")
                StarRatingRow(
                    rating = rating,
                    onRatingChange = { rating = it }
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Write a comment") },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (rating > 0) {
                        onSubmit(rating, comment.trim())
                    }
                },
                enabled = rating > 0
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun StarRatingRow(
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Row {
        (1..5).forEach { star ->
            Icon(
                imageVector = if (star <= rating)
                    Icons.Filled.Favorite
                else
                    Icons.Outlined.FavoriteBorder,
                contentDescription = "Star $star",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onRatingChange(star) }
                    .padding(2.dp),
                tint = if (star <= rating)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
fun formatDatePretty(dateString: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, formatter)
        date.format(DateTimeFormatter.ofPattern("d MMM yyyy"))  // 10 Mar 2025
    } catch (e: Exception) {
        dateString   // fallback if formatting fails
    }
}