package com.example.tripshare.ui.account

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.model.EmergencyContactEntity
import com.example.tripshare.data.model.UserEntity
import com.example.tripshare.data.model.VerificationMethod
import com.example.tripshare.data.repo.EmergencyRepository
import com.example.tripshare.data.repo.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

data class CreateAccountUiState(
    val fullName: String = "",
    val email: String = "",
    val icNumber: String = "",
    val password: String = "",
    val verificationMethod: VerificationMethod = VerificationMethod.PHONE,
    val phoneNumber: String = "",
    val contacts: List<EmergencyContactUi> = listOf(EmergencyContactUi()),
    val error: String? = null,
    val isGoogleSignUp: Boolean = false,
    val saving: Boolean = false,
    val message: String? = null,
    val success: Boolean = false
)

// Rename the mapper so it doesn't collide
private fun List<EmergencyContactUi>.toEmergencyEntities(userId: Long): List<EmergencyContactEntity> =
    this.filter { it.name.isNotBlank() || it.phone.isNotBlank() }
        .map {
            EmergencyContactEntity(
                userId = userId,
                name = it.name.trim(),
                relationship = it.relationship.trim(),
                phone = it.phone.trim()
            )
        }


class CreateAccountViewModel(
    private val app: Application,                 // <-- keep this and use it directly
    private val repo: UserRepository,
    private val emergencyRepo: EmergencyRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(CreateAccountUiState())
    val ui: StateFlow<CreateAccountUiState> = _ui

    fun update(transform: (CreateAccountUiState) -> CreateAccountUiState) {
        _ui.value = transform(_ui.value)
    }

    fun setScannedIc(ic: String) {
        update { it.copy(icNumber = ic) }
    }

    fun initFromGoogle(name: String, email: String) {
        update {
            it.copy(
                fullName = name,
                email = email,
                verificationMethod = VerificationMethod.EMAIL, // Default to Email
                isGoogleSignUp = true // Mark as Google user
            )
        }
    }

    fun addContact() = update { it.copy(contacts = it.contacts + EmergencyContactUi()) }

    fun updateContact(index: Int, transform: (EmergencyContactUi) -> EmergencyContactUi) {
        update {
            val list = it.contacts.toMutableList()
            if (index in list.indices) list[index] = transform(list[index])
            it.copy(contacts = list)
        }
    }

    fun checkVerificationStatus(onVerified: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) return

        update { it.copy(saving = true) }

        // 🔄 RELOAD is required! Firebase caches the "false" state locally.
        user.reload().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (user.isEmailVerified) {
                    // ✅ Success!
                    update { it.copy(saving = false, message = "Email Verified! Welcome aboard.") }
                    onVerified()
                } else {
                    // ❌ Not clicked yet
                    update { it.copy(saving = false, error = "Not verified yet. Please click the link in your email.") }
                }
            } else {
                update { it.copy(saving = false, error = "Failed to check status: ${task.exception?.message}") }
            }
        }
    }

    fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            update { it.copy(saving = true) }

            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        update { it.copy(
                            saving = false,
                            message = "Verification link sent to ${user.email}. Please check your inbox (and spam)."
                        )}
                        Log.d("EmailVerification", "Email sent.")
                    } else {
                        update { it.copy(
                            saving = false,
                            error = "Failed to send email: ${task.exception?.message}"
                        )}
                    }
                }
        } else {
            update { it.copy(error = "No user found. Please register first.") }
        }
    }

    // Function to check if the user's email is verified
    fun isEmailVerified(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.isEmailVerified == true
    }

    /** Register user, save contacts, persist login */
    suspend fun submitAndRegister(): Long {
        val state = _ui.value
        val isPasswordValid = state.isGoogleSignUp || state.password.isNotBlank()

        if (state.email.isBlank() || !isPasswordValid || state.fullName.isBlank() || state.icNumber.isBlank()) {
            update { it.copy(error = "Please fill all required fields (including IC)") }
            return -1
        }

        update { it.copy(saving = true, error = null, success = false) }

        // ✅ 1) Check cloud FIRST (avoid creating Firebase Auth user unnecessarily)
        try {
            val alreadyExistsInCloud = repo.checkUserExists(state.email)
            if (alreadyExistsInCloud) {
                update {
                    it.copy(
                        saving = false,
                        error = "This email is already registered. Please Login to sync your data."
                    )
                }
                return -1
            }
        } catch (e: Exception) {
            // optional: decide whether to block registration on network failure
            e.printStackTrace()
            // You can choose to continue or stop here. I'll continue.
        }

        // ✅ 2) Create Firebase Auth user + send verification immediately (non-Google)
        if (!state.isGoogleSignUp) {
            try {
                val authResult = Firebase.auth
                    .createUserWithEmailAndPassword(state.email, state.password)
                    .await()

                val user = authResult.user
                if (user == null) {
                    update { it.copy(saving = false, error = "Auth Failed: user is null") }
                    return -1
                }

                if (!user.isEmailVerified) {
                    user.sendEmailVerification().await()
                }
            } catch (e: Exception) {
                update { it.copy(saving = false, error = "Auth/Verify Failed: ${e.message}") }
                return -1
            }
        }

        // ✅ 3) Create local profile (Room)
        return try {
            val newUser = UserEntity(
                name = state.fullName,
                email = state.email,
                firebaseId = state.email.lowercase(),
                icNumber = state.icNumber,
                phoneNumber = state.phoneNumber.ifBlank { null },
                verified = state.isGoogleSignUp, // Google users treated as verified
                verifiedEmail = if (state.isGoogleSignUp) state.email else null,
                passwordHash = if (state.isGoogleSignUp) "" else state.password,
                location = "",
                bio = "",
                profilePhoto = ""
            )

            val userId = repo.createUser(newUser)
            emergencyRepo.insertAllWithUserId(userId, state.contacts.toEmergencyEntities(userId))

            // ❗ IMPORTANT: Do NOT save AuthPrefs here if you want to stay on register until verified
            // AuthPrefs.saveRegistration(app, userId, state.email, state.password)

            // Optional: repo.getUserData(state.email)

            update {
                it.copy(
                    saving = false,
                    success = true,
                    error = null,
                    message = if (state.isGoogleSignUp)
                        "Account created."
                    else
                        "Account created. Verification email sent to ${state.email}. Please check inbox/spam."
                )
            }

            userId
        } catch (e: Exception) {
            update { it.copy(saving = false, error = "Registration Failed: ${e.message}") }
            -1
        }
    }

}

