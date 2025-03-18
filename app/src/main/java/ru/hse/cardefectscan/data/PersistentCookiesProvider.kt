package ru.hse.cardefectscan.data

import okhttp3.Cookie
import okhttp3.HttpUrl

interface PersistentCookiesProvider {
    fun provideCookies(): MutableMap<HttpUrl, MutableList<Cookie>>
}