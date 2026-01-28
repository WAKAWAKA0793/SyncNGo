package com.example.tripshare.ui.trip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.location.Geocoder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class, com.google.maps.android.compose.MapsComposeExperimentalApi::class)
@Composable
fun AddLodgingScreen(
    tripId: Long,
    plannerVm: ItineraryPlannerViewModel,
    onClose: () -> Unit,
    editingPlanId: Long? = null
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // form state
    var lodgingName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }

    // dates / times
    var checkInDate by remember { mutableStateOf(LocalDate.now()) }
    var checkOutDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var checkInTime by remember { mutableStateOf(LocalTime.of(14, 0)) }   // 2pm
    var checkOutTime by remember { mutableStateOf(LocalTime.of(12, 0)) }  // noon

    // map state
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

    fun extractLineValue(text: String?, prefix: String): String? {
        if (text.isNullOrBlank()) return null
        return text.lines()
            .firstOrNull { it.trim().startsWith(prefix) }
            ?.substringAfter(prefix)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }

    var prefilled by remember(editingPlanId) { mutableStateOf(false) }

    LaunchedEffect(editingPlan) {
        if (editingPlanId != null && editingPlan != null && !prefilled) {

            // Your repo check-in title: "Check-in: <lodgingName>"
            lodgingName = editingPlan.title.substringAfter("Check-in:", editingPlan.title).trim()

            // location stored as address OR lodgingName in repo
            address = editingPlan.subtitle.orEmpty()

            // check-in time/date
            checkInDate = editingPlan.date
            checkInTime = editingPlan.time ?: checkInTime

            // lat/lng
            if (editingPlan.lat != null && editingPlan.lng != null) {
                val ll = LatLng(editingPlan.lat, editingPlan.lng)
                selectedLatLng = ll
                cameraPositionState.position = CameraPosition.fromLatLngZoom(ll, 16f)
            }

            // Parse notes/confirmation (repo builds notes with these lines)
            // "Phone:", "Website:", "Email:", "Conf#:"
            val notes = editingPlan.description
            phone = extractLineValue(notes, "Phone:").orEmpty()
            website = extractLineValue(notes, "Website:").orEmpty()
            email = extractLineValue(notes, "Email:").orEmpty()
            confirmation = extractLineValue(notes, "Conf#:").orEmpty()

            // find matching check-out row (heuristic using same lodgingName)
            val checkOutTitle = "Check-out: ${lodgingName.ifBlank { "Lodging" }}"
            val outPlan = plans.firstOrNull { it.title.trim() == checkOutTitle }
            if (outPlan != null) {
                checkOutDate = outPlan.date
                checkOutTime = outPlan.time ?: checkOutTime
            }

            prefilled = true
        }
    }

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
            { _, hour, minute -> onPicked(LocalTime.of(hour, minute)) },
            initial.hour,
            initial.minute,
            true
        ).show()
    }

    fun geocodeAndUpdateMap(query: String) {
        if (query.isBlank()) return
        scope.launch {
            val geo = Geocoder(ctx)
            val result = withContext(Dispatchers.IO) {
                try {
                    geo.getFromLocationName(query, 1)
                } catch (_: Exception) {
                    null
                }
            }
            val first = result?.firstOrNull()
            if (first != null) {
                val ll = LatLng(first.latitude, first.longitude)
                selectedLatLng = ll
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(ll, 16f)
                )
            }
        }
    }

    fun saveLodging() {
        if (lodgingName.isBlank()) return

        if (editingPlanId == null) {
            plannerVm.addLodging(
                lodgingName = lodgingName,
                checkInDate = checkInDate,
                checkInTime = checkInTime,
                checkOutDate = checkOutDate,
                checkOutTime = checkOutTime,
                address = address.ifBlank { null },
                phone = phone.ifBlank { null },
                website = website.ifBlank { null },
                email = email.ifBlank { null },
                confirmation = confirmation.ifBlank { null },
                lat = selectedLatLng?.latitude,
                lng = selectedLatLng?.longitude,
                onDone = { onClose() }
            )
        } else {
            plannerVm.updateLodging(
                checkInPlanId = editingPlanId,
                lodgingName = lodgingName,
                checkInDate = checkInDate,
                checkInTime = checkInTime,
                checkOutDate = checkOutDate,
                checkOutTime = checkOutTime,
                address = address.ifBlank { null },
                phone = phone.ifBlank { null },
                website = website.ifBlank { null },
                email = email.ifBlank { null },
                confirmation = confirmation.ifBlank { null },
                lat = selectedLatLng?.latitude,
                lng = selectedLatLng?.longitude,
                onDone = { onClose() }
            )
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editingPlanId == null) "Add Lodging" else "Edit Lodging") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },

            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            OutlinedTextField(
                value = lodgingName,
                onValueChange = {
                    lodgingName = it
                    // you can also geocode on name if address is empty
                    if (address.isBlank()) geocodeAndUpdateMap(it)
                },
                label = { Text("Lodging Name *") },
                modifier = Modifier.fillMaxWidth()
            )

            // INLINE MAP (like your activity screen)
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
                    selectedLatLng?.let { ll ->
                        Marker(
                            state = rememberMarkerState(position = ll),
                            title = lodgingName.ifBlank { "Lodging" },
                            snippet = address
                        )
                    }
                }
            }

            // check-in row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { openDatePicker(checkInDate) { picked -> checkInDate = picked } }
                ) {
                    Text("Check-in date", style = MaterialTheme.typography.labelMedium)
                    Text(checkInDate.format(dateFmt), style = MaterialTheme.typography.bodyLarge)
                    Divider(Modifier.padding(top = 4.dp))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { openTimePicker(checkInTime) { picked -> checkInTime = picked } }
                ) {
                    Text("Check-in time", style = MaterialTheme.typography.labelMedium)
                    Text(checkInTime.format(timeFmt), style = MaterialTheme.typography.bodyLarge)
                    Divider(Modifier.padding(top = 4.dp))
                }
            }

            // check-out row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            openDatePicker(checkOutDate) { picked ->
                                checkOutDate = picked
                                if (picked.isBefore(checkInDate)) checkInDate = picked
                            }
                        }
                ) {
                    Text("Check-out date", style = MaterialTheme.typography.labelMedium)
                    Text(checkOutDate.format(dateFmt), style = MaterialTheme.typography.bodyLarge)
                    Divider(Modifier.padding(top = 4.dp))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { openTimePicker(checkOutTime) { picked -> checkOutTime = picked } }
                ) {
                    Text("Check-out time", style = MaterialTheme.typography.labelMedium)
                    Text(checkOutTime.format(timeFmt), style = MaterialTheme.typography.bodyLarge)
                    Divider(Modifier.padding(top = 4.dp))
                }
            }

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone #") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Website") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = confirmation,
                onValueChange = { confirmation = it },
                label = { Text("Notes / Confirmation #") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { saveLodging() },
                enabled = lodgingName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(if (editingPlanId == null) "Save Lodging" else "Save Changes")
            }
        }
    }
}
