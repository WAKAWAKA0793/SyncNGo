@file:OptIn(com.google.maps.android.compose.MapsComposeExperimentalApi::class)

package com.example.tripshare.ui.trip

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FlightTakeoff
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.model.TripCategory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

/* ------------------------------ Small helpers ------------------------------ */

data class LatLngD(val lat: Double, val lng: Double)


fun hasFineLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

/** Zero-padded native date picker -> "yyyy-MM-dd" */
/** Zero-padded native date picker -> "yyyy-MM-dd" */
fun showDatePicker(context: Context, minDateMillis: Long? = null, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val dialog = DatePickerDialog(
        context,
        { _, y, m, d -> onDateSelected(String.format("%04d-%02d-%02d", y, m + 1, d)) },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    // Disable past dates (default to today if null)
    dialog.datePicker.minDate = minDateMillis ?: (System.currentTimeMillis() - 1000)
    dialog.show()
}

@Composable
fun DatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    minDate: Long? = null, // 1. Add this optional parameter
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    OutlinedTextField(
        value = value,
        onValueChange = {}, // read-only
        readOnly = true,
        label = { Text(label) },
        modifier = modifier,
        placeholder = { Text("Pick a date") },
        trailingIcon = {
            IconButton(onClick = {
                // 2. Pass 'minDate' (or null) as the second argument
                showDatePicker(context, minDate, onValueChange)
            }) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Pick date")
            }
        }
    )
}

@SuppressLint("MissingPermission") // we check before calling
fun fetchReadableLocation(
    context: Context,
    onSuccess: (lat: Double, lon: Double, label: String) -> Unit,
    onError: (Throwable) -> Unit = {}
) {
    val fused = LocationServices.getFusedLocationProviderClient(context)
    val cts = CancellationTokenSource()
    fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
        .addOnSuccessListener { loc ->
            if (loc == null) {
                onError(IllegalStateException("Location is null")); return@addOnSuccessListener
            }
            val lat = loc.latitude
            val lon = loc.longitude
            val label = try {
                val gc = Geocoder(context, Locale.getDefault())
                val addr = if (Build.VERSION.SDK_INT >= 33)
                    gc.getFromLocation(lat, lon, 1)?.firstOrNull()
                else
                    @Suppress("DEPRECATION") gc.getFromLocation(lat, lon, 1)?.firstOrNull()
                val city = addr?.locality ?: addr?.subAdminArea
                val country = addr?.countryName
                listOfNotNull(city, country).joinToString(", ").ifBlank { "$lat,$lon" }
            } catch (_: Throwable) { "$lat,$lon" }
            onSuccess(lat, lon, label)
        }
        .addOnFailureListener(onError)
}

fun reverseGeocodeLabel(context: Context, lat: Double, lng: Double): String {
    return try {
        val gc = Geocoder(context, Locale.getDefault())
        val addr = if (Build.VERSION.SDK_INT >= 33)
            gc.getFromLocation(lat, lng, 1)?.firstOrNull()
        else
            @Suppress("DEPRECATION") gc.getFromLocation(lat, lng, 1)?.firstOrNull()

        // Get all the parts we might want
        val place = addr?.featureName     // The specific place name (e.g., "Eiffel Tower")
        val city = addr?.locality ?: addr?.subAdminArea  // The city (e.g., "Paris")
        val country = addr?.countryName   // The country (e.g., "France")

        // Combine them, but remove duplicates.
        // e.g., if place="Paris" and city="Paris", it will only use "Paris" once.
        val parts = listOfNotNull(place, city, country).distinct()

        parts.joinToString(", ").ifBlank { "$lat,$lng" }

    } catch (_: Throwable) { "$lat,$lng" }
}

fun geocodeQuery(context: Context, query: String): LatLngD? {
    if (query.isBlank()) return null
    return try {
        val gc = Geocoder(context, Locale.getDefault())
        val r = if (Build.VERSION.SDK_INT >= 33)
            gc.getFromLocationName(query, 1)?.firstOrNull()
        else
            @Suppress("DEPRECATION") gc.getFromLocationName(query, 1)?.firstOrNull()
        r?.let { LatLngD(it.latitude, it.longitude) }
    } catch (_: Throwable) { null }
}

/* ------------------------------ Map picker sheet ------------------------------ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerSheet(
    title: String,
    initial: LatLngD? = null,
    onUseCurrent: () -> Unit,
    onDismiss: () -> Unit,
    onPicked: (label: String, lat: Double, lng: Double) -> Unit,
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var marker by remember { mutableStateOf(initial ?: LatLngD(3.1390, 101.6869)) } // KL default
    val markerState = rememberMarkerState(position = LatLng(marker.lat, marker.lng))

    LaunchedEffect(markerState.position) {
        val p = markerState.position
        marker = LatLngD(p.latitude, p.longitude)
    }

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(marker.lat, marker.lng), 14f)
    }

    fun moveTo(lat: Double, lng: Double) {
        marker = LatLngD(lat, lng)
        markerState.position = LatLng(lat, lng)
        scope.launch {
            cameraState.animate(
                update = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    LatLng(lat, lng), 16f
                ),
                durationMs = 450
            )
        }
    }

    var query by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search a place or address") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                trailingIcon = {
                    TextButton(onClick = {
                        geocodeQuery(context, query)?.let { moveTo(it.lat, it.lng) }
                    }) { Text("Search") }
                }
            )

            OutlinedButton(onClick = onUseCurrent, modifier = Modifier.fillMaxWidth()) {
                Text("Use current location")
            }

            Box(Modifier.fillMaxWidth().height(360.dp)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraState,
                    onMapClick = { latLng -> moveTo(latLng.latitude, latLng.longitude) }
                ) {
                    Marker(
                        state = markerState,
                        draggable = true,
                        title = "Selected location"
                    )
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(
                    onClick = {
                        val label = reverseGeocodeLabel(context, marker.lat, marker.lng)
                        onPicked(label, marker.lat, marker.lng)
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Confirm location") }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

/* ------------------------------ The screen ------------------------------ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    vm: CreateTripViewModel,
    onCancel: () -> Unit,
    onSuccess: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // State
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }

    // Categories
    val categories = TripCategory.values().toList()
    var selectedCategory by remember { mutableStateOf<TripCategory?>(null) }

    // Logic for category selection
    LaunchedEffect(ui.category) {
        selectedCategory = ui.category.takeIf { it.isNotBlank() }?.let {
            kotlin.runCatching { TripCategory.valueOf(it) }.getOrNull()
        }
    }

    // Map Locations
    val startInitial = if (ui.startLat != null && ui.startLng != null) LatLngD(ui.startLat!!, ui.startLng!!) else null


    // Permission Launcher
    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fetchReadableLocation(ctx, onSuccess = { _, _, label ->
                vm.update { it.copy(startLocation = label) }
            })
        }
    }

    LaunchedEffect(ui.successTripId) {
        if (ui.successTripId != null) {
            onSuccess()
            vm.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Plan a new trip", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
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
                        else Text("Create", fontWeight = FontWeight.Bold)
                    }
                }
            )
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

            // 1. Trip Name & Desc Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Where to next?",
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

            // 2. Category Section (Chips)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabelSection("Trip Vibes")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

            // 3. Date Section (Combined Row)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabelSection("When")
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    // --- Start Date ---
                    Box(Modifier
                        .weight(1f)
                        .clickable {
                            // Picker for Start Date (Min = Today)
                            showDatePicker(ctx) { date ->
                                vm.update { s -> s.copy(startDate = date) }
                            }
                        }
                        .padding(16.dp)
                    ) {
                        Column {
                            Text("Starts", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = ui.startDate.ifBlank { "Select Date" },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (ui.startDate.isNotBlank()) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }

                    // Vertical Divider
                    Box(Modifier.fillMaxHeight().width(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))

                    // --- End Date ---
                    Box(Modifier
                        .weight(1f)
                        .clickable {
                            // Calculate Minimum Date for End Picker
                            // 1. If Start Date is set, user cannot pick a date BEFORE it.
                            // 2. If Start Date is empty, user cannot pick a date BEFORE Today.
                            val minEndMillis = if (ui.startDate.isNotBlank()) {
                                try {
                                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                                    sdf.parse(ui.startDate)?.time
                                } catch (e: Exception) { null }
                            } else null

                            showDatePicker(ctx, minDateMillis = minEndMillis) { date ->
                                vm.update { s -> s.copy(endDate = date) }
                            }
                        }
                        .padding(16.dp)
                    ) {
                        Column {
                            Text("Ends", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
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

            // 4. Location Section (Modern Card)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabelSection("Destinations")

                // Start Point
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

            // 5. Settings Section (Participants, Budget, Privacy)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LabelSection("Details")

                // Participants
                ModernSettingRow(
                    icon = Icons.Outlined.Group,
                    title = "Max Participants",
                    value = "",
                    onClick = { /* Focus logic handled by text field */ }
                ) {
                    // A visible container box
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), // Light background
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
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                // Cost Split Switch
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

                // Budget (Animated Expand)
                AnimatedVisibility(
                    visible = ui.splitCost,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    var budgetExpanded by remember { mutableStateOf(false) }
                    val budgetOptions = listOf("RM0 - RM150", "RM150 - RM500", "RM500 - RM1000", "RM1000 - RM1500", "RM2000+")

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
                            offset = androidx.compose.ui.unit.DpOffset(x = 200.dp, y = 0.dp)
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
                    icon = if(ui.privacy == Privacy.Private) Icons.Outlined.Lock else Icons.Outlined.Public,
                    title = "Privacy",
                    value = if(ui.privacy == Privacy.Private) "Private" else "Everyone",
                    onClick = { showPrivacy = true }
                )
            }

            Spacer(Modifier.height(40.dp)) // Bottom padding
        }
    }

    // Sheets
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


    if (showPrivacy) {
        PrivacyPickerSheet(
            current = ui.privacy,
            onDismiss = { showPrivacy = false },
            onPick = { vm.update { s -> s.copy(privacy = it) }; showPrivacy = false }
        )
    }
}

/* ------------------------------ MODERN UI COMPONENTS ------------------------------ */

@Composable
fun LabelSection(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    )
}

@Composable
fun ModernLocationCard(
    title: String,
    value: String,
    lat: Double?,
    lng: Double?,
    onPick: () -> Unit,
    onCurrentLocation: (() -> Unit)?
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().clickable { onPick() }
    ) {
        Column {
            // Map Preview Area
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (lat != null && lng != null) {
                    val pos = LatLng(lat, lng)
                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(pos, 15f)
                        },
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false,
                            scrollGesturesEnabled = false,
                            zoomGesturesEnabled = false,
                            rotationGesturesEnabled = false,
                            tiltGesturesEnabled = false
                        )
                    ) {
                        Marker(state = rememberMarkerState(position = pos))
                    }

                    // Gradient overlay to ensure text readability at bottom
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                                    startY = 100f
                                )
                            )
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Tap to select location", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Floating Action Button for Current Location
                if (onCurrentLocation != null) {
                    SmallFloatingActionButton(
                        onClick = onCurrentLocation,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Current Location")
                    }
                }
            }

            // Text Details
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Place, null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                    Text(
                        value.ifBlank { "Select location..." },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(Icons.Default.ArrowForwardIos, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun ModernSettingRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))

        if (trailingContent != null) {
            trailingContent()
        } else {
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForwardIos, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
        }
    }
}

// Basic TextField wrapper for inline editing
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        modifier = modifier
    )
}



/* ------------------------------ Small composables ------------------------------ */

@Composable
private fun LocationRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        placeholder = { Text("Tap to choose on map") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPickerSheet(
    current: Privacy,
    onDismiss: () -> Unit,
    onPick: (Privacy) -> Unit
) {
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selected by remember(current) { mutableStateOf(current) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheet) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.width(48.dp))
            Text("Who can see this trip?", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { onPick(selected) }) { Text("Done") }
        }
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier.navigationBarsPadding().imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrivacyCard(
                title = "Private",
                subtitle = "Need wait the approve to join this trip.",
                leading = { Icon(Icons.Outlined.Lock, null) },
                selected = (selected == Privacy.Private),
                onClick = { selected = Privacy.Private }
            )

            PrivacyCard(
                title = "Everyone",
                subtitle = "This trip will be out there for everyone to see and love.",
                leading = { Icon(Icons.Outlined.Public, null) },
                selected = (selected == Privacy.Public),
                onClick = { selected = Privacy.Public }
            )

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(onClick = { onPick(selected) }, modifier = Modifier.weight(1f)) { Text("Done") }
            }
        }
    }
}

@Composable
private fun PrivacyCard(
    title: String,
    subtitle: String,
    leading: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit
) {
    val outline = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (selected) 2.dp else 0.dp,
        border = if (selected) null else DividerDefaults.color.copy(alpha = 0.4f).let {
            BorderStroke(1.dp, it)
        },
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) { leading() }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape)
                    .background(if (selected) Color(0xFF00C27A) else outline),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun MapPreviewCard(
    title: String,
    lat: Double?,
    lng: Double?,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onTap() }
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (lat != null && lng != null) {
                    val pos = LatLng(lat, lng)
                    val camera = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(pos, 14f)
                    }
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = camera,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false
                        ),
                        properties = MapProperties(isMyLocationEnabled = false),
                        onMapClick = { onTap() }
                    ) {
                        Marker(state = rememberMarkerState(position = pos), title = title)
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tap to choose on map", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
