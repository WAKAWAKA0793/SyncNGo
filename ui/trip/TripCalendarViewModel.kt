package com.example.tripshare.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.db.TripCalendarDao
import com.example.tripshare.data.model.CalendarEventEntity
import com.example.tripshare.data.model.TripDocumentEntity
import com.example.tripshare.data.model.TripEntity
import com.example.tripshare.data.model.TripNoteEntity
import com.example.tripshare.data.repo.TripCalendarRepository
import com.example.tripshare.data.repo.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TripCalendarViewModel(
    private val tripRepository: TripRepository,
    private val calendarDao: TripCalendarDao, // Keep for read operations if needed
    // 👇 Inject the new Repo (or pass TripDao and init it here)
    private val calendarRepo: TripCalendarRepository,
    private val currentUserId: Long,
    private val tripId: Long
) : ViewModel() {

    init {
        // ✅ now sync user notes once (includes notes for this trip too)
        calendarRepo.startUserNotesSync(viewModelScope)
    }

    fun saveNote(tripId: Long, date: LocalDate, content: String) {
        viewModelScope.launch {
            calendarRepo.saveNote(
                tripId = tripId,              // trip note
                date = date.toString(),
                content = content
            )
        }
    }

    // ✅ New: save note anywhere (global note)
    fun saveGlobalNote(date: LocalDate, content: String) {
        viewModelScope.launch {
            calendarRepo.saveNote(
                tripId = null,                // global note
                date = date.toString(),
                content = content
            )
        }
    }
    /**
     * Observe the Trip details (to get Start/End dates).
     */
    fun observeTrip(tripId: Long): Flow<TripEntity?> {
        return tripRepository.observeTrip(tripId)
    }

    /**
     * Observe all calendar events for the trip.
     */
    fun events(tripId: Long): Flow<List<CalendarEventEntity>> {
        return calendarDao.observeEvents(tripId)
    }

    /**
     * Observe all documents for the trip.
     */
    fun documents(tripId: Long): Flow<List<TripDocumentEntity>> {
        return calendarDao.observeDocs(tripId)
    }

    /**
     * Observe all daily notes (reminders) for the trip.
     */
    fun notes(tripId: Long): Flow<List<TripNoteEntity>> {
        return calendarDao.observeNotes(tripId)
    }

    val activeTrips: Flow<List<TripEntity>> =
        tripRepository.observeJoinedActiveTrips(currentUserId)

    /**
     * Saves the note.
     */


}