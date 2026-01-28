package com.example.tripshare.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.example.tripshare.data.AuthPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    vm: EditProfileViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    val ctx = LocalContext.current

    val userIdNullable: Long? by AuthPrefs.getUserId(ctx).collectAsState(initial = null)
    val userId = userIdNullable
    val canSave = (userId ?: -1L) > 0

    // Load user once ID is available
    LaunchedEffect(userId) {
        userId?.takeIf { it > 0 }?.let { vm.loadUser(it) }
    }

    // Snackbar
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(ui.error) {
        ui.error?.let { snackbar.showSnackbar(it) }
    }

    LaunchedEffect(ui.success) {
        if (ui.success) {
            snackbar.showSnackbar("Profile saved")
            vm.clearSuccess()
            onSaved()
        }
    }

    // Local-only fields for password change UI
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    val wantsPasswordChange =
        currentPassword.isNotBlank() || newPassword.isNotBlank() || confirmPassword.isNotBlank()

    val passwordValid =
        if (!wantsPasswordChange) true
        else newPassword.length >= 6 && newPassword == confirmPassword && currentPassword.isNotBlank()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (!passwordValid) return@Button
                        val id = userId ?: return@Button

                        // ✅ Your existing save (name/bio/etc)
                        // Email + password require VM changes (see note below)
                        vm.save(id) { /* handled via success state */ }
                    },
                    enabled = canSave && !ui.saving && passwordValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (ui.saving) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 10.dp)
                        )
                    }
                    Text(if (ui.saving) "Saving…" else "Save")
                }

                if (!passwordValid) {
                    Text(
                        text = "Password must be at least 6 characters and match confirm password.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    ) { inner ->

        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            if (ui.saving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            val scroll = rememberScrollState()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scroll)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = ui.fullName,
                    onValueChange = { t -> vm.update { it.copy(fullName = t) } },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !ui.saving,
                    singleLine = true
                )

                OutlinedTextField(
                    value = ui.bio,
                    onValueChange = { t -> vm.update { it.copy(bio = t) } },
                    label = { Text("Bio") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    enabled = !ui.saving
                )


            }
        }
    }
}
