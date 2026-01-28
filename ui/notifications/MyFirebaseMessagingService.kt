package com.example.tripshare.ui.notifications

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.example.tripshare.MainActivity
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.db.AppDatabase
import com.example.tripshare.data.model.LocalNotificationEntity
import com.example.tripshare.ui.notifications.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        // ✅ Ensure channels exist (Android 8+)
        NotificationHelper.createChannels(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // ✅ FIXED: Launch a coroutine to avoid blocking the Main Thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Safely get the user ID from DataStore
                val uid = AuthPrefs.getUserId(applicationContext).first()

                if (uid != null && uid > 0L) {
                    Log.d(TAG, "onNewToken: token ready for uid=$uid (save it via repo)")
                    // TODO: Call your repository here to save the token to the server
                    // Example: UserRepository.updateFcmToken(uid, token)
                } else {
                    Log.w(TAG, "onNewToken: user not logged in yet; token not saved.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "onNewToken: failed to save token", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // It is safe to block here because onMessageReceived runs on a worker thread
        val recipientId = getCurrentUserIdBlocking() ?: return

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "TripShare"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: ""

        val type = remoteMessage.data["type"] ?: "INFO"
        val relatedId = remoteMessage.data["relatedId"]?.toLongOrNull()

        // 1️⃣ Save in-app notification to Room Database
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppDatabase.get(applicationContext)
                    .localNotificationDao()
                    .insert(
                        LocalNotificationEntity(
                            recipientId = recipientId,
                            type = type,
                            title = title,
                            body = body,
                            relatedId = relatedId
                        )
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2️⃣ Build intent to open MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "notifications")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 3️⃣ Determine Channel ID based on type
        val channelId = when (type) {
            "JOIN_REQUEST" -> NotificationHelper.CHANNEL_JOIN_REQUESTS
            "PAYMENT", "BILL_SPLIT" -> NotificationHelper.CHANNEL_PAYMENTS
            "TRIP_REMINDER", "TRIP_START" -> NotificationHelper.CHANNEL_TRIP_REMINDERS
            "WAITLIST_AVAILABLE" -> NotificationHelper.CHANNEL_WAITLIST
            else -> NotificationHelper.CHANNEL_CHAT_MESSAGES
        }

        // 4️⃣ Show system notification
        NotificationHelper.showNotification(
            context = this,
            channelId = channelId,
            id = System.currentTimeMillis().toInt(),
            title = title,
            body = body,
            contentIntent = pendingIntent
        )
    }

    /**
     * Helper to read DataStore (AuthPrefs) in a blocking way.
     * Only use this inside background threads (like onMessageReceived).
     */
    private fun getCurrentUserIdBlocking(): Long? {
        return try {
            runBlocking {
                AuthPrefs.getUserId(applicationContext).first()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read userId from AuthPrefs", e)
            null
        }
    }

    companion object {
        private const val TAG = "MyFCMService"
    }
}