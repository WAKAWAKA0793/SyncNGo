package com.example.tripshare.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.model.TripCategory
import com.example.tripshare.data.repo.TripRepository
import com.example.tripshare.data.repo.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

// ... (Keep your Helper Data Classes & Functions exactly the same) ...
data class HomeUiState(
    val upcoming: List<TripUi> = emptyList(),
    val past: List<TripUi> = emptyList(),
    val discover: List<TripUi> = emptyList()
)

class HomeViewModel(
    private val repo: TripRepository,
    private val userRepo: UserRepository // 👈 Add this line!
) : ViewModel() {

    private val currentUserId = MutableStateFlow(-1L)
    fun setCurrentUserId(id: Long) { currentUserId.value = id }

    private val _selectedCategory = MutableStateFlow<TripCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    init {
        val authUser = FirebaseAuth.getInstance().currentUser
        if (authUser != null) {
            Log.d("HomeViewModel", "🔌 Starting Trip Sync for ${authUser.email}...")

            // 1. Sync Trips (TripRepository)
            repo.startUserTripsSync(authUser.uid, viewModelScope)

            // 2. Sync User Profile (UserRepository)
            // ✅ Now this works because we use 'userRepo'
            userRepo.startUserDocSync(viewModelScope, authUser.email ?: "")
        } else {
            Log.e("HomeViewModel", "❌ No logged in user. Cannot sync.")
        }
    }

    fun toggleCategory(category: TripCategory) {
        if (_selectedCategory.value == category) {
            _selectedCategory.value = null
        } else {
            _selectedCategory.value = category
        }
    }

    val allTrips: StateFlow<List<TripUi>> =
        repo.observeTrips()
            .map { entities ->
                val today = LocalDate.now()
                entities.map { entity ->
                    val status = if (entity.isArchived || entity.endDate?.isBefore(today) == true)
                        TripStatus.PAST else TripStatus.UPCOMING

                    entity.toUi(status)
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val uiState: StateFlow<HomeUiState> =
        currentUserId
            .filter { it != -1L }
            .flatMapLatest { uid ->
                combine(
                    allTrips,
                    repo.observeJoinedTripIds(uid),
                    _selectedCategory
                ) { all, joinedIds, catFilter ->

                    val upcomingJoined = all
                        .filter { it.status == TripStatus.UPCOMING && it.id in joinedIds }
                    val pastJoined = all
                        .filter { it.status == TripStatus.PAST && it.id in joinedIds }

                    val discover = all.filter { trip ->
                        val isNotJoined = trip.status == TripStatus.UPCOMING && trip.id !in joinedIds
                        val matchesCategory = if (catFilter == null) true else trip.category == catFilter.label
                        isNotJoined && matchesCategory
                    }

                    HomeUiState(
                        upcoming = upcomingJoined,
                        past = pastJoined,
                        discover = discover
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, HomeUiState())
}
/* ---------- date utils ---------- */


private val parseFormats = listOf(
    DateTimeFormatter.ISO_LOCAL_DATE,
    DateTimeFormatter.ofPattern("yyyy/MM/dd"),
    DateTimeFormatter.ofPattern("dd-MM-yyyy")
)

internal fun parseDate(raw: String?): LocalDate? {
    if (raw.isNullOrBlank()) return null
    for (fmt in parseFormats) runCatching { return LocalDate.parse(raw, fmt) }
    return null
}

private val niceFmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    .withLocale(Locale.getDefault())

internal fun formatDateRange(start: LocalDate?, end: LocalDate?): String = when {
    start != null && end != null -> "${start.format(niceFmt)} – ${end.format(niceFmt)}"
    start != null -> "From ${start.format(niceFmt)}"
    end != null -> "Until ${end.format(niceFmt)}"
    else -> "Anytime"
}