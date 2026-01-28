package com.example.tripshare.ui.account

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.model.UserEntity
import com.example.tripshare.data.repo.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val isSuccess: Boolean = false,
    val googleUserToRegister: Pair<String, String>? = null
)

class LoginViewModel(
    private val app: Application,
    private val repo: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val auth = FirebaseAuth.getInstance()

    // Standard Email/Pass Login
    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter email and password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, message = null)

            try {
                // 1) Firebase Auth sign-in
                auth.signInWithEmailAndPassword(email, pass).await()

                // 2) Sync profile (Cloud -> Room)
                repo.syncUserFromCloud(email)

                // 3) Load local user (repair if missing)
                var localUser = repo.findByEmail(email)

                if (localUser == null) {
                    val recoveredUser = UserEntity(
                        email = email,
                        name = "Traveler",
                        verified = true
                    )

                    val newId = repo.createUser(recoveredUser)
                    localUser = repo.getUserById(newId)
                }

                if (localUser == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Login Error: Could not load or create user profile."
                    )
                    return@launch
                }

                // 4) Save login session locally
                AuthPrefs.saveRegistration(app, localUser.id, localUser.email, pass)

                // ✅ 5) Update FCM token for this user (VERY IMPORTANT)
                try {
                    val token = FirebaseMessaging.getInstance().token.await()
                    repo.updateFcmToken(localUser.id, token) // ✅ use repo
                    Log.d("LoginVM", "FCM token updated for uid=${localUser.id}")
                } catch (e: Exception) {
                    Log.w("LoginVM", "FCM token update failed: ${e.message}")
                    // don't fail login just because token update failed
                }

                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)

            } catch (e: Exception) {
                e.printStackTrace()
                val msg =
                    if (e.message?.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) == true)
                        "Invalid email or password"
                    else e.message ?: "Login failed"

                _uiState.value = _uiState.value.copy(isLoading = false, error = msg)
            }
        }
    }

    // Handle Google/Facebook Login (after Firebase Auth already signed in)
    fun handleSocialLogin(email: String, displayName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, message = null)

            try {
                // 1) Sync from Cloud
                repo.syncUserFromCloud(email)

                // 2) Load local user
                val user = repo.findByEmail(email)

                if (user != null) {
                    // Save session (no password for social)
                    AuthPrefs.saveRegistration(app, user.id, user.email, "")

                    // ✅ Update token too
                    try {
                        val token = FirebaseMessaging.getInstance().token.await()
                        repo.updateFcmToken(user.id, token)
                        Log.d("LoginVM", "FCM token updated for social uid=${user.id}")
                    } catch (e: Exception) {
                        Log.w("LoginVM", "FCM token update failed (social): ${e.message}")
                    }

                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                    return@launch
                }

                // If not found in cloud and not in local -> go register
                Log.d("LoginVM", "User not found in Cloud or Local. Redirecting to Register.")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    googleUserToRegister = email to displayName
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Social Login Failed: ${e.message}"
                )
            }
        }
    }

    fun onRegisterNavigationHandled() {
        _uiState.value = _uiState.value.copy(googleUserToRegister = null)
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter your email address first.",
                message = null
            )
            return
        }

        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _uiState.value = _uiState.value.copy(message = "Reset link sent to $email", error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed: ${e.message}", message = null)
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
