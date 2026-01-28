package com.example.tripshare.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tripshare.data.db.TripDao
import com.example.tripshare.data.model.TripEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TripHeaderViewModel(
    private val tripDao: TripDao,
    private val tripId: Long
) : ViewModel() {

    // just expose the TripEntity so UI can read .name, .startDate, .endDate
    val trip: StateFlow<TripEntity?> =
        tripDao.observeById(tripId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

    companion object {
        fun provideFactory(
            tripDao: TripDao,
            tripId: Long
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TripHeaderViewModel(
                    tripDao = tripDao,
                    tripId = tripId
                )
            }
        }
    }
}
