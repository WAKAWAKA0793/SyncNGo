package com.example.tripshare.ui.account

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tripshare.ui.theme.Blue200
import com.example.tripshare.ui.theme.Blue300
import com.example.tripshare.ui.theme.Blue50
import com.example.tripshare.ui.theme.Blue600
import com.example.tripshare.ui.theme.Blue700
import com.example.tripshare.ui.theme.Blue900
import com.example.tripshare.ui.theme.BlueGrey600
import com.example.tripshare.ui.theme.BlueGrey700
import com.example.tripshare.ui.theme.ErrorMain
import com.example.tripshare.ui.theme.Grey200
import com.example.tripshare.ui.theme.Grey300
import com.example.tripshare.ui.theme.Grey600
import com.example.tripshare.ui.theme.LightBlue50
import com.example.tripshare.ui.theme.White



@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onGoRegister: () -> Unit,
    onForgotPassword: () -> Unit,
    onGoogleLogin: () -> Unit,
    onAppleLogin: () -> Unit,
    onEmailLogin: (String) -> Unit,
    onFacebookLogin: () -> Unit,
    onContinueAsGuest: () -> Unit,
    isLoading: Boolean = false,
    socialError: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    val errorToShow = localError ?: socialError

    // Soft blue background gradient (modern look)
    val bgBrush = Brush.verticalGradient(
        colors = listOf(Blue50, LightBlue50, White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top header
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Blue900
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Log in to continue your trips.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BlueGrey700
                    )
                )

                Spacer(Modifier.height(18.dp))

                // Main card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {

                        // Row: title + sign up
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Log in",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Blue900
                                )
                            )
                            TextButton(onClick = onGoRegister, enabled = !isLoading) {
                                Text("Sign up", color = Blue700, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        Divider(color = Grey200)
                        Spacer(Modifier.height(14.dp))

                        // Email field
                        // Email field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            singleLine = true,
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Blue600,
                                focusedLabelColor = Blue700,
                                cursorColor = Blue700,
                                unfocusedBorderColor = Grey300,
                                // --- ADD THESE LINES ---
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Spacer(Modifier.height(12.dp))

                        // Password field
                        // Password field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            singleLine = true,
                            enabled = !isLoading,
                            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPass = !showPass }, enabled = !isLoading) {
                                    Icon(
                                        imageVector = if (showPass) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (showPass) "Hide password" else "Show password",
                                        tint = BlueGrey600
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Blue600,
                                focusedLabelColor = Blue700,
                                cursorColor = Blue700,
                                unfocusedBorderColor = Grey300,
                                // --- ADD THESE LINES ---
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        if (!errorToShow.isNullOrBlank()) {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = errorToShow,
                                color = ErrorMain,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        // Forgot password
                        TextButton(
                            onClick = {
                                forgotEmail = email // prefill with whatever user typed
                                showForgotDialog = true
                                onForgotPassword() // optional: keep if you use navigation; if not, you can remove this line
                            },
                            enabled = !isLoading
                        ) {
                            Text("Forgot password?", color = Blue700)
                        }

                        Spacer(Modifier.height(10.dp))

                        // Continue button (primary blue gradient look via containerColor + tonalElevation)
                        Button(
                            onClick = {
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    localError = null
                                    onLogin(email, password)
                                } else {
                                    localError = "Please enter email and password"
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Blue700,
                                contentColor = White,
                                disabledContainerColor = Blue200,
                                disabledContentColor = White
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp),
                                    color = White
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("Signing in…", fontWeight = FontWeight.SemiBold)
                            } else {
                                Text("Continue", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Or divider
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Divider(Modifier.weight(1f), color = Grey200)
                            Text(
                                "  or  ",
                                style = MaterialTheme.typography.bodySmall,
                                color = Grey600
                            )
                            Divider(Modifier.weight(1f), color = Grey200)
                        }

                        Spacer(Modifier.height(16.dp))

                        // Google
                        OutlinedButton(
                            onClick = onGoogleLogin,
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Blue900
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.dp
                            )
                        ) {
                            Text("Continue with Google", fontWeight = FontWeight.SemiBold)
                            if (isLoading) {
                                Spacer(Modifier.width(10.dp))
                                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(Modifier.height(12.dp))

// Continue with Facebook
                        OutlinedButton(
                            onClick = onFacebookLogin,
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Blue900
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                        ) {
                            // If you have a Facebook icon in drawable, use it here:
                            // Icon(painter = painterResource(R.drawable.ic_facebook), contentDescription = null, tint = Color.Unspecified)

                            Spacer(Modifier.width(10.dp))
                            Text("Continue with Facebook", fontWeight = FontWeight.SemiBold)
                            if (isLoading) {
                                Spacer(Modifier.width(10.dp))
                                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(Modifier.height(12.dp))

                    }
                }
            }

            // Bottom footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Don’t have an account?", color = BlueGrey700)
                    Spacer(Modifier.width(6.dp))
                    TextButton(onClick = onGoRegister, enabled = !isLoading) {
                        Text("Sign up", color = Blue700, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(6.dp))

                // tiny accent bar for modern feel
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Brush.horizontalGradient(listOf(Blue300, Blue700)))
                )
            }
        }
    }
    if (showForgotDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Reset password") },
            text = {
                Column {
                    Text("Enter your email and we’ll send you a reset link.")
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // We'll call viewModel from the parent (see section 2)
                        onEmailLogin(forgotEmail) // reuse existing callback if you want
                        showForgotDialog = false
                    }
                ) { Text("Send link") }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) { Text("Cancel") }
            }
        )
    }
}

