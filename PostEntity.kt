// data/model/PostEntity.kt
package com.example.tripshare.data.model
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // if user deleted → posts deleted
        )
    ],
    indices = [Index("userId")] // ✅ index for faster joins
)
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val firebaseId: String? = null,
    val userId: Long, // foreign key to UserEntity.id
    val userName: String,
    val title: String,
    val content: String,
    val text: String? = null,
    val userAvatar: String? = null,
    val location: String? = null,
    val timeAgo: String = "Just now",
    val imageUrl: String? = null,

    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,


)

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("postId")]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val postId: Long,
    val userId: Long,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CommentWithUser(
    @Embedded val comment: CommentEntity,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity
)

@Entity(
    tableName = "post_likes",
    primaryKeys = ["postId", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("postId"), Index("userId")]
)
data class PostLikeEntity(
    val postId: Long,
    val userId: Long
)
