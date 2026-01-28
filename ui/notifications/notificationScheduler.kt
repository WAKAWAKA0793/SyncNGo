package com.example.tripshare.ui.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val TAG_PREFIX = "notif_worker_"

    // Schedule a trip reminder at a specific epochMillis
    fun scheduleTripReminder(
        context: Context,
        notifId: Int,
        title: String,
        body: String,
        epochMillis: Long,
        tripId: Long? = null
    ) {
        val delay = maxOf(0L, epochMillis - System.currentTimeMillis())
        val input = workDataOf(
            "channel" to NotificationHelper.CHANNEL_TRIP_REMINDERS,
            "notifId" to notifId,
            "title" to title,
            "body" to body,
            "tripId" to (tripId ?: -1L)
        )

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(input)
            .addTag(TAG_PREFIX + notifId)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            TAG_PREFIX + notifId,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    // Schedule a payment reminder (same approach)
    fun schedulePaymentReminder(
        context: Context,
        notifId: Int,
        title: String,
        body: String,
        epochMillis: Long,
        invoiceId: Long? = null
    ) {
        val delay = maxOf(0L, epochMillis - System.currentTimeMillis())
        val input = workDataOf(
            "channel" to NotificationHelper.CHANNEL_PAYMENTS,
            "notifId" to notifId,
            "title" to title,
            "body" to body,
            "invoiceId" to (invoiceId ?: -1L)
        )

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(input)
            .addTag(TAG_PREFIX + notifId)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            TAG_PREFIX + notifId,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    // Immediate notification for join request with action buttons
    fun notifyJoinRequest(
        context: Context,
        notifId: Int,
        groupId: Long,
        requesterName: String,
        acceptActionIntent: PendingIntent,
        declineActionIntent: PendingIntent
    ) {
        // content intent opens group screen
        val contentIntent = Intent(context, com.example.tripshare.MainActivity::class.java).apply {
            putExtra("openGroupId", groupId)
            putExtra("navigate_to", "join_requests")

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPending = PendingIntent.getActivity(
            context,
            notifId,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val acceptAction = NotificationHelper.buildAction(
            iconRes = android.R.drawable.checkbox_on_background,
            title = "Accept",
            pendingIntent = acceptActionIntent
        )
        val declineAction = NotificationHelper.buildAction(
            iconRes = android.R.drawable.ic_delete,
            title = "Decline",
            pendingIntent = declineActionIntent
        )

        NotificationHelper.showNotification(
            context,
            NotificationHelper.CHANNEL_JOIN_REQUESTS,
            notifId,
            "Join request from $requesterName",
            "$requesterName wants to join your private group",
            contentPending,
            actions = listOf(acceptAction, declineAction)
        )
    }

    fun cancelNotification(context: Context, notifId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(TAG_PREFIX + notifId)
        // also cancel displayed notification
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.cancel(notifId)
    }
}
