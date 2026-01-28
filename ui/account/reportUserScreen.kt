package com.example.tripshare.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class ReportReason(val title: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportUserScreen(
    targetUserId: Long,
    currentUserId: Long,
    reportVm: ReportViewModel,
    onBack: () -> Unit,
    navBack: () -> Unit
) {
    val reasons = listOf(
        ReportReason("Harassment or Bullying", "Harmful or threatening messages/behavior."),
        ReportReason("Spam or Misleading Content", "Unsolicited or deceptive content."),
        ReportReason("Impersonation", "Pretending to be someone else."),
        ReportReason("Inappropriate Content", "Graphic, explicit, or offensive material."),
        ReportReason("Privacy Violation", "Sharing private information without consent."),
        ReportReason("Other Reason", "Reason not listed above.")
    )

    var selectedReason by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var blockUser by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report User") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },

        // ✅ Sticky submit button
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            if (selectedReason == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please select a reason")
                                }
                                return@Button
                            }
                            reportVm.submitReport(
                                reporterUserId = currentUserId,
                                reportedUserId = targetUserId,
                                reason = selectedReason!!,
                                description = description,
                                blockUser = blockUser,
                                onDone = { navBack() }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedReason != null
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🔔 Safety Notice
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0CC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Your Safety Matters\n\nThank you for helping us keep our community safe. Your report is confidential.",
                    modifier = Modifier.padding(16.dp)
                )
            }

            // 📌 Report Reasons
            reasons.forEach { reason ->
                ReportOption(
                    reason = reason,
                    selected = reason.title == selectedReason,
                    onSelect = { selectedReason = reason.title }
                )
            }

            // 📝 Extra Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe the issue") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false
            )

            // 🚫 Block User Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Block User", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Switch(checked = blockUser, onCheckedChange = { blockUser = it })
            }

            // 🔗 Help Center
            TextButton(onClick = { /* Navigate to Help Center */ }) {
                Text("Visit our Safety & Help Center")
            }

            Spacer(Modifier.height(80.dp)) // leave room so content isn’t hidden behind bottom bar
        }
    }
}


@Composable
fun ReportOption(reason: ReportReason, selected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(reason.title, fontWeight = FontWeight.Bold)
            Text(reason.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
