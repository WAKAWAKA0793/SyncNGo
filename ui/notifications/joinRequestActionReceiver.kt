package com.example.tripshare.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.tripshare.data.db.AppDatabase
import com.example.tripshare.data.model.LocalNotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JoinRequestActionReceiver : BroadcastReceiver() {
    // In JoinRequestActionReceiver.kt

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        val action = intent.action
        val groupId = intent.getLongExtra("groupId", -1L)
        val requesterId = intent.getLongExtra("requesterId", -1L)
        val notifId = intent.getIntExtra("notifId", -1)

        // 👇 Get the recipient ID from the intent
        val recipientId = intent.getLongExtra("recipientId", -1L)

        val db = AppDatabase.get(context.applicationContext)
        val localNotifDao = db.localNotificationDao()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (recipientId != -1L) {
                    if (action == "ACTION_ACCEPT_JOIN") {
                        localNotifDao.insert(
                            LocalNotificationEntity(
                                recipientId = recipientId, // 👈 FIX: Pass the value here
                                type = "JOIN_REQUEST_DECISION",
                                title = "Request accepted",
                                body = "You accepted the join request.",
                                relatedId = groupId,
                                isRead = false,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    } else if (action == "ACTION_DECLINE_JOIN") {
                        localNotifDao.insert(
                            LocalNotificationEntity(
                                recipientId = recipientId, // 👈 FIX: Pass the value here
                                type = "JOIN_REQUEST_DECISION",
                                title = "Request declined",
                                body = "You declined the join request.",
                                relatedId = groupId,
                                isRead = false,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }

        val nm =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.cancel(notifId)
    }
}