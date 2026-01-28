package com.example.tripshare.ui.trip

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.db.JoinRequestDao
import com.example.tripshare.data.db.LocalNotificationDao
import com.example.tripshare.data.db.TripDao
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.db.WaitlistDao
import com.example.tripshare.data.model.JoinRequestEntity
import com.example.tripshare.data.model.LocalNotificationEntity
import com.example.tripshare.data.model.OrganizerRating
import com.example.tripshare.data.model.RatingSummary
import com.example.tripshare.data.model.TripDetailsUiModel
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.WaitlistEntity
import com.example.tripshare.data.repo.ReviewRepository
import com.example.tripshare.data.repo.TripRepository
import com.example.tripshare.ui.notifications.NotificationHelper
import com.example.tripshare.ui.notifications.NotificationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripDetailsViewModel(
    private val tripDao: TripDao,
    private val userDao: UserDao,
    private val waitlistDao: WaitlistDao,
    private val joinRequestDao: JoinRequestDao,
    private val repo: TripRepository,
    private val tripId: Long,
    private val context: Context,
    private val notifVm: NotificationViewModel,
    private val currentUserId: Long,
    private val localNotificationDao: LocalNotificationDao,
    private val reviewRepo: ReviewRepository
) : ViewModel() {

    private val _joining = MutableStateFlow(false)
    val joining: StateFlow<Boolean> = _joining

    private val _hasRequested = MutableStateFlow(false)
    val hasRequested: StateFlow<Boolean> = _hasRequested

    // ✅ Init block to schedule reminder safely
    init {
        viewModelScope.launch {
            // 1. Sync the participants list (Fixes missing members in UI)
            repo.startParticipantSync(tripId, this)

            // 2. Schedule notification if applicable
            if (currentUserId != -1L) {
                val trip = tripDao.getTripById(tripId)
                if (trip != null && trip.startDate != null) {
                    val startMillis = trip.startDate.atStartOfDay(java.time.ZoneId.systemDefault())
                        .toInstant().toEpochMilli()

                    notifVm.scheduleTripStartReminder(
                        tripId = trip.id,
                        currentUserId = currentUserId,
                        tripTitle = trip.name,
                        startAtMillis = startMillis
                    )
                }
            }
        }
    }
    private val _ratingSummary = MutableStateFlow<RatingSummary?>(null)
    val ratingSummary: StateFlow<RatingSummary?> = _ratingSummary.asStateFlow()

    fun loadOrganizerRating(organizerId: Long) {
        viewModelScope.launch {
            _ratingSummary.value = reviewRepo.getRatingSummary(organizerId)
        }
    }

    // ✅ Check both Waitlist and JoinRequest tables so the button state persists
    fun refreshRequestState(userId: Long) = viewModelScope.launch {
        val inWaitlist = waitlistDao.observeWaitlist(tripId).firstOrNull()?.any { it.userId == userId } == true
        val inRequests = joinRequestDao.exists(tripId, userId)
        _hasRequested.value = inWaitlist || inRequests
    }


    val tripUiModel: StateFlow<TripDetailsUiModel?> =
        tripDao.observeTripFull(tripId)
            .map { aggregate ->
                aggregate?.let { agg ->
                    // 1️⃣ Find OWNER participant (this was missing)
                    val ownerParticipant = agg.participants
                        .firstOrNull { it.role == com.example.tripshare.data.model.ParticipantRole.OWNER }

// 2️⃣ Resolve local UserEntity using userId
                    val ownerUser = ownerParticipant?.userId?.let { uid ->
                        userDao.getUserById(uid)
                    }

// 3️⃣ Resolve name & avatar safely
                    val organizerNameResolved =
                        ownerUser?.name?.takeIf { it.isNotBlank() }
                            ?: ownerParticipant?.displayName?.takeIf { it.isNotBlank() }
                            ?: ownerUser?.email?.takeIf { it.isNotBlank() }
                            ?: "Trip Host"

                    val organizerAvatarResolved =
                        ownerUser?.profilePhoto
                            ?: "https://i.pravatar.cc/50?u=${agg.trip.organizerId}"

                    TripDetailsUiModel(
                        trip = agg.trip,
                        participants = agg.participants,
                        meetingPoints = agg.meetingPoints,
                        itinerary = agg.itinerary,
                        organizerRating = OrganizerRating(
                            organizerName = organizerNameResolved,
                            avatarUrl = organizerAvatarResolved,
                            averageRating = 4.9,
                            reviewCount = 120
                        )
                    )

                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private fun humanizeCategory(trip: TripEntity?): String =
        trip?.category?.name
            ?.replace('_', ' ')
            ?.lowercase()
            ?.replaceFirstChar { it.titlecase(java.util.Locale.getDefault()) }
            ?: "N/A"

    fun joinTrip(
        userId: Long,
        email: String,
        displayName: String,
        onJoined: () -> Unit,
        onWaitlist: (WaitlistEntity) -> Unit
    ) {
        viewModelScope.launch {
            _joining.value = true
            try {
                val trip = tripDao.getTripById(tripId)
                val participants = tripDao.observeParticipants(tripId).first()
                val waitlist = waitlistDao.observeWaitlist(tripId).firstOrNull() ?: emptyList()
                val max = trip?.maxParticipants ?: 0

                // 1. Check if they are already a full participant (stop if true)
                if (participants.any { it.userId == userId }) {
                    _joining.value = false
                    return@launch
                }

                // 2. Check if they are currently on the waitlist
                val isOnWaitlist = waitlist.any { it.userId == userId }

                if (participants.size < max) {
                    // ✅ SLOT AVAILABLE: Promote them from Waitlist -> Participant

                    if (isOnWaitlist) {
                        // Remove from waitlist first
                        waitlistDao.deleteByUserId(tripId, userId)
                    }

                    // Add to participants
                    if (trip?.firebaseId != null) {
                        repo.joinTrip(
                            tripId = tripId,
                            tripFirebaseId = trip.firebaseId, // 👈 Required for Cloud
                            myUserId = userId,
                            myName = displayName,
                            myEmail = email
                        )
                    }
                    NotificationHelper.showNotification(
                        context = context,
                        channelId = NotificationHelper.CHANNEL_TRIP_REMINDERS,
                        id = System.currentTimeMillis().toInt(),
                        title = "You joined the trip!",
                        body = "Welcome to ${trip?.name}."
                    )

                    // Refresh the "Has Requested" state to unlock the button
                    _hasRequested.value = false
                    onJoined()

                } else {
                    // ⛔ TRIP IS FULL

                    if (isOnWaitlist) {
                        // If already on waitlist, just stop
                        _joining.value = false
                        return@launch
                    }

                    // Add to waitlist logic...
                    val wait = WaitlistEntity(
                        tripId = tripId,
                        tripName = trip?.name ?: "Trip",
                        location = humanizeCategory(trip),
                        date = trip?.startDate?.toString() ?: "N/A",
                        position = participants.size + 1,
                        alertsEnabled = true,
                        userId = userId
                    )
                    waitlistDao.insert(wait)

                    // Update state to disable button
                    _hasRequested.value = true
                    onWaitlist(wait)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _joining.value = false
            }
        }
    }

    /** PRIVATE trips → create a waitlist entry immediately (request flow). */
    fun requestToJoin(
        userId: Long,
        tripId: Long,
        ownerId: Long,
        email: String,
        displayName: String,
        onRequested: () -> Unit
    ) {
        viewModelScope.launch {
            // ✅ FIX: Start loading
            _joining.value = true

            try {
                // 1. Fetch trip details to populate WaitlistEntity correctly
                val trip = tripDao.getTripById(tripId) ?: return@launch

                joinRequestDao.insert(
                    JoinRequestEntity(
                        tripId = tripId,
                        userId = userId,
                        status = "PENDING"
                    )
                )
                // B. Insert Notification for the HOST
                localNotificationDao.insert(
                    LocalNotificationEntity(
                        recipientId = ownerId,
                        type = "JOIN_REQUEST",
                        title = "Join request from $displayName",
                        body = "$displayName wants to join your trip '${trip.name}'.",
                        relatedId = tripId,
                        timestamp = System.currentTimeMillis()
                    )
                )

                // ✅ FIX: Update state immediately so button disables
                _hasRequested.value = true
                onRequested()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // ✅ FIX: Stop loading even if there is an error
                _joining.value = false
            }
        }
    }

    // Factory
    class TripDetailsVmFactory(
        private val tripDao: TripDao,
        private val repo: TripRepository,
        private val userDao: UserDao,
        private val waitlistDao: WaitlistDao,
        private val joinRequestDao: JoinRequestDao,
        private val localNotificationDao: LocalNotificationDao,
        private val tripId: Long,
        private val context: Context,
        private val reviewRepo: ReviewRepository,
        private val notifVm: NotificationViewModel,
        private val currentUserId: Long
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return TripDetailsViewModel(
                tripDao = tripDao,
                userDao = userDao,
                repo = repo,
                waitlistDao = waitlistDao,
                joinRequestDao = joinRequestDao,
                tripId = tripId,
                reviewRepo = reviewRepo,
                context = context,
                localNotificationDao = localNotificationDao,
                notifVm = notifVm,
                currentUserId = currentUserId
            ) as T
        }
    }
}