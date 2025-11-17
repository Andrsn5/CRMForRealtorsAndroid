package com.company.crm.data.prefs

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data class Authenticated(
        val token: String,
        val userId: Int,
        val role: String?
    ) : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}