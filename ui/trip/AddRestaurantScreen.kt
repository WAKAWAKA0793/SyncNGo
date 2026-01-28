@file:OptIn(
    ExperimentalMaterial3Api::class,
    com.google.maps.android.compose.MapsComposeExperimentalApi::class
)

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

@Composable
fun AddRestaurantScreen(
    tripId: Long,
    plannerVm: ItineraryPlannerViewModel,
    onClose: () -> Unit,
    editingPlanId: Long? = null
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // form state
    var restaurantName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("2") }

    // date/time of dining
    var dineDate by remember { mutableStateOf(LocalDate.now()) }
    var dineTime by remember { mutableStateOf(LocalTime.of(19, 0)) } // 7pm

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

    var prefilled by remember(editingPlanId) { mutableStateOf(false) }

    fun extractLineValue(text: String?, prefix: String): String? {
        if (text.isNullOrBlank()) return null
        return text.lines()
            .firstOrNull { it.trim().startsWith(prefix) }
            ?.substringAfter(prefix)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }

    LaunchedEffect(editingPlan) {
        if (editingPlanId != null && editingPlan != null && !prefilled) {
            restaurantName = editingPlan.title.ifBlank { restaurantName }
            address = editingPlan.subtitle.orEmpty()

            dineDate = editingPlan.date
            dineTime = editingPlan.time ?: dineTime

            // Notes format created in repo addRestaurantPlan():
            // Address: ...
            // Guests: ...
            // <notes>
            val desc = editingPlan.description.orEmpty()
            people = extractLineValue(desc, "Guests:") ?: people

            // remove auto-generated header lines and keep only user notes
            val cleanedNotes = desc.lines()
                .filterNot { it.trim().startsWith("Address:") }
                .filterNot { it.trim().startsWith("Guests:") }
                .joinToString("\n")
                .trim()
            notes = cleanedNotes

            // restore pin
            if (editingPlan.lat != null && editingPlan.lng != null) {
                val ll = LatLng(editingPlan.lat, editingPlan.lng)
                selectedLatLng = ll
                cameraPositionState.position = CameraPosition.fromLatLngZoom(ll, 16f)
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
                    Geocoder(ctx).getFromLocationName(query, 1)
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

    fun saveRestaurant() {
        val pplInt = people.toIntOrNull()

        if (editingPlanId == null) {
            plannerVm.addRestaurant(
                name = if (restaurantName.isBlank()) "Restaurant" else restaurantName,
                dineDate = dineDate,
                dineTime = dineTime,
                address = address.ifBlank { null },
                people = pplInt,
                lat = selectedLatLng?.latitude,
                lng = selectedLatLng?.longitude,
                notes = notes,
                onDone = { _ -> onClose() }
            )
        } else {
            plannerVm.updateRestaurant(
                planId = editingPlanId,
                name = if (restaurantName.isBlank()) "Restaurant" else restaurantName,
                dineDate = dineDate,
                dineTime = dineTime,
                address = address.ifBlank { null },
                people = pplInt,
                lat = selectedLatLng?.latitude,
                lng = selectedLatLng?.longitude,
                notes = notes,
                onDone = { onClose() }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editingPlanId == null) "Add Restaurant" else "Edit Restaurant") },
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
                value = restaurantName,
                onValueChange = {
                    restaurantName = it
                    if (address.isBlank()) geocodeAndUpdateMap(it)
                },
                label = { Text("Restaurant name *") },
                modifier = Modifier.fillMaxWidth()
            )

            // inline map
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
                            title = restaurantName.ifBlank { "Restaurant" },
                            snippet = address
                        )
                    }
                }
            }

            // date & time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { openDatePicker(dineDate) { picked -> dineDate = picked } }
                ) {
                    Text("Date", style = MaterialTheme.typography.labelMedium)
                    Text(dineDate.format(dateFmt), style = MaterialTheme.typography.bodyLarge)
                    Divider(Modifier.padding(top = 4.dp))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { openTimePicker(dineTime) { picked -> dineTime = picked } }
                ) {
                    Text("Time", style = MaterialTheme.typography.labelMedium)
                    Text(dineTime.format(timeFmt), style = MaterialTheme.typography.bodyLarge)
                    Divider(Modifier.padding(top = 4.dp))
                }
            }

            // people
            OutlinedTextField(
                value = people,
                onValueChange = { people = it },
                label = { Text("People") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Website / Booking link") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / Special requests") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { saveRestaurant() },
                enabled = restaurantName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(if (editingPlanId == null) "Save Restaurant" else "Save Changes")

            }
        }
    }
}
