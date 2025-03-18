package ru.hse.cardefectscan.domain.model

import okhttp3.Cookie
import okhttp3.HttpUrl

data class CookieWithUrl(
    val cookie: Cookie,
    val url: HttpUrl,
)