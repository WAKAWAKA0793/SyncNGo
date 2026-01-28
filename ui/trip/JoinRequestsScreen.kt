// ui/trip/JoinRequestsScreen.kt
package com.example.tripshare.ui.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// ui/trip/JoinRequestsScreen.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinRequestsScreen(
    vm: JoinRequestsViewModel,
    onBack: () -> Unit
) {
    val pending = vm.pending.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Join Requests") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Pending Requests", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)

            // Optional: Show a warning text if full
            if (pending.isNotEmpty() && pending.first().isTripFull) {
                Text(
                    text = "Trip is currently full. Reject requests or remove participants to accept more.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text("${pending.size} pending request${if (pending.size == 1) "" else "s"}")

            if (pending.isEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text("No pending requests right now.")
                return@Column
            }

            pending.forEach { item: PendingRequestUI ->
                val subtitle = "Wants to join ${item.tripName}"
                RequestCard(
                    name = item.user?.name ?: "Traveler",
                    avatar = item.user?.profilePhoto,
                    subtitle = subtitle,
                    isTripFull = item.isTripFull, // Pass the status
                    onAccept = { vm.accept(item.req) },
                    onReject  = { vm.reject(item.req) }
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun RequestCard(
    name: String,
    avatar: String?,
    subtitle: String,
    isTripFull: Boolean, // Add this parameter
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = avatar ?: "https://i.pravatar.cc/80?img=1",
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    enabled = !isTripFull // Disable button if trip is full
                ) {
                    Text(if (isTripFull) "Full" else "Accept")
                }

                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject")
                }
            }
        }
    }
}
