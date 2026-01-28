package com.example.tripshare.ui.messages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel,
    modifier: Modifier = Modifier,
    onOpenChat: (chatId: String, title: String) -> Unit = { _, _ -> },
    onBack: () -> Unit = {},
    // Call this to actually delete the chat in your ViewModel / repo
    onDeleteChat: (chatId: String) -> Unit = {}
) {
    val rooms by viewModel.roomsUi.collectAsState()

    // which room is selected for delete
    var roomToDelete by remember { mutableStateOf<ChatRoomUi?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats",fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { contentPadding ->
        if (rooms.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No conversations yet", style = MaterialTheme.typography.bodyLarge)
            }
            return@Scaffold
        }

        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(rooms, key = { it.room.id }) { ui ->
                ChatListRow(
                    ui = ui,
                    onClick = { onOpenChat(ui.room.id, ui.room.title ?: "") },
                    onLongClick = { roomToDelete = ui }
                )
                Divider()
            }
        }

        // Delete-confirm dialog
        roomToDelete?.let { selected ->
            AlertDialog(
                onDismissRequest = { roomToDelete = null },
                title = { Text("Delete chat?") },
                text = {
                    Text(
                        "This will remove the conversation \"${selected.room.title ?: "Chat"}\". " +
                                "This action cannot be undone."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteChat(selected.room.id)
                        roomToDelete = null
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { roomToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatListRow(
    ui: ChatRoomUi,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val room = ui.room
    val avatarUrl = ui.avatarUrl
    val unread = ui.unreadCount

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = room.title ?: "Chat",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = room.title ?: "Chat",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                room.updatedAt.takeIf { it > 0 }?.let { ts ->
                    Text(
                        text = formatTimeShort(ts),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = room.lastMessage ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (unread > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                tonalElevation = 2.dp,
                shape = CircleShape,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (unread > 99) "99+" else unread.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}

/** Simple time helper used in the row to display a short label */
private fun formatTimeShort(epochMs: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - epochMs
    val minute = 60_000L
    val hour = 60 * minute
    val day = 24 * hour

    return when {
        diff < minute -> "now"
        diff < hour -> "${diff / minute}m"
        diff < day -> "${diff / hour}h"
        else -> {
            val df = SimpleDateFormat("MMM d", Locale.getDefault())
            df.format(Date(epochMs))
        }
    }
}
