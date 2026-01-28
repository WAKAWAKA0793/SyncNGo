package com.example.tripshare.ui.notifications


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tripshare.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Expects inputData:
 *  - "channel" : String
 *  - "notifId" : Int
 *  - "title" : String
 *  - "body" : String
 *  - optional extras for deep link (e.g., "tripId")
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val data = inputData
            val channel = data.getString("channel") ?: NotificationHelper.CHANNEL_TRIP_REMINDERS
            val notifId = data.getInt("notifId", System.currentTimeMillis().toInt())
            val title = data.getString("title") ?: "Reminder"
            val body = data.getString("body") ?: ""
            val tripId = data.getLong("tripId", -1L)

            // Build content intent to open app (pass tripId if present)
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                if (tripId != -1L) putExtra("tripId", tripId)
            }
            val contentPendingIntent = PendingIntent.getActivity(
                applicationContext, notifId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            NotificationHelper.showNotification(
                applicationContext,
                channel,
                notifId,
                title,
                body,
                contentPendingIntent
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
