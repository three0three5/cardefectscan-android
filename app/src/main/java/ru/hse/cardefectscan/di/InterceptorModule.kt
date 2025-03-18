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

@Module
@InstallIn(SingletonComponent::class)
class InterceptorModule {

    @Provides
    @IntoSet
    fun provideLoggingInterceptor(): Interceptor = LoggingInterceptor()

    @Provides
    @IntoSet
    fun provideExceptionHandlerInterceptor(): Interceptor = ExceptionHandlerInterceptor()

    @Provides
    fun provideTokenInterceptor(
        authRepository: AuthRepository,
        authUseCase: AuthUseCase
    ): Interceptor = TokenInterceptor(authRepository, authUseCase)
}