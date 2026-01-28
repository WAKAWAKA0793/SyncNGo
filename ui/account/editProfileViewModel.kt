package com.example.tripshare.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.model.EmergencyContactEntity
import com.example.tripshare.data.repo.EmergencyRepository
import com.example.tripshare.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val fullName: String = "",
    val location: String = "",
    val bio: String = "",
    val contacts: List<EmergencyContactUi> = listOf(EmergencyContactUi()),
    val error: String? = null,
    val saving: Boolean = false,
    val profilePhoto: String? = null,
    val success: Boolean = false,
    val email: String = ""
)

class EditProfileViewModel(
    private val userRepo: UserRepository,
    private val emergencyRepo: EmergencyRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(EditProfileUiState())
    val ui: StateFlow<EditProfileUiState> = _ui

    fun update(t: (EditProfileUiState) -> EditProfileUiState) {
        _ui.value = t(_ui.value)
    }

    fun clearSuccess() = update { it.copy(success = false) }


    fun loadUser(userId: Long) {
        viewModelScope.launch {
            val user = userRepo.getUserById(userId)
            val stored = emergencyRepo.contacts(userId).firstOrNull().orEmpty()
            update {
                it.copy(
                    fullName = user?.name.orEmpty(),
                    location = user?.location.orEmpty(),
                    profilePhoto = user?.profilePhoto,
                    bio = user?.bio.orEmpty(),
                    contacts = stored.map { e ->
                        EmergencyContactUi(e.name, e.relationship, e.phone)
                    }.ifEmpty { listOf(EmergencyContactUi()) }
                )
            }
        }
    }

    fun save(userId: Long, onDone: () -> Unit) {
        val s = ui.value
        if (s.fullName.isBlank()) {
            update { it.copy(error = "Full name cannot be empty") }
            return
        }

        viewModelScope.launch {
            update { it.copy(saving = true, error = null) }
            try {
                userRepo.getUserById(userId)?.let { u ->
                    userRepo.updateUser(
                        u.copy(
                            name = s.fullName,
                            location = s.location,
                            bio = s.bio,
                            profilePhoto = s.profilePhoto // ✅ add this
                        )
                    )

                }

                // 2) Replace contacts (delete + insert)
                val entities: List<EmergencyContactEntity> =
                    s.contacts
                        .filter { it.name.isNotBlank() || it.phone.isNotBlank() } // keep only filled rows
                        .map {
                            EmergencyContactEntity(
                                userId = userId,
                                name = it.name.trim(),
                                relationship = it.relationship.trim(),
                                phone = "${it.countryCode} ${it.phone}".trim()

                            )
                        }

                emergencyRepo.replaceContacts(userId, entities)

                update { it.copy(saving = false, success = true) }
                onDone()
            } catch (e: Exception) {
                update { it.copy(saving = false, error = e.message ?: "Failed to save") }
            }
        }
    }
}