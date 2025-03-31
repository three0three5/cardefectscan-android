package ru.hse.cardefectscan.di

import android.content.Context
import android.util.Log
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.network.cachecontrol.CacheControlCacheStrategy
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import ru.hse.cardefectscan.data.remote.MinioClient
import ru.hse.cardefectscan.domain.usecase.ImageUseCase
import ru.hse.generated.apis.ImagesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ImageModule {
    @Provides
    @Singleton
    fun provideImageUseCase(
        imagesApi: ImagesApi,
        minioClient: MinioClient,
    ): ImageUseCase = ImageUseCase(
        imagesApi,
        minioClient,
    )

    @OptIn(ExperimentalCoilApi::class)
    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
        @AuthenticatedOkHttpClient client: OkHttpClient,
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = {
                            client.newBuilder()
                                .addInterceptor(RequestHeaderInterceptor())
                                .build()
                        },
                        cacheStrategy = { CacheControlCacheStrategy() },
                    )
                )
            }
            .build()
    }
}

class RequestHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        Log.d("RequestHeaderInterceptor", "Initial headers: ${originalRequest.headers}")
        val modifiedRequest = originalRequest.newBuilder()
            .headers(originalRequest.headers)
            .removeHeader("cache-control")
            .addHeader("Cache-Control", "no-cache")
            .build()
        Log.d("RequestHeaderInterceptor", "New headers: ${originalRequest.headers}")

        val response = chain.proceed(modifiedRequest)

        return if (response.code != 200) {
            response.newBuilder()
                .headers(response.headers)
                .addHeader("Cache-Control", "no-store")
                .build()
        } else {
            response
        }
    }
}