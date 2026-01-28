package com.example.tripshare.ui.messages

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tripshare.data.model.MessageStatus
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    defaultChatTitle: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onMyAvatarClick: () -> Unit = {},
    onOtherAvatarClick: (senderId: String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val dynamicTitle by viewModel.chatTitle.collectAsState()
    val chatTitle = if (dynamicTitle.isNotBlank()) dynamicTitle else defaultChatTitle

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chatTitle) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            ChatInputRow(
                sending = uiState.sending,
                onSend = { text ->
                    viewModel.sendMessage(text)
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        if (uiState.messages.isNotEmpty()) {
                            listState.animateScrollToItem(uiState.messages.size - 1)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.messages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No messages yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Send the first message to start the conversation.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                MessagesList(
                    messages = uiState.messages,
                    currentUserId = viewModel.currentUserId,
                    currentUserAvatarUrl = uiState.currentUserAvatarUrl,
                    listState = listState,
                    onRetry = { msgUi -> viewModel.retryMessage(msgUi.message) },
                    onMessageLongPress = { msgUi -> viewModel.markMessageRead(msgUi.message.id) },
                    onMyAvatarClick = onMyAvatarClick,
                    onOtherAvatarClick = onOtherAvatarClick
                )
            }

            LaunchedEffect(uiState.messages.size) {
                if (uiState.messages.isNotEmpty()) {
                    // optional autoscroll – keep if you want
                }
            }

            LaunchedEffect(uiState.error) {
                val err = uiState.error
                if (!err.isNullOrBlank()) {
                    snackbarHostState.showSnackbar(err)
                }
            }
        }
    }
}

@Composable
fun MessagesList(
    messages: List<MessageUi>,
    currentUserId: String,
    currentUserAvatarUrl: String?,
    listState: LazyListState,
    onRetry: (MessageUi) -> Unit,
    onMessageLongPress: (MessageUi) -> Unit,
    onMyAvatarClick: () -> Unit,
    onOtherAvatarClick: (senderId: String) -> Unit
) {
    LazyColumn(
        state = listState,
        reverseLayout = false,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        items(messages, key = { it.message.id }) { messageUi ->
            val message = messageUi.message
            val isMe = messageUi.isMe

            Log.d(
                "ChatUI",
                "render msgId=${message.id} sender='${message.senderId}' isMe=${messageUi.isMe} currentUser=$currentUserId avatar=${messageUi.senderAvatarUrl}"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {

                // THEIR AVATAR (Left)
                if (!isMe) {
                    AvatarSmall(
                        url = messageUi.senderAvatarUrl,
                        onClick = { onOtherAvatarClick(messageUi.message.senderId) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // MESSAGE BUBBLE
                MessageBubble(
                    messageUi = messageUi,
                    onRetry = { onRetry(messageUi) },
                    onLongPress = { onMessageLongPress(messageUi) }
                )

                // MY AVATAR (Right)
                if (isMe) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AvatarSmall(
                        url = currentUserAvatarUrl,
                        onClick = { onMyAvatarClick() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    messageUi: MessageUi,
    onRetry: () -> Unit,
    onLongPress: () -> Unit
) {
    val msg = messageUi.message
    val isMe = messageUi.isMe

    val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val textColor = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    val shape = if (isMe) {
        RoundedCornerShape(topStart = 12.dp, topEnd = 4.dp, bottomEnd = 12.dp, bottomStart = 12.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp)
    }

    Surface(
        shape = shape,
        color = bubbleColor,
        shadowElevation = if (isMe) 2.dp else 1.dp,
        modifier = Modifier
            .widthIn(max = 280.dp)
            .combinedClickable(onClick = {}, onLongClick = onLongPress)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(text = msg.content, color = textColor, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeString(msg.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f)
                )
                if (isMe) {
                    Spacer(modifier = Modifier.width(6.dp))
                    when (msg.status) {
                        MessageStatus.PENDING -> {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = "Pending",
                                modifier = Modifier.size(14.dp),
                                tint = textColor.copy(alpha = 0.7f)
                            )
                        }
                        MessageStatus.SENT, MessageStatus.DELIVERED, MessageStatus.READ -> {
                            Icon(
                                if (msg.status == MessageStatus.READ) Icons.Default.DoneAll else Icons.Default.Done,
                                contentDescription = msg.status.name,
                                modifier = Modifier.size(14.dp),
                                tint = textColor.copy(alpha = 0.7f)
                            )
                        }
                        MessageStatus.FAILED -> {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Failed",
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable(onClick = onRetry),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarSmall(
    url: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val finalModifier = modifier
        .size(36.dp)
        .clip(RoundedCornerShape(18.dp))
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)

    if (!url.isNullOrBlank()) {
        val imageRequest = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build()

        AsyncImage(
            model = imageRequest,
            contentDescription = "avatar",
            modifier = finalModifier
        )
    } else {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = finalModifier
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "avatar",
                modifier = Modifier.padding(6.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChatInputRow(sending: Boolean, onSend: (String) -> Unit) {
    var input by remember { mutableStateOf("") }
    val canSend = input.isNotBlank() && !sending
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message…") },
                maxLines = 4,
                singleLine = false
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (canSend) {
                        onSend(input.trim())
                        input = ""
                    }
                },
                enabled = canSend
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

private fun timeString(epochMs: Long): String {
    return try {
        val df = SimpleDateFormat("HH:mm", Locale.getDefault())
        df.format(Date(epochMs))
    } catch (e: Exception) {
        ""
    }
}
