package com.example.tripshare.data.repo

import android.content.Context
import com.example.tripshare.data.AuthPrefs
import com.example.tripshare.data.db.TripDao
import com.example.tripshare.data.model.TripEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

class TripHistoryRepository(
    private val tripDao: TripDao,
    private val appContext: Context   // pass Application context
) {

    /**
     * Observe past trips for the currently logged-in user.
     */
    fun observePastTrips(): Flow<List<TripEntity>> {
        return AuthPrefs.getUserId(appContext)
            .filterNotNull()
            .flatMapLatest { userId ->
                tripDao.observeArchivedTripsForUser(userId)
            }
    }
}

