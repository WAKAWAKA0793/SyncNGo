package com.example.tripshare.ui.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.model.WaitlistEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitlistScreen(
    vm: WaitlistViewModel,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val myUserId by AuthPrefs.getUserId(ctx).collectAsState(initial = null)

    // ✅ set userId once when it becomes available/changes
    LaunchedEffect(myUserId) {
        vm.setUserId(myUserId)
    }

    // ✅ collect stable flow
    val waitlist by vm.myWaitlist.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Waitlist",fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* notifications overview */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { inner ->
        if (waitlist.isEmpty()) {
            Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No waitlist entries yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(inner)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(waitlist, key = { it.id }) { item ->
                    WaitlistCard(
                        item = item,
                        onToggle = { enabled -> vm.toggleAlert(item, enabled) }
                    )
                }
            }
        }
    }
}

@Composable
fun WaitlistCard(
    item: WaitlistEntity,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(item.tripName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(item.location, style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(8.dp))
            Text("Date: ${item.date}", fontWeight = FontWeight.Medium)
            Text("Waitlist Position: #${item.position}", color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (item.alertsEnabled) "Alerts Enabled" else "Alerts Disabled",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = item.alertsEnabled,
                    onCheckedChange = onToggle
                )
            }
        }
    }
}
