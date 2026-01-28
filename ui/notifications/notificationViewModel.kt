package com.example.tripshare.ui.notifications

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.db.LocalNotificationDao
import com.example.tripshare.data.db.TripDao
import com.example.tripshare.data.db.WaitlistDao
import com.example.tripshare.data.model.LocalNotificationEntity
import com.example.tripshare.data.model.WaitlistEntity
import com.example.tripshare.data.repo.WaitlistRepository
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class NotificationViewModel(
    private val tripDao: TripDao,
    private val waitlistDao: WaitlistDao,
    private val waitlistRepo: WaitlistRepository,
    private val localNotificationDao: LocalNotificationDao,
    private val appContext: Context
) : ViewModel() {

    // ─────────────────────────── Join request + notification ───────────────────────────

    fun requestJoinPrivateTrip(
        tripId: Long,
        ownerId: Long,
        userId: Long,
        requesterName: String,
        tripName: String,
        location: String,
        date: String,
        tripImageUrl: String? = null
    ) {
        viewModelScope.launch {
            // 1️⃣ Add to waitlist
            val entry = WaitlistEntity(
                tripId = tripId,
                tripName = tripName,
                location = location,
                date = date,
                position = 0,
                alertsEnabled = true,
                tripImageUrl = tripImageUrl,
                userId = userId
            )

            try {
                waitlistRepo.add(entry)
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
            val functions = FirebaseFunctions.getInstance()

            val data = hashMapOf(
                "recipientId" to ownerId,
                "requesterName" to requesterName,
                "tripName" to tripName,
                "tripId" to tripId
            )

            functions
                .getHttpsCallable("sendJoinRequestNotification")
                .call(data)
                .addOnSuccessListener {
                    // Notification sent successfully!
                    // You might want to show a Snackbar here.
                }
                .addOnFailureListener { e ->
                    // Handle error (e.g., no internet)
                    e.printStackTrace()
                }
            // 2️⃣ Save in-app notification for the HOST
            localNotificationDao.insert(
                LocalNotificationEntity(
                    recipientId = ownerId,
                    type = "JOIN_REQUEST",
                    title = "Join request from $requesterName",
                    body = "$requesterName wants to join your trip $tripName",
                    relatedId = tripId
                )
            )

            // 3️⃣ Post system notification
            val notifId = buildJoinRequestNotifId(tripId, userId)
            val (acceptPending, declinePending) = buildJoinRequestActionIntents(
                context = appContext,
                notifId = notifId,
                groupId = tripId,
                requesterId = userId,
                currentUserId = ownerId
            )


                NotificationScheduler.notifyJoinRequest(
                    context = appContext,
                    notifId = notifId,
                    groupId = tripId,
                    requesterName = requesterName,
                    acceptActionIntent = acceptPending,
                    declineActionIntent = declinePending
                )
            }
        }


    fun setWaitlistAlertsEnabled(tripId: Long, userId: Long, enabled: Boolean) {
        viewModelScope.launch {
            val existing = waitlistRepo.findByUser(tripId, userId)
            if (existing != null) {
                waitlistRepo.toggleAlert(existing, enabled)
            }
        }
    }

    fun cancelJoinRequestNotification(tripId: Long, userId: Long) {
        val notifId = buildJoinRequestNotifId(tripId, userId)
        NotificationScheduler.cancelNotification(appContext, notifId)
    }

    private fun buildJoinRequestNotifId(groupId: Long, requesterId: Long): Int =
        ("4${groupId}_${requesterId}").hashCode()


    // ─────────────────────────── Trip reminders ───────────────────────────

    // 1. Update Trip Start Reminder (Upcoming Trip)
    fun scheduleTripStartReminder(
        tripId: Long,
        currentUserId: Long,
        tripTitle: String,
        startAtMillis: Long,
        remindBeforeMs: Long = 24 * 60 * 60 * 1000L
    ) {
        val notifId = buildTripNotifId(tripId)
        val title = "Trip Upcoming: $tripTitle"
        val body = "Your trip starts tomorrow! Tap to view details."

        // 🛑 Production Logic:
        // val triggerTime = startAtMillis - remindBeforeMs

        // ✅ TEST LOGIC: Trigger 5 seconds from NOW
        val triggerTime = System.currentTimeMillis() + 5000

        // Schedule System Notification
        NotificationScheduler.scheduleTripReminder(
            context = appContext,
            notifId = notifId,
            title = title,
            body = body,
            epochMillis = triggerTime,
            tripId = tripId
        )

        // Save In-App Notification
        viewModelScope.launch {
            localNotificationDao.insert(
                LocalNotificationEntity(
                    recipientId = currentUserId,
                    type = "TRIP_REMINDER",
                    title = title,
                    body = body,
                    relatedId = tripId,
                    isRead = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // 2. Update Itinerary Reminder
    fun scheduleItineraryReminder(
        tripId: Long,
        currentUserId: Long,
        itineraryId: Long,
        itemTitle: String,
        itemTimeMillis: Long,
        remindBeforeMs: Long = 60 * 60 * 1000L // 1 hour
    ) {
        val notifId = buildItineraryNotifId(tripId, itineraryId)
        val title = "Upcoming: $itemTitle"
        val body = "Starting in 1 hour."

        val triggerTime = itemTimeMillis - remindBeforeMs
        if (triggerTime <= System.currentTimeMillis()) return

        NotificationScheduler.scheduleTripReminder(
            context = appContext,
            notifId = notifId,
            title = title,
            body = body,
            epochMillis = triggerTime,
            tripId = tripId
        )

        viewModelScope.launch {
            localNotificationDao.insert(
                LocalNotificationEntity(
                    recipientId = currentUserId,
                    type = "TRIP_REMINDER",
                    title = title,
                    body = body,
                    relatedId = itineraryId,
                    isRead = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun cancelItineraryReminder(tripId: Long, itineraryId: Long) {
        val notifId = buildItineraryNotifId(tripId, itineraryId)
        NotificationScheduler.cancelNotification(appContext, notifId)
    }

    fun rescheduleItineraryReminder(
        tripId: Long,
        currentUserId: Long,
        itineraryId: Long,
        itemTitle: String,
        itemTimeMillis: Long,
        remindBeforeMs: Long = 60 * 60 * 1000L
    ) {
        // ✅ 1) Cancel existing scheduled work + notification
        cancelItineraryReminder(tripId, itineraryId)

        // ✅ 2) Schedule again with the new time
        scheduleItineraryReminder(
            tripId = tripId,
            currentUserId = currentUserId,
            itineraryId = itineraryId,
            itemTitle = itemTitle,
            itemTimeMillis = itemTimeMillis,
            remindBeforeMs = remindBeforeMs
        )
    }

    fun notifyWaitlistIfSlotAvailable(
        tripId: Long,
        tripName: String,
        forceOneSlotOpen: Boolean = false
    ) {
        viewModelScope.launch {
            val trip = tripDao.getTripById(tripId) ?: return@launch
            val maxParticipants = trip.maxParticipants
            val currentCount = tripDao.getParticipantCount(tripId)
            var openedSlots = maxParticipants - currentCount

            if (forceOneSlotOpen && openedSlots <= 0) openedSlots = 1
            if (openedSlots <= 0) return@launch

            val waitlist = waitlistDao.getForTrip(tripId)
            val targets = waitlist.filter { it.alertsEnabled }.sortedBy { it.position }.take(openedSlots)

            targets.forEach { entry ->
                // 1. Insert into DB
                localNotificationDao.insert(
                    LocalNotificationEntity(
                        recipientId = entry.userId,
                        type = "WAITLIST_AVAILABLE",
                        title = "A slot is available!",
                        body = "A spot opened up in \"$tripName\". Join now!",
                        relatedId = tripId,
                        isRead = false,
                        timestamp = System.currentTimeMillis()
                    )
                )

                // 2. TRIGGER SYSTEM POPUP
                val contentIntent = android.content.Intent(
                    appContext,
                    com.example.tripshare.MainActivity::class.java
                ).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                            android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("navigate_to", "trip_details")
                    putExtra("tripId", tripId)
                }

                val pendingIntent = android.app.PendingIntent.getActivity(
                    appContext,
                    ("waitlist_${tripId}_${entry.userId}").hashCode(),
                    contentIntent,
                    android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
                )

                NotificationHelper.showNotification(
                    context = appContext,
                    channelId = NotificationHelper.CHANNEL_WAITLIST,
                    id = ("waitlist_${tripId}_${entry.userId}").hashCode(),
                    title = "Spot Available!",
                    body = "A spot opened in '$tripName'. Tap to join now.",
                    contentIntent = pendingIntent
                )
            }
        }
    }
    private fun buildCalendarReminderNotifId(
        tripId: Long,
        date: String,
        time: String?,
        text: String
    ): Int = ("CAL_${tripId}_${date}_${time.orEmpty()}_${text}").hashCode()

    fun scheduleCalendarReminder(
        tripId: Long,
        date: LocalDate,
        time24: String?,              // "HH:mm" or null
        text: String,
        remindBeforeMs: Long = 60 * 60 * 1000L // 1 hour
    ) {
        val time = runCatching { LocalTime.parse(time24 ?: "09:00") } // default 09:00 if blank
            .getOrDefault(LocalTime.of(9, 0))

        val eventMillis = date.atTime(time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val remindAt = eventMillis - remindBeforeMs
        if (remindAt <= System.currentTimeMillis()) return

        val notifId = buildCalendarReminderNotifId(tripId, date.toString(), time24, text)

        NotificationScheduler.scheduleTripReminder(
            context = appContext,
            notifId = notifId,
            title = "Reminder",
            body = text,
            epochMillis = remindAt,
            tripId = tripId
        )
    }

    fun cancelCalendarReminder(
        tripId: Long,
        date: LocalDate,
        time24: String?,
        text: String
    ) {
        val notifId = buildCalendarReminderNotifId(tripId, date.toString(), time24, text)
        NotificationScheduler.cancelNotification(appContext, notifId)
    }

    fun notifyBillSplit(
        tripId: Long,
        tripName: String,
        expenseTitle: String,
        payerId: Long,
        payerName: String,
        splits: List<com.example.tripshare.ui.expense.SplitShare>,
        currency: String,
        dueAtMillis: Long? = null
    ) {
        viewModelScope.launch {
            val remindBeforeMs = 60 * 60 * 1000L // 1 hour

            splits.forEach { split ->
                if (split.userId != payerId && split.amountOwed > 0.01) {
                    val amountFormatted = String.format("%.2f", split.amountOwed)

                    // 1) Save in-app notification
                    localNotificationDao.insert(
                        LocalNotificationEntity(
                            recipientId = split.userId,
                            type = "BILL_SPLIT",
                            title = "Bill Split: $expenseTitle",
                            body = "In \"$tripName\", $payerName paid. You owe $currency $amountFormatted.",
                            relatedId = tripId,
                            isRead = false,
                            timestamp = System.currentTimeMillis()
                        )
                    )

                    // 2) System notification time
                    val triggerTime = if (dueAtMillis != null) {
                        dueAtMillis - remindBeforeMs
                    } else {
                        System.currentTimeMillis() // no due time → show now (or you can skip scheduling)
                    }

                    if (triggerTime <= System.currentTimeMillis() && dueAtMillis != null) {
                        // due time is too close/past; skip scheduling
                        return@forEach
                    }

                    // Stable-ish id so it won't spam duplicates per user per expenseTitle/due time
                    val reminderId = ("BILL_${tripId}_${split.userId}_${expenseTitle}_${dueAtMillis ?: 0L}")
                        .hashCode()

                    NotificationScheduler.scheduleTripReminder(
                        context = appContext,
                        notifId = reminderId,
                        title = "Bill Split: $expenseTitle",
                        body = "You owe $currency $amountFormatted to $payerName.",
                        epochMillis = triggerTime,
                        tripId = tripId
                    )
                }
            }
        }
    }


    // 3. Update Payment Reminder
    fun schedulePaymentReminder(
        invoiceId: Long,
        currentUserId: Long,
        dueAtMillis: Long,
        title: String = "Payment reminder",
        body: String = "You have an unpaid balance. Tap to view."
    ) {
        val notifId = buildPaymentNotifId(invoiceId)

        val remindBeforeMs = 60 * 60 * 1000L // 1 hour
        val triggerTime = dueAtMillis - remindBeforeMs
        if (triggerTime <= System.currentTimeMillis()) return

        NotificationScheduler.schedulePaymentReminder(
            context = appContext,
            notifId = notifId,
            title = title,
            body = body,
            epochMillis = triggerTime,
            invoiceId = invoiceId
        )

        viewModelScope.launch {
            localNotificationDao.insert(
                LocalNotificationEntity(
                    recipientId = currentUserId,
                    type = "PAYMENT",
                    title = title,
                    body = body,
                    relatedId = invoiceId,
                    isRead = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }


    private fun buildTripNotifId(tripId: Long): Int = ("1$tripId").hashCode()
    private fun buildItineraryNotifId(tripId: Long, itineraryId: Long): Int = ("2${tripId}_${itineraryId}").hashCode()
    private fun buildPaymentNotifId(invoiceId: Long): Int = ("3$invoiceId").hashCode()
}

// Factory
class NotificationViewModelFactory(
    private val tripDao: TripDao,
    private val waitlistDao: WaitlistDao,
    private val waitlistRepo: WaitlistRepository,
    private val localNotificationDao: LocalNotificationDao,
    private val appContext: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return NotificationViewModel(
                tripDao, waitlistDao, waitlistRepo, localNotificationDao, appContext
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}