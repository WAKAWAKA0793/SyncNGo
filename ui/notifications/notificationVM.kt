package com.example.tripshare.ui.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

// In notificationVM.kt

fun buildJoinRequestActionIntents(
    context: Context,
    notifId: Int,
    groupId: Long,
    requesterId: Long,
    currentUserId: Long // 👈 1. Add this parameter
): Pair<PendingIntent, PendingIntent> {

    val acceptIntent = Intent(context, com.example.tripshare.ui.notifications.JoinRequestActionReceiver::class.java).apply {
        action = "ACTION_ACCEPT_JOIN"
        putExtra("groupId", groupId)
        putExtra("requesterId", requesterId)
        putExtra("notifId", notifId)
        putExtra("recipientId", currentUserId) // 👈 2. Add to extras
    }

    val declineIntent = Intent(context, com.example.tripshare.ui.notifications.JoinRequestActionReceiver::class.java).apply {
        action = "ACTION_DECLINE_JOIN"
        putExtra("groupId", groupId)
        putExtra("requesterId", requesterId)
        putExtra("notifId", notifId)
        putExtra("recipientId", currentUserId) // 👈 3. Add to extras
    }

    val acceptPending = PendingIntent.getBroadcast(context, notifId + 1, acceptIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    val declinePending = PendingIntent.getBroadcast(context, notifId + 2, declineIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    return Pair(acceptPending, declinePending)
}