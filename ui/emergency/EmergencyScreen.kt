package com.example.tripshare.ui.emergency

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tripshare.R
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.model.InsurancePolicyEntity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    vm: EmergencyViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()

    // State
    val userId by AuthPrefs.getUserId(context).collectAsState(initial = null)
    val contacts by vm.contacts.collectAsState()
    val localNumbers by vm.localNumbers.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var newName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var isSavingContact by remember { mutableStateOf(false) }
    var newRelationship by remember { mutableStateOf("") }
    val relationshipOptions = listOf("Parent", "Spouse", "Siblings", "Friend")
    var relExpanded by rememberSaveable { mutableStateOf(false) }
    // Loading state for the SOS button
    var isSendingSOS by remember { mutableStateOf(false) }

    // 🔔 Track whether the siren is currently active
    var isAlarmActive by remember { mutableStateOf(false) }

    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.emergency_siren).apply {
            isLooping = true // keep the alarm sounding
        }
    }

    val stopAlarm: () -> Unit = {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        isAlarmActive = false
    }

    val insurance by vm.insurance.collectAsState()
    var showInsuranceSheet by remember { mutableStateOf(false) }

    // Load Insurance when userId changes
    LaunchedEffect(userId) {
        userId?.let { vm.loadInsurance(it) }
    }

    // Helper to open URLs
    val onOpenUrl: (String) -> Unit = { url ->
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open link", Toast.LENGTH_SHORT).show()
        }
    }

    // Make sure we release the player when this Composable leaves the composition
    DisposableEffect(Unit) {
        onDispose {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
    }

    // Load Custom Contacts
    LaunchedEffect(userId) {
        val id = userId ?: return@LaunchedEffect
        if (id > 0) vm.loadContacts(id)
    }

    // Detect Location on Startup (for Country Codes)
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let { vm.updateLocalNumbersByLocation(context, it.latitude, it.longitude) }
            }
        }
    }

    // Action Handlers
    val onCall: (String) -> Unit = { phone ->
        if (!isLikelyValidPhone(phone)) {
            Toast.makeText(
                context,
                "This emergency contact number looks invalid. Please check it in your profile.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            try {
                context.startActivity(intent)
            } catch (e: SecurityException) {
                Toast.makeText(context, "Call permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    val onSOS: () -> Unit = {
        scope.launch {
            // 1. Permission Check
            val hasSms = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED

            val hasLoc = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasSms || !hasLoc) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    100
                )
            } else {
                if (isAlarmActive) {
                    stopAlarm()
                } else {
                    isSendingSOS = true
                    if (!mediaPlayer.isPlaying) mediaPlayer.start()
                    isAlarmActive = true

                    try {
                        val priority = Priority.PRIORITY_HIGH_ACCURACY
                        val cancellationToken = CancellationTokenSource().token

                        locationClient.getCurrentLocation(priority, cancellationToken)
                            .addOnSuccessListener { loc ->
                                if (loc != null) {
                                    // ✅ FIX 1: Corrected Map Link Format (Standard Google Maps URL)
                                    // Previous version had a typo. This one works universally.
                                    val mapLink = "https://maps.google.com/?q=${loc.latitude},${loc.longitude}"
                                    val message = "🚨 SOS! Help Me!\nLoc: $mapLink"

                                    val smsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                        context.getSystemService(android.telephony.SmsManager::class.java)
                                    } else {
                                        @Suppress("DEPRECATION")
                                        android.telephony.SmsManager.getDefault()
                                    }

                                    // ✅ FIX 2: Simplified Contact Filter
                                    // We temporarily removed strict validation to ensure it tries to send.
                                    val validContacts = contacts.filter { it.phone.isNotBlank() }

                                    // 🔍 DEBUG: Show how many contacts were found
                                    Toast.makeText(context, "Found ${validContacts.size} contacts to message.", Toast.LENGTH_SHORT).show()

                                    if (validContacts.isEmpty()) {
                                        Toast.makeText(context, "ERROR: Contact list is empty!", Toast.LENGTH_LONG).show()
                                    } else {
                                        validContacts.forEach { c ->
                                            try {
                                                smsManager?.sendTextMessage(
                                                    c.phone,
                                                    null,
                                                    message,
                                                    null,
                                                    null
                                                )
                                                // 🔍 DEBUG: Confirm send attempt
                                                Toast.makeText(context, "Sent to ${c.name}", Toast.LENGTH_SHORT).show()
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Failed to send to ${c.name}: ${e.message}", Toast.LENGTH_LONG).show()
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Location not found. Try standing near a window.", Toast.LENGTH_LONG).show()
                                }
                                isSendingSOS = false
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Loc Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                isSendingSOS = false
                            }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        isSendingSOS = false
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Assistance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            newName = ""
                            newPhone = ""
                            newRelationship = ""
                            showAddSheet = true
                        }

                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Emergency Contact")
                    }
                }
            )

        }
    ) { inner ->
        Column(
            modifier = Modifier.padding(inner).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- SOS Button ---
            Button(
                onClick = { onSOS() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                enabled = !isSendingSOS  // still block while acquiring GPS/SMS
            ) {
                when {
                    isSendingSOS -> {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Acquiring GPS...",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    isAlarmActive -> {
                        Text(
                            "Stop Alarm",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                    else -> {
                        Text(
                            "Emergency SOS",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                }
            }

            Text("Pressing SOS will send your live location to all contacts below.", style = MaterialTheme.typography.bodySmall)

            // --- My Emergency Contacts ---
            Text("My Emergency Contacts", fontWeight = FontWeight.SemiBold)

            if (contacts.isEmpty()) {
                Text("No contacts found.", color = Color.Gray)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    contacts.forEach { contact ->
                        // Use 'key' to help Compose identify items when they are removed
                        key(contact.id) { // Assuming your Entity has an ID, otherwise use contact.phone
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.EndToStart) {
                                        // Trigger the delete in ViewModel
                                        userId?.let { uid ->
                                            vm.deleteContact(uid, contact)
                                        }
                                        true
                                    } else {
                                        false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    SwipeDeleteBackground(dismissState)
                                },
                                enableDismissFromStartToEnd = false, // Disable left-to-right swipe
                                content = {
                                    EmergencyContactCard(contact.name, contact.phone, onCall)
                                }
                            )
                        }
                    }
                }
            }

            // --- Local Emergency Services ---
            Text("Local Services (${localNumbers.countryName})", fontWeight = FontWeight.SemiBold)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EmergencyServiceCard("Police", localNumbers.police, onCall)
                EmergencyServiceCard("Ambulance", localNumbers.ambulance, onCall)
                EmergencyServiceCard("Fire Department", localNumbers.fire, onCall)
            }

            // --- Travel Insurance ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Travel Insurance", fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { showInsuranceSheet = true }) {
                    Icon(
                        // If insurance exists, show Edit (Pencil), else Add (Plus)
                        imageVector = if (insurance != null) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = "Edit Insurance",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (insurance == null) {
                Card(
                    onClick = { showInsuranceSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No insurance details added", color = Color.Gray)
                        Text("Add policy for quick claims access", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else {
                insurance?.let { policy ->
                    InsuranceCard(
                        policy = policy,
                        onCall = onCall,
                        onFileClaim = onOpenUrl
                    )
                }
            }
            if (showInsuranceSheet) {
                InsuranceEntrySheet(
                    initialPolicy = insurance,
                    onDismiss = { showInsuranceSheet = false },
                    onSave = { provider, policyNo, phone, url ->
                        val uid = userId
                        if (uid != null) {
                            vm.saveInsurance(uid, provider, policyNo, phone, url)
                            showInsuranceSheet = false
                            Toast.makeText(context, "Insurance details saved", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            if (showAddSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showAddSheet = false },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Add Emergency Contact",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "This contact will receive your SOS SMS.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            )
                        )

                        OutlinedTextField(
                            value = newPhone,
                            onValueChange = { newPhone = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("+60 12-345 6789") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            )
                        )
                        ExposedDropdownMenuBox(
                            expanded = relExpanded,
                            onExpandedChange = { relExpanded = !relExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = newRelationship,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Relationship") },
                                placeholder = { Text("Select") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                singleLine = true
                            )

                            ExposedDropdownMenu(
                                expanded = relExpanded,
                                onDismissRequest = { relExpanded = false }
                            ) {
                                relationshipOptions.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            newRelationship = item
                                            relExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        val canSave =
                            newName.isNotBlank() &&
                                    newRelationship.isNotBlank() &&
                                    isLikelyValidPhone(newPhone)


                        Button(
                            onClick = {
                                scope.launch {
                                    val uid = userId ?: return@launch

                                    if (!canSave) {
                                        Toast.makeText(
                                            context,
                                            "Please enter a valid name and phone number.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }

                                    isSavingContact = true
                                    try {
                                        // ✅ Add contact via ViewModel
                                        vm.addContact(
                                            userId = uid,
                                            name = newName.trim(),
                                            phone = newPhone.trim(),
                                            relationship = newRelationship.trim()
                                        )

                                        vm.loadContacts(uid) // refresh list
                                        Toast.makeText(context, "Contact added!", Toast.LENGTH_SHORT).show()
                                        showAddSheet = false
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Failed to add contact: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } finally {
                                        isSavingContact = false
                                    }
                                }
                            },
                            enabled = !isSavingContact,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            if (isSavingContact) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("Saving...")
                            } else {
                                Text("Save Contact")
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

        }
    }
}

// --- Helper Composable Components ---
@Composable
fun InsuranceCard(
    policy: InsurancePolicyEntity,
    onCall: (String) -> Unit,
    onFileClaim: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(policy.providerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Policy #: ${policy.policyNumber}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Call Support Button
                Button(
                    onClick = { onCall(policy.emergencyPhone) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Support")
                }

                // File Claim Button
                Button(
                    onClick = { onFileClaim(policy.claimUrl) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("File Claim")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceEntrySheet(
    initialPolicy: InsurancePolicyEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var provider by remember { mutableStateOf(initialPolicy?.providerName ?: "") }
    var policyNo by remember { mutableStateOf(initialPolicy?.policyNumber ?: "") }
    var phone by remember { mutableStateOf(initialPolicy?.emergencyPhone ?: "") }
    var url by remember { mutableStateOf(initialPolicy?.claimUrl ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth().padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Insurance Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = provider,
                onValueChange = { provider = it },
                label = { Text("Provider Name (e.g., Allianz)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = policyNo,
                onValueChange = { policyNo = it },
                label = { Text("Policy Number") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Emergency Hotline") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Claim Web URL") },
                placeholder = { Text("https://portal.insurance.com/claim") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onSave(provider, policyNo, phone, url) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = provider.isNotBlank() && policyNo.isNotBlank()
            ) {
                Text("Save Insurance Info")
            }
        }
    }
}

@Composable
fun EmergencyContactCard(
    name: String,
    phone: String,
    onCall: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, fontWeight = FontWeight.Medium)
                Text(phone, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { onCall(phone) }) {
                Icon(Icons.Default.Call, contentDescription = "Call")
            }
        }
    }
}

@Composable
fun EmergencyServiceCard(
    service: String,
    phone: String,
    onCall: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(service, fontWeight = FontWeight.Medium)
                // Highlight the emergency number in Red
                Text(phone, style = MaterialTheme.typography.bodySmall, color = Color.Red)
            }
            IconButton(onClick = { onCall(phone) }) {
                Icon(Icons.Default.Call, contentDescription = "Call")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeDeleteBackground(dismissState: androidx.compose.material3.SwipeToDismissBoxState) {
    val color by animateColorAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
            Color.Red
        else
            Color.Transparent,
        label = "SwipeBackground"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color.White
        )
    }
}

private fun isLikelyValidPhone(phone: String): Boolean {
    // Strip spaces, dashes, plus, etc.
    val digits = phone.filter { it.isDigit() }
    // Basic rule: 8–15 digits is “reasonable” for a phone number
    return digits.length in 8..15
}