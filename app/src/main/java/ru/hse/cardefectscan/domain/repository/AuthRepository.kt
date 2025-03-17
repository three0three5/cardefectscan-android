package ru.hse.cardefectscan.domain.repository

import android.content.Context

class AuthRepository(context: Context) {
    private val prefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)

    fun getToken(): RefreshToken? {
        val value = prefs.getString(TOKEN_LABEL, null) ?: return null
        val token = RefreshToken(
            value,
            prefs.getLong(EXPIRATION_LABEL, 0),
        )
        return token
    }

    fun saveToken(token: String, expiresAt: Long) {
        prefs.edit()
            .putString(TOKEN_LABEL, token)
            .putLong(EXPIRATION_LABEL, System.currentTimeMillis() + expiresAt)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    data class RefreshToken(
        val value: String,
        val expiresAt: Long,
    )

    companion object {
        const val TOKEN_LABEL = "refresh_token"
        const val AUTH_PREFS = "auth_prefs"
        const val EXPIRATION_LABEL = "token_expiration"
    }
}

