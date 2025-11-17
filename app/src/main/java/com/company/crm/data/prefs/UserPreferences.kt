package com.company.crm.data.prefs

import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    suspend fun saveAuthData(token: String, userId: Int, role: String?)
    suspend fun clear()
    suspend fun getToken(): String?
    suspend fun getUserId(): Int?
    suspend fun getRole(): String?
    val authStateFlow: Flow<AuthState>
}