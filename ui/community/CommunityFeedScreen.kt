package com.example.tripshare.ui.community

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tripshare.data.db.PostWithUser
import com.example.tripshare.data.model.CommentWithUser
import com.example.tripshare.data.model.PostEntity
import com.example.tripshare.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommunityFeedScreen(
    posts: List<PostWithUser>,
    currentUserId: Long,
    onBack: () -> Unit,
    onProfileClick: (Long) -> Unit,
    onCreatePost: () -> Unit,
    onPostClick: (Long) -> Unit,
    onLikeClick: (postId: Long, isLiked: Boolean) -> Unit,
    onSaveClick: (postId: Long, isSaved: Boolean) -> Unit,
    onDeletePost: (Long) -> Unit,
    onCommentSubmit: (postId: Long, text: String) -> Unit,
    observeComments: (Long) -> Flow<List<CommentWithUser>>
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Community, 1 = My Post

    val myPosts = remember(posts, currentUserId) {
        posts.filter { it.user.id == currentUserId }
    }
    val displayedPosts = if (selectedTab == 0) posts else myPosts

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Discover",fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                )

                // 🔺 Tabs: Community / My Post
                JournalTabBar(
                    selectedTab = selectedTab,
                    onTabChange = { selectedTab = it }
                )
            }
        },
        floatingActionButton = {
            // ➕ same add button as before
            FloatingActionButton(onClick = onCreatePost) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        }
    ) { inner ->
        // 🧳 Travel journal grid feed (2 columns like your screenshot)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = inner,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(displayedPosts, key = { it.post.id }) { postWithUser ->
                PostCard(
                    postWithUser = postWithUser,
                    onClick = { onPostClick(postWithUser.post.id) }
                )
            }
        }
    }
}


/**
 * Renders a single post, managing its own comment expansion state.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InstagramPostItem(
    postWithUser: PostWithUser,
    currentUserId: Long,
    onProfileClick: (Long) -> Unit,
    onPostClick: (Long) -> Unit,
    onLikeClick: (Long, Boolean) -> Unit,
    onSaveClick: (Long, Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onCommentSubmit: (postId: Long, text: String) -> Unit,
    observeComments: (Long) -> Flow<List<CommentWithUser>>
) {
    val post = postWithUser.post
    val user = postWithUser.user

    // ⭐️ Local optimistic UI state for like & save to avoid referencing missing props
    var isLikedState by rememberSaveable { mutableStateOf(false) }
    var isSavedState by rememberSaveable { mutableStateOf(false) }

    // ⭐️ ADDED: State for inline comments
    var commentsExpanded by rememberSaveable { mutableStateOf(false) }

    // ⭐️ ADDED: Observe comments ONLY when expanded
    val comments by if (commentsExpanded) {
        observeComments(post.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        PostHeader(
            user = user,
            isOwnPost = user.id == currentUserId,
            onProfileClick = { onProfileClick(user.id) },
            onDeleteClick = onDeleteClick
        )

        if (!post.imageUrl.isNullOrBlank()) {
            Image(
                painter = rememberAsyncImagePainter(post.imageUrl),
                contentDescription = "Post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 500.dp)
                    // Double-tap toggles local like state and calls callback
                    .combinedClickable(
                        onClick = { /* optional */ },
                        onDoubleClick = {
                            val newState = !isLikedState
                            isLikedState = newState
                            onLikeClick(post.id, newState)
                        }
                    ),
                contentScale = ContentScale.Crop
            )
        }

        PostActionBar(
            isLiked = isLikedState,
            isSaved = isSavedState,
            onLikeClick = {
                val newState = !isLikedState
                isLikedState = newState
                onLikeClick(post.id, newState)      // 👈 calls up to VM
            },
            onCommentClick = { commentsExpanded = !commentsExpanded },
            onSaveClick = {
                val newState = !isSavedState
                isSavedState = newState
                onSaveClick(post.id, newState)
            }
        )

        PostContent(
            post = post,
            user = user,
            onViewCommentsClick = { commentsExpanded = !commentsExpanded }
        )

        if (commentsExpanded) {
            CommentSection(
                comments = comments,
                onCommentSubmit = { text -> onCommentSubmit(post.id, text) },
                onProfileClick = onProfileClick
            )
        }
    }
}

/** 👤 Top bar: Avatar, Username, Delete button */
@Composable
private fun PostHeader(
    user: UserEntity,
    isOwnPost: Boolean, // ⭐️ ADDED
    onProfileClick: () -> Unit,
    onDeleteClick: () -> Unit // ⭐️ ADDED
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onProfileClick) // ⭐️ ADDED
                .padding(4.dp)
        ) {
            val avatarPainter = rememberAsyncImagePainter(user.profilePhoto ?: "")
            if (!user.profilePhoto.isNullOrBlank()) {
                Image(
                    painter = avatarPainter,
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(32.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.Person, "Avatar", modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // ⭐️ ADDED: Show "More" button only if it's the user's own post
        if (isOwnPost) {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, "More options")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PostActionBar(
    isLiked: Boolean,
    isSaved: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Like"
            )
        }

        IconButton(onClick = onCommentClick) {
            Icon(Icons.Outlined.ChatBubbleOutline, "Comment")
        }
        Spacer(Modifier.weight(1f))

        IconButton(onClick = onSaveClick) {
            Icon(
                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = "Save post"
            )
        }
    }
}


/** 💬 Content block: Likes, Caption, Comments link */
@Composable
private fun PostContent(
    post: PostEntity,
    user: UserEntity,
    onViewCommentsClick: () -> Unit // ⭐️ UPDATED
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (post.likes > 0) {
            Text(
                text = "${post.likes} likes",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        val caption = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(user.name)
            }
            append(" ")
            if (post.title.isNotBlank()) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(post.title)
                }
                append(" ")
            }
            append(post.content)
        }
        Text(text = caption, style = MaterialTheme.typography.bodyMedium)

        // ⭐️ UPDATED: "View comments" button
        if (post.comments > 0) {
            Text(
                text = "View all ${post.comments} comments",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onViewCommentsClick() }
            )
        } else {
            Text(
                text = "Add a comment...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onViewCommentsClick() }
            )
        }

        Text(
            text = post.timeAgo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** ⭐️ NEW COMPOSABLE: Inline Comment Section */
@Composable
private fun CommentSection(
    comments: List<CommentWithUser>,
    onProfileClick: (Long) -> Unit,
    onCommentSubmit: (text: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        Divider(modifier = Modifier.padding(bottom = 8.dp))

        // ⭐️ SHOW COMMENTS
        comments.forEach { item ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(item.user.profilePhoto ?: ""),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick(item.user.id) },
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(8.dp))

                Column {
                    Text(item.user.name, fontWeight = FontWeight.Bold)
                    Text(item.comment.text)
                }
            }
        }

        // ⭐ COMMENT INPUT at bottom
        CommentInput(
            onSubmit = onCommentSubmit
        )
    }
}





/** ⭐️ NEW COMPOSABLE: Comment input text field */
@Composable
private fun CommentInput(
    onSubmit: (text: String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Add a comment...") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                if (text.isNotBlank()) {
                    onSubmit(text)
                    text = ""
                    focusManager.clearFocus()
                }
            })
        )
        TextButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSubmit(text)
                    text = ""
                    focusManager.clearFocus()
                }
            },
            enabled = text.isNotBlank()
        ) {
            Text("Post")
        }
    }
}

@Composable
fun JournalTabBar(
    selectedTab: Int,
    onTabChange: (Int) -> Unit
) {
    val tabs = listOf("Public", "My Post")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            val interaction = remember { MutableInteractionSource() }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .clickable(
                        interactionSource = interaction,
                        indication = null
                    ) { onTabChange(index) }
            ) {
                // TEXT STYLE
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (isSelected) 18.sp else 16.sp,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // RED INDICATOR — SHORT & CENTERED (XHS STYLE)
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .width(26.dp)
                            .height(3.dp)
                            .background(
                                color = Color(0xFFFF2442), // XHS red
                                shape = RoundedCornerShape(50)
                            )
                    )
                } else {
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }
    }
}

