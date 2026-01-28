@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    com.google.maps.android.compose.MapsComposeExperimentalApi::class
)

package com.example.tripshare.ui.trip

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tripshare.ui.notifications.NotificationHelper
import com.example.tripshare.ui.notifications.NotificationScheduler
import com.example.tripshare.ui.notifications.RequestNotificationPermissionIfNeeded
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun AddActivityScreen(
    tripId: Long,
    plannerVm: ItineraryPlannerViewModel,
    onClose: () -> Unit,
    editingPlanId: Long? = null
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var eventName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }
    var endDate by remember { mutableStateOf(startDate) }
    var endTime by remember { mutableStateOf(startTime) }

    // keep notificationsAllowed in this composable's scope so callbacks can update it
    var notificationsAllowed by remember { mutableStateOf(false) }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.1390, 101.6869), 12f)
    }

    val dateFmt = remember { DateTimeFormatter.ofPattern("EEE, MMM d, yyyy") }
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val plans by plannerVm.plans.collectAsState()
    val editingPlan = remember(plans, editingPlanId) {
        editingPlanId?.let { id -> plans.firstOrNull { it.id == id } }
    }
    var prefilled by remember(editingPlanId) { mutableStateOf(false) }

    LaunchedEffect(editingPlan) {
        if (editingPlanId != null && editingPlan != null && !prefilled) {
            eventName = editingPlan.title
            address = editingPlan.subtitle.orEmpty()
            confirmation = editingPlan.description.orEmpty()

            startDate = editingPlan.date
            startTime = editingPlan.time ?: LocalTime.now().withSecond(0).withNano(0)
            endDate = editingPlan.endDate ?: startDate
            endTime = editingPlan.endTime ?: startTime

            selectedLatLng = if (editingPlan.lat != null && editingPlan.lng != null) {
                LatLng(editingPlan.lat, editingPlan.lng)
            } else null

            // optionally move camera if coords exist
            selectedLatLng?.let { ll ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(ll, 16f)
            }

            prefilled = true
        }
    }

    // create channels on composition
    LaunchedEffect(Unit) {
        NotificationHelper.createChannels(ctx)
    }

    // Request notifications permission (Android 13+). This is inside the composable and updates local state.
    RequestNotificationPermissionIfNeeded(
        onGranted = { notificationsAllowed = true },
        onDenied = { notificationsAllowed = false }
    )

    fun openDatePicker(initial: LocalDate, onPicked: (LocalDate) -> Unit) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, initial.year)
            set(Calendar.MONTH, initial.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, initial.dayOfMonth)
        }
        DatePickerDialog(
            ctx,
            { _, y, m, d -> onPicked(LocalDate.of(y, m + 1, d)) },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun openTimePicker(initial: LocalTime, onPicked: (LocalTime) -> Unit) {
        TimePickerDialog(
            ctx,
            { _, h, m -> onPicked(LocalTime.of(h, m)) },
            initial.hour,
            initial.minute,
            true
        ).show()
    }

    fun geocodeAndUpdateMap(query: String) {
        if (query.isBlank()) return
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    Geocoder(ctx, Locale.getDefault()).getFromLocationName(query, 1)
                } catch (_: Exception) {
                    null
                }
            }
            val first = result?.firstOrNull()
            if (first != null) {
                val ll = LatLng(first.latitude, first.longitude)
                selectedLatLng = ll
                // animate must be called from a coroutine; cameraPositionState.animate is suspend
                scope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(ll, 16f))
                }
            }
        }
    }

    fun saveActivityAndClose() {
        scope.launch {
            var lat: Double? = selectedLatLng?.latitude
            var lng: Double? = selectedLatLng?.longitude

            if ((lat == null || lng == null) && address.isNotBlank()) {
                val first = withContext(Dispatchers.IO) {
                    try {
                        Geocoder(ctx).getFromLocationName(address, 1)?.firstOrNull()
                    } catch (_: Exception) {
                        null
                    }
                }
                lat = first?.latitude
                lng = first?.longitude
            }

            if (editingPlanId == null) {
                plannerVm.addActivityPlan(
                    title = if (eventName.isBlank()) "Activity" else eventName,
                    note = confirmation,
                    locationName = address.ifBlank { eventName.ifBlank { "Location" } },
                    startDate = startDate,
                    startTime = startTime,
                    endDate = endDate,
                    endTime = endTime,
                    lat = lat,
                    lng = lng
                )
            } else {
                plannerVm.updateActivityPlan(
                    planId = editingPlanId,
                    title = if (eventName.isBlank()) "Activity" else eventName,
                    note = confirmation,
                    locationName = address.ifBlank { eventName.ifBlank { "Location" } },
                    startDate = startDate,
                    startTime = startTime,
                    endDate = endDate,
                    endTime = endTime,
                    lat = lat,
                    lng = lng
                )
            }

            // compute epoch millis for start time (uses system default timezone)
            val startEpochMillis = startDate
                .atTime(startTime)
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()q
                .toEpochMilli()

            // schedule reminders only if notifications allowed
            if (notificationsAllowed) {
                val remind24hBefore = startEpochMillis - TimeUnit.HOURS.toMillis(24)
                val remind1hBefore = startEpochMillis - TimeUnit.HOURS.toMillis(1)

                val titleText = if (eventName.isBlank()) "Activity reminder" else eventName
                val bodyText = "Starts ${startDate.format(dateFmt)} at ${startTime.format(timeFmt)} — ${address.ifBlank { "Tap to open" }}"

                val baseKey = "activity_${tripId}_$startEpochMillis"
                val notif24hId = ("${baseKey}_24h").hashCode()
                val notif1hId = ("${baseKey}_1h").hashCode()
                val notifStartId = ("${baseKey}_start").hashCode()

                if (remind24hBefore > System.currentTimeMillis()) {
                    NotificationScheduler.scheduleTripReminder(
                        context = ctx,
                        notifId = notif24hId,
                        title = "Upcoming: $titleText",
                        body = "Starts in 24 hours — ${address.ifBlank { "Open itinerary" }}",
                        epochMillis = remind24hBefore,
                        tripId = tripId
                    )
                }

                if (remind1hBefore > System.currentTimeMillis()) {
                    NotificationScheduler.scheduleTripReminder(
                        context = ctx,
                        notifId = notif1hId,
                        title = "Upcoming in 1 hour: $titleText",
                        body = bodyText,
                        epochMillis = remind1hBefore,
                        tripId = tripId
                    )
                }

                // optional start-time notification
                if (startEpochMillis > System.currentTimeMillis()) {
                    NotificationScheduler.scheduleTripReminder(
                        context = ctx,
                        notifId = notifStartId,
                        title = "Starting now: $titleText",
                        body = bodyText,
                        epochMillis = startEpochMillis,
                        tripId = tripId
                    )
                }
            } else {
                // UX: let user know reminders were not scheduled because notifications are disabled/denied
                Toast.makeText(ctx, "Saved activity. Enable notifications to receive reminders.", Toast.LENGTH_LONG).show()
            }

            onClose()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editingPlanId == null) "Add Activity" else "Edit Activity") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text("Activity Type", style = MaterialTheme.typography.labelMedium)
                Text("Activity", style = MaterialTheme.typography.bodyLarge)
                Divider(Modifier.padding(top = 4.dp))
            }

            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                    geocodeAndUpdateMap(it)
                },
                label = { Text("Address / Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        selectedLatLng = latLng
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(latLng, 16f)
                            )
                        }
                    }
                ) {
                    selectedLatLng?.let { current ->
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(position = current),
                            title = eventName.ifBlank { "Activity location" },
                            snippet = address
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            openDatePicker(startDate) { picked ->
                                startDate = picked
                                if (endDate.isBefore(picked)) endDate = picked
                            }
                        }
                ) {
                    Text("Start Date", style = MaterialTheme.typography.labelMedium)
                    Text(startDate.format(dateFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            openTimePicker(startTime) { picked ->
                                startTime = picked
                                if (endDate == startDate && endTime.isBefore(picked)) {
                                    endTime = picked
                                }
                            }
                        }
                ) {
                    Text("Start Time", style = MaterialTheme.typography.labelMedium)
                    Text(startTime.format(timeFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            openDatePicker(endDate) { picked ->
                                endDate = picked
                                if (picked.isBefore(startDate)) startDate = picked
                            }
                        }
                ) {
                    Text("End Date", style = MaterialTheme.typography.labelMedium)
                    Text(endDate.format(dateFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            openTimePicker(endTime) { picked ->
                                endTime = picked
                                if (endDate == startDate && picked.isBefore(startTime)) {
                                    endTime = startTime
                                }
                            }
                        }
                ) {
                    Text("End Time", style = MaterialTheme.typography.labelMedium)
                    Text(endTime.format(timeFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
            }

            OutlinedTextField(
                value = confirmation,
                onValueChange = { confirmation = it },
                label = { Text("Notes / Confirmation #") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { saveActivityAndClose() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(if (editingPlanId == null) "Save Activity" else "Save Changes")

            }
        }
    }
}

/**
 * Compose-friendly permission requester for POST_NOTIFICATIONS.
 * Calls onGranted when permission is granted; onDenied when not granted.
 * (You can also keep this in its own file; kept here for convenience.)
 */
@Composable
private fun RequestNotificationPermissionIfNeeded(
    onGranted: () -> Unit = {},
    onDenied: () -> Unit = {}
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        LaunchedEffect(Unit) { onGranted() } // pre-T has permission by default
        return
    }

    val ctx = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) onGranted() else onDenied()
    }

    LaunchedEffect(Unit) {
        val alreadyGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) {
            onGranted()
        } else {
            // Launch the permission request once when screen starts
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

