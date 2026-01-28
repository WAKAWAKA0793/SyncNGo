package com.example.tripshare.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tripshare.data.db.TripCommentDao
import com.example.tripshare.data.db.TripDao
import com.example.tripshare.data.model.ExpensePaymentEntity
import com.example.tripshare.data.model.TripCommentEntity
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.repo.ExpenseRepository
import com.example.tripshare.data.repo.ItineraryRepository
import com.example.tripshare.ui.notifications.NotificationViewModel
import com.example.tripshare.utils.formatTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

data class SelectedExpenseDetails(
    val paymentId: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val date: LocalDate,
    val payerName: String,
    val payerPhotoUrl: String?,
    val splits: List<SplitInfo>
)

data class SplitInfo(
    val name: String,
    val photoUrl: String?,
    val amount: Double
)

class ItineraryPlannerViewModel(
    private val repo: ItineraryRepository,
    private val notifVm: NotificationViewModel,
    private val tripCommentDao: TripCommentDao,
    private val currentUserId: Long,
    private val expenseRepo: ExpenseRepository,
    private val tripDao: TripDao,
    val tripId: Long
) : ViewModel() {
    val tripName: String? = null
    val tripStart: LocalDate? = null
    val tripEnd: LocalDate? = null
    // Live timeline list shown in the Plan sheet
    val plans: StateFlow<List<PlanEntryUi>> =
        repo.observePlans(tripId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
    private val selectedPlanId = MutableStateFlow<Long?>(null)
    init {
        // ✅ START SYNC HERE
        repo.startSync(tripId, viewModelScope)
    }
    // ─────────────────────────────────────────────
    // 2️⃣ Public comments state
    // ─────────────────────────────────────────────
    val currentPlanComments: StateFlow<List<CommentUi>> =
        selectedPlanId
            .filterNotNull()
            .flatMapLatest { planId ->
                tripCommentDao.observeCommentsForPlan(planId)
            }
            .map { raw ->
                raw.map { item ->
                    CommentUi(
                        id = item.comment.id,
                        authorName = item.author?.name ?: "Unknown",
                        text = item.comment.text,
                        timestamp = formatTime(item.comment.timestamp),
                        authorAvatarUrl = item.author?.profilePhoto
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // ─────────────────────────────────────────────
    // 3️⃣ Select a plan (call when dialog opens)
    // ─────────────────────────────────────────────
    fun loadComments(planId: Long) {
        selectedPlanId.value = planId
    }

    // ─────────────────────────────────────────────
    // 4️⃣ Post a comment
    // ─────────────────────────────────────────────
    fun postComment(planId: Long, text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            tripCommentDao.insert(
                TripCommentEntity(
                    planId = planId,
                    userId = currentUserId,
                    text = text.trim(),
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // ─────────────────────────────────────────────
    // 5️⃣ Optional: delete comment
    // ─────────────────────────────────────────────
    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            tripCommentDao.delete(commentId)
        }
    }

    private fun scheduleReminder(
        planId: Long,
        title: String,
        date: LocalDate,
        time: LocalTime?,
        hoursBefore: Long = 1
    ) {
        val targetTime = time ?: LocalTime.of(9, 0)

        val triggerEpoch = date.atTime(targetTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val remindAt = triggerEpoch - (hoursBefore * 60 * 60 * 1000)

        if (remindAt <= System.currentTimeMillis()) return

        notifVm.scheduleItineraryReminder(
            tripId = tripId,
            currentUserId = currentUserId,
            itineraryId = planId,
            itemTitle = title,
            itemTimeMillis = triggerEpoch,
            remindBeforeMs = hoursBefore * 60 * 60 * 1000
        )
    }


    val availableTargetTrips: StateFlow<List<TripEntity>> =
        tripDao.observeTripsForUser(currentUserId) // Assuming this DAO method exists
            .map { trips -> trips.filter { it.id != tripId } } // Exclude current trip
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    private val _copyStatus = MutableStateFlow<String?>(null)
    val copyStatus = _copyStatus.asStateFlow()

    fun clearCopyStatus() { _copyStatus.value = null }

    // ✅ NEW: Logic to copy plans to a specific target trip
    fun copyItineraryToTrip(targetTrip: TripEntity) {
        viewModelScope.launch {
            val sourcePlans = plans.value
            if (sourcePlans.isEmpty()) return@launch

            try {
                sourcePlans.forEach { plan ->
                    repo.quickAddPlan(
                        tripId = targetTrip.id, // Access ID from entity
                        type = plan.type,
                        date = plan.date,
                        time = plan.time,
                        title = plan.title,
                        subtitle = plan.subtitle,
                        location = null
                    )
                }
                // ✅ Update message to include the trip name
                _copyStatus.value = "Copied to ${targetTrip.name} successfully"
            } catch (e: Exception) {
                _copyStatus.value = "Failed to copy: ${e.message}"
            }
        }
    }



    val planExpenseMap: StateFlow<Map<Long, List<ExpensePaymentEntity>>> =
        expenseRepo.observePayments(tripId)
            .map { list ->
                list.filter { it.itineraryPlanId != null }
                    .groupBy { it.itineraryPlanId!! } // Use groupBy instead of associateBy
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyMap()
            )

    fun deletePlan(planId: Long) {
        viewModelScope.launch {
            // ✅ Cancel scheduled work + displayed notif first
            notifVm.cancelItineraryReminder(tripId = tripId, itineraryId = planId)

            // ✅ Then delete from DB
            repo.deletePlan(planId)
        }
    }

    // -----------------------------------------------------------------------
    // 1. Generic "quick add"
    // -----------------------------------------------------------------------
    fun addQuickPlan(
        type: PlanType,
        date: LocalDate = LocalDate.now(),
        time: LocalTime? = null,
        title: String = type.label,
        subtitle: String? = null,
        location: String? = null,
        onDone: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val newId = repo.quickAddPlan(
                tripId = tripId,
                type = type,
                date = date,
                time = time,
                title = title,
                subtitle = subtitle,
                location = location
            )
            scheduleReminder(newId, title, date, time)

            onDone(newId)
        }
    }

    /**
     * Backwards compatibility with older calls like plannerVm.addPlan(type)
     */
    fun addPlan(type: PlanType) {
        addQuickPlan(type = type, date = LocalDate.now(), onDone = {})
    }

    fun addFlight(
        depDate: LocalDate,
        depTime: LocalTime?,
        airline: String,
        flightNumber: String,
        origin: String,         // 👈 ADD THIS
        confirmation: String?,
        lat: Double?,
        lng: Double?,
        onDone: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val newId = repo.addFlightPlan(
                tripId = tripId,
                depDate = depDate,
                depTime = depTime,
                airline = airline,
                flightNumber = flightNumber,
                origin = origin,    // 👈 PASS IT
                confirmation = confirmation,
                lat = lat,
                lng = lng
            )

            scheduleReminder(
                planId = newId,
                title = "Flight $flightNumber",
                date = depDate,
                time = depTime,
                hoursBefore = 3
            )

            onDone(newId)
        }
    }

    fun updateFlight(
        planId: Long,
        depDate: LocalDate,
        depTime: LocalTime?,
        airline: String,
        flightNumber: String,
        origin: String,         // 👈 ADD THIS
        confirmation: String?,
        lat: Double?,
        lng: Double?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val ui = PlanEntryUi(
                id = planId,
                date = depDate,
                time = depTime,
                title = "Flight ${flightNumber.trim()} ($airline)", // Optional: Add airline to title
                subtitle = origin.trim(), // 👈 SAVE ORIGIN AS SUBTITLE/LOCATION
                description = confirmation?.trim(),
                type = PlanType.Flight,
                lat = lat,
                lng = lng
            )

            repo.updatePlan(tripId, ui)
            scheduleReminder(planId, ui.title, depDate, depTime)
            onDone()
        }
    }

    fun updateFlight(
        planId: Long,
        depDate: LocalDate,
        depTime: LocalTime?,
        airline: String,
        flightNumber: String,
        confirmation: String?,
        lat: Double?,          // ✅ NEW
        lng: Double?,          // ✅ NEW
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val ui = PlanEntryUi(
                id = planId,
                date = depDate,
                time = depTime,
                title = "Flight ${flightNumber.trim()}",
                subtitle = airline.trim(),
                description = confirmation?.trim(),
                type = PlanType.Flight,
                lat = lat,        // ✅
                lng = lng         // ✅
            )

            repo.updatePlan(tripId, ui)
            scheduleReminder(planId, ui.title, depDate, depTime)
            onDone()
        }
    }



    // ItineraryPlannerViewModel.kt
    fun addLodging(
        lodgingName: String,
        checkInDate: LocalDate,
        checkInTime: LocalTime?,
        checkOutDate: LocalDate?,
        checkOutTime: LocalTime?,
        address: String?,
        phone: String?,
        website: String?,
        email: String?,
        confirmation: String?,
        lat: Double? = null,
        lng: Double? = null,
        onDone: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val (checkInId, _) = repo.addLodgingPlans(
                tripId = tripId,
                lodgingName = lodgingName,
                checkInDate = checkInDate,
                checkInTime = checkInTime,
                checkOutDate = checkOutDate,
                checkOutTime = checkOutTime,
                address = address,
                phone = phone,
                website = website,
                email = email,
                confirmation = confirmation,
                lat = lat,
                lng = lng
            )
            scheduleReminder(checkInId, "Check-in: $lodgingName", checkInDate, checkInTime)

            onDone(checkInId)
        }
    }
    fun updateLodging(
        checkInPlanId: Long,
        lodgingName: String,
        checkInDate: LocalDate,
        checkInTime: LocalTime?,
        checkOutDate: LocalDate?,
        checkOutTime: LocalTime?,
        address: String?,
        phone: String?,
        website: String?,
        email: String?,
        confirmation: String?,
        lat: Double?,
        lng: Double?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val notes = buildString {
                if (!address.isNullOrBlank()) append("Address: $address\n")
                if (!phone.isNullOrBlank()) append("Phone: $phone\n")
                if (!website.isNullOrBlank()) append("Website: $website\n")
                if (!email.isNullOrBlank()) append("Email: $email\n")
                if (!confirmation.isNullOrBlank()) append("Conf#: $confirmation\n")
            }.trim()

            // Update check-in row
            val checkInUi = PlanEntryUi(
                id = checkInPlanId,
                date = checkInDate,
                time = checkInTime,
                title = "Check-in: ${lodgingName.ifBlank { "Lodging" }}",
                subtitle = address ?: lodgingName,
                description = notes,
                type = PlanType.Lodging,
                lat = lat,
                lng = lng
            )
            repo.updatePlan(tripId, checkInUi)

            // Update check-out row if exists (heuristic)
            val checkOutTitle = "Check-out: ${lodgingName.ifBlank { "Lodging" }}"
            val checkOutPlan = plans.value.firstOrNull { it.type == PlanType.Lodging && it.title.trim() == checkOutTitle }

            if (checkOutPlan != null && checkOutDate != null) {
                val checkOutUi = PlanEntryUi(
                    id = checkOutPlan.id,
                    date = checkOutDate,
                    time = checkOutTime,
                    title = checkOutTitle,
                    subtitle = address ?: lodgingName,
                    description = notes,
                    type = PlanType.Lodging,
                    lat = lat,
                    lng = lng
                )
                repo.updatePlan(tripId, checkOutUi)
            }

            scheduleReminder(checkInPlanId, checkInUi.title, checkInDate, checkInTime)
            onDone()
        }
    }

    fun addRestaurant(
        name: String,
        dineDate: LocalDate,
        dineTime: LocalTime?,
        address: String?,
        people: Int?,
        lat: Double? = null,
        lng: Double? = null,
        notes: String? = null,
        onDone: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val id = repo.addRestaurantPlan(
                tripId = tripId,
                name = name,
                dineDate = dineDate,
                dineTime = dineTime,
                address = address,
                people = people,
                lat = lat,
                lng = lng,
                notes = notes
            )
            // 🔔 Reminder
            scheduleReminder(id, "Restaurant: $name", dineDate, dineTime)
            onDone(id)
        }
    }
    fun updateRestaurant(
        planId: Long,
        name: String,
        dineDate: LocalDate,
        dineTime: LocalTime?,
        address: String?,
        people: Int?,
        lat: Double?,
        lng: Double?,
        notes: String?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val sb = buildString {
                if (!address.isNullOrBlank()) append("Address: $address\n")
                if (people != null) append("Guests: $people\n")
                if (!notes.isNullOrBlank()) append(notes.trim()).append("\n")
            }.trim()

            val ui = PlanEntryUi(
                id = planId,
                date = dineDate,
                time = dineTime,
                title = name.ifBlank { "Restaurant" },
                subtitle = address,
                description = sb,
                type = PlanType.Restaurant,
                lat = lat,
                lng = lng
            )

            repo.updatePlan(tripId, ui)
            scheduleReminder(planId, ui.title, dineDate, dineTime)
            onDone()
        }
    }

    fun addCarRental(
        agency: String,
        confirmation: String?,
        description: String?,
        carName: String?,
        carType: String,
        pickupName: String,
        pickupAddr: String?,
        pickupDate: LocalDate,
        pickupTime: LocalTime?,
        dropName: String,
        dropAddr: String?,
        dropDate: LocalDate,
        dropTime: LocalTime?,
        lat: Double?,
        lng: Double?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            // 👇 Capture the ID (Ensure your Repo returns Long)
            val id = repo.addCarRentalPlan(
                tripId = tripId,
                agency = agency,
                confirmation = confirmation,
                description = description,
                carName = carName,
                carType = carType,
                pickupName = pickupName,
                pickupAddr = pickupAddr,
                pickupDate = pickupDate,
                pickupTime = pickupTime,
                dropName = dropName,
                dropAddr = dropAddr,
                dropDate = dropDate,
                dropTime = dropTime,
                lat = lat,
                lng = lng
            )

            // 🔔 Reminder for Pickup
            scheduleReminder(id, "Car Pickup: $agency", pickupDate, pickupTime)

            onDone()
        }
    }
    fun updateCarRental(
        pickupPlanId: Long,
        agency: String,
        confirmation: String?,
        description: String?,
        carName: String?,
        carType: String,
        pickupName: String,
        pickupAddr: String?,
        pickupDate: LocalDate,
        pickupTime: LocalTime?,
        dropName: String,
        dropAddr: String?,
        dropDate: LocalDate,
        dropTime: LocalTime?,
        lat: Double?,
        lng: Double?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val commonHeader = buildString {
                append(agency)
                if (!carType.isBlank()) append(" • $carType")
                if (!carName.isNullOrBlank()) append(" • $carName")
            }.ifBlank { "Car rental" }

            val pickupNotes = buildString {
                if (!pickupAddr.isNullOrBlank()) append("Pick-up address: $pickupAddr\n")
                if (!confirmation.isNullOrBlank()) append("Conf#: $confirmation\n")
                if (!description.isNullOrBlank()) append("Notes: $description\n")
                append("Drop-off: $dropName")
                if (!dropAddr.isNullOrBlank()) append(" ($dropAddr)")
            }.trim()

            // ✅ Update pickup row (this is the id user tapped Edit on)
            repo.updatePlan(
                tripId = tripId,
                plan = PlanEntryUi(
                    id = pickupPlanId,
                    date = pickupDate,
                    time = pickupTime,
                    title = "Car pick-up – $commonHeader",
                    subtitle = pickupName,
                    description = pickupNotes,
                    type = PlanType.CarRental,
                    lat = lat,
                    lng = lng
                )
            )

            // ✅ Try update drop-off row too (heuristic: match title)
            val dropTitle = "Car drop-off – $commonHeader"
            val dropPlan = plans.value.firstOrNull {
                it.type == PlanType.CarRental && it.title.trim() == dropTitle
            }

            if (dropPlan != null) {
                val dropNotes = buildString {
                    if (!dropAddr.isNullOrBlank()) append("Drop-off address: $dropAddr\n")
                    if (!confirmation.isNullOrBlank()) append("Conf#: $confirmation\n")
                    if (!description.isNullOrBlank()) append("Notes: $description\n")
                    append("Pick-up: $pickupName")
                    if (!pickupAddr.isNullOrBlank()) append(" ($pickupAddr)")
                }.trim()

                repo.updatePlan(
                    tripId = tripId,
                    plan = PlanEntryUi(
                        id = dropPlan.id,
                        date = dropDate,
                        time = dropTime,
                        title = dropTitle,
                        subtitle = dropName,
                        description = dropNotes,
                        type = PlanType.CarRental,
                        lat = lat,
                        lng = lng
                    )
                )
            }

            // 🔔 reschedule pickup reminder
            scheduleReminder(pickupPlanId, "Car Pickup: $agency", pickupDate, pickupTime)

            onDone()
        }
    }

    fun addRail(
        carrier: String,
        confirmation: String?,
        depStation: String,
        depAddress: String?,
        depDate: LocalDate,
        depTime: LocalTime?,
        arrStation: String,
        arrAddress: String?,
        arrDate: LocalDate,
        arrTime: LocalTime?,
        onDone: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val id = repo.addRailPlan(
                tripId = tripId,
                carrier = carrier,
                confirmation = confirmation,
                depStation = depStation,
                depAddress = depAddress,
                depDate = depDate,
                depTime = depTime,
                arrStation = arrStation,
                arrAddress = arrAddress,
                arrDate = arrDate,
                arrTime = arrTime
            )

            // 🔔 Reminder for Train Departure
            scheduleReminder(id, "Rail: $carrier ($depStation)", depDate, depTime)

            onDone(id)
        }
    }
    fun updateRail(
        planId: Long,
        carrier: String,
        confirmation: String?,
        depStation: String,
        depAddress: String?,
        depDate: LocalDate,
        depTime: LocalTime?,
        arrStation: String,
        arrAddress: String?,
        arrDate: LocalDate,
        arrTime: LocalTime?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val notes = buildString {
                if (!confirmation.isNullOrBlank()) append("Conf#: $confirmation\n")
                append("Departure: $depStation\n")
                if (!depAddress.isNullOrBlank()) append("$depAddress\n")
                append("Departing: $depDate")
                if (depTime != null) append(" ${depTime}\n") else append("\n\n")

                append("\nArrival: $arrStation\n")
                if (!arrAddress.isNullOrBlank()) append("$arrAddress\n")
                append("Arriving: $arrDate")
                if (arrTime != null) append(" ${arrTime}\n")
            }.trim()

            val ui = PlanEntryUi(
                id = planId,
                date = depDate,
                time = depTime,
                title = carrier.ifBlank { "Rail" },
                subtitle = depStation,
                description = notes,
                type = PlanType.Rail,
                endDate = arrDate,
                endTime = arrTime,
                lat = null,
                lng = null
            )

            repo.updatePlan(tripId, ui)
            scheduleReminder(planId, ui.title, depDate, depTime)
            onDone()
        }
    }

    fun addCruise(
        cruiseLine: String,
        shipName: String,
        confirmation: String?,
        startPortName: String,
        startPortAddr: String?,
        startDate: LocalDate,
        startTime: LocalTime?,
        endPortName: String,
        endPortAddr: String?,
        endDate: LocalDate,
        endTime: LocalTime?,
        lat: Double?,
        lng: Double?,
        onDone: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val id = repo.addCruisePlan(
                tripId = tripId,
                cruiseLine = cruiseLine,
                shipName = shipName,
                confirmation = confirmation,
                startPortName = startPortName,
                startPortAddr = startPortAddr,
                startDate = startDate,
                startTime = startTime,
                endPortName = endPortName,
                endPortAddr = endPortAddr,
                endDate = endDate,
                endTime = endTime,
                lat = lat,
                lng = lng
            )

            // 🔔 Reminder for Cruise Departure
            scheduleReminder(id, "Cruise: $cruiseLine ($startPortName)", startDate, startTime)

            onDone(id)
        }
    }
    fun updateCruise(
        planId: Long,
        cruiseLine: String,
        shipName: String,
        confirmation: String?,
        startPortName: String,
        startPortAddr: String?,
        startDate: LocalDate,
        startTime: LocalTime?,
        endPortName: String,
        endPortAddr: String?,
        endDate: LocalDate?,
        endTime: LocalTime?,
        lat: Double?,
        lng: Double?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val sb = buildString {
                if (shipName.isNotBlank()) append("Ship: $shipName\n")
                if (!confirmation.isNullOrBlank()) append("Conf#: $confirmation\n")

                append("Start port: $startPortName\n")
                if (!startPortAddr.isNullOrBlank()) append("$startPortAddr\n")
                append("Departs: ${startDate}\n")
                if (startTime != null) append(" ${startTime}\n")

                if (endDate != null) {
                    append("\nEnd port: $endPortName\n")
                    if (!endPortAddr.isNullOrBlank()) append("$endPortAddr\n")
                    append("Arrives: ${endDate}\n")
                    if (endTime != null) append(" ${endTime}\n")
                }
            }.trim()

            repo.updatePlan(
                tripId = tripId,
                plan = PlanEntryUi(
                    id = planId,
                    date = startDate,
                    time = startTime,
                    title = cruiseLine.ifBlank { "Cruise" },
                    subtitle = startPortName,
                    description = sb,
                    type = PlanType.Cruise,
                    lat = lat,
                    lng = lng,
                    endDate = endDate,
                    endTime = endTime
                )
            )

            scheduleReminder(planId, "Cruise: ${cruiseLine.ifBlank { "Cruise" }}", startDate, startTime)
            onDone()
        }
    }

    fun addActivityPlan(
        title: String,
        note: String,
        locationName: String,
        startDate: LocalDate,
        startTime: LocalTime,
        endDate: LocalDate?,
        endTime: LocalTime?,
        lat: Double?,
        lng: Double?
    ) {
        viewModelScope.launch {
            val newPlan = PlanEntryUi(
                id = 0L,
                date = startDate,
                time = startTime,
                title = title.ifBlank { locationName },
                subtitle = locationName,
                description = note, // ✅ NOW MAPPED (was missing before)
                type = PlanType.Activity,
                lat = lat,
                lng = lng,
                endDate = endDate,
                endTime = endTime
            )
            val id = repo.insertPlan(tripId, newPlan)

            scheduleReminder(id, title.ifBlank { "Activity" }, startDate, startTime)
        }
    }

    fun updateActivityPlan(
        planId: Long,
        title: String,
        note: String,
        locationName: String,
        startDate: LocalDate,
        startTime: LocalTime,
        endDate: LocalDate?,
        endTime: LocalTime?,
        lat: Double?,
        lng: Double?
    ) {
        viewModelScope.launch {
            val updated = PlanEntryUi(
                id = planId,
                date = startDate,
                time = startTime,
                title = title.ifBlank { locationName },
                subtitle = locationName,
                description = note, // ✅ keep showing in dialog later
                type = PlanType.Activity,
                lat = lat,
                lng = lng,
                endDate = endDate,
                endTime = endTime
            )

            // ✅ You need an update call in your ItineraryRepository.
            // Example name (implement in repo): repo.updatePlan(tripId, updated)
            repo.updatePlan(tripId, updated)

            // optional: re-schedule reminder for new time
            val newEventMillis = startDate.atTime(startTime)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            notifVm.rescheduleItineraryReminder(
                tripId = tripId,
                currentUserId = currentUserId,
                itineraryId = planId,
                itemTitle = updated.title,
                itemTimeMillis = newEventMillis,
                remindBeforeMs = 60 * 60 * 1000L // 1 hour
            )

        }
    }

    private val _selectedExpense = MutableStateFlow<SelectedExpenseDetails?>(null)
    val selectedExpense = _selectedExpense.asStateFlow()

    fun clearSelectedExpense() {
        _selectedExpense.value = null
    }

    // 2. Logic to fetch full details (Payer + Splits)
    fun selectExpenseForPreview(expenseId: Long) {
        viewModelScope.launch {
            // A. Fetch the payment + Payer
            val payments = expenseRepo.observePaymentsForTripWithPayer(tripId).firstOrNull() ?: emptyList()
            val row = payments.find { it.payment.expensePaymentId == expenseId } ?: return@launch

            // B. Fetch the splits + Users
            val allSplits = expenseRepo.observeSplits(tripId).firstOrNull() ?: emptyList()
            val participants = tripDao.observeTripsForUser(currentUserId).firstOrNull() // This might be wrong DAO, let's use the one from ExpenseRepo if available or assume we have participants.
            // Actually, we can just use expenseRepo.observeParticipants if available or infer from splits.
            // Let's use the participants flow if we have it, or fetch it.
            // Since we don't have direct access to participants in this VM easily without adding a flow,
            // let's try to map splits using the 'participants' list from ExpenseRepo logic.

            // Simpler approach: Fetch participants from expenseRepo
            val users = expenseRepo.observeParticipants(tripId).firstOrNull() ?: emptyList()

            val relevantSplits = allSplits.filter { it.expensePaymentId == expenseId }
            val splitInfos = relevantSplits.mapNotNull { s ->
                val user = users.find { it.id == s.userId }
                if (user != null) {
                    SplitInfo(user.name, user.profilePhoto, s.amountOwed)
                } else null
            }

            val p = row.payment
            val u = row.payer
            val date = millisToLocalDate(p.paidAtMillis ?: p.dueAtMillis ?: System.currentTimeMillis())

            _selectedExpense.value = SelectedExpenseDetails(
                paymentId = p.expensePaymentId,
                title = p.title,
                amount = p.amount,
                category = p.category,
                date = date,
                payerName = u.name,
                payerPhotoUrl = u.profilePhoto,
                splits = splitInfos
            )
        }
    }

    // Helper to match ExpenseViewModel logic
    private fun millisToLocalDate(millis: Long): LocalDate =
        java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    // -----------------------------------------------------------------------
    // Factory helpers
    // -----------------------------------------------------------------------
    companion object {
        fun provideFactory(
            repo: ItineraryRepository,
            notifVm: NotificationViewModel,
            tripCommentDao: TripCommentDao,
            currentUserId: Long,
            expenseRepo: ExpenseRepository,
            tripDao: TripDao,
            tripId: Long
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ItineraryPlannerViewModel(
                    repo = repo,
                    notifVm = notifVm,
                    expenseRepo = expenseRepo,
                    tripCommentDao = tripCommentDao,
                    currentUserId = currentUserId,
                    tripId = tripId,
                    tripDao = tripDao
                )
            }
        }

    }
}