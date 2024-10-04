package com.github.diarmaidlindsay.myanimequiz.di

import com.github.diarmaidlindsay.myanimequiz.data.repository.QuizRepository
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import com.github.diarmaidlindsay.myanimequiz.scopes.DatastoreScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideDatastoreScope(): DatastoreScope = object : DatastoreScope {
        override val coroutineContext = SupervisorJob() + Dispatchers.IO
    }

    @Provides
    @Singleton
    fun provideQuizRepository(
        authUseCase: AuthUseCase,
        userPreferencesRepository: UserPreferencesRepository, scope: DatastoreScope
    ): QuizRepository {
        return QuizRepository(authUseCase, userPreferencesRepository, scope)
    }
}