package ru.hse.cardefectscan.data.local

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import ru.hse.cardefectscan.data.CookieHandler
import ru.hse.cardefectscan.data.PersistentCookiesProvider

class SimpleCookieJar(
    private val handlers: List<CookieHandler>,
    private val persistentCookiesProvider: PersistentCookiesProvider,
) : CookieJar {
    private val cookies by lazy {
        persistentCookiesProvider.provideCookies()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        Log.d("TokenCookieJar", "trying to load cookie for $url")
        return cookies[url] ?: mutableListOf()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        Log.d("TokenCookieJar", "save cookies $cookies from response $url")
        this.cookies[url] = cookies.toMutableList()
        handlers.forEach {
            it.handleCookies(url, cookies)
        }
    }
}