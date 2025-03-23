package ru.hse.cardefectscan.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import ru.hse.cardefectscan.data.remote.ExceptionHandlerInterceptor
import ru.hse.cardefectscan.data.remote.LoggingInterceptor
import ru.hse.cardefectscan.data.remote.TokenInterceptor
import ru.hse.cardefectscan.domain.repository.AuthRepository
import ru.hse.cardefectscan.domain.usecase.AuthUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InterceptorModule {

    @Provides
    @IntoSet
    @Singleton
    fun provideLoggingInterceptor(): Interceptor = LoggingInterceptor()

    @Provides
    @IntoSet
    @Singleton
    fun provideExceptionHandlerInterceptor(): Interceptor = ExceptionHandlerInterceptor()

    @Provides
    @Singleton
    fun provideTokenInterceptor(
        authRepository: AuthRepository,
        authUseCase: AuthUseCase
    ): Interceptor = TokenInterceptor(authRepository, authUseCase)
}