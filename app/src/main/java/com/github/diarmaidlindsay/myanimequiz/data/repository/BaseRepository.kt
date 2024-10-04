package com.github.diarmaidlindsay.myanimequiz.data.repository

import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import com.github.diarmaidlindsay.myanimequiz.scopes.DatastoreScope
import kotlinx.coroutines.flow.first
import timber.log.Timber

abstract class BaseRepository(
    private val authUseCase: AuthUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val datastoreScope: DatastoreScope
) {
    suspend fun handleResponseError(error: String) {
        when (error) {
            "invalid_token" -> {
                val refreshToken = userPreferencesRepository.refreshToken.first()
                if (refreshToken != null) {
                    try {
                        authUseCase.refreshToken(refreshToken, datastoreScope)
                    } catch (e: Exception) {
                        Timber.e(e)
                        userPreferencesRepository.removeTokens()
                    }
                } else {
                    userPreferencesRepository.removeTokens()
                }
            }
        }
    }
}