package ru.hse.cardefectscan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import ru.hse.cardefectscan.data.local.SimpleCookieJar
import ru.hse.cardefectscan.data.remote.ExceptionHandlerInterceptor
import ru.hse.cardefectscan.data.remote.LoggingInterceptor
import ru.hse.cardefectscan.data.remote.TokenInterceptor
import ru.hse.cardefectscan.domain.repository.AuthRepository
import ru.hse.cardefectscan.domain.usecase.AuthUseCase
import ru.hse.cardefectscan.utils.SERVER_BASE_URL
import ru.hse.generated.apis.AuthApi
import java.io.File
import javax.inject.Qualifier

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
        authRepository: AuthRepository,
    ): okhttp3.CookieJar = SimpleCookieJar(
        handlers = listOf(authRepository),
        persistentCookiesProvider = authRepository,
    )

    @Provides
    @AuthOkHttpClient
    fun provideAuthClient(
        cookieJar: CookieJar,
        cache: Cache,
        interceptors: Set<@JvmSuppressWildcards Interceptor>,
    ): OkHttpClient {
        val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .cache(cache)
        interceptors.forEach {
            client.addInterceptor(it)
        }
        return client.build()
    }

    @Provides
    @DefaultOkHttpClient
    fun provideClient(
        cookieJar: CookieJar,
        cache: Cache,
        interceptors: Set<@JvmSuppressWildcards Interceptor>,
        tokenInterceptor: TokenInterceptor,
    ): OkHttpClient {
        val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(tokenInterceptor)
            .cache(cache)
        interceptors.forEach {
            client.addInterceptor(it)
        }
        return client.build()
    }

    @Provides
    fun provideAuthApi(
        baseUrl: String,
        @AuthOkHttpClient client: OkHttpClient,
    ): AuthApi = AuthApi(
        basePath = baseUrl,
        client = client
    )

    companion object {
        const val CACHE_NAME = "httpCache"
        const val CACHE_SIZE: Long = 100 * 1024 * 1024 // 100 MB
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultOkHttpClient