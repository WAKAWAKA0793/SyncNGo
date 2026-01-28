// ui/trip/RoutePlannerVM.kt
package com.example.tripshare.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tripshare.data.repo.RouteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoutePlannerViewModel(
    private val repo: RouteRepository,
    private val tripId: Long
) : ViewModel() {

    val stops: StateFlow<List<RouteStopUi>> =
        repo.observeStopsUi(tripId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun incNights(stopId: Long) = viewModelScope.launch { repo.changeNights(stopId, +1) }
    fun decNights(stopId: Long) = viewModelScope.launch { repo.changeNights(stopId, -1) }

    companion object {
        fun provideFactory(repo: RouteRepository, tripId: Long): ViewModelProvider.Factory =
            viewModelFactory { initializer { RoutePlannerViewModel(repo, tripId) } }
    }
}
