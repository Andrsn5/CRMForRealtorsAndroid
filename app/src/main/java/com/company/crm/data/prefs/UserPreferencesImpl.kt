package com.company.crm.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferences {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("key_token")
        private val KEY_USER_ID = intPreferencesKey("key_user_id")
        private val KEY_ROLE = stringPreferencesKey("key_role")
    }

    override suspend fun saveAuthData(token: String, userId: Int, role: String?) {
        dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_ID] = userId
            role?.let { prefs[KEY_ROLE] = it }
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    override suspend fun getToken(): String? =
        dataStore.data.map { it[KEY_TOKEN] }.first()

    override suspend fun getUserId(): Int? =
        dataStore.data.map { it[KEY_USER_ID] }.first()

    override suspend fun getRole(): String? =
        dataStore.data.map { it[KEY_ROLE] }.first()

    override val authStateFlow: Flow<AuthState> =
        dataStore.data.map { prefs ->
            val token = prefs[KEY_TOKEN]
            val userId = prefs[KEY_USER_ID]
            val role = prefs[KEY_ROLE]

            when {
                token == null || userId == null -> AuthState.Unauthenticated
                else -> AuthState.Authenticated(token, userId, role)
            }
        }.catch { exception ->
            AuthState.Error("Failed to read auth state: ${exception.message}")
        }
}