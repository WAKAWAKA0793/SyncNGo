package com.example.tripshare.data.repo

import com.example.tripshare.data.db.ReviewDao
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.model.RatingSummary
import com.example.tripshare.data.model.ReviewEntity
import com.example.tripshare.data.model.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReviewRepository(
    private val dao: ReviewDao,
    private val userDao: UserDao, // 👈 Inject this
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun observeReviewsForUser(userId: Long) = dao.observeReviewsForUser(userId)

    // ✅ 1. SYNC: Listen for reviews about a specific user
    fun startReviewSync(targetUserId: Long, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            // Get the Firebase ID (Email/UID) of the profile owner
            val targetUser = userDao.getUserById(targetUserId) ?: return@launch
            val targetFid = targetUser.firebaseId ?: return@launch

            firestore.collection("users").document(targetFid).collection("reviews")
                .orderBy("timestamp")
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener

                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshots.documents) {
                            val fid = doc.id
                            val reviewerFid = doc.getString("reviewerUid")
                            val text = doc.getString("text") ?: ""
                            val rating = (doc.getLong("rating") ?: 5).toInt()
                            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                            // 1. Find Local Reviewer ID
                            // If reviewer not found locally, create a stub or ignore.
                            var localReviewerId = 0L
                            var reviewerName = "Unknown"
                            var reviewerAvatar = ""

                            if (reviewerFid != null) {
                                var reviewer = userDao.findByFirebaseId(reviewerFid)
                                if (reviewer == null) {
                                    // Create Stub User so Foreign Key doesn't fail
                                    val stub = UserEntity(
                                        firebaseId = reviewerFid,
                                        name = doc.getString("reviewerName") ?: "User",
                                        profilePhoto = doc.getString("reviewerAvatar")
                                    )
                                    localReviewerId = userDao.insertUser(stub)
                                    reviewerName = stub.name
                                    reviewerAvatar = stub.profilePhoto ?: ""
                                } else {
                                    localReviewerId = reviewer.id
                                    reviewerName = reviewer.name
                                    reviewerAvatar = reviewer.profilePhoto ?: ""
                                }
                            }

                            // 2. Upsert Review
                            val existing = dao.getByFirebaseId(fid)
                            val entity = ReviewEntity(
                                id = existing?.id ?: 0L,
                                firebaseId = fid,
                                targetUserId = targetUserId,
                                reviewerId = localReviewerId,
                                reviewerName = reviewerName,
                                reviewerAvatarUrl = reviewerAvatar,
                                rating = rating,
                                text = text,
                                timeAgo = "Recently", // You can calculate this from timestamp
                                timestamp = timestamp
                            )

                            if (existing != null) {
                                dao.update(entity)
                            } else {
                                dao.insert(entity)
                            }
                        }
                    }
                }
        }
    }

    // ✅ 2. UPLOAD: Save to Cloud
    suspend fun submitReview(review: ReviewEntity) {
        // 1. Get IDs
        val targetUser = userDao.getUserById(review.targetUserId) ?: return
        val targetFid = targetUser.firebaseId ?: return // Cannot review local-only users

        val reviewer = userDao.getUserById(review.reviewerId) ?: return
        val reviewerFid = reviewer.firebaseId ?: return

        // 2. Save Local (Optimistic)
        val localId = dao.insert(review)

        // 3. Upload
        val docRef = firestore.collection("users").document(targetFid)
            .collection("reviews").document() // Auto-ID

        val map = hashMapOf(
            "reviewerUid" to reviewerFid,
            "reviewerName" to review.reviewerName,
            "reviewerAvatar" to review.reviewerAvatarUrl,
            "rating" to review.rating,
            "text" to review.text,
            "timestamp" to System.currentTimeMillis()
        )

        docRef.set(map).await()

        // Update local with Firebase ID
        dao.update(review.copy(id = localId, firebaseId = docRef.id))
    }

    suspend fun editReview(review: ReviewEntity) {
        // 1. Update Local Room DB
        dao.update(review)

        // 2. Update Firebase (if it exists in the cloud)
        if (review.firebaseId != null) {
            val targetUser = userDao.getUserById(review.targetUserId) ?: return
            val targetFid = targetUser.firebaseId ?: return

            // Path: users/{targetUserFid}/reviews/{reviewFid}
            firestore.collection("users").document(targetFid)
                .collection("reviews").document(review.firebaseId)
                .update(
                    mapOf(
                        "rating" to review.rating,
                        "text" to review.text,
                        // Optional: "timestamp" to System.currentTimeMillis() if you want to update the time
                    )
                ).await()
        }
    }

    // ✅ DELETE: Ensure this function is correct (already mostly present in your file)
    suspend fun deleteReview(reviewId: Long) {
        val review = dao.getById(reviewId) ?: return // Get details before deleting

        // 1. Delete Local
        dao.deleteById(reviewId)

        // 2. Delete Cloud
        if (review.firebaseId != null) {
            val targetUser = userDao.getUserById(review.targetUserId) ?: return
            val targetFid = targetUser.firebaseId ?: return

            firestore.collection("users").document(targetFid)
                .collection("reviews").document(review.firebaseId)
                .delete()
                .await()
        }
    }

    suspend fun getRatingSummary(userId: Long): RatingSummary {
        return dao.getRatingSummary(userId) ?: RatingSummary(0.0, 0)
    }




    /** Insert a new review (used by ReviewViewModel.submitReview). */
    suspend fun insertReview(review: ReviewEntity) {
        dao.insert(review)
    }

    /** Optional helper: clear all reviews for a user. */
    suspend fun deleteReviewsForUser(userId: Long) {
        dao.deleteForUser(userId)
    }

    suspend fun updateReview(review: ReviewEntity) {
        dao.update(review)
    }


}
