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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
fun AddCarRentalScreen(
    tripId: Long,
    vm: ItineraryPlannerViewModel,
    onClose: () -> Unit,
    editingPlanId: Long? = null
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // ── rental basic ──
    var agency by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val plans by vm.plans.collectAsState()
    val editingPlan = remember(plans, editingPlanId) {
        editingPlanId?.let { id -> plans.firstOrNull { it.id == id } }
    }
    // ── NEW: car details ──
    var carName by remember { mutableStateOf("") }
    var carType by remember { mutableStateOf("Economy") }
    var carTypeMenu by remember { mutableStateOf(false) }
    val carTypes = listOf(
        "Economy" to "small, cheapest, good for city",
        "Compact" to "a bit bigger, still easy to park",
        "Intermediate / Sedan" to "normal sedan, comfy",
        "SUV" to "higher seat, more luggage",
        "MPV / Van" to "group / family, 6–7 seats",
        "Luxury" to "premium, higher deposit"
    )

    // pickup
    var pickupName by remember { mutableStateOf("") }
    var pickupAddr by remember { mutableStateOf("") }
    var pickupDate by remember { mutableStateOf(LocalDate.now()) }
    var pickupTime by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }

    // dropoff
    var sameLocation by remember { mutableStateOf(true) }
    var dropName by remember { mutableStateOf("") }
    var dropAddr by remember { mutableStateOf("") }
    var dropDate by remember { mutableStateOf(pickupDate.plusDays(1)) }
    var dropTime by remember { mutableStateOf(pickupTime) }

    // map
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.1390, 101.6869), 11f)
    }

    val dateFmt = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }

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

    fun geocodeToMap(text: String) {
        if (text.isBlank()) return
        scope.launch {
            val r = withContext(Dispatchers.IO) {
                try {
                    Geocoder(ctx, Locale.getDefault()).getFromLocationName(text, 1)
                } catch (_: Exception) { null }
            }?.firstOrNull()
            if (r != null) {
                val ll = LatLng(r.latitude, r.longitude)
                selectedLatLng = ll
                camera.animate(CameraUpdateFactory.newLatLngZoom(ll, 14f))
            }
        }
    }
    fun extractLineValue(text: String?, prefix: String): String? {
        if (text.isNullOrBlank()) return null
        return text.lines()
            .firstOrNull { it.trim().startsWith(prefix) }
            ?.substringAfter(prefix)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }

    fun parseCommonHeader(title: String): Triple<String, String, String?> {
        // title example: "Car pick-up – Agency • SUV • Myvi"
        val header = title.substringAfter("–", title).trim()
        val parts = header.split("•").map { it.trim() }.filter { it.isNotBlank() }
        val agency = parts.getOrNull(0).orEmpty()
        val carType = parts.getOrNull(1) ?: "Economy"
        val carName = parts.getOrNull(2)
        return Triple(agency, carType, carName)
    }
    var prefilled by remember(editingPlanId) { mutableStateOf(false) }

    LaunchedEffect(editingPlan) {
        if (editingPlanId != null && editingPlan != null && !prefilled) {

            // 1) title -> agency/carType/carName
            val (ag, ct, cn) = parseCommonHeader(editingPlan.title)
            agency = ag
            carType = ct
            carName = cn.orEmpty()

            // 2) pickup values
            pickupName = editingPlan.subtitle.orEmpty()
            pickupDate = editingPlan.date
            pickupTime = (editingPlan.time ?: pickupTime)

            // 3) notes parsing (these were saved inside notes)
            val notes = editingPlan.description
            confirmation = extractLineValue(notes, "Conf#:").orEmpty()
            desc = extractLineValue(notes, "Notes:").orEmpty()

            // Pickup addr is "Pick-up address:"
            pickupAddr = extractLineValue(notes, "Pick-up address:").orEmpty()

            // Drop-off is "Drop-off: X (addr)"
            val dropLine = notes?.lines()?.firstOrNull { it.trim().startsWith("Drop-off:") }
            val dropText = dropLine?.substringAfter("Drop-off:")?.trim().orEmpty()
            val namePart = dropText.substringBefore("(").trim()
            val addrPart = dropText.substringAfter("(", "").substringBefore(")").trim()

            dropName = namePart
            dropAddr = addrPart

            // 4) find the drop-off row (heuristic)
            val commonHeader = editingPlan.title.substringAfter("–", "").trim()
            val dropPlan = plans.firstOrNull {
                it.type == PlanType.CarRental &&
                        it.title.trim() == "Car drop-off – $commonHeader"
            }

            if (dropPlan != null) {
                dropDate = dropPlan.date
                dropTime = dropPlan.time ?: dropTime
            }

            // 5) sameLocation toggle
            sameLocation = dropName.isBlank() || dropName == pickupName

            // 6) map pin
            if (editingPlan.lat != null && editingPlan.lng != null) {
                selectedLatLng = LatLng(editingPlan.lat, editingPlan.lng)
                camera.position = CameraPosition.fromLatLngZoom(selectedLatLng!!, 14f)
            }

            prefilled = true
        }
    }

    fun save() {
        scope.launch {
            val finalDropName = if (sameLocation) pickupName else dropName
            val finalDropAddr = if (sameLocation) pickupAddr else dropAddr

            // geocode if needed
            var lat: Double? = selectedLatLng?.latitude
            var lng: Double? = selectedLatLng?.longitude
            if (lat == null || lng == null) {
                val query = pickupAddr.ifBlank { pickupName }
                if (query.isNotBlank()) {
                    val r = withContext(Dispatchers.IO) {
                        try {
                            Geocoder(ctx).getFromLocationName(query, 1)?.firstOrNull()
                        } catch (_: Exception) {
                            null
                        }
                    }
                    lat = r?.latitude
                    lng = r?.longitude
                }
            }

            if (editingPlanId == null) {
                vm.addCarRental(
                    agency = agency,
                    confirmation = confirmation.ifBlank { null },
                    description = desc.ifBlank { null },
                    carName = carName.ifBlank { null },
                    carType = carType,
                    pickupName = pickupName.ifBlank { "Pick-up location" },
                    pickupAddr = pickupAddr.ifBlank { null },
                    pickupDate = pickupDate,
                    pickupTime = pickupTime,
                    dropName = finalDropName.ifBlank { "Drop-off location" },
                    dropAddr = finalDropAddr.ifBlank { null },
                    dropDate = dropDate,
                    dropTime = dropTime,
                    lat = lat,
                    lng = lng,
                    onDone = { onClose() }
                )
            } else {
                vm.updateCarRental(
                    pickupPlanId = editingPlanId,
                    agency = agency,
                    confirmation = confirmation.ifBlank { null },
                    description = desc.ifBlank { null },
                    carName = carName.ifBlank { null },
                    carType = carType,
                    pickupName = pickupName.ifBlank { "Pick-up location" },
                    pickupAddr = pickupAddr.ifBlank { null },
                    pickupDate = pickupDate,
                    pickupTime = pickupTime,
                    dropName = finalDropName.ifBlank { "Drop-off location" },
                    dropAddr = finalDropAddr.ifBlank { null },
                    dropDate = dropDate,
                    dropTime = dropTime,
                    lat = lat,
                    lng = lng,
                    onDone = { onClose() }
                )
            }
        }
    }

            Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Car Rental") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, null)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ===== CAR DETAILS =====
            Text("CAR DETAILS", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = carName,
                onValueChange = { carName = it },
                label = { Text("Car name / model (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = carTypeMenu,
                onExpandedChange = { carTypeMenu = !carTypeMenu }
            ) {
                OutlinedTextField(
                    value = carType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Car type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = carTypeMenu)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = carTypeMenu,
                    onDismissRequest = { carTypeMenu = false }
                ) {
                    carTypes.forEach { (label, explain) ->
                        DropdownMenuItem(
                            text = { Text("$label – $explain") },
                            onClick = {
                                carType = label
                                carTypeMenu = false
                            }
                        )
                    }
                }
            }

            Divider()

            // ===== RENTAL INFO =====
            OutlinedTextField(
                value = agency,
                onValueChange = { agency = it },
                label = { Text("Rental Agency") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = confirmation,
                onValueChange = { confirmation = it },
                label = { Text("Confirmation #") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            // ===== PICK UP =====
            Text("PICK UP", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = pickupName,
                onValueChange = {
                    pickupName = it
                    if (pickupAddr.isBlank()) geocodeToMap(it)
                },
                label = { Text("Location Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { pickDate(pickupDate) { pickupDate = it } }
                ) {
                    Text("Date", style = MaterialTheme.typography.labelMedium)
                    Text(pickupDate.format(dateFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { pickTime(pickupTime) { pickupTime = it } }
                ) {
                    Text("Time", style = MaterialTheme.typography.labelMedium)
                    Text(pickupTime.format(timeFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
            }

            // ===== MAP =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = camera,
                    onMapClick = { ll ->
                        selectedLatLng = ll
                        scope.launch {
                            camera.animate(CameraUpdateFactory.newLatLngZoom(ll, 15f))
                        }
                    }
                ) {
                    selectedLatLng?.let { ll ->
                        Marker(
                            state = rememberMarkerState(position = ll),
                            title = pickupName.ifBlank { "Car rental" }
                        )
                    }
                }
            }

            // ===== DROP OFF =====
            Text("DROP OFF", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    "Pick-up and drop-off locations are the same",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = sameLocation,
                    onCheckedChange = { sameLocation = it }
                )
            }

            if (!sameLocation) {
                OutlinedTextField(
                    value = dropName,
                    onValueChange = { dropName = it },
                    label = { Text("Drop-off Location Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { pickDate(dropDate) { dropDate = it } }
                ) {
                    Text("Date", style = MaterialTheme.typography.labelMedium)
                    Text(dropDate.format(dateFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { pickTime(dropTime) { dropTime = it } }
                ) {
                    Text("Time", style = MaterialTheme.typography.labelMedium)
                    Text(dropTime.format(timeFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
            }

            Button(
                onClick = { save() },
                enabled = agency.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Car Rental")
            }
        }
    }
}
