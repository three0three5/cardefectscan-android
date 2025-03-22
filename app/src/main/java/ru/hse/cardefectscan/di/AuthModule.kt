package ru.hse.cardefectscan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.hse.cardefectscan.domain.repository.AuthRepository
import ru.hse.cardefectscan.domain.usecase.AuthUseCase
import ru.hse.generated.apis.AuthApi

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
    ): AuthRepository = AuthRepository(
        context
    )

    @Provides
    @Singleton
    fun provideAuthUseCase(
        authRepository: AuthRepository,
        authClient: AuthApi,
    ): AuthUseCase = AuthUseCase(
        authRepository,
        authClient,
    )
}