package com.github.diarmaidlindsay.myanimequiz.ui.main

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.domain.model.AuthState
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val accessToken = userPreferencesRepository.accessToken

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun startAuthFlow(authorizationLauncher: ActivityResultLauncher<Intent>) {
        authUseCase.startAuthFlow(authorizationLauncher)
    }

    fun handleAuthResponse(result: ActivityResult) {
        authUseCase.handleAuthResponse(result, viewModelScope) { state ->
            _authState.update { state }
        }
    }

    fun checkTokenExpiry() {
        authUseCase.checkTokenExpiry(viewModelScope)
    }
}