package com.example.tripshare.ui.messages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.tripshare.data.db.AppDatabase
import com.example.tripshare.data.remote.RemoteChatService
import com.example.tripshare.data.remote.RemoteChatStub // optional, only if the class exists here
import com.example.tripshare.data.repo.ChatRepository
import kotlinx.coroutines.Dispatchers

@Composable
fun rememberChatRepository(db: AppDatabase): ChatRepository {
    // give it the interface type so Kotlin can infer, and it's easier to swap impls
    val remote: RemoteChatService = remember {
        // DEV: if RemoteChatStub isn't found here, replace this line with the anonymous stub below
        RemoteChatStub()
    }

    return remember {
        ChatRepository(
            chatRoomDao = db.chatRoomDao(),
            messageDao = db.messageDao(),
            groupChatDao = db.groupChatDao(),
            tripDao = db.tripDao(),
            remote = remote,
            ioDispatcher = Dispatchers.IO
        )
    }
}
