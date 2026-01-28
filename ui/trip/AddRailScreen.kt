@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tripshare.ui.trip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun AddRailScreen(
    tripId: Long,
    vm: ItineraryPlannerViewModel,
    onClose: () -> Unit,
    editingPlanId: Long? = null
) {
    val ctx = LocalContext.current

    var carrier by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }

    // departure
    var depStation by remember { mutableStateOf("") }
    var depAddress by remember { mutableStateOf("") }
    var depDate by remember { mutableStateOf(LocalDate.now()) }
    var depTime by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }

    // arrival
    var arrStation by remember { mutableStateOf("") }
    var arrAddress by remember { mutableStateOf("") }
    var arrDate by remember { mutableStateOf(depDate) }
    var arrTime by remember { mutableStateOf(depTime.plusHours(1)) }

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

            // Repo title is carrier, so use title as carrier
            carrier = editingPlan.title
            confirmation = extractLineValue(editingPlan.description, "Conf#:").orEmpty()

            // Departure station stored in "location" (subtitle in UI)
            depStation = editingPlan.subtitle.orEmpty()
            depDate = editingPlan.date
            depTime = editingPlan.time ?: depTime

            // Notes built in repo include dep/arr addresses and stations
            val notes = editingPlan.description.orEmpty()
            val lines = notes.lines().map { it.trim() }.filter { it.isNotBlank() }

            // Format in repo:
            // Conf#: ...
            // Departure: depStation
            // depAddress
            // Departing: yyyy-MM-dd HH:mm
            //
            // Arrival: arrStation
            // arrAddress
            // Arriving: yyyy-MM-dd HH:mm
            val depIdx = lines.indexOfFirst { it.startsWith("Departure:") }
            if (depIdx >= 0) {
                depStation = lines[depIdx].substringAfter("Departure:").trim()
                depAddress = lines.getOrNull(depIdx + 1)
                    ?.takeIf { !it.startsWith("Departing:") }
                    .orEmpty()
            }

            val arrIdx = lines.indexOfFirst { it.startsWith("Arrival:") }
            if (arrIdx >= 0) {
                arrStation = lines[arrIdx].substringAfter("Arrival:").trim()
                arrAddress = lines.getOrNull(arrIdx + 1)
                    ?.takeIf { !it.startsWith("Arriving:") }
                    .orEmpty()
            }

            // Arrival date/time are already stored in endDate/endTime for Rail
            arrDate = editingPlan.endDate ?: depDate
            arrTime = editingPlan.endTime ?: depTime.plusHours(1)

            prefilled = true
        }
    }

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

    fun save() {
        if (carrier.isBlank() || depStation.isBlank() || arrStation.isBlank()) return

        if (editingPlanId == null) {
            vm.addRail(
                carrier = carrier,
                confirmation = confirmation.ifBlank { null },
                depStation = depStation,
                depAddress = depAddress.ifBlank { null },
                depDate = depDate,
                depTime = depTime,
                arrStation = arrStation,
                arrAddress = arrAddress.ifBlank { null },
                arrDate = arrDate,
                arrTime = arrTime,
                onDone = { onClose() }
            )
        } else {
            vm.updateRail(
                planId = editingPlanId,
                carrier = carrier,
                confirmation = confirmation.ifBlank { null },
                depStation = depStation,
                depAddress = depAddress.ifBlank { null },
                depDate = depDate,
                depTime = depTime,
                arrStation = arrStation,
                arrAddress = arrAddress.ifBlank { null },
                arrDate = arrDate,
                arrTime = arrTime,
                onDone = { onClose() }
            )
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editingPlanId == null) "Add Rail" else "Edit Rail") },
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
            OutlinedTextField(
                value = carrier,
                onValueChange = { carrier = it },
                label = { Text("Carrier") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = confirmation,
                onValueChange = { confirmation = it },
                label = { Text("Confirmation #") },
                modifier = Modifier.fillMaxWidth()
            )

            // DEPARTURE
            Text("DEPARTURE", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = depStation,
                onValueChange = { depStation = it },
                label = { Text("Departure Station") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { pickDate(depDate) { depDate = it } }
                ) {
                    Text("Date", style = MaterialTheme.typography.labelMedium)
                    Text(depDate.format(dateFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { pickTime(depTime) { depTime = it } }
                ) {
                    Text("Time", style = MaterialTheme.typography.labelMedium)
                    Text(depTime.format(timeFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
            }


            // ARRIVAL
            Text("ARRIVAL", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = arrStation,
                onValueChange = { arrStation = it },
                label = { Text("Arrival Station") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { pickDate(arrDate) { arrDate = it } }
                ) {
                    Text("Date", style = MaterialTheme.typography.labelMedium)
                    Text(arrDate.format(dateFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { pickTime(arrTime) { arrTime = it } }
                ) {
                    Text("Time", style = MaterialTheme.typography.labelMedium)
                    Text(arrTime.format(timeFmt))
                    Divider(Modifier.padding(top = 4.dp))
                }
            }


            Button(
                onClick = { save() },
                enabled = carrier.isNotBlank() && depStation.isNotBlank() && arrStation.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (editingPlanId == null) "Save Rail" else "Save Changes")
            }
        }
    }
}
