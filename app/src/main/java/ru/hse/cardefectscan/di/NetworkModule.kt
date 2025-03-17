package ru.hse.cardefectscan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import ru.hse.cardefectscan.data.local.TokenCookieJar
import ru.hse.cardefectscan.utils.SERVER_BASE_URL
import ru.hse.generated.apis.AuthApi
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    fun provideBaseUrl(): String = SERVER_BASE_URL

    @Provides
    fun provideCache(
        @ApplicationContext context: Context
    ): Cache = Cache(
        File(context.cacheDir, CACHE_NAME),
        CACHE_SIZE,
    )

    @Provides
    fun provideCookieJar(
    ): CookieJar = TokenCookieJar()

    @Provides
    fun provideClient(
        cookieJar: CookieJar,
        cache: Cache,
    ): OkHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .cache(cache)
        .build()

    @Provides
    fun provideAuthClient(
        baseUrl: String,
        client: OkHttpClient,
    ): AuthApi = AuthApi(
        basePath = baseUrl,
        client = client
    )

    companion object {
        const val CACHE_NAME = "httpCache"
        const val CACHE_SIZE: Long = 100 * 1024 * 1024 // 100 MB
    }
}