package ru.hse.cardefectscan.domain.repository

import android.content.Context

class AuthRepository(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun isAuthenticated(): Boolean {
        val token = prefs.getString("refresh_token", null) ?: return false
        val expiration = prefs.getLong("token_expiration", 0L)
        return System.currentTimeMillis() < expiration
    }

    fun saveToken(token: String, expiresIn: Long) {
        prefs.edit()
            .putString("refresh_token", token)
            .putLong("token_expiration", System.currentTimeMillis() + expiresIn)
            .apply()
    }

    fun clearAuth() {
        prefs.edit().clear().apply()
    }
}

