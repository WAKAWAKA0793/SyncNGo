@file:OptIn(ExperimentalMaterial3Api::class)

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
import java.util.Locale

@OptIn(com.google.maps.android.compose.MapsComposeExperimentalApi::class)
@Composable
fun AddFlightScreen(
    tripId: Long,
    plannerVm: ItineraryPlannerViewModel,
    onClose: () -> Unit,
    editingPlanId: Long? = null
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // ===== Field State =====
    var airline by remember { mutableStateOf("") }
    var flightNumber by remember { mutableStateOf("") }
    var bookingNumber by remember { mutableStateOf("") }
    var seatNumber by remember { mutableStateOf("") }

    // Locations
    var depAirport by remember { mutableStateOf("") }
    var arrAirport by remember { mutableStateOf("") }

    // Date/Time
    var depDate by remember { mutableStateOf(LocalDate.now()) }
    var depTime by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }

    // Map Pins
    var depLatLng by remember { mutableStateOf<LatLng?>(null) }
    var arrLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Cameras
    val depCamera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.1390, 101.6869), 11f)
    }
    val arrCamera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.1390, 101.6869), 11f)
    }

    val dateFmt = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }

    // --- Helpers ---

    fun pickDate(current: LocalDate, onPicked: (LocalDate) -> Unit) {
        val c = Calendar.getInstance().apply {
            set(Calendar.YEAR, current.year)
            set(Calendar.MONTH, current.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, current.dayOfMonth)
        }
        DatePickerDialog(
            ctx,
            { _, y, m, d -> onPicked(LocalDate.of(y, m + 1, d)) },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun pickTime(current: LocalTime, onPicked: (LocalTime) -> Unit) {
        TimePickerDialog(
            ctx,
            { _, h, m -> onPicked(LocalTime.of(h, m)) },
            current.hour,
            current.minute,
            true
        ).show()
    }

    fun geocodeToLatLng(text: String, onResult: (LatLng?) -> Unit) {
        if (text.isBlank()) return
        scope.launch {
            val r = withContext(Dispatchers.IO) {
                try {
                    Geocoder(ctx, Locale.getDefault()).getFromLocationName(text, 1)
                } catch (_: Exception) { null }
            }?.firstOrNull()
            onResult(if (r != null) LatLng(r.latitude, r.longitude) else null)
        }
    }

    suspend fun geocodeIfNeeded(name: String, current: LatLng?): LatLng? {
        if (current != null || name.isBlank()) return current
        return withContext(Dispatchers.IO) {
            try {
                val res = Geocoder(ctx, Locale.getDefault()).getFromLocationName(name, 1)?.firstOrNull()
                if (res != null) LatLng(res.latitude, res.longitude) else null
            } catch (_: Exception) { null }
        }
    }

    fun extractLineValue(text: String?, prefix: String): String? {
        if (text.isNullOrBlank()) return null
        return text.lines()
            .firstOrNull { it.trim().startsWith(prefix, ignoreCase = true) }
            ?.substringAfter(prefix)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }

    fun buildConfirmation(): String? {
        val lines = buildList {
            if (depAirport.isNotBlank()) add("From: ${depAirport.trim()}")
            if (arrAirport.isNotBlank()) add("To: ${arrAirport.trim()}")
            if (bookingNumber.isNotBlank()) add("Booking: ${bookingNumber.trim()}")
            if (seatNumber.isNotBlank()) add("Seat: ${seatNumber.trim()}")
        }
        return lines.joinToString("\n").ifBlank { null }
    }

    // --- Edit Mode Prefill ---
    val plans by plannerVm.plans.collectAsState()
    val editingPlan = remember(plans, editingPlanId) {
        editingPlanId?.let { id -> plans.firstOrNull { it.id == id } }
    }
    var prefilled by remember(editingPlanId) { mutableStateOf(false) }

    LaunchedEffect(editingPlan) {
        if (editingPlanId != null && editingPlan != null && !prefilled) {
            flightNumber = editingPlan.title.removePrefix("Flight ").trim().ifBlank { editingPlan.title }
            airline = editingPlan.subtitle.orEmpty()
            depDate = editingPlan.date
            depTime = editingPlan.time ?: depTime

            val notes = editingPlan.description
            arrAirport = extractLineValue(notes, "To:") ?: ""
            bookingNumber = extractLineValue(notes, "Booking:") ?: ""
            seatNumber = extractLineValue(notes, "Seat:") ?: ""

            // Restore pins
            if (editingPlan.lat != null && editingPlan.lng != null) {
                depLatLng = LatLng(editingPlan.lat, editingPlan.lng)
                depCamera.position = CameraPosition.fromLatLngZoom(depLatLng!!, 14f)
            }
            // Arrival lat/lng isn't stored in main entity column usually,
            // but we can try geocoding arrAirport again if needed or leave empty.
            if (arrAirport.isNotBlank()) {
                geocodeToLatLng(arrAirport) { ll ->
                    arrLatLng = ll
                    if(ll!=null) arrCamera.position = CameraPosition.fromLatLngZoom(ll, 14f)
                }
            }

            prefilled = true
        }
    }

    fun save() {
        if (airline.isBlank() || flightNumber.isBlank()) return
        val confirmation = buildConfirmation()

        scope.launch {
            val finalDepLatLng = geocodeIfNeeded(depAirport, depLatLng)

            if (editingPlanId == null) {
                plannerVm.addFlight(
                    depDate = depDate,
                    depTime = depTime,
                    airline = airline.trim(),
                    flightNumber = flightNumber.trim(),
                    origin = depAirport.ifBlank { "Airport" }, // 👈 PASS depAirport HERE
                    confirmation = confirmation,
                    lat = finalDepLatLng?.latitude,
                    lng = finalDepLatLng?.longitude,
                    onDone = { onClose() }
                )
            } else {
                plannerVm.updateFlight(
                    planId = editingPlanId,
                    depDate = depDate,
                    depTime = depTime,
                    airline = airline.trim(),
                    flightNumber = flightNumber.trim(),
                    origin = depAirport.ifBlank { "Airport" }, // 👈 PASS depAirport HERE
                    confirmation = confirmation,
                    lat = finalDepLatLng?.latitude,
                    lng = finalDepLatLng?.longitude,
                    onDone = { onClose() }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editingPlanId == null) "Add Flight" else "Edit Flight") },
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ===== 1. Flight Info =====
            Text("FLIGHT DETAILS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = airline,
                onValueChange = { airline = it },
                label = { Text("Airline (e.g. Malaysia Airlines)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = flightNumber,
                onValueChange = { flightNumber = it },
                label = { Text("Flight Number (e.g. MH2252)") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = bookingNumber,
                    onValueChange = { bookingNumber = it },
                    label = { Text("Booking Ref") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = seatNumber,
                    onValueChange = { seatNumber = it },
                    label = { Text("Seat") },
                    modifier = Modifier.weight(1f)
                )
            }

            Divider()

            // ===== 2. Departure =====
            Text("DEPARTURE", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = depAirport,
                onValueChange = {
                    depAirport = it
                    // auto-geocode on typing
                    geocodeToLatLng(it) { ll ->
                        if (ll != null) {
                            depLatLng = ll
                            scope.launch { depCamera.animate(CameraUpdateFactory.newLatLngZoom(ll, 14f)) }
                        }
                    }
                },
                label = { Text("From (Airport/City)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("KLIA, London Heathrow...") }
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    Modifier.weight(1f).clickable { pickDate(depDate) { depDate = it } }
                ) {
                    Text("Date", style = MaterialTheme.typography.labelMedium)
                    Text(depDate.format(dateFmt), style = MaterialTheme.typography.bodyLarge)
                    Divider(Modifier.padding(top = 4.dp))
                }

                Column(
                    Modifier.weight(1f).clickable { pickTime(depTime) { depTime = it } }
                ) {
                    Text("Time", style = MaterialTheme.typography.labelMedium)
                    Text(depTime.format(timeFmt), style = MaterialTheme.typography.bodyLarge)
                    Divider(Modifier.padding(top = 4.dp))
                }
            }

            // Map Preview
            Box(Modifier.fillMaxWidth().height(160.dp)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = depCamera,
                    onMapClick = { ll ->
                        depLatLng = ll
                        scope.launch { depCamera.animate(CameraUpdateFactory.newLatLngZoom(ll, 15f)) }
                    }
                ) {
                    depLatLng?.let { Marker(state = rememberMarkerState(position = it), title = "Departure") }
                }
            }

            Divider()

            // ===== 3. Arrival =====
            Text("ARRIVAL", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = arrAirport,
                onValueChange = {
                    arrAirport = it
                    geocodeToLatLng(it) { ll ->
                        if (ll != null) {
                            arrLatLng = ll
                            scope.launch { arrCamera.animate(CameraUpdateFactory.newLatLngZoom(ll, 14f)) }
                        }
                    }
                },
                label = { Text("To (Airport/City)") },
                modifier = Modifier.fillMaxWidth()
            )

            Box(Modifier.fillMaxWidth().height(160.dp)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = arrCamera,
                    onMapClick = { ll ->
                        arrLatLng = ll
                        scope.launch { arrCamera.animate(CameraUpdateFactory.newLatLngZoom(ll, 15f)) }
                    }
                ) {
                    arrLatLng?.let { Marker(state = rememberMarkerState(position = it), title = "Arrival") }
                }
            }

            Button(
                onClick = { save() },
                enabled = airline.isNotBlank() && flightNumber.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(if (editingPlanId == null) "Save Flight" else "Save Changes")
            }

            // Padding for scrolling
            androidx.compose.foundation.layout.Spacer(Modifier.height(24.dp))
        }
    }
}