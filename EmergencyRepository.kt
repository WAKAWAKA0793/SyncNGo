package com.example.tripshare.data.repo

import android.util.Log
import com.example.tripshare.data.db.EmergencyContactDao
import com.example.tripshare.data.db.InsuranceDao
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.model.EmergencyContactEntity
import com.example.tripshare.data.model.InsurancePolicyEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EmergencyRepository(
    private val dao: EmergencyContactDao,
    private val insuranceDao: InsuranceDao,
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun startSync(userId: Long, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val user = userDao.getUserById(userId) ?: return@launch
            val userFid = user.firebaseId ?: return@launch

            // Listen to users/{uid}/emergency_contacts
            firestore.collection("users").document(userFid).collection("emergency_contacts")
                .addSnapshotListener { snapshots, e ->
                    if (e != null || snapshots == null) return@addSnapshotListener
                    if (snapshots.metadata.hasPendingWrites()) return@addSnapshotListener

                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshots.documents) {
                            val fid = doc.id
                            val name = doc.getString("name") ?: ""
                            val rel = doc.getString("relationship") ?: ""
                            val phone = doc.getString("phone") ?: ""

                            val existing = dao.getByFirebaseId(fid)
                            val entity = existing?.copy(
                                name = name,
                                relationship = rel,
                                phone = phone
                            ) ?: EmergencyContactEntity(
                                userId = userId,
                                name = name,
                                relationship = rel,
                                phone = phone,
                                firebaseId = fid
                            )

                            if (existing != null) dao.update(entity) else dao.insert(entity)
                        }

                        // Handle deletions: If a doc is missing in snapshot but exists locally with a firebaseId, delete it.
                        // (Optional: Requires more complex diffing logic, omitted for brevity.
                        // The simple listener usually handles additions/updates well.)
                    }
                }
        }
    }

    // ─────────────────────────── CRUD + CLOUD PUSH ───────────────────────────

    suspend fun updateContact(contact: EmergencyContactEntity) {
        dao.update(contact)
        uploadContact(contact)
    }


    suspend fun addContact(contact: EmergencyContactEntity) {
        // 1. Insert Locally first
        val localId = dao.insert(contact)
        Log.d("EmergencyRepo", "Locally inserted contact ID: $localId")

        // 2. Upload to Cloud
        try {
            uploadContact(contact.copy(id = localId))
            Log.d("EmergencyRepo", "Successfully uploaded contact $localId to Firestore")
        } catch (e: Exception) {
            Log.e("EmergencyRepo", "Failed to upload contact $localId", e)
        }
    }

    private suspend fun uploadContact(contact: EmergencyContactEntity) {
        // A. Find the local user
        val user = userDao.getUserById(contact.userId)

        if (user == null) {
            Log.e("EmergencyRepo", "UPLOAD FAILED: User ${contact.userId} not found in Room database.")
            return
        }

        // B. Get Firebase ID (Check DB first, then Fallback to Auth)
        var userFid = user.firebaseId

        if (userFid.isNullOrBlank()) {
            Log.w("EmergencyRepo", "User ${contact.userId} has no firebaseId in DB. Checking FirebaseAuth...")
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                userFid = currentUser.uid
                // Self-Heal: Update local DB so we don't have to ask next time
                userDao.updateUser(user.copy(firebaseId = userFid))
                Log.d("EmergencyRepo", "Repaired missing firebaseId in local DB: $userFid")
            } else {
                Log.e("EmergencyRepo", "UPLOAD FAILED: User is not logged in to Firebase.")
                return
            }
        }

        // C. Prepare the Document Reference
        // Path: users/{userFid}/emergency_contacts/{contactFid}
        val collectionRef = firestore.collection("users").document(userFid!!)
            .collection("emergency_contacts")

        val docRef = if (contact.firebaseId != null) {
            collectionRef.document(contact.firebaseId)
        } else {
            collectionRef.document() // Generate new ID
        }

        // D. Update local with new FID if needed (so we don't create duplicates later)
        if (contact.firebaseId == null) {
            dao.update(contact.copy(firebaseId = docRef.id))
        }

        val map = mapOf(
            "name" to contact.name,
            "relationship" to contact.relationship,
            "phone" to contact.phone,
            "updatedAt" to System.currentTimeMillis()
        )

        // E. FORCE WAIT (.await)
        Log.d("EmergencyRepo", "Uploading to ${docRef.path}...")
        docRef.set(map, SetOptions.merge()).await()
    }

    suspend fun deleteContact(contact: EmergencyContactEntity) {
        // 1. Local Delete
        dao.delete(contact)

        // 2. Cloud Delete
        val fid = contact.firebaseId
        if (fid == null) {
            Log.w("EmergencyRepo", "Contact has no firebaseId, cannot delete remotely.")
            return
        }

        val user = userDao.getUserById(contact.userId)
        val userFid = user?.firebaseId

        if (userFid != null) {
            try {
                firestore.collection("users").document(userFid)
                    .collection("emergency_contacts").document(fid)
                    .delete()
                    .await() // CRITICAL FIX: Wait for deletion
            } catch (e: Exception) {
                Log.e("EmergencyRepo", "Remote delete failed", e)
            }
        }
    }

    // ─────────────────────────── EXISTING READS ───────────────────────────

    fun contacts(userId: Long): Flow<List<EmergencyContactEntity>> =
        dao.observeByUserId(userId)

    fun getInsurance(userId: Long): Flow<InsurancePolicyEntity?> =
        insuranceDao.observePolicy(userId)

    suspend fun saveInsurance(policy: InsurancePolicyEntity) {
        insuranceDao.insertPolicy(policy)
    }

    // Legacy helper (optional keep)
    suspend fun replaceContacts(userId: Long, contacts: List<EmergencyContactEntity>) {
        dao.deleteByUserId(userId)
        if (contacts.isNotEmpty()) dao.insertAll(contacts)
    }


    /** One-shot load */
    suspend fun getContacts(userId: Long): List<EmergencyContactEntity> =
        dao.getByUserId(userId)



    /** Convenience for initial save after sign-up */
    suspend fun insertAllWithUserId(userId: Long, contacts: List<EmergencyContactEntity>) {
        contacts.forEach { contact ->
            // addContact() handles both Room insert and Firestore upload
            addContact(contact.copy(userId = userId))
        }
    }

}
