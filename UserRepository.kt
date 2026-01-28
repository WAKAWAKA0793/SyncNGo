package com.example.tripshare.data.repo

import android.util.Log
import com.example.tripshare.data.db.UserDao
import com.example.tripshare.data.model.UserEntity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun updateFcmToken(userId: Long, token: String) {
        try {
            firestore
                .collection("users")
                .document(userId.toString())
                .update("fcmToken", token)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
    // Helper: Use email as doc id
    private fun userDoc(email: String) =
        firestore.collection("users").document(email.lowercase())

    // 1. Fetch user data from Firestore (Single Shot)
    fun getUserData(email: String) {
        val userDoc = userDoc(email)

        userDoc.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserEntity::class.java)
                    Log.d("Firestore", "User Data: ${user?.name}, ${user?.email}")
                } else {
                    Log.d("Firestore", "No such document!")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting document", e)
            }
    }

    // 2. Create User: Room first, then Cloud
    suspend fun createUser(user: UserEntity): Long {
        val localId = userDao.insertUser(user)
        saveUserToCloud(user.copy(id = localId))
        return localId
    }

    // 3. Update User: Update Room, then Sync to Cloud
    suspend fun updateUser(user: UserEntity) {
        // Update Room first
        userDao.updateUser(user)

        // Then sync with Firestore
        saveUserToCloud(user)

        // (Optional) Update specific fields directly if needed, though saveUserToCloud covers this
        updateUserProfilePhotoInFirestore(user.email, user.profilePhoto)
    }

    // 4. Update Avatar URL specific helper
    suspend fun setAvatarUrl(userId: Long, url: String) {
        val u = userDao.getUserById(userId) ?: return
        val updated = u.copy(profilePhoto = url)
        userDao.updateUser(updated)
        saveUserToCloud(updated)
    }

    // 5. Sync Listener: Listen for Cloud changes and update Room
    fun startUserDocSync(
        scope: CoroutineScope,
        email: String
    ): com.google.firebase.firestore.ListenerRegistration {

        return userDoc(email)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, exception ->
                if (exception != null || snapshot == null || !snapshot.exists()) {
                    return@addSnapshotListener
                }

                // Avoid sync loop by checking for pending writes
                if (snapshot.metadata.hasPendingWrites()) return@addSnapshotListener

                scope.launch(Dispatchers.IO) {
                    val localUser = userDao.findByEmail(email)

                    // Get the latest data from Firestore
                    val cloudName = snapshot.getString("name")
                    val cloudPhone = snapshot.getString("phoneNumber")
                    val cloudPhoto = snapshot.getString("profilePhoto")
                    val cloudLocation = snapshot.getString("location") ?: ""
                    val cloudBio = snapshot.getString("bio") ?: ""
                    val verified = snapshot.getBoolean("verified") ?: false

                    // Update Room with the latest Firestore data
                    if (localUser != null) {
                        userDao.updateUser(
                            localUser.copy(
                                name = cloudName ?: localUser.name,
                                phoneNumber = cloudPhone ?: localUser.phoneNumber,
                                profilePhoto = cloudPhoto ?: localUser.profilePhoto,
                                location = cloudLocation,
                                bio = cloudBio,
                                verified = verified,
                                firebaseId = snapshot.id
                            )
                        )
                    } else {
                        // Insert new user into Room if it doesn't exist locally
                        userDao.insertUser(
                            UserEntity(
                                email = email,
                                name = cloudName ?: "Unknown",
                                phoneNumber = cloudPhone,
                                profilePhoto = cloudPhoto,
                                location = cloudLocation,
                                bio = cloudBio,
                                verified = verified,
                                firebaseId = snapshot.id
                            )
                        )
                    }
                }
            }
    }

    // 6. Check if user exists in Cloud (used during registration)
    suspend fun checkUserExists(email: String): Boolean {
        return try {
            val snapshot = userDoc(email).get().await()
            snapshot.exists()
        } catch (e: Exception) {
            false
        }
    }

    // ─── PRIVATE HELPERS ───

    // Cloud Write Helper
    private suspend fun saveUserToCloud(user: UserEntity) {
        // ❌ DELETE THIS TRY-CATCH BLOCK
        // try {
        val map = hashMapOf(
            "name" to user.name,
            "email" to user.email,
            "icNumber" to user.icNumber,
            "phoneNumber" to user.phoneNumber,
            "location" to user.location,
            "bio" to user.bio,
            "profilePhoto" to user.profilePhoto,
            "verified" to user.verified,
            "createdAt" to user.createdAt,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        // ✅ Allow the exception to crash/propagate so the ViewModel catches it
        userDoc(user.email).set(map, SetOptions.merge()).await()

        Log.d("Firestore", "Success: User saved to cloud: ${user.email}")

        // } catch (e: Exception) {
        //     e.printStackTrace()
        // }
    }

    private fun updateUserProfilePhotoInFirestore(email: String, newAvatarUrl: String?) {
        val userRef = firestore.collection("users").document(email.lowercase())

        userRef.update("profilePhoto", newAvatarUrl)
            .addOnSuccessListener {
                Log.d("UserRepository", "User profile updated in Firestore")
            }
            .addOnFailureListener { e ->
                Log.w("UserRepository", "Error updating user profile in Firestore", e)
            }
    }
    suspend fun findByEmail(email: String): UserEntity? {
        return userDao.findByEmail(email)
    }

    suspend fun syncUserFromCloud(email: String): Boolean {
        return try {
            // 1. Get the document from Firestore
            val snapshot = userDoc(email).get().await()

            if (snapshot.exists()) {
                // 2. Convert to UserEntity
                // We manually map fields to ensure we don't overwrite the local ID if it exists
                val cloudName = snapshot.getString("name") ?: ""
                val cloudPhone = snapshot.getString("phoneNumber")
                val cloudPhoto = snapshot.getString("profilePhoto")
                val cloudBio = snapshot.getString("bio") ?: ""
                val cloudLocation = snapshot.getString("location") ?: ""
                val cloudIc = snapshot.getString("icNumber")
                val verified = snapshot.getBoolean("verified") ?: false
                val firebaseId = snapshot.id

                // 3. Check if we already have this user locally
                val localUser = userDao.findByEmail(email)

                if (localUser == null) {
                    // CASE A: New Login on this device -> Insert
                    val newUser = UserEntity(
                        name = cloudName,
                        email = email,
                        icNumber = cloudIc,
                        phoneNumber = cloudPhone,
                        profilePhoto = cloudPhoto,
                        bio = cloudBio,
                        location = cloudLocation,
                        verified = verified,
                        firebaseId = firebaseId
                    )
                    userDao.insertUser(newUser)
                } else {
                    // CASE B: User exists -> Update fields to match Cloud
                    val updatedUser = localUser.copy(
                        name = cloudName,
                        phoneNumber = cloudPhone,
                        profilePhoto = cloudPhoto,
                        bio = cloudBio,
                        location = cloudLocation,
                        verified = verified,
                        firebaseId = firebaseId
                    )
                    userDao.updateUser(updatedUser)
                }
                true // Sync success
            } else {
                false // User not found in cloud
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false // Network error
        }
    }
    // ─── DAO WRAPPERS ───

    suspend fun getUserById(userId: Long): UserEntity? =
        userDao.getUserById(userId)

    suspend fun emailTaken(email: String): Boolean =
        userDao.emailExists(email) > 0


    fun observeUserById(userId: Long): Flow<UserEntity?> =
        userDao.observeUserById(userId)
}