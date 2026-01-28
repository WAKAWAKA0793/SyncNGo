package com.example.tripshare.ui.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.tripshare.R

object NotificationHelper {
    const val CHANNEL_TRIP_REMINDERS = "trip_reminders"
    const val CHANNEL_PAYMENTS = "payments"
    const val CHANNEL_JOIN_REQUESTS = "join_requests"
    const val CHANNEL_CHAT_MESSAGES = "chat_messages"
    const val CHANNEL_WAITLIST = "waitlist"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val tripChannel = NotificationChannel(
                CHANNEL_TRIP_REMINDERS,
                "Trip reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Reminders for upcoming trips & itinerary items" }

            val paymentChannel = NotificationChannel(
                CHANNEL_PAYMENTS,
                "Payment reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Reminders for outstanding payments" }

            val joinChannel = NotificationChannel(
                CHANNEL_JOIN_REQUESTS,
                "Group join requests",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notifications for requests to join private groups" }

            val chatChannel = NotificationChannel(
                CHANNEL_CHAT_MESSAGES,
                "Chat messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notifications for new chat messages" }

            val waitlistChannel = NotificationChannel(
                CHANNEL_WAITLIST,
                "Waitlist alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notify when a slot opens and you can join a trip" }

            nm.createNotificationChannel(waitlistChannel)
            nm.createNotificationChannel(tripChannel)
            nm.createNotificationChannel(paymentChannel)
            nm.createNotificationChannel(joinChannel)
            nm.createNotificationChannel(chatChannel)
        }
    }

    /**
     * Check whether the app currently has permission to post notifications.
     * On pre-Android 13 devices this returns true (no runtime permission required).
     */
    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Show notification safely: check permission first and guard against SecurityException.
     *
     * If permission is not granted on Android 13+, this function returns without posting.
     * You should request the permission from the user earlier (Activity/Composable).
     */
    fun showNotification(
        context: Context,
        channelId: String,
        id: Int,
        title: String,
        body: String,
        contentIntent: PendingIntent? = null,
        actions: List<NotificationCompat.Action> = emptyList()
    ) {

        // choose a small icon resource — replace with your drawable
        val smallIconRes = try {
            R.drawable.ic_notification
        } catch (e: Exception) {
            android.R.drawable.ic_dialog_info
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIconRes)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        contentIntent?.let { builder.setContentIntent(it) }
        actions.forEach { builder.addAction(it) }

        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (se: SecurityException) {
            // Safety net: in case permission check missed something, catch SecurityException.
            // Optionally log this or track analytics to understand how often this occurs.
            se.printStackTrace()
        }
    }

    fun buildAction(
        iconRes: Int,
        title: String,
        pendingIntent: PendingIntent
    ): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(iconRes, title, pendingIntent).build()
    }

    fun cancel(context: Context, id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }
}
