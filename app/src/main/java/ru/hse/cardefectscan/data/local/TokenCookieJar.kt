package ru.hse.cardefectscan.data.local

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class TokenCookieJar( // TODO persist
) : CookieJar {
    private val cookies = mutableMapOf<HttpUrl, MutableList<Cookie>>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        Log.d("TokenCookieJar", "load cookie for $url")
        return cookies[url] ?: mutableListOf()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        Log.d("TokenCookieJar", "save cookies $cookies from response $url")
        this.cookies[url] = cookies.toMutableList()
    }
}