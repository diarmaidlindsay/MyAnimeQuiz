package com.github.diarmaidlindsay.myanimequiz.di

import android.content.Context
import com.github.diarmaidlindsay.myanimequiz.data.factory.AuthorizationExceptionFactory
import com.github.diarmaidlindsay.myanimequiz.data.factory.AuthorizationResponseFactory
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.data.service.AuthorizationServiceWrapper
import com.github.diarmaidlindsay.myanimequiz.domain.service.IAuthorizationService
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.openid.appauth.AuthorizationService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStoreCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideAuthorizationService(@ApplicationContext context: Context): AuthorizationService {
        return AuthorizationService(context)
    }

    @Provides
    @Singleton
    fun provideIAuthorizationService(
        authorizationService: AuthorizationService
    ): IAuthorizationService {
        return AuthorizationServiceWrapper(authorizationService)
    }

    @Provides
    @Singleton
    fun provideAuthUseCase(
        authService: IAuthorizationService,
        userPreferencesRepository: UserPreferencesRepository,
        authorizationResponseFactory: AuthorizationResponseFactory,
        authorizationExceptionFactory: AuthorizationExceptionFactory
    ): AuthUseCase {
        return AuthUseCase(
            authService,
            userPreferencesRepository,
            authorizationResponseFactory,
            authorizationExceptionFactory
        )
    }
}