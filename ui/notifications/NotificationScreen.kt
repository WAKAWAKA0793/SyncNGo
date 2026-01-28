package com.example.tripshare.ui.notifications

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.db.LocalNotificationDao
import com.example.tripshare.data.model.LocalNotificationEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class NotificationCenterViewModel(
    private val dao: LocalNotificationDao,
    private val currentUserId: Long
) : ViewModel() {

    val notifications: StateFlow<List<LocalNotificationEntity>> =
        dao.observeForUser(currentUserId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            dao.markRead(id)
        }
    }

    fun deleteNotification(item: LocalNotificationEntity) {
        viewModelScope.launch {
            dao.delete(item)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }
}


// ───────────────────────────── Screen ─────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    vm: NotificationCenterViewModel,
    onBack: () -> Unit,
    onOpenTrip: (Long) -> Unit = {},         // called when user taps a trip-related notification
    onOpenJoinRequests: (Long) -> Unit = {},
    onOpenTripDashboard: (Long) -> Unit = {},
    onOpenPayment: (Long) -> Unit = {},
    onOpenItinerary: (Long) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val notifications by vm.notifications.collectAsState()

    // Ask for POST_NOTIFICATIONS permission where needed
    RequestNotificationPermissionIfNeeded(
        onGranted = {
            scope.launch {
                snackbarHostState.showSnackbar("Notification permission granted")
            }
        },
        onDenied = {
            scope.launch {
                snackbarHostState.showSnackbar("Notification permission denied")
            }
        }
    )

    var showClearAllConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications",fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = { showClearAllConfirm = true }) {
                            Text("Clear all")
                        }
                    }
                }

            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            EmptyNotificationState(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "${notifications.size} notification${if (notifications.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(
                    items = notifications,
                    key = { it.id }
                ) { item ->
                    NotificationCard(
                        item = item,
                        onClick = {
                            vm.markAsRead(item.id)
                            when (item.type) {
                                "JOIN_REQUEST" -> {

                                    item.relatedId?.let { id -> onOpenJoinRequests(id) }
                                }
                                "TRIP_REMINDER" -> {
                                    item.relatedId?.let { id -> onOpenItinerary(id) }
                                }
                                "TRIP_START" -> {

                                    item.relatedId?.let { id -> onOpenTripDashboard(id) }
                                }
                                "WAITLIST_AVAILABLE" -> {
                                    item.relatedId?.let { onOpenTrip(it) }
                                }
                                "PAYMENT" -> {
                                    item.relatedId?.let { id -> onOpenPayment(id) }
                                }
                                "BILL_SPLIT" -> item.relatedId?.let { id -> onOpenPayment(id) }
                            }
                        },
                        onDelete = {
                            vm.deleteNotification(item)
                            scope.launch {
                                snackbarHostState.showSnackbar("Notification deleted")
                            }
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }

        if (showClearAllConfirm) {
            AlertDialog(
                onDismissRequest = { showClearAllConfirm = false },
                title = { Text("Clear all notifications?") },
                text = { Text("This will remove all notifications from this screen.") },
                confirmButton = {
                    Button(onClick = {
                        showClearAllConfirm = false
                        vm.clearAll()
                        scope.launch {
                            snackbarHostState.showSnackbar("All notifications cleared")
                        }
                    }) {
                        Text("Clear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearAllConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


// ───────────────────────────── UI Pieces ─────────────────────────────

@Composable
private fun EmptyNotificationState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier
                    .height(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "No notifications yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

        }
    }
}

@Composable
private fun NotificationCard(
    item: LocalNotificationEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val surfaceColor = if (item.isRead) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isRead) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (item.type) {
                        "JOIN_REQUEST" -> Icons.Default.PersonAdd
                        "TRIP_REMINDER" -> Icons.Default.Event
                        "PAYMENT" -> Icons.Default.Payment
                        "WAITLIST_AVAILABLE" -> Icons.Default.Notifications
                        "BILL_SPLIT" -> Icons.Default.AttachMoney
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (item.isRead) FontWeight.Normal else FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!item.isRead) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .height(8.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .alpha(0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.body,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Divider()
        }
    }
}
