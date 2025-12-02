package com.company.crm.data.repository

import com.company.crm.data.api.ApiService
import com.company.crm.data.prefs.AuthState
import com.company.crm.data.prefs.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class BaseRepository {
    protected abstract val api: ApiService
    protected abstract val prefs: UserPreferences

    protected suspend fun getCurrentEmployeeId(): Int {
        return prefs.getUserId() ?: throw IllegalStateException("User not authenticated")
    }
    protected fun getCurrentEmployeeIdFlow(): Flow<Int> {
        return prefs.authStateFlow.map { authState ->
            when (authState) {
                is AuthState.Authenticated -> authState.userId
                else -> throw IllegalStateException("User not authenticated")
            }
        }
    }
    protected suspend fun requireAdminRole() {
        val role = prefs.getRole()
        if (role != "ADMIN") {
            throw SecurityException("Admin role required")
        }
    }

    protected suspend fun getCurrentRole(): String {
        return prefs.getRole() ?: "EMPLOYEE"
    }
    protected fun requireAdminRoleFlow(): Flow<Unit> {
        return prefs.authStateFlow.map { authState ->
            when (authState) {
                is AuthState.Authenticated -> {
                    if (authState.role != "ADMIN") {
                        throw SecurityException("Admin role required")
                    }
                }
                else -> throw SecurityException("Authentication required")
            }
        }
    }

    protected fun requireManagerRoleFlow(): Flow<Unit> {
        return prefs.authStateFlow.map { authState ->
            when (authState) {
                is AuthState.Authenticated -> {
                    if (authState.role != "MANAGER" && authState.role != "ADMIN") {
                        throw SecurityException("Manager or Admin role required")
                    }
                }
                else -> throw SecurityException("Authentication required")
            }
        }
    }
}