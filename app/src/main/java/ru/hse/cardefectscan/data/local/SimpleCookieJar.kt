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
            .also { Log.d("SimpleCookieJar", "Provided cookies: $it") }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        Log.d("SimpleCookieJar", "trying to load cookie for $url")
        val cookiesForUrl = cookies[url] ?: mutableListOf()
        Log.d("SimpleCookieJar", "Cookies: $cookiesForUrl")
        return cookiesForUrl
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        Log.d("SimpleCookieJar", "save cookies $cookies from response $url")
        this.cookies[url] = cookies.toMutableList()
        handlers.forEach {
            it.handleCookies(url, cookies)
        }
    }
}