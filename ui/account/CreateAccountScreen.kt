package com.example.tripshare.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripshare.data.model.VerificationMethod
import com.example.tripshare.ui.theme.Blue50
import com.example.tripshare.ui.theme.Blue700
import com.example.tripshare.ui.theme.Blue800
import com.example.tripshare.ui.theme.Blue900
import com.example.tripshare.ui.theme.BlueGrey200
import com.example.tripshare.ui.theme.BlueGrey400
import com.example.tripshare.ui.theme.BlueGrey500
import com.example.tripshare.ui.theme.Red50
import com.example.tripshare.ui.theme.Red700
import com.example.tripshare.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun CreateAccountScreen(
    vm: CreateAccountViewModel,
    onSuccess: (String, String) -> Unit,
    onScanIc: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    val scroll = rememberScrollState()
    var showPass by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 1. Modern Field Colors (Blue Theme)
    // 1. Modern Field Colors (Blue Theme)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Blue700,
        focusedLabelColor = Blue700,
        cursorColor = Blue700,
        unfocusedBorderColor = BlueGrey200,
        unfocusedContainerColor = White,
        focusedContainerColor = White,

        // --- ADD THESE LINES TO MAKE INPUT TEXT BLACK ---
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black
    )

    if (ui.success) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Blue50),
            border = androidx.compose.foundation.BorderStroke(1.dp, Blue700),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                // Header
                Text(
                    text = "Account Created!",
                    style = MaterialTheme.typography.titleMedium,
                    color = Blue900,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))

                // Instructions
                Text(
                    text = "We have sent a verification link to ${ui.email}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BlueGrey500
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "1. Click the link in your email.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "2. Then come back here and click below:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(20.dp))

                // ✅ BUTTON 1: CHECK STATUS (Primary Action - Solid Blue)
                Button(
                    onClick = {
                        vm.checkVerificationStatus {
                            // Call the navigation callback to go to Home/Login
                            onSuccess(ui.email, ui.password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue700)
                ) {
                    Text("I Have Verified My Email")
                }

                Spacer(Modifier.height(12.dp))

                // ✅ BUTTON 2: RESEND (Secondary Action - Outlined with Loader)
                OutlinedButton(
                    onClick = { vm.sendVerificationEmail() },
                    enabled = !ui.saving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Blue700),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Blue700)
                ) {
                    if (ui.saving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Blue700,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Sending...")
                    } else {
                        Text("Resend Verification Email")
                    }
                }
            }
        }
    }
// Show a Toast or Snackbar when 'ui.message' is set
    if (ui.message != null) {
        // Just a simple Text for now, or use a SnackbarHost in your Scaffold
        Text(
            text = ui.message!!,
            color = Blue700,
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue50) // Soft modern background
            .verticalScroll(scroll)
            .padding(20.dp), // Increased padding
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 2. Modern Header
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Blue900 // Darkest blue for contrast
            )
        )
        Text(
            text = "Sign up to start your journey",
            style = MaterialTheme.typography.bodyMedium,
            color = BlueGrey500
        )
        Spacer(Modifier.height(32.dp))

        // 3. Section: Profile Details
        SectionCard(title = "Profile Details") {
            ModernTextField(
                value = ui.fullName,
                onValueChange = { t -> vm.update { it.copy(fullName = t) } },
                label = "Full Name",
                placeholder = "e.g. John Doe",
                colors = fieldColors
            )

            // --- ADD IC NUMBER FIELD HERE ---
            OutlinedTextField(
                value = ui.icNumber,
                onValueChange = { t -> vm.update { it.copy(icNumber = t) } },
                readOnly = true,
                label = { Text("IC Number (Scan Only)") },
                trailingIcon = {
                    // The Camera Button
                    IconButton(onClick = onScanIc) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera, // Ensure you import androidx.compose.material.icons.filled.PhotoCamera
                            contentDescription = "Scan IC",
                            tint = Blue700
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = ui.email,
                // Only allow update if not Google Sign Up
                onValueChange = { if (!ui.isGoogleSignUp) vm.update { s -> s.copy(email = it) } },
                readOnly = ui.isGoogleSignUp,
                label = { Text("Email Address") },
                // FIX 1: Wrap the string in a Text Composable
                placeholder = { Text("john@example.com", color = BlueGrey200) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(), // Ensure modifiers are applied
                shape = RoundedCornerShape(12.dp),  // Ensure shape is applied
                colors = fieldColors
            )

            Spacer(Modifier.height(16.dp)) // Add spacing between fields

            // --- PASSWORD FIELD ---
            if (!ui.isGoogleSignUp) {
                OutlinedTextField(
                    value = ui.password,
                    onValueChange = { t -> vm.update { it.copy(password = t) } },
                    label = { Text("Password") },
                    placeholder = { Text("Create a password", color = BlueGrey200) },
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                imageVector = if (showPass) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Password",
                                tint = BlueGrey500
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        if (!ui.isGoogleSignUp) {
            SectionCard(title = "Verification Method") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. Phone Radio Button
                    RadioButton(
                        selected = ui.verificationMethod == VerificationMethod.PHONE,
                        onClick = { vm.update { it.copy(verificationMethod = VerificationMethod.PHONE) } },
                        colors = RadioButtonDefaults.colors(selectedColor = Blue700)
                    )
                    Text("Phone", style = MaterialTheme.typography.bodyLarge)

                    Spacer(Modifier.width(24.dp))

                    // 2. Email Radio Button (Fixing the error here)
                    RadioButton(
                        selected = ui.verificationMethod == VerificationMethod.EMAIL,
                        onClick = {
                            vm.update { it.copy(verificationMethod = VerificationMethod.EMAIL) }
                        }, // <--- Ensure this closing brace exists!
                        colors = RadioButtonDefaults.colors(selectedColor = Blue700)
                    )
                    Text("Email", style = MaterialTheme.typography.bodyLarge)
                }

                if (ui.verificationMethod == VerificationMethod.PHONE) {
                    ModernTextField(
                        value = ui.phoneNumber,
                        onValueChange = { t -> vm.update { it.copy(phoneNumber = t) } },
                        label = "Phone Number",
                        placeholder = "+1 234 567 890",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = fieldColors
                    )
                    Text(
                        "We will text you a verification code.",
                        style = MaterialTheme.typography.bodySmall,
                        color = BlueGrey500
                    )
                } else {
                    Text(
                        "We'll send a verification code to your email.",
                        style = MaterialTheme.typography.bodySmall,
                        color = BlueGrey500
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
        // 5. Section: Emergency Contact
        SectionCard(title = "Emergency Contact") {
            Text(
                "Provide details for at least one emergency contact.",
                style = MaterialTheme.typography.bodySmall,
                color = BlueGrey500
            )

            ui.contacts.forEachIndexed { index, contact ->
                Surface(
                    color = Blue50, // Very subtle highlight for nested items
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Contact #${index + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            color = Blue800
                        )

                        ModernTextField(
                            value = contact.name,
                            onValueChange = { t -> vm.updateContact(index) { it.copy(name = t) } },
                            label = "Full Name",
                            placeholder = "Contact's name",
                            colors = fieldColors
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RelationshipDropdown(
                                value = contact.relationship,
                                onValueChange = { selected ->
                                    vm.updateContact(index) { it.copy(relationship = selected) }
                                },
                                colors = fieldColors,
                                modifier = Modifier.weight(1f)
                            )

                            ModernTextField(
                                value = contact.phone,
                                onValueChange = { t -> vm.updateContact(index) { it.copy(phone = t) } },
                                label = "Phone",
                                placeholder = "Number",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = fieldColors,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = { vm.addContact() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Blue700),
                border = androidx.compose.foundation.BorderStroke(1.dp, Blue700)
            ) {
                Text("+ Add Another Contact")
            }
        }

        Spacer(Modifier.height(24.dp))

        if (ui.error != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Red50),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = ui.error!!,
                    color = Red700,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // 6. Primary Action Button
        Button(
            onClick = {
                scope.launch {
                    val userId = vm.submitAndRegister()
                    if (userId > 0) {
                        onSuccess(ui.email, ui.password)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // Taller button for modern touch
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue700, // Primary Brand Color
                contentColor = White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                "Create Account",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "By creating an account, you agree to our Terms of Service.",
            style = MaterialTheme.typography.bodySmall,
            color = BlueGrey400,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
    }
}

// --- Helper Composable for Clean Code ---

@Composable
fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Blue900 // Darker blue for section headers
            )
            content()
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    colors: TextFieldColors
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = BlueGrey200) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        singleLine = true,
        keyboardOptions = keyboardOptions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelationshipDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors,
    modifier: Modifier = Modifier
) {
    val options = listOf("Parent", "Spouse", "Siblings", "Friend")
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {}, // selection only (no typing)
            readOnly = true,
            label = { Text("Relationship") },
            placeholder = { Text("Select", color = BlueGrey200) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = colors,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onValueChange(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
