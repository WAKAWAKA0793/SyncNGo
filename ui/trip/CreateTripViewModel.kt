// ui/trip/CreateTripViewModel.kt
package com.example.tripshare.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.model.CalendarEventEntity
import com.example.tripshare.data.model.CostSharingMethod
import com.example.tripshare.data.model.ParticipantInviteEntity
import com.example.tripshare.data.model.PaymentMethod
import com.example.tripshare.data.model.RouteStopEntity
import com.example.tripshare.data.model.StopType
import com.example.tripshare.data.model.TripCategory
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.TripMeetingPointEntity
import com.example.tripshare.data.model.TripPaymentMethodEntity
import com.example.tripshare.data.model.Visibility
import com.example.tripshare.data.repo.RouteRepository
import com.example.tripshare.data.repo.TripRepository
import com.example.tripshare.data.repo.UserRepository
import com.example.tripshare.ui.home.HomeViewModel
import com.example.tripshare.ui.notifications.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException

data class RouteUi(val label: String = "", val type: StopType = StopType.STOP, val order: Int = 0)
data class InviteUi(val identifier: String = "")
data class ChecklistUi(val title: String = "", val done: Boolean = false)
enum class Privacy { Public, Private }

data class CreateTripUi(
    val name: String = "",
    val category: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val maxParticipants: String = "",
    val privacy: Privacy = Privacy.Public,
    val costSharing: CostSharingMethod = CostSharingMethod.EQUAL_SPLIT,
    val paymentDeadline: String = "",
    val paymentEwallet: Boolean = false,
    val paymentCash: Boolean = false,
    val paymentBank: Boolean = false,
    val editingTripId: Long? = null, // 👈 Tracks edit mode
    val startLocation: String = "",
    val endLocation: String = "",
    val startLat: Double? = null,
    val startLng: Double? = null,
    val endLat: Double? = null,
    val endLng: Double? = null,
    val stops: List<RouteUi> = emptyList(),
    val invites: List<InviteUi> = emptyList(),
    val waitlistEnabled: Boolean = false,
    val checklist: List<ChecklistUi> = listOf(
        ChecklistUi("Book flights"),
        ChecklistUi("Pack essentials")
    ),
    val splitCost: Boolean = false,
    val budget: String = "",
    val description: String = "",
    val saving: Boolean = false,
    val error: String? = null,
    val successTripId: Long? = null
)

class HomeVmFactory(
    private val repo: TripRepository,
    private val userRepo: UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo, userRepo) as T
    }
}

class CreateTripViewModel(
    private val repo: TripRepository,
    private val routeRepo: RouteRepository,
    private val notifVm: NotificationViewModel,
    private val currentUserId: Long
) : ViewModel() {
    private val _ui = MutableStateFlow(CreateTripUi())
    val ui: StateFlow<CreateTripUi> = _ui

    fun update(f: (CreateTripUi) -> CreateTripUi) { _ui.value = f(_ui.value) }


    private fun parseDateOrNull(s: String): LocalDate? =
        if (s.isBlank()) null else try { LocalDate.parse(s) } catch (_: DateTimeParseException) { null }

    private fun parseCategoryOrDefault(s: String): TripCategory =
        when (s.trim().lowercase()) {
            "leisure"  -> TripCategory.Leisure
            "business" -> TripCategory.Business
            "general", "" -> TripCategory.General
            else -> runCatching { TripCategory.valueOf(s) }.getOrDefault(TripCategory.General)
        }

    // Helper to extract the largest number from a string like "RM150 - RM500"
    private fun extractMaxBudget(range: String): Double? {
        if (range.isBlank()) return null
        val numbers = range.replace("[^0-9.-]".toRegex(), "")
            .split("-")
            .mapNotNull { it.toDoubleOrNull() }
        return numbers.maxOrNull()
    }

    // 1. Load Trip for Editing
    fun loadTrip(tripId: Long) {
        viewModelScope.launch {
            val trip = repo.getTrip(tripId)
            if (trip == null) {
                update { it.copy(error = "Trip not found") }
                return@launch
            }

            val stops = routeRepo.getRouteStops(tripId)

            update { current ->
                current.copy(
                    editingTripId = trip.id,
                    name = trip.name,
                    category = trip.category.name.lowercase().replaceFirstChar { it.uppercase() },
                    startDate = trip.startDate?.toString() ?: "",
                    endDate = trip.endDate?.toString() ?: "",
                    description = trip.description,
                    maxParticipants = trip.maxParticipants.toString(),
                    budget = trip.budgetDisplay ?: trip.budget?.toString() ?: "",
                    privacy = if (trip.visibility == Visibility.Private) Privacy.Private else Privacy.Public,
                    startLocation = stops.find { it.type == StopType.START }?.label ?: "",
                    stops = stops.filter { it.type == StopType.STOP }
                        .sortedBy { it.orderInRoute }
                        .map { RouteUi(it.label, StopType.STOP, it.orderInRoute) },
                    waitlistEnabled = trip.waitlistEnabled,
                    costSharing = if (trip.costSharing) CostSharingMethod.EQUAL_SPLIT else CostSharingMethod.ORGANIZER_PAYS,
                    splitCost = trip.costSharing
                )
            }
        }
    }

    // 2. Combined Submit (Handles both CREATE and UPDATE)
    fun submit(userId: Long, displayName: String, email: String?) {
        val s = _ui.value
        if (s.name.isBlank()) {
            update { it.copy(error = "Trip name is required") }
            return
        }
        val currentUser = FirebaseAuth.getInstance().currentUser
        val firebaseUid = currentUser?.uid ?: ""
        viewModelScope.launch {
            _ui.value = _ui.value.copy(saving = true, error = null)

            val startLD = parseDateOrNull(s.startDate)
            val endLD = parseDateOrNull(s.endDate)
            val categoryEnum = parseCategoryOrDefault(s.category)
            val visibility =
                if (s.privacy == Privacy.Private) com.example.tripshare.data.model.Visibility.Private
                else com.example.tripshare.data.model.Visibility.Public
            val rawBudget = s.budget

            val numericBudget = extractMaxBudget(rawBudget)

            val tripEntity = TripEntity(
                id = s.editingTripId ?: 0L,
                name = s.name,
                organizerId = userId.toString(),
                category = categoryEnum,
                visibility = visibility,
                startDate = startLD,
                endDate = endLD,
                maxParticipants = s.maxParticipants.toIntOrNull() ?: 0,
                costSharing = s.splitCost,
                paymentDeadline = parseDateOrNull(s.paymentDeadline),
                waitlistEnabled = s.waitlistEnabled,
                budget = numericBudget,
                budgetDisplay = rawBudget,
                description = s.description
            )

            val finalTripId: Long

            // ─── DECIDE: CREATE vs UPDATE ───
            if (s.editingTripId != null) {
                // UPDATE MODE
                repo.updateTrip(tripEntity)

                // Clear old stops to replace with new ones
                routeRepo.deleteAllStopsForTrip(s.editingTripId)

                finalTripId = s.editingTripId
            } else {
                // CREATE MODE
                val payments = buildList {
                    if (s.paymentEwallet) add(TripPaymentMethodEntity(0, 0, PaymentMethod.EWALLET))
                    if (s.paymentCash) add(TripPaymentMethodEntity(0, 0, PaymentMethod.CASH))
                    if (s.paymentBank) add(TripPaymentMethodEntity(0, 0, PaymentMethod.BANK_TRANSFER))
                }
                val invites = s.invites.map { ParticipantInviteEntity(0, 0, it.identifier) }

                val initialEvents = buildList {
                    if (s.startDate.isNotBlank()) {
                        add(
                            CalendarEventEntity(
                                0L, 0L, "Trip Starts: ${s.name}", s.startDate, "08:00",
                                s.startLocation.ifBlank { "Start" }, "travel"
                            )
                        )
                    }
                    if (s.endDate.isNotBlank() && s.endDate != s.startDate) {
                        add(
                            CalendarEventEntity(
                                0L, 0L, "Trip Ends: ${s.name}", s.endDate, "18:00",
                                s.endLocation.ifBlank { "End" }, "travel"
                            )
                        )
                    }
                }

                // ✅ UPDATED: Pass creator details to ensure they are added as a participant immediately
                finalTripId = repo.createTrip(
                    trip = tripEntity,
                    routes = emptyList(), // Routes are handled via routeRepo below
                    payments = payments,
                    invites = invites,
                    events = initialEvents,
                    creatorId = userId,
                    creatorName = displayName,
                    creatorEmail = email ?: "",
                    creatorFirebaseId = firebaseUid
                )

                // Note: repo.addOwnerParticipant is no longer needed here as createTrip now handles it.

                if (startLD != null) {
                    val startMillis = startLD.atStartOfDay(java.time.ZoneId.systemDefault())
                        .toInstant().toEpochMilli()

                    notifVm.scheduleTripStartReminder(
                        tripId = finalTripId,
                        currentUserId = currentUserId,
                        tripTitle = s.name,
                        startAtMillis = startMillis
                    )
                }
            }

            // ✅ NOW finalTripId is ready: save meeting point
            repo.deleteMeetingPointsForTrip(finalTripId)

            if (s.startLocation.isNotBlank()) {
                repo.insertMeetingPoints(
                    listOf(
                        TripMeetingPointEntity(
                            tripId = finalTripId,
                            label = s.startLocation,
                            lat = s.startLat,
                            lng = s.startLng
                        )
                    )
                )
            }

            // ─── SAVE STOPS ───
            val routesOrdered = buildList {
                var order = 0
                if (s.startLocation.isNotBlank()) {
                    add(
                        RouteStopEntity(
                            0, finalTripId, StopType.START, s.startLocation,
                            s.startLat, s.startLng, order++,
                            startLD?.toString(), startLD?.toString(), 0
                        )
                    )
                }
                s.stops.forEach { r ->
                    add(RouteStopEntity(0, finalTripId, StopType.STOP, r.label, null, null, order++, null, null, 0))
                }
            }

            routeRepo.insertRouteStops(routesOrdered)

            update { it.copy(saving = false, successTripId = finalTripId) }
        }
    }


    fun clearSuccess() {
        _ui.value = _ui.value.copy(successTripId = null)
    }
}