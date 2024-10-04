package com.github.diarmaidlindsay.myanimequiz.ui.main

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import com.github.diarmaidlindsay.myanimequiz.ui.callbacks.AuthCodeExchangedCallback
import com.github.diarmaidlindsay.myanimequiz.ui.callbacks.AuthResponseHandledCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val accessToken = userPreferencesRepository.accessToken

    fun startAuthFlow(authorizationLauncher: ActivityResultLauncher<Intent>) {
        authUseCase.startAuthFlow(authorizationLauncher)
    }

    fun handleAuthResponse(
        result: ActivityResult,
        authResponseHandledCallback: AuthResponseHandledCallback,
        authCodeExchangedCallback: AuthCodeExchangedCallback
    ) {
        authUseCase.handleAuthResponse(
            result,
            authResponseHandledCallback,
            authCodeExchangedCallback,
            viewModelScope
        )
    }

    fun checkTokenExpiry() {
        authUseCase.checkTokenExpiry(viewModelScope)
    }
}