package com.example.tripshare.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.db.PostWithUser
import com.example.tripshare.data.db.ReviewWithReviewer
import com.example.tripshare.data.db.TripHistoryDao
import com.example.tripshare.data.model.ReviewEntity
import com.example.tripshare.data.model.UserEntity
import com.example.tripshare.data.repo.PostRepository
import com.example.tripshare.data.repo.ReviewRepository
import com.example.tripshare.data.repo.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class TripHistoryUi(
    val tripId: Long,
    val title: String,
    val locationLabel: String,
    val monthLabel: String,     // e.g. "Nov 2023"
    val sharedAmount: String?,  // e.g. "1200"
    val statusLabel: String     // e.g. "Completed" / "Upcoming"
)

data class TravelPostUi(
    val id: Long,
    val imageUrl: String,
    val caption: String,
    val locationLabel: String?,
    val createdLabel: String    // e.g. "2d", "Aug 2024"
)

class ProfileViewModel(
    private val postRepo: PostRepository,
    private val repo: UserRepository,
    private val tripHistoryDao: TripHistoryDao,
    private val reviewRepo: ReviewRepository
) : ViewModel() {

    // --- User ---

    fun observeUser(userId: Long): Flow<UserEntity?> {
        return repo.observeUserById(userId)
    }

    fun loadProfile(userId: Long) {
        viewModelScope.launch {
            // Start listening for reviews about this user
            reviewRepo.startReviewSync(userId, viewModelScope)
        }
    }
    // Inside ProfileViewModel.kt

    fun blockUser(targetUserId: Long) {
        viewModelScope.launch {
            // TODO: Call your repository to save this block to the database
            // repository.blockUser(currentUserId, targetUserId)

            // For now, we just simulate the action
            println("User $targetUserId has been blocked.")
        }
    }
    // --- Posts ---

    fun observeUserPosts(userId: Long): Flow<List<PostWithUser>> =
        postRepo.observePostsByUser(userId)

    fun observeTravelPosts(userId: Long): Flow<List<TravelPostUi>> {
        return postRepo.observePostsByUser(userId)   // Flow<List<PostWithUser>>
            .map { list ->
                list.map { pw ->
                    val post = pw.post
                    TravelPostUi(
                        id = post.id,
                        imageUrl = post.imageUrl ?: "",
                        // Right now we just reuse title as caption.
                        // Later you can switch to post.caption if you add it.
                        caption = post.title ?: "",
                        locationLabel = null,      // TODO: map from post if you have a field
                        createdLabel = ""          // TODO: format created time if available
                    )
                }
            }
    }

    // --- Trip history ---

    fun observeTripHistory(userId: Long): Flow<List<TripHistoryUi>> {
        return tripHistoryDao.observePastTrips(userId)
            .map { trips ->
                trips.map { trip ->
                    TripHistoryUi(
                        tripId = trip.id,
                        // Placeholder values – adjust when you know TripEntity fields
                        title = "Trip #${trip.id}",
                        locationLabel = "",
                        monthLabel = formatMonth(trip.startDate),
                        sharedAmount = null,
                        statusLabel = if (trip.isArchived) "Completed" else "Active"
                    )
                }
            }
    }

    // --- Updates ---
    fun observeReviews(userId: Long): Flow<List<ReviewWithReviewer>> = // <--- Was ReviewEntity
        reviewRepo.observeReviewsForUser(userId)

    fun updateAvatar(userId: Long, url: String) {
        viewModelScope.launch { repo.setAvatarUrl(userId, url) }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            repo.updateUser(user)
        }
    }

    // In ProfileViewModel.kt
    fun updateReview(reviewId: Long, newRating: Int, newText: String) {
        viewModelScope.launch {
            // We need the original review object to keep IDs consistent.
            // For now, we assume the UI passes the original entity or we construct a copy.
            // Ideally, you pass the whole 'ReviewEntity' from the UI.
        }
    }

    // Better approach for the ViewModel:
    // In ProfileViewModel.kt

    fun editReview(originalReview: ReviewEntity, newRating: Int, newText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Create a copy of the review with new data
            val updatedReview = originalReview.copy(
                rating = newRating,
                text = newText
            )
            // Call the repository function we made in Step 1
            reviewRepo.editReview(updatedReview)
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            reviewRepo.deleteReview(reviewId)
        }
    }

    // Helper to turn millis → "Nov 2023"
    private fun formatMonth(date: LocalDate?): String {
        if (date == null) return ""   // or "Unknown"

        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        return "$month ${date.year}"  // e.g. "Nov 2023"
    }
}