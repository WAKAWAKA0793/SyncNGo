package com.example.tripshare.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// One DataStore instance per app process
val Context.dataStore by preferencesDataStore("auth_prefs")

object AuthPrefs {
    // Keys
    private val KEY_REGISTERED   = booleanPreferencesKey("registered")
    private val KEY_USER_ID      = longPreferencesKey("user_id")
    private val KEY_EMAIL        = stringPreferencesKey("email")
    private val KEY_PASSWORD     = stringPreferencesKey("password")
    private val KEY_DISPLAY_NAME = stringPreferencesKey("display_name")

    // ─────────────────────────────────────────────
    // Reads (Flows return null when not set)
    // ─────────────────────────────────────────────
    fun isRegistered(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_REGISTERED] ?: false }

    fun getUserId(context: Context): Flow<Long?> =
        context.dataStore.data.map { it[KEY_USER_ID] }

    fun getEmail(context: Context): Flow<String?> =
        context.dataStore.data.map { it[KEY_EMAIL] }

    fun getDisplayName(context: Context): Flow<String?> =
        context.dataStore.data.map { it[KEY_DISPLAY_NAME] }

    /**
     * For legacy callers; returns Pair(email, password) which may be nulls if not set.
     */
    fun getCredentials(context: Context): Flow<Pair<String?, String?>> =
        context.dataStore.data.map { prefs -> prefs[KEY_EMAIL] to prefs[KEY_PASSWORD] }

    // ─────────────────────────────────────────────
    // Writes
    // ─────────────────────────────────────────────
    suspend fun setUserId(context: Context, userId: Long?) {
        context.dataStore.edit { prefs ->
            if (userId == null) prefs.remove(KEY_USER_ID) else prefs[KEY_USER_ID] = userId
        }
    }

    suspend fun setEmail(context: Context, email: String?) {
        context.dataStore.edit { prefs ->
            if (email.isNullOrBlank()) prefs.remove(KEY_EMAIL) else prefs[KEY_EMAIL] = email
        }
    }

    suspend fun setDisplayName(context: Context, name: String?) {
        context.dataStore.edit { prefs ->
            if (name.isNullOrBlank()) prefs.remove(KEY_DISPLAY_NAME) else prefs[KEY_DISPLAY_NAME] = name
        }
    }

    /**
     * Convenience setter to update all core identity fields at once.
     */
    suspend fun setUser(
        context: Context,
        userId: Long?,
        displayName: String?,
        email: String?,
        password: String? = null,
        registered: Boolean? = null
    ) {
        context.dataStore.edit { prefs ->
            if (userId == null) prefs.remove(KEY_USER_ID) else prefs[KEY_USER_ID] = userId
            if (displayName.isNullOrBlank()) prefs.remove(KEY_DISPLAY_NAME) else prefs[KEY_DISPLAY_NAME] = displayName
            if (email.isNullOrBlank()) prefs.remove(KEY_EMAIL) else prefs[KEY_EMAIL] = email
            if (password.isNullOrBlank()) prefs.remove(KEY_PASSWORD) else prefs[KEY_PASSWORD] = password
            if (registered != null) prefs[KEY_REGISTERED] = registered
        }
    }
    suspend fun setGuest(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[KEY_REGISTERED] = false
            prefs[KEY_USER_ID] = -1L
            prefs.remove(KEY_EMAIL)
            prefs.remove(KEY_PASSWORD)
            prefs.remove(KEY_DISPLAY_NAME)
        }
    }

    /**
     * Original "registration" helper, now with optional displayName.
     * Keeps backward compatibility with your existing calls.
     */
    suspend fun saveRegistration(
        context: Context,
        userId: Long,
        email: String,
        password: String,
        displayName: String? = null
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_REGISTERED] = true
            prefs[KEY_USER_ID] = userId
            prefs[KEY_EMAIL] = email
            prefs[KEY_PASSWORD] = password
            if (!displayName.isNullOrBlank()) prefs[KEY_DISPLAY_NAME] = displayName
        }
    }

    /**
     * Clears everything (logout).
     */
    suspend fun logout(context: Context) {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}
