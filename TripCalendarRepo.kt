package com.example.tripshare.data.repo

import com.example.tripshare.data.db.TripCalendarDao
import com.example.tripshare.data.model.TripNoteEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TripCalendarRepository(
    private val calendarDao: TripCalendarDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    // ✅ Sync ALL notes for this user (global + trip-tagged)
    fun startUserNotesSync(scope: CoroutineScope) {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .collection("notes")
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) return@addSnapshotListener

                scope.launch(Dispatchers.IO) {
                    for (doc in snapshots.documents) {
                        val fid = doc.id
                        val date = doc.getString("date") ?: ""
                        val noteContent = doc.getString("note") ?: ""
                        val tripId = doc.getLong("tripId")?.toLong() // ✅ nullable

                        val existing = calendarDao.getNoteByFirebaseId(fid)

                        val entity = existing?.copy(
                            tripId = tripId,
                            date = date,
                            note = noteContent
                        ) ?: TripNoteEntity(
                            firebaseId = fid,
                            tripId = tripId,
                            date = date,
                            note = noteContent
                        )

                        if (existing != null) {
                            calendarDao.updateNote(entity)
                        } else {
                            // de-dup by (tripId?, date)
                            val localSameKey = calendarDao.getNoteByDate(tripId, date)
                            if (localSameKey != null) {
                                calendarDao.updateNote(
                                    localSameKey.copy(firebaseId = fid, note = noteContent)
                                )
                            } else {
                                calendarDao.insertNote(entity)
                            }
                        }
                    }
                }
            }
    }

    // ✅ Save note "anywhere": tripId can be null
    suspend fun saveNote(tripId: Long?, date: String, content: String) {
        val existing = calendarDao.getNoteByDate(tripId, date)
        val noteId: Long

        if (existing != null) {
            val updated = existing.copy(note = content)
            calendarDao.updateNote(updated)
            noteId = existing.noteId
        } else {
            val newNote = TripNoteEntity(tripId = tripId, date = date, note = content)
            noteId = calendarDao.insertNote(newNote)
        }

        uploadNoteToCloud(noteId)
    }

    private suspend fun uploadNoteToCloud(noteId: Long) {
        val uid = auth.currentUser?.uid ?: return
        val note = calendarDao.getNoteById(noteId) ?: return

        val docRef = if (note.firebaseId != null) {
            firestore.collection("users").document(uid).collection("notes").document(note.firebaseId)
        } else {
            firestore.collection("users").document(uid).collection("notes").document()
        }

        if (note.firebaseId == null) {
            calendarDao.updateNote(note.copy(firebaseId = docRef.id))
        }

        val map = mutableMapOf<String, Any>(
            "date" to note.date,
            "note" to note.note,
            "updatedAt" to System.currentTimeMillis()
        )

        // only write tripId if tagged
        note.tripId?.let { map["tripId"] = it }

        docRef.set(map, SetOptions.merge())
    }
}
