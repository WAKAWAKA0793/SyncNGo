package com.example.tripshare.ui.notifications

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Compose-friendly permission requester for POST_NOTIFICATIONS.
 * - onGranted: called when permission is granted (or pre-T devices).
 * - onDenied: called when permission is denied.
 */
@Composable
fun RequestNotificationPermissionIfNeeded(
    onGranted: () -> Unit = {},
    onDenied: () -> Unit = {}
) {
    val ctx = LocalContext.current

    // pre-Tiramisu: treat as granted by default
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        LaunchedEffect(Unit) { onGranted() }
        return
    }

    var showRationale by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onGranted() else onDenied()
    }

    LaunchedEffect(Unit) {
        val status = ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
        if (status == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else if (!permissionRequested) {
            permissionRequested = true
            // Optionally show rationale before calling launcher — this example launches immediately.
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Notification permission") },
            text = { Text("We need permission to send trip reminders and payment reminders.") },
            confirmButton = {
                Button(onClick = {
                    showRationale = false
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) { Text("Allow") }
            },
            dismissButton = {
                TextButton(onClick = { showRationale = false }) { Text("Not now") }
            }
        )
    }
}
