package com.example.tripshare.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.db.PostWithUser
import com.example.tripshare.data.model.CommentEntity
import com.example.tripshare.data.model.CommentWithUser
import com.example.tripshare.data.repo.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PostViewModel(private val repo: PostRepository) : ViewModel() {
    val posts: Flow<List<PostWithUser>> = repo.observePosts()

    fun onLikePost(postId: Long, isLiked: Boolean, userId: Long) {
        viewModelScope.launch {
            repo.setPostLiked(postId, userId, isLiked)
        }
    }

    suspend fun isPostLiked(postId: Long, userId: Long): Boolean {
        return repo.isPostLikedByUser(postId, userId)
    }

    // Inside PostViewModel class
    fun onDeleteComment(comment: CommentEntity) {
        viewModelScope.launch {
            repo.deleteComment(comment)
        }
    }

    fun observeComments(postId: Long) =
        repo.observeComments(postId)



    fun onSavePost(postId: Long, isSaved: Boolean) {
        viewModelScope.launch {
            repo.savePost(postId)   // <-- no isSaved argument
        }
    }

    fun onDeletePost(postId: Long) {
        viewModelScope.launch {
            repo.deletePost(postId)
        }
    }


    fun comments(postId: Long): Flow<List<CommentWithUser>> = repo.observeComments(postId)

    fun addComment(postId: Long, userId: Long, text: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            repo.addComment(
                CommentEntity(
                    postId = postId,
                    userId = userId,
                    text = text,
                    timestamp = now
                )
            )
        }
    }

}