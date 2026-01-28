// ui/trip/JoinRequestsViewModel.kt
package com.example.tripshare.ui.trip

import androidx.lifecycle.*
import com.example.tripshare.data.db.*
import com.example.tripshare.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PendingRequestUI(
    val req: JoinRequestEntity,
    val user: UserEntity?,
    val tripName: String,
    val isTripFull: Boolean = false // New field
)

class JoinRequestsViewModel(
    private val tripDao: TripDao,
    private val userDao: UserDao,
    private val joinRequestDao: JoinRequestDao,
    private val tripId: Long
) : ViewModel() {

    // 2. Update the flow to check participant counts
    val pending: StateFlow<List<PendingRequestUI>> =
        joinRequestDao.observePending(tripId)
            .map { reqs ->
                // Fetch trip details to get the Max Limit
                val trip = tripDao.getTripById(tripId)
                // Fetch current participant count (assuming you have a method for this)
                val currentCount = tripDao.getParticipants(tripId).size

                // Check if full (Replace 'maxParticipants' with your actual TripEntity field name)
                val isFull = if (trip != null) currentCount >= trip.maxParticipants else false

                reqs.map { r ->
                    val user = userDao.findById(r.userId)
                    val tripName = trip?.name ?: "your trip"

                    // Pass the isFull status to the UI object
                    PendingRequestUI(
                        req = r,
                        user = user,
                        tripName = tripName,
                        isTripFull = isFull
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun accept(req: JoinRequestEntity) = viewModelScope.launch {
        // 3. SAFETY CHECK: Double check inside the function to prevent race conditions
        val trip = tripDao.getTripById(req.tripId)
        val currentParticipants = tripDao.getParticipants(req.tripId)

        // Change 'maxParticipants' to whatever your column name is (e.g., maxPeople, limit)
        if (trip != null && currentParticipants.size >= trip.maxParticipants) {
            // Optional: You could emit a Toast/Snackbar event here saying "Trip is full!"
            return@launch // STOP execution here
        }

        userDao.findById(req.userId)?.let { u ->
            val display = u.name.ifBlank { u.email }
            tripDao.insertParticipants(
                listOf(
                    TripParticipantEntity(
                        tripId = req.tripId,
                        userId = u.id,
                        displayName = display,
                        email = u.email
                    )
                )
            )
        }
        joinRequestDao.update(req.copy(status = "ACCEPTED"))
    }

    fun reject(req: JoinRequestEntity) = viewModelScope.launch {
        joinRequestDao.update(req.copy(status = "REJECTED"))
    }

    class Factory(
        private val tripDao: TripDao,
        private val userDao: UserDao,
        private val joinRequestDao: JoinRequestDao,
        private val tripId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            JoinRequestsViewModel(tripDao, userDao, joinRequestDao, tripId) as T
    }
}
