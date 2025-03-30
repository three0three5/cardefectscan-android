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
import ru.hse.cardefectscan.data.remote.MinioClient
import ru.hse.cardefectscan.data.remote.TokenInterceptor
import ru.hse.cardefectscan.domain.repository.AuthRepository
import ru.hse.cardefectscan.utils.SERVER_BASE_URL
import ru.hse.generated.apis.AuthApi
import ru.hse.generated.apis.ImagesApi
import ru.hse.generated.apis.RequestsApi
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideBaseUrl(): String = SERVER_BASE_URL

    @Provides
    @Singleton
    fun provideCache(
        @ApplicationContext context: Context
    ): Cache = Cache(
        File(context.cacheDir, CACHE_NAME),
        CACHE_SIZE,
    )

    @Provides
    @Singleton
    fun provideCookieJar(
        authRepository: AuthRepository,
    ): CookieJar = SimpleCookieJar(
        handlers = listOf(authRepository),
        persistentCookiesProvider = authRepository,
    )

    @Provides
    @DefaultOkHttpClient
    @Singleton
    fun provideClient(
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
    @AuthenticatedOkHttpClient
    @Singleton
    fun provideAuthenticatedClient(
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
    @Singleton
    fun provideAuthApi(
        baseUrl: String,
        @DefaultOkHttpClient client: OkHttpClient,
    ): AuthApi = AuthApi(
        basePath = baseUrl,
        client = client
    )

    @Provides
    @Singleton
    fun provideMinioClient(
        @DefaultOkHttpClient client: OkHttpClient,
        @ApplicationContext context: Context,
    ): MinioClient = MinioClient(
        client,
        context,
    )

    @Provides
    @Singleton
    fun provideImagesApi(
        baseUrl: String,
        @AuthenticatedOkHttpClient client: OkHttpClient,
    ): ImagesApi = ImagesApi(
        basePath = baseUrl,
        client = client,
    )

    @Provides
    @Singleton
    fun provideRequestsApi(
        baseUrl: String,
        @AuthenticatedOkHttpClient client: OkHttpClient,
    ): RequestsApi = RequestsApi(
        basePath = baseUrl,
        client = client,
    )

    companion object {
        const val CACHE_NAME = "httpCache"
        const val CACHE_SIZE: Long = 100 * 1024 * 1024 // 100 MB
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedOkHttpClient