package com.example.tripshare.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import com.example.tripshare.data.model.CommentEntity
import com.example.tripshare.data.model.CommentWithUser
import com.example.tripshare.data.model.PostEntity
import com.example.tripshare.data.model.PostLikeEntity
import com.example.tripshare.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Update
    suspend fun update(post: PostEntity)

    @Delete
    suspend fun delete(post: PostEntity)

    @Transaction
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun observePosts(): Flow<List<PostWithUser>>

    @Query("SELECT * FROM posts WHERE firebaseId = :fid LIMIT 1")
    suspend fun getPostByFirebaseId(fid: String): PostEntity?

    // 👇 For foreign key relation with User
    @Transaction
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun observePostsWithUsers(): Flow<List<PostWithUser>>

    @Transaction
    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY id DESC")
    fun observePostsByUser(userId: Long): Flow<List<PostWithUser>>

    // legacy increment-style helpers (if used elsewhere)
    @Query("UPDATE posts SET likes = likes + 1 WHERE id = :postId")
    suspend fun likePost(postId: Long)

    @Query("UPDATE posts SET shares = shares + 1 WHERE id = :postId")
    suspend fun savePost(postId: Long)

    @Query("UPDATE posts SET comments = comments + 1 WHERE id = :postId")
    suspend fun addCommentCount(postId: Long)

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: Long): PostEntity?

    @Transaction
    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getPostWithUser(id: Long): PostWithUser?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLike(like: PostLikeEntity): Long

    @Query("DELETE FROM post_likes WHERE postId = :postId AND userId = :userId")
    suspend fun deleteLike(postId: Long, userId: Long): Int

    @Query("SELECT EXISTS(SELECT 1 FROM post_likes WHERE postId = :postId AND userId = :userId)")
    suspend fun hasUserLiked(postId: Long, userId: Long): Boolean

    @Query("UPDATE posts SET likes = likes + 1 WHERE id = :postId")
    suspend fun incrementLikes(postId: Long)

    @Query("UPDATE posts SET likes = likes - 1 WHERE id = :postId AND likes > 0")
    suspend fun decrementLikes(postId: Long)


    // --- New: delete by id helper ---
    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deleteById(postId: Long)
}

data class PostWithUser(
    @Embedded val post: PostEntity,
    @Relation(parentColumn = "userId", entityColumn = "id")
    val user: UserEntity,
)

@Dao
interface CommentDao {
    @Transaction
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun observeCommentsWithUser(postId: Long): Flow<List<CommentWithUser>>

    @Insert
    suspend fun insert(comment: CommentEntity)
    @Delete suspend fun delete(comment: CommentEntity)
}
