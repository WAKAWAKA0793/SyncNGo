package com.example.tripshare.ui.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.tripshare.data.db.PostWithUser
import com.example.tripshare.data.db.ReviewWithReviewer
import com.example.tripshare.data.model.ReviewEntity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    vm: ProfileViewModel,
    userId: Long,
    onBack: () -> Unit,
    currentUserId: Long,
    onReport: (Long) -> Unit,
    onPostClick: (Long) -> Unit,
    onOpenProfile: (Long) -> Unit
)
{
    val user by vm.observeUser(userId).collectAsState(initial = null)
    val tripHistory by vm.observeTripHistory(userId).collectAsState(initial = emptyList())
    val userPosts by vm.observeUserPosts(userId).collectAsState(initial = emptyList())

    // This flow now returns List<ReviewWithReviewer>
    val reviews by vm.observeReviews(userId).collectAsState(initial = emptyList())

    val scrollState = rememberScrollState()

    // --- Dialog states ---
    var showBlockDialog by remember { mutableStateOf(false) }
    var showReviewsPopup by remember { mutableStateOf(false) }
    var showTripHistoryPopup by remember { mutableStateOf(false) }
    var reviewToEdit by remember { mutableStateOf<ReviewEntity?>(null) }
    // --- Block User Dialog ---
    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = { Text("Block this user?") },
            text = { Text("You will no longer see their posts, trips, or messages. This action cannot be easily undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.blockUser(userId)
                        showBlockDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Block") }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (reviewToEdit != null) {
        EditReviewDialog(
            review = reviewToEdit!!,
            onDismiss = { reviewToEdit = null },
            onConfirm = { r, newRating, newText ->
                vm.editReview(r, newRating, newText) // Call ViewModel
                reviewToEdit = null
            }
        )
    }

    // --- Reviews Popup ---
    if (showReviewsPopup) {
        ReviewsListDialog(
            reviews = reviews,
            currentUserId = currentUserId, // ✅ Pass ID
            onDismiss = { showReviewsPopup = false },
            onReviewerClick = { reviewerId ->
                showReviewsPopup = false
                onOpenProfile(reviewerId)
            },
            onEdit = { review -> reviewToEdit = review }, // ✅ Handle Edit
            onDelete = { review -> vm.deleteReview(review.id) } // ✅ Handle Delete
        )
    }
    // --- Trip History Popup (NEW) ---
    if (showTripHistoryPopup) {
        TripHistoryDialog(
            tripHistory = tripHistory,
            onDismiss = { showTripHistoryPopup = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        user?.let { u ->
            val avatar = (u.profilePhoto ?: "https://i.pravatar.cc/160?u=${u.id}") + "?v=${u.createdAt}"
            val tagline = if (u.bio.isNotBlank()) u.bio else "Adventure seeker & budget traveler"
            val year = java.time.Instant.ofEpochMilli(u.createdAt)
                .atZone(java.time.ZoneId.systemDefault()).year
            val joinedText = "Joined $year"

            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ===== Header Card =====
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = avatar,
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .border(
                                            BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
                                            CircleShape
                                        )
                                )
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = u.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = tagline,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                joinedText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(onClick = { onReport(userId) }) { Text("Report") }
                                OutlinedButton(onClick = { showBlockDialog = true }) { Text("Block") }
                            }
                        }
                    }
                }

                // ✅ Trips Joined card (click opens travel history popup)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SmallStatCard(
                        title = "Trips Joined",
                        value = tripHistory.size.toString(),
                        subtitle = "Tap to view history",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showTripHistoryPopup = true }
                    )
                }

                // ===== Travel Posts =====
                Text("Travel Posts", style = MaterialTheme.typography.titleMedium)

                val postsWithImage = userPosts.filter { !it.post.imageUrl.isNullOrBlank() }

                if (postsWithImage.isEmpty()) {
                    Text(
                        "No travel posts yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    TravelPostGrid(
                        posts = postsWithImage,
                        onPostClick = onPostClick
                    )
                }

                // ===== Reviews =====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reviews", style = MaterialTheme.typography.titleMedium)
                    if (reviews.isNotEmpty()) {
                        Text(
                            "See All (${reviews.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { showReviewsPopup = true }
                        )
                    }
                }

                if (reviews.isEmpty()) {
                    Text(
                        "No reviews yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    CompactRatingSummary(reviews = reviews, onClick = { showReviewsPopup = true })
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

// ==========================================
// POPUPS
// ==========================================

@Composable
fun TripHistoryDialog(
    tripHistory: List<TripHistoryUi>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Travel History (${tripHistory.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider()

                if (tripHistory.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No trips yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tripHistory, key = { it.tripId }) { h ->
                            val tripName = h.title.takeIf { it.isNotBlank() }
                                ?: h.locationLabel.takeIf { it.isNotBlank() }
                                ?: "Trip"

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            tripName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            h.monthLabel, // ✅ show date here
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    StatusPill(h.statusLabel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog that shows the full list of reviews
 */
@Composable
fun ReviewsListDialog(
    reviews: List<ReviewWithReviewer>,
    currentUserId: Long, // <--- Receive Viewer ID
    onDismiss: () -> Unit,
    onReviewerClick: (Long) -> Unit,
    onEdit: (ReviewEntity) -> Unit,   // <--- Callback
    onDelete: (ReviewEntity) -> Unit  // <--- Callback
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ... Header Row (same as before) ...
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reviews (${reviews.size})", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }

                Divider()

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(reviews) { wrapper ->
                        val review = wrapper.review
                        val user = wrapper.reviewer
                        val displayName = user?.name ?: review.reviewerName
                        val displayAvatar = user?.profilePhoto ?: review.reviewerAvatarUrl

                        // ✅ Check ownership
                        val isMyReview = (review.reviewerId == currentUserId)

                        ReviewCard(
                            avatarUrl = displayAvatar,
                            name = displayName,
                            date = review.timeAgo,
                            stars = review.rating,
                            text = review.text,
                            isOwner = isMyReview, // <--- Flag
                            onProfileClick = { onReviewerClick(review.reviewerId) },
                            onEdit = { onEdit(review) },
                            onDelete = { onDelete(review) }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// COMPACT REVIEWS UI
// ==========================================

@Composable
private fun CompactRatingSummary(
    reviews: List<ReviewWithReviewer>, // ✅ CHANGED: Accepts Relation class
    onClick: () -> Unit
) {
    val total = reviews.size
    // ✅ Updated to access inner 'review' entity
    val avg = if (total > 0) reviews.map { it.review.rating }.average() else 0.0
    val counts = (1..5).associateWith { star -> reviews.count { it.review.rating == star } }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.width(80.dp)
            ) {
                Text(
                    text = String.format("%.1f", avg),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "out of 5",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                (5 downTo 1).forEach { star ->
                    CompactRatingBarRow(star, counts[star] ?: 0, total)
                }
            }
        }
    }
}

@Composable
private fun CompactRatingBarRow(star: Int, count: Int, total: Int) {
    val fraction = if (total > 0) count.toFloat() / total.toFloat() else 0f
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$star", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(12.dp))
        Spacer(Modifier.width(6.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}

// ==========================================
// HELPERS
// ==========================================

@Composable
private fun SmallStatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ReviewCard(
    avatarUrl: String,
    name: String,
    date: String,
    stars: Int,
    text: String,
    isOwner: Boolean, // <--- New Param
    onProfileClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // --- Avatar & Name ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onProfileClick() }
                ) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(name, style = MaterialTheme.typography.titleSmall)
                        Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // --- Stars OR Menu ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★".repeat(stars), color = MaterialTheme.colorScheme.primary)

                    if (isOwner) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                          DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                              DropdownMenuItem(text = { Text("Edit") }, onClick = { onEdit() })
                              DropdownMenuItem(text = { Text("Delete") }, onClick = { onDelete() })
                            }
                        }
                    }
                }
            }
            if (text.isNotBlank()) {
                Text(text, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun EditReviewDialog(
    review: ReviewEntity,
    onDismiss: () -> Unit,
    onConfirm: (ReviewEntity, Int, String) -> Unit
) {
    var rating by remember { mutableStateOf(review.rating) }
    var text by remember { mutableStateOf(review.text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Review") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Star Rating Selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..5).forEach { star ->
                        val isSelected = star <= rating
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.Star else Icons.Default.StarBorder,
                            contentDescription = "$star Stars",
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { rating = star }
                        )
                    }
                }

                androidx.compose.material3.OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Your experience") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(review, rating, text) },
                enabled = text.isNotBlank() && rating > 0
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun StatusPill(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFFDDF5E6),
        border = BorderStroke(1.dp, Color(0xFFB8EACB))
    ) {
        Text(
            text,
            color = Color(0xFF2E7D32),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun TravelPostGrid(
    posts: List<PostWithUser>,
    onPostClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 360.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(posts, key = { it.post.id }) { postWithUser ->
            val post = postWithUser.post
            if (!post.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = post.title,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPostClick(post.id) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}