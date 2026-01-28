package com.example.tripshare.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.model.WaitlistEntity
import com.example.tripshare.data.repo.WaitlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WaitlistViewModel(
    private val repo: WaitlistRepository,
    private val tripId: Long = -1L
) : ViewModel() {

    init {
        // ✅ START SYNC
        if (tripId != -1L) {
            viewModelScope.launch {
                repo.startWaitlistSync(tripId, viewModelScope)
            }
        }
    }
    private val userIdFlow = MutableStateFlow<Long?>(null)

    // ✅ One stable flow (Compose collects this safely)
    val myWaitlist: StateFlow<List<WaitlistEntity>> =
        userIdFlow
            .flatMapLatest { uid ->
                if (uid == null || uid <= 0L) {
                    kotlinx.coroutines.flow.flowOf(emptyList())
                } else {
                    repo.observeForUser(uid)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setUserId(userId: Long?) {
        userIdFlow.value = userId
    }

    fun toggleAlert(item: WaitlistEntity, enabled: Boolean) {
        viewModelScope.launch { repo.toggleAlert(item, enabled) }
    }
    fun joinTrip(userId: Long) {
        if (tripId == -1L) return
        viewModelScope.launch {
            repo.joinWaitlist(tripId, userId)
        }
    }

    fun leave(item: WaitlistEntity) {
        viewModelScope.launch {
            repo.leaveWaitlist(item)
        }
    }

    fun add(entry: WaitlistEntity) {
        viewModelScope.launch { repo.add(entry) }
    }

    fun remove(entry: WaitlistEntity) {
        viewModelScope.launch { repo.remove(entry) }
    }
}
