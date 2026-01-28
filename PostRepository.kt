package com.example.tripshare.data.repo

import com.example.tripshare.data.db.CommentDao
import com.example.tripshare.data.db.PostDao
import com.example.tripshare.data.db.PostWithUser
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.model.CommentEntity
import com.example.tripshare.data.model.CommentWithUser
import com.example.tripshare.data.model.PostEntity
import com.example.tripshare.data.model.PostLikeEntity
import com.example.tripshare.data.model.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PostRepository(
    private val dao: PostDao,
    private val userDao: UserDao,
    private val commentDao: CommentDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun observePosts(): Flow<List<PostWithUser>> = dao.observePosts()


    // ✅ 1. ADD POST (Modified to Upload to Cloud)
    suspend fun addPost(post: PostEntity): Long {
        // 1. Generate the Cloud ID immediately
        val newDocRef = firestore.collection("posts").document()
        val cloudId = newDocRef.id

        // 2. Save Locally with the Cloud ID already set
        val postWithId = post.copy(firebaseId = cloudId)
        val localId = dao.insert(postWithId)

        // 3. Upload to Cloud (Using .set() with the known ID)
        val cloudPost = postWithId.copy(id = localId)
        uploadPostToCloud(cloudPost, newDocRef.id)

        return localId
    }

    private fun uploadPostToCloud(post: PostEntity, docId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val author = userDao.getUserById(post.userId)
            val authorUid = author?.firebaseId ?: return@launch

            val map = hashMapOf(
                "authorUid" to authorUid,
                "authorName" to post.userName,
                "authorAvatar" to (post.userAvatar ?: ""),
                "title" to post.title,
                "content" to post.content,
                "location" to (post.location ?: ""),
                "imageUrl" to (post.imageUrl ?: ""),
                "timestamp" to System.currentTimeMillis(),
                "likes" to 0,
                "comments" to 0
            )

            // Use .set() because we already generated the ID
            firestore.collection("posts").document(docId).set(map)
        }
    }

    // ✅ 2. SYNC POSTS (Download from Cloud)
    fun startPostSync(scope: CoroutineScope) {
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) return@addSnapshotListener

                scope.launch(Dispatchers.IO) {
                    for (doc in snapshots.documents) {
                        val firebaseId = doc.id
                        val authorUid = doc.getString("authorUid") ?: continue

                        // 1. Check if post already exists locally
                        val existingPost = dao.getPostByFirebaseId(firebaseId)

                        // 2. Resolve User (Create stub if missing)
                        var localUserId = userDao.findByFirebaseId(authorUid)?.id
                        if (localUserId == null) {
                            val newStubUser = UserEntity(
                                firebaseId = authorUid,
                                name = doc.getString("authorName") ?: "Unknown",
                                profilePhoto = doc.getString("authorAvatar"),
                                email = "stub_$authorUid@tripshare.com"
                            )
                            localUserId = userDao.insertUser(newStubUser)
                        }

                        // 3. Prepare Data
                        // If it exists, use its ID (to update). If not, use 0 (to insert).
                        val post = PostEntity(
                            id = existingPost?.id ?: 0L,
                            firebaseId = firebaseId,
                            userId = localUserId,
                            userName = doc.getString("authorName") ?: "Unknown",
                            userAvatar = doc.getString("authorAvatar"),
                            title = doc.getString("title") ?: "",
                            content = doc.getString("content") ?: "",
                            imageUrl = doc.getString("imageUrl"),
                            likes = (doc.getLong("likes") ?: 0).toInt(),
                            comments = (doc.getLong("comments") ?: 0).toInt(),
                            timeAgo = "Recently" // Logic to calculate time can go here
                        )

                        // 4. Upsert (Insert or Update)
                        if (existingPost != null) {
                            dao.update(post) // Update existing row
                        } else {
                            dao.insert(post) // Insert new row
                        }
                    }
                }
            }
    }

    fun observePostsByUser(userId: Long): Flow<List<PostWithUser>> =
        dao.observePostsByUser(userId)

    suspend fun setPostLiked(postId: Long, userId: Long, isLiked: Boolean) {
        if (isLiked) {
            // Try insert; if already liked, row won't be inserted (IGNORE)
            val inserted = dao.insertLike(PostLikeEntity(postId = postId, userId = userId))
            if (inserted > 0) {
                dao.incrementLikes(postId)
            }
        } else {
            // Remove like; only decrement if a row was actually deleted
            val deleted = dao.deleteLike(postId, userId)
            if (deleted > 0) {
                dao.decrementLikes(postId)
            }
        }
    }

    suspend fun isPostLikedByUser(postId: Long, userId: Long): Boolean {
        return dao.hasUserLiked(postId, userId)
    }

    /**
     * Delete by id (ViewModel calls repo.deletePost(postId))
     * Uses DAO single-query delete for efficiency.
     */
    suspend fun deletePost(postId: Long) {
        dao.deleteById(postId)
    }




    suspend fun savePost(postId: Long) = dao.savePost(postId)

    suspend fun deleteComment(comment: CommentEntity) {
        commentDao.delete(comment)
    }

    fun observeComments(postId: Long): Flow<List<CommentWithUser>> {
        return commentDao.observeCommentsWithUser(postId)
    }

    suspend fun addComment(comment: CommentEntity) = commentDao.insert(comment)
}
