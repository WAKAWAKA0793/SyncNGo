// ui/group/TripGroupViewModel.kt
package com.example.tripshare.ui.group

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.model.ItineraryItemEntity
import com.example.tripshare.data.model.PollEntity
import com.example.tripshare.data.model.PollVoterDetail
import com.example.tripshare.data.model.ReviewEntity
import com.example.tripshare.data.model.VoteOptionEntity
import com.example.tripshare.data.repo.ReviewRepository
import com.example.tripshare.data.repo.TripRepository
import com.example.tripshare.data.repo.VoteRepository
import com.example.tripshare.ui.notifications.NotificationScheduler
import com.example.tripshare.ui.notifications.buildJoinRequestActionIntents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ============== UI holder for group tab ==============
data class TripGroupUi(
    val votes: List<PollEntity> = emptyList(),
    val itinerary: List<ItineraryItemEntity> = emptyList()
)

class ReviewViewModel(
    private val repo: ReviewRepository,
    private val userDao: UserDao
    // Remove 'currentUserId' from constructor to avoid stale ID issues
) : ViewModel() {

    // ✅ UPDATED: Pass reviewerId as a parameter here
    // inside ReviewViewModel
    fun submitReview(targetUserId: Long, reviewerId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            val reviewer = userDao.findById(reviewerId) // you already use this pattern in MainActivity :contentReference[oaicite:6]{index=6}

            val review = ReviewEntity(
                targetUserId = targetUserId,
                reviewerId = reviewerId,
                reviewerName = reviewer?.name ?: "Unknown",
                reviewerAvatarUrl = reviewer?.profilePhoto ?: "",
                rating = rating,
                timeAgo = "Just now",
                text = comment
            )

            repo.insertReview(review)
        }
    }

}

// ============== Voting (poll) ViewModel ==============
class VoteViewModel(
    private val repo: VoteRepository,
    private val tripId: Long
) : ViewModel() {

    val polls: StateFlow<List<PollEntity>> = repo.observePolls(tripId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    init {
        repo.startSync(tripId, viewModelScope)
    }
    fun createPoll(question: String, options: List<String>, allowMultiple: Boolean) {
        viewModelScope.launch {
            repo.createPoll(tripId, question, allowMultiple, options)
        }
    }

    // ✅ NEW: Update existing poll
    fun updatePoll(pollId: Long, question: String, options: List<String>, allowMultiple: Boolean) {
        viewModelScope.launch {
            repo.updatePoll(pollId, question, options, allowMultiple)
        }
    }

    // ✅ NEW: Delete poll
    fun deletePoll(pollId: Long) {
        viewModelScope.launch {
            repo.deletePoll(pollId)
        }
    }
    // In VoteViewModel class
    fun getVoters(pollId: Long): Flow<List<PollVoterDetail>> = repo.observeVoters(pollId)

    fun getOptions(pollId: Long): Flow<List<VoteOptionEntity>> =
        repo.observeOptions(pollId)

    fun vote(poll: PollEntity, optionId: Long, userId: Long) {
        viewModelScope.launch {
            repo.vote(poll, optionId, userId)
        }
    }
}


// ============== Group + Notifications ViewModel ==============
// Note: TripRepository removed – it wasn't used for anything valid yet.
class TripGroupViewModel(
    private val appContext: Context,
    private val tripRepo: TripRepository
) : ViewModel() {

    /**
     * User requests to join a private group (trip).
     * Currently:
     *  - just sends a notification with Accept / Decline actions.
     * Later you can add DB logic (JoinRequestEntity) here if you want.
     *
     * @param tripId        the private group / trip id
     * @param requesterId   current user id
     * @param requesterName name shown in notification
     */
    fun requestJoinPrivateGroup(
        tripId: Long,
        ownerId: Long,      // 👈 ADD THIS (Trip Host ID)
        requesterId: Long,
        requesterName: String
    ) {
        viewModelScope.launch {
            // 1️⃣ Build notification actions
            val notifId = buildJoinRequestNotifId(tripId, requesterId)

            val (acceptPending, declinePending) = buildJoinRequestActionIntents(
                context = appContext,
                notifId = notifId,
                groupId = tripId,
                requesterId = requesterId, // 👈 FIX: Use the 'requesterId' parameter
                currentUserId = ownerId
            )

            // 2️⃣ Show join-request notification
            NotificationScheduler.notifyJoinRequest(
                context = appContext,
                notifId = notifId,
                groupId = tripId,
                requesterName = requesterName,
                acceptActionIntent = acceptPending,
                declineActionIntent = declinePending
            )
        }
    }

    fun updateTripImage(tripId: Long, uriString: String) {
        viewModelScope.launch {
            try {
                // 1. Take persistable permission so we can read this URI later
                val uri = uriString.toUri()
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                appContext.contentResolver.takePersistableUriPermission(uri, flag)

                // 2. Update DB
                tripRepo.updateTripImage(tripId, uriString)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Optional helper: cancel a join request notification (if you need it)
     */
    fun cancelJoinRequestNotification(tripId: Long, requesterId: Long) {
        val notifId = buildJoinRequestNotifId(tripId, requesterId)
        NotificationScheduler.cancelNotification(appContext, notifId)
    }

    // --- helper to generate a stable notification ID for join requests ---
    private fun buildJoinRequestNotifId(groupId: Long, requesterId: Long): Int =
        ("4${groupId}_${requesterId}").hashCode()
}

// ============== ViewModel factories if you don't use Hilt ==============
class VoteViewModelFactory(
    private val repo: VoteRepository,
    private val tripId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoteViewModel::class.java)) {
            return VoteViewModel(repo, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class TripGroupViewModelFactory(
    private val appContext: Context,
    private val repository: TripRepository // ✅ Add Repository here
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripGroupViewModel::class.java)) {
            // ✅ Pass repository to the ViewModel
            return TripGroupViewModel(appContext, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}