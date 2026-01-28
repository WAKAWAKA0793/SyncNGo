// ui/emergency/EmergencyViewModel.kt
package com.example.tripshare.ui.emergency

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.model.EmergencyContactEntity
import com.example.tripshare.data.model.InsurancePolicyEntity
import com.example.tripshare.data.repo.EmergencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class EmergencyViewModel(
    private val emergencyRepo: EmergencyRepository,
    private val userId: Long
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<EmergencyContactEntity>>(emptyList())
    val contacts: StateFlow<List<EmergencyContactEntity>> = _contacts

    private val _localNumbers = MutableStateFlow(EmergencyCountryData.DEFAULT)
    val localNumbers = _localNumbers.asStateFlow()

    init {
        // Start Cloud Sync
        emergencyRepo.startSync(userId, viewModelScope)

        // Observe Contacts
        viewModelScope.launch {
            emergencyRepo.contacts(userId).collect { list ->
                _contacts.value = list
            }
        }

        // Observe Insurance
        viewModelScope.launch {
            emergencyRepo.getInsurance(userId).collect { policy ->
                _insurance.value = policy
            }
        }
    }
    // ----------------------------
    // Sync & Load
    // ----------------------------
    fun loadContacts(userId: Long) {
        viewModelScope.launch {
            // ✅ Start Cloud Sync
            emergencyRepo.startSync(userId, viewModelScope)

            // Observe local DB
            emergencyRepo.contacts(userId).collect { list ->
                _contacts.value = list
            }
        }
    }

    // ----------------------------
    // CRUD Operations (Now Synced)
    // ----------------------------
    fun addContact(
        userId: Long,
        name: String,
        relationship: String = "",
        countryCode: String = "",
        phone: String
    ) {
        viewModelScope.launch {
            try {
                val normalizedPhone = buildString {
                    if (countryCode.isNotBlank()) append(countryCode.trim()).append(" ")
                    append(phone.trim())
                }.trim()

                val entity = EmergencyContactEntity(
                    userId = userId,
                    name = name.trim(),
                    relationship = relationship.trim(),
                    phone = normalizedPhone
                )
                emergencyRepo.addContact(entity)
                // Success!
            } catch (e: Exception) {
                // Now you will actually catch errors if Firebase fails
                e.printStackTrace()
            }
        }
    }

    fun deleteContact(userId: Long, contact: EmergencyContactEntity) {
        viewModelScope.launch {
            emergencyRepo.deleteContact(contact)
        }
    }

    fun updateContact(updated: EmergencyContactEntity) {
        viewModelScope.launch {
            // Ensure we keep the ID and FirebaseID of the existing record
            // but force the correct userId
            val finalEntity = updated.copy(userId = userId)
            emergencyRepo.updateContact(finalEntity)
        }
    }

    // ----------------------------
    // Location & Insurance (Unchanged)
    // ----------------------------
    fun updateLocalNumbersByLocation(context: Context, lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (Geocoder.isPresent()) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, long, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val countryCode = addresses[0].countryCode
                        _localNumbers.value = EmergencyCountryData.getNumbers(countryCode)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _insurance = MutableStateFlow<InsurancePolicyEntity?>(null)
    val insurance = _insurance.asStateFlow()

    fun loadInsurance(userId: Long) {
        viewModelScope.launch {
            emergencyRepo.getInsurance(userId).collect { policy ->
                _insurance.value = policy
            }
        }
    }

    fun saveInsurance(
        userId: Long,
        provider: String,
        policyNo: String,
        phone: String,
        url: String
    ) {
        viewModelScope.launch {
            val newPolicy = InsurancePolicyEntity(
                id = _insurance.value?.id ?: 0L,
                userId = userId,
                providerName = provider.trim(),
                policyNumber = policyNo.trim(),
                emergencyPhone = phone.trim(),
                claimUrl = url.trim()
            )
            emergencyRepo.saveInsurance(newPolicy)
        }
    }

companion object {
    fun provideFactory(
        repo: EmergencyRepository,
        userId: Long
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EmergencyViewModel(repo, userId) as T
        }
    }
}
}