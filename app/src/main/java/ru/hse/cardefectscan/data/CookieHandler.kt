package ru.hse.cardefectscan.data

import okhttp3.Cookie
import okhttp3.HttpUrl

interface CookieHandler {
    fun handleCookies(url: HttpUrl, cookiesResponse: List<Cookie>)
}