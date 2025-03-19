package ru.hse.cardefectscan.domain.repository

import android.content.Context
import android.util.Log
import okhttp3.Cookie
import okhttp3.HttpUrl
import ru.hse.cardefectscan.data.CookieHandler
import ru.hse.cardefectscan.data.PersistentCookiesProvider
import ru.hse.cardefectscan.domain.model.CookieWithUrl
import ru.hse.generated.infrastructure.Serializer

class AuthRepository(
    context: Context,
) : PersistentCookiesProvider, CookieHandler {
    private val prefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
    var jwtToken = ""

    fun getRefreshToken(): CookieWithUrl? {
        val value = prefs.getString(TOKEN_LABEL, null) ?: return null
        val cookie = Serializer.moshi.adapter(CookieWithUrl::class.java).fromJson(value)
        return cookie
    }

    override fun provideCookies(): MutableMap<HttpUrl, MutableList<Cookie>> {
        val token = getRefreshToken() ?: return mutableMapOf()
        return mutableMapOf(
            token.url to mutableListOf(token.cookie)
        )
    }

    override fun handleCookies(url: HttpUrl, cookies: List<Cookie>) {
        Log.d("AuthRepository", "handle cookies")
        val cookie = cookies.filter {
            it.name == TOKEN_LABEL
        }.getOrNull(0) ?: return
        Log.d("AuthRepository", "save $cookie")
        saveRefreshToken(
            CookieWithUrl(
                url = url,
                cookie = cookie,
            )
        )
    }

    fun clear() {
        jwtToken = ""
        prefs.edit().clear().apply()
    }

    private fun saveRefreshToken(token: CookieWithUrl) {
        val json = Serializer.moshi.adapter(CookieWithUrl::class.java).toJson(token)
        prefs.edit()
            .putString(TOKEN_LABEL, json)
            .apply()
    }

    companion object {
        const val TOKEN_LABEL = "refresh_token"
        const val AUTH_PREFS = "auth_prefs"
    }
}

