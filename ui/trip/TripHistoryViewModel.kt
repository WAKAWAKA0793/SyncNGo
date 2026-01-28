package com.example.tripshare.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.repo.TripHistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class TripHistoryViewModel(
    private val repo: TripHistoryRepository
) : ViewModel() {

    val pastTrips = repo.observePastTrips()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList<TripEntity>())
}
