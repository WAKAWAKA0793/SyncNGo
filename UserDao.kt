package com.example.tripshare.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import com.example.tripshare.data.model.EmergencyContactEntity
import com.example.tripshare.data.model.InsurancePolicyEntity
import com.example.tripshare.data.model.LocalNotificationEntity
import com.example.tripshare.data.model.UserEntity
import kotlinx.coroutines.flow.Flow


data class UserWithContacts(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val contacts: List<EmergencyContactEntity>
)

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT id FROM users WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findUserIdByExactName(name: String): Long?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    fun observeUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUserById(userId: Long): Flow<UserEntity?>

    @Query("SELECT name FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUsername(userId: Long): String?

    @Query("UPDATE users SET profilePhoto = :url WHERE id = :userId")
    suspend fun updateAvatarUrl(userId: Long, url: String)

    @Query("SELECT name FROM users WHERE id = :id LIMIT 1")
    fun observeUserName(id: Long): kotlinx.coroutines.flow.Flow<String?>

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?


    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun emailExists(email: String): Int

    @Query("SELECT * FROM users WHERE firebaseId = :fid LIMIT 1")
    suspend fun findByFirebaseId(fid: String): UserEntity?

    @Query("SELECT DISTINCT userId FROM trip_participants WHERE tripId = :tripId")
    suspend fun getUserIdsForTrip(tripId: Long): List<Long>

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("""
        SELECT u.* FROM users u
        INNER JOIN trip_participants tp ON u.id = tp.userId
        WHERE tp.tripId = :tripId
    """)
    fun observeParticipants(tripId: Long): Flow<List<UserEntity>>
}

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts WHERE userId=:userId")
    fun observeByUserId(userId: Long): Flow<List<EmergencyContactEntity>>

    @Query("SELECT * FROM emergency_contacts WHERE userId=:userId")
    suspend fun getByUserId(userId: Long): List<EmergencyContactEntity>

    @Insert suspend fun insertAll(list: List<EmergencyContactEntity>)
    @Query("DELETE FROM emergency_contacts WHERE userId=:userId")
    suspend fun deleteByUserId(userId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: EmergencyContactEntity): Long

    @Update
    suspend fun update(contact: EmergencyContactEntity)

    @Delete
    suspend fun delete(contact: EmergencyContactEntity)

    @Query("SELECT * FROM emergency_contacts WHERE firebaseId = :fid LIMIT 1")
    suspend fun getByFirebaseId(fid: String): EmergencyContactEntity?

}

@Dao
interface LocalNotificationDao {
    @Query("SELECT * FROM local_notifications WHERE recipientId = :userId ORDER BY timestamp DESC")
    fun observeForUser(userId: Long): Flow<List<LocalNotificationEntity>>

    @Insert
    suspend fun insert(n: LocalNotificationEntity)

    @Query("UPDATE local_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: Long)

    @Delete
    suspend fun delete(n: LocalNotificationEntity)

    @Query("DELETE FROM local_notifications")
    suspend fun clearAll()
}

@Dao
interface InsuranceDao {
    @Query("SELECT * FROM insurance_policies WHERE userId = :userId LIMIT 1")
    fun observePolicy(userId: Long): Flow<InsurancePolicyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolicy(policy: InsurancePolicyEntity)

    @Query("DELETE FROM insurance_policies WHERE userId = :userId")
    suspend fun deletePolicy(userId: Long)
}