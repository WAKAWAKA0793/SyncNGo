package com.example.tripshare.ui.community

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tripshare.data.db.PostWithUser
import com.example.tripshare.data.model.CommentEntity
import com.example.tripshare.data.model.CommentWithUser
import com.example.tripshare.utils.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postWithUser: PostWithUser,
    vm: PostViewModel,
    currentUserId: Long,
    onBack: () -> Unit,
    onProfileClick: (Long) -> Unit = {}
) {
    val post = postWithUser.post
    val user = postWithUser.user
    val isOwnPost = currentUserId == user.id

    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(post.likes) }
    var saved by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }

    // ⭐ NEW: State to track which comment is being deleted
    var commentToDelete by remember { mutableStateOf<CommentEntity?>(null) }

    val comments by vm.observeComments(post.id).collectAsState(initial = emptyList())

    LaunchedEffect(post.id, currentUserId) {
        if (currentUserId != -1L) {
            isLiked = vm.isPostLiked(post.id, currentUserId)
        }
    }

    // ⭐ NEW: Delete Confirmation Dialog
    if (commentToDelete != null) {
        AlertDialog(
            onDismissRequest = { commentToDelete = null },
            title = { Text("Delete Comment") },
            text = { Text("Are you sure you want to delete this comment?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        commentToDelete?.let { vm.onDeleteComment(it) }
                        commentToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { commentToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ... (Header Item code remains the same) ...
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    val avatar = rememberAsyncImagePainter(user.profilePhoto ?: "")
                    if (!user.profilePhoto.isNullOrBlank()) {
                        Image(
                            painter = avatar, contentDescription = null,
                            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(50.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(user.name, style = MaterialTheme.typography.titleMedium)
                        Text(post.timeAgo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.weight(1f))
                    if (isOwnPost) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options")
                            }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(
                                    text = { Text("Delete Post", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showMenu = false
                                        vm.onDeletePost(post.id)
                                        onBack()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ... (Content Item code remains the same) ...
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    if (post.title.isNotBlank()) Text(post.title, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    if (post.content.isNotBlank()) Text(post.content)
                    Spacer(Modifier.height(12.dp))
                    if (!post.imageUrl.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(post.imageUrl), contentDescription = null,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp, max = 320.dp).clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // ... (Action Buttons Item code remains the same) ...
            item {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (currentUserId != -1L) {
                            val newStatus = !isLiked
                            isLiked = newStatus
                            if (newStatus) likeCount++ else likeCount--
                            vm.onLikePost(post.id, newStatus, currentUserId)
                        }
                    }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    if (likeCount > 0) {
                        Text(text = "$likeCount", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { saved = !saved }) {
                        Icon(imageVector = if (saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, contentDescription = "Save")
                    }
                }
            }

            item {
                Divider()
                Text("Comments", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            // ⭐ UPDATE: Pass onLongClick to CommentItem
            items(comments, key = { it.comment.id }) { commentWithUser ->
                CommentItem(
                    commentWithUser = commentWithUser,
                    currentUserId = currentUserId, // Need to know who is logged in
                    onProfileClick = { onProfileClick(commentWithUser.user.id) },
                    onLongClick = {
                        // Only set deletion state if it's the user's own comment
                        commentToDelete = commentWithUser.comment
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // ... (Add Comment Item remains the same) ...
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Add a comment…") },
                        maxLines = 3
                    )
                    IconButton(
                        enabled = newComment.isNotBlank() && currentUserId != -1L,
                        onClick = {
                            val textToSend = newComment
                            vm.addComment(post.id, currentUserId, textToSend)
                            newComment = ""
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send comment")
                    }
                }
            }
        }
    }
}

// Keep the fixed CommentItem
@OptIn(ExperimentalFoundationApi::class) // ⭐ Required for combinedClickable
@Composable
fun CommentItem(
    commentWithUser: CommentWithUser,
    currentUserId: Long,           // 1. Pass current user ID
    onProfileClick: () -> Unit,
    onLongClick: () -> Unit,       // 2. Add callback for long press
    modifier: Modifier = Modifier
) {
    val user = commentWithUser.user
    val comment = commentWithUser.comment

    // 3. Check if the logged-in user owns this comment
    val isOwner = currentUserId == user.id

    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (!user.profilePhoto.isNullOrBlank()) {
            Image(
                painter = rememberAsyncImagePainter(user.profilePhoto), contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onProfileClick() },
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                Icons.Default.Person, null,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onProfileClick() }
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier
                // 4. ⭐ Detect Long Press on the text area
                .combinedClickable(
                    onClick = { /* Optional: Handle normal click here */ },
                    onLongClick = {
                        if (isOwner) { // Only allow if user owns the comment
                            onLongClick()
                        }
                    }
                )
        ) {
            Text(user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(formatTime(comment.timestamp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}