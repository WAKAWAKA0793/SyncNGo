@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
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

@Composable
fun AddCruiseScreen(
    tripId: Long,
    vm: ItineraryPlannerViewModel,
    onClose: () -> Unit,
    editingPlanId: Long? = null
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var cruiseLine by remember { mutableStateOf("") }
    var shipName by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }

    // start port
    var startPortName by remember { mutableStateOf("") }
    var startPortAddr by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }

    // end port
    var samePort by remember { mutableStateOf(true) }
    var endPortName by remember { mutableStateOf("") }
    var endPortAddr by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf(startDate.plusDays(3)) }
    var endTime by remember { mutableStateOf(startTime) }

    // map
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.1390, 101.6869), 10f)
    }

    val dateFmt = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val plans by vm.plans.collectAsState()
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

            // title is usually cruiseLine (based on your repo)
            cruiseLine = editingPlan.title

            // subtitle is startPortName in repo
            startPortName = editingPlan.subtitle.orEmpty()

            // description holds ship/confirmation/start/end addresses (we’ll parse)
            val notes = editingPlan.description.orEmpty()

            shipName = extractLineValue(notes, "Ship:").orEmpty()
            confirmation = extractLineValue(notes, "Conf#:").orEmpty()

            // start port addr
            startPortAddr = extractLineValue(notes, "Start port:")?.let { "" } ?: startPortAddr
            // better: parse line AFTER "Start port:" if you stored addr on next line (repo does)
            // In repo addCruisePlan(), you append startPortName line then addr line.
            // So we parse by scanning lines:
            val lines = notes.lines().map { it.trim() }.filter { it.isNotBlank() }
            val startIdx = lines.indexOfFirst { it.startsWith("Start port:") }
            if (startIdx >= 0) {
                startPortName = lines[startIdx].substringAfter("Start port:").trim()
                startPortAddr = lines.getOrNull(startIdx + 1)
                    ?.takeIf { !it.startsWith("Departs:") && !it.startsWith("Arrives:") && !it.startsWith("End port:") }
                    .orEmpty()
            }

            val depLine = lines.firstOrNull { it.startsWith("Departs:") }.orEmpty()
            // "Departs: yyyy-MM-dd HH:mm"
            val depText = depLine.substringAfter("Departs:", "").trim()
            val depParts = depText.split(" ").filter { it.isNotBlank() }
            depParts.getOrNull(0)?.let { startDate = runCatching { LocalDate.parse(it) }.getOrElse { startDate } }
            depParts.getOrNull(1)?.let { startTime = runCatching { LocalTime.parse(it) }.getOrElse { startTime } }

            // end port optional
            val endIdx = lines.indexOfFirst { it.startsWith("End port:") }
            if (endIdx >= 0) {
                samePort = false
                endPortName = lines[endIdx].substringAfter("End port:").trim()
                endPortAddr = lines.getOrNull(endIdx + 1)
                    ?.takeIf { !it.startsWith("Arrives:") }
                    .orEmpty()

                val arrLine = lines.firstOrNull { it.startsWith("Arrives:") }.orEmpty()
                val arrText = arrLine.substringAfter("Arrives:", "").trim()
                val arrParts = arrText.split(" ").filter { it.isNotBlank() }
                arrParts.getOrNull(0)?.let { endDate = runCatching { LocalDate.parse(it) }.getOrElse { endDate } }
                arrParts.getOrNull(1)?.let { endTime = runCatching { LocalTime.parse(it) }.getOrElse { endTime } }
            } else {
                samePort = true
                endPortName = ""
                endPortAddr = ""
                // you can keep existing default endDate/endTime
            }

            // map pin
            if (editingPlan.lat != null && editingPlan.lng != null) {
                val ll = LatLng(editingPlan.lat, editingPlan.lng)
                selectedLatLng = ll
                camera.position = CameraPosition.fromLatLngZoom(ll, 13f)
            }

            prefilled = true
        }
    }

    fun pickDate(init: LocalDate, onPicked: (LocalDate) -> Unit) {
        val c = Calendar.getInstance().apply {
            set(Calendar.YEAR, init.year)
            set(Calendar.MONTH, init.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, init.dayOfMonth)
        }
        DatePickerDialog(
            ctx,
            { _, y, m, d -> onPicked(LocalDate.of(y, m + 1, d)) },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun pickTime(init: LocalTime, onPicked: (LocalTime) -> Unit) {
        TimePickerDialog(
            ctx,
            { _, h, m -> onPicked(LocalTime.of(h, m)) },
            init.hour,
            init.minute,
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
                camera.animate(CameraUpdateFactory.newLatLngZoom(ll, 13f))
            }
        }
    }

    fun saveCruise() {
        scope.launch {
            val endNameFinal = if (samePort) startPortName else endPortName
            val endAddrFinal = if (samePort) startPortAddr else endPortAddr

            var lat: Double? = selectedLatLng?.latitude
            var lng: Double? = selectedLatLng?.longitude
            if (lat == null || lng == null) {
                val q = startPortAddr.ifBlank { startPortName }
                if (q.isNotBlank()) {
                    val r = withContext(Dispatchers.IO) {
                        try {
                            Geocoder(ctx).getFromLocationName(q, 1)?.firstOrNull()
                        } catch (_: Exception) { null }
                    }
                    lat = r?.latitude
                    lng = r?.longitude
                }
            }

            if (editingPlanId == null) {
                vm.addCruise(
                    cruiseLine = cruiseLine,
                    shipName = shipName,
                    confirmation = confirmation.ifBlank { null },
                    startPortName = startPortName.ifBlank { "Starting port" },
                    startPortAddr = startPortAddr.ifBlank { null },
                    startDate = startDate,
                    startTime = startTime,
                    endPortName = endNameFinal.ifBlank { "Ending port" },
                    endPortAddr = endAddrFinal.ifBlank { null },
                    endDate = endDate,
                    endTime = endTime,
                    lat = lat,
                    lng = lng,
                    onDone = { onClose() }
                )
            } else {
                vm.updateCruise(
                    planId = editingPlanId,
                    cruiseLine = cruiseLine,
                    shipName = shipName,
                    confirmation = confirmation.ifBlank { null },
                    startPortName = startPortName.ifBlank { "Starting port" },
                    startPortAddr = startPortAddr.ifBlank { null },
                    startDate = startDate,
                    startTime = startTime,
                    endPortName = endNameFinal.ifBlank { "Ending port" },
                    endPortAddr = endAddrFinal.ifBlank { null },
                    endDate = endDate,
                    endTime = endTime,
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
                title = { Text(if (editingPlanId == null) "Add Cruise" else "Edit Cruise") },
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
            // top row names
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = cruiseLine,
                    onValueChange = { cruiseLine = it },
                    label = { Text("Cruise Line Name") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = shipName,
                    onValueChange = { shipName = it },
                    label = { Text("Ship Name") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = confirmation,
                onValueChange = { confirmation = it },
                label = { Text("Confirmation #") },
                modifier = Modifier.fillMaxWidth()
            )

            // STARTING PORT
            Text("STARTING PORT", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = startPortName,
                onValueChange = {
                    startPortName = it
                    if (startPortAddr.isBlank()) geocodeToMap(it)
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
                        .clickable { pickDate(startDate) { startDate = it } }
                ) {
                    Text("Date", style = MaterialTheme.typography.labelMedium)
                    Text(startDate.format(dateFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { pickTime(startTime) { startTime = it } }
                ) {
                    Text("Time", style = MaterialTheme.typography.labelMedium)
                    Text(startTime.format(timeFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
            }

            // MAP
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
                            camera.animate(CameraUpdateFactory.newLatLngZoom(ll, 14f))
                        }
                    }
                ) {
                    selectedLatLng?.let { ll ->
                        Marker(
                            state = rememberMarkerState(position = ll),
                            title = startPortName.ifBlank { "Cruise port" }
                        )
                    }
                }
            }

            // ENDING PORT
            Text("ENDING PORT", style = MaterialTheme.typography.labelLarge)
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    "Starting and ending ports are the same",
                    modifier = Modifier.weight(1f)
                )
                Switch(checked = samePort, onCheckedChange = { samePort = it })
            }

            if (!samePort) {
                OutlinedTextField(
                    value = endPortName,
                    onValueChange = { endPortName = it },
                    label = { Text("Location Name") },
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
                        .clickable { pickDate(endDate) { endDate = it } }
                ) {
                    Text("Date", style = MaterialTheme.typography.labelMedium)
                    Text(endDate.format(dateFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { pickTime(endTime) { endTime = it } }
                ) {
                    Text("Time", style = MaterialTheme.typography.labelMedium)
                    Text(endTime.format(timeFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
            }

            Button(
                onClick = { saveCruise() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (editingPlanId == null) "Save Cruise" else "Save Changes")
            }
        }
    }
}
