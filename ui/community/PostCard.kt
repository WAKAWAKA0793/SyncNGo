package com.example.tripshare.ui.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.tripshare.data.db.PostWithUser

@Composable
fun PostCard(
    postWithUser: PostWithUser,
    onClick: () -> Unit
) {
    val post = postWithUser.post
    val user = postWithUser.user

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // 1️⃣ IMAGE first (just like the screenshot)
            if (!post.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    // If the path is a raw file path (e.g. /data/user/...), Coil usually handles it.
                    // If not, you can force it like this: model = File(post.imageUrl)
                    model = post.imageUrl,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp, max = 260.dp)
                        .clip(RoundedCornerShape(12.dp)), // Changed to 12.dp to match XHS style
                    contentScale = ContentScale.Crop
                )
            }

            // 2️⃣ CAPTION (combined title + content)
            val caption = buildString {
                if (post.title.isNotBlank()) {
                    append(post.title.trim())
                    append(" ")
                }
                if (post.content.isNotBlank()) {
                    append(post.content.trim())
                }
            }.ifBlank { user.name } // fallback


            // 3️⃣ USER + LIKES row (bottom)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Avatar
                val painter = rememberAsyncImagePainter(user.profilePhoto)
                Image(
                    painter = painter,
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Username
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.weight(1f))

                // Likes
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Likes",
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = post.likes.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
