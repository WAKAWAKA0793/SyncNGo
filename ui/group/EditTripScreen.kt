package com.example.tripshare.ui.group

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FlightTakeoff
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.model.TripCategory
import com.example.tripshare.ui.trip.BasicTextField
import com.example.tripshare.ui.trip.CreateTripViewModel
import com.example.tripshare.ui.trip.LabelSection
import com.example.tripshare.ui.trip.LatLngD
import com.example.tripshare.ui.trip.LocationPickerSheet
import com.example.tripshare.ui.trip.ModernLocationCard
import com.example.tripshare.ui.trip.ModernSettingRow
import com.example.tripshare.ui.trip.ModernTextField
import com.example.tripshare.ui.trip.Privacy
import com.example.tripshare.ui.trip.PrivacyPickerSheet
import com.example.tripshare.ui.trip.fetchReadableLocation
import com.example.tripshare.ui.trip.hasFineLocationPermission
import com.example.tripshare.ui.trip.showDatePicker
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripScreen(
    tripId: Long,
    vm: CreateTripViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Load trip when entering screen
    LaunchedEffect(tripId) {
        vm.loadTrip(tripId)
    }

    // Navigate away when save success
    LaunchedEffect(ui.successTripId) {
        if (ui.successTripId != null) {
            onSuccess()
            vm.clearSuccess()
        }
    }

    // Local UI state
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }

    // Categories
    val categories = TripCategory.values().toList()
    var selectedCategory by remember { mutableStateOf<TripCategory?>(null) }

    LaunchedEffect(ui.category) {
        selectedCategory = ui.category.takeIf { it.isNotBlank() }?.let {
            kotlin.runCatching { TripCategory.valueOf(it) }.getOrNull()
        }
    }

    // Map Initial
    val startInitial = if (ui.startLat != null && ui.startLng != null) LatLngD(ui.startLat!!, ui.startLng!!) else null
    val endInitial = if (ui.endLat != null && ui.endLng != null) LatLngD(ui.endLat!!, ui.endLng!!) else null

    // Permission launcher (current location)
    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fetchReadableLocation(ctx, onSuccess = { lat, lon, label ->
                vm.update { it.copy(startLocation = label, startLat = lat, startLng = lon) }
            })
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Edit trip",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                val userId = AuthPrefs.getUserId(ctx).firstOrNull()
                                val displayName = AuthPrefs.getDisplayName(ctx).firstOrNull() ?: "User"
                                val email = AuthPrefs.getEmail(ctx).firstOrNull()
                                if (userId != null) {
                                    vm.submit(userId = userId, displayName = displayName, email = email)
                                }
                            }
                        },
                        enabled = !ui.saving && ui.name.isNotBlank()
                    ) {
                        if (ui.saving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        bottomBar = {
            // Optional bottom bar (nice on edit): Cancel + Save
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }

                androidx.compose.material3.Button(
                    onClick = {
                        scope.launch {
                            val userId = AuthPrefs.getUserId(ctx).firstOrNull()
                            val displayName = AuthPrefs.getDisplayName(ctx).firstOrNull() ?: "User"
                            val email = AuthPrefs.getEmail(ctx).firstOrNull()
                            if (userId != null) {
                                vm.submit(userId = userId, displayName = displayName, email = email)
                            }
                        }
                    },
                    enabled = !ui.saving && ui.name.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (ui.saving) "Saving..." else "Save changes")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1) Trip Name & Desc
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Update your plan",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )

                ModernTextField(
                    value = ui.name,
                    onValueChange = { vm.update { s -> s.copy(name = it) } },
                    label = "Trip Name",
                    placeholder = "e.g., Summer in Bali",
                    icon = Icons.Outlined.FlightTakeoff
                )

                ModernTextField(
                    value = ui.description,
                    onValueChange = { vm.update { s -> s.copy(description = it) } },
                    label = "Description",
                    placeholder = "What are we doing?",
                    icon = Icons.Outlined.Description,
                    singleLine = false,
                    minLines = 3
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // 2) Category chips
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabelSection("Trip Vibes")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = {
                                selectedCategory = cat
                                vm.update { it.copy(category = cat.name) }
                            },
                            label = { Text(cat.label) },
                            leadingIcon = if (selectedCategory == cat) {
                                { Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            // 3) Dates (same combined row)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabelSection("When")
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Start
                    Box(
                        Modifier
                            .weight(1f)
                            .clickable { showDatePicker(ctx) { vm.update { s -> s.copy(startDate = it) } } }
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                "Starts",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = ui.startDate.ifBlank { "Select Date" },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (ui.startDate.isNotBlank()) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }

                    // Divider
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )

                    // End
                    Box(
                        Modifier
                            .weight(1f)
                            .clickable { showDatePicker(ctx) { vm.update { s -> s.copy(endDate = it) } } }
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                "Ends",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = ui.endDate.ifBlank { "Select Date" },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (ui.endDate.isNotBlank()) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // 4) Locations (start + end)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabelSection("Destinations")

                ModernLocationCard(
                    title = "Start Point",
                    value = ui.startLocation,
                    lat = ui.startLat,
                    lng = ui.startLng,
                    onPick = { showStartPicker = true },
                    onCurrentLocation = {
                        if (hasFineLocationPermission(ctx)) {
                            fetchReadableLocation(ctx, onSuccess = { lat, lon, label ->
                                vm.update { it.copy(startLocation = label, startLat = lat, startLng = lon) }
                            })
                        } else {
                            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                )

            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // 5) Details
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LabelSection("Details")

                // Participants inline field (same as Create)
                ModernSettingRow(
                    icon = Icons.Outlined.Group,
                    title = "Max Participants",
                    value = "",
                    onClick = { /* focus handled by text field */ }
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier.width(80.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            if (ui.maxParticipants.isBlank()) {
                                Text("0", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            }
                            BasicTextField(
                                value = ui.maxParticipants,
                                onValueChange = { vm.update { s -> s.copy(maxParticipants = it) } },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Split costs switch
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AttachMoney, null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(16.dp))
                        Text("Split Costs", style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = ui.splitCost,
                        onCheckedChange = { vm.update { s -> s.copy(splitCost = it) } }
                    )
                }

                // Budget dropdown (only when splitCost = true)
                AnimatedVisibility(
                    visible = ui.splitCost,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    var budgetExpanded by remember { mutableStateOf(false) }
                    val budgetOptions = listOf(
                        "RM0 - RM150",
                        "RM150 - RM500",
                        "RM500 - RM1000",
                        "RM1000 - RM1500",
                        "RM1500 - RM2000",
                        "RM2000+"
                    )

                    Box {
                        ModernSettingRow(
                            icon = Icons.Outlined.AccountBalanceWallet,
                            title = "Estimated Budget",
                            value = ui.budget.ifBlank { "Select" },
                            onClick = { budgetExpanded = true }
                        )
                        DropdownMenu(
                            expanded = budgetExpanded,
                            onDismissRequest = { budgetExpanded = false },
                            offset = DpOffset(x = 200.dp, y = 0.dp)
                        ) {
                            budgetOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        vm.update { s -> s.copy(budget = opt) }
                                        budgetExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Privacy
                ModernSettingRow(
                    icon = if (ui.privacy == Privacy.Private) Icons.Outlined.Lock else Icons.Outlined.Public,
                    title = "Privacy",
                    value = if (ui.privacy == Privacy.Private) "Private" else "Everyone",
                    onClick = { showPrivacy = true }
                )
            }

            Spacer(Modifier.height(60.dp))
        }
    }

    // Sheets (same behavior as Create)
    if (showStartPicker) {
        LocationPickerSheet(
            title = "Where do you start?",
            initial = startInitial,
            onUseCurrent = {
                if (hasFineLocationPermission(ctx)) {
                    fetchReadableLocation(ctx, onSuccess = { lat, lon, label ->
                        vm.update { it.copy(startLocation = label, startLat = lat, startLng = lon) }
                    })
                } else {
                    requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            onDismiss = { showStartPicker = false },
            onPicked = { label, lat, lng ->
                vm.update { it.copy(startLocation = label, startLat = lat, startLng = lng) }
                showStartPicker = false
            }
        )
    }

    if (showEndPicker) {
        LocationPickerSheet(
            title = "Where are you going?",
            initial = endInitial,
            onUseCurrent = { /* usually not current location */ },
            onDismiss = { showEndPicker = false },
            onPicked = { label, lat, lng ->
                vm.update { it.copy(endLocation = label, endLat = lat, endLng = lng) }
                showEndPicker = false
            }
        )
    }

    if (showPrivacy) {
        PrivacyPickerSheet(
            current = ui.privacy,
            onDismiss = { showPrivacy = false },
            onPick = { vm.update { s -> s.copy(privacy = it) }; showPrivacy = false }
        )
    }
}
