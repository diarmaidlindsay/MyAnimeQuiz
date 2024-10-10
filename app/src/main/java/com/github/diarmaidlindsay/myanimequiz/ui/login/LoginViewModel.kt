package com.github.diarmaidlindsay.myanimequiz.ui.login

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.diarmaidlindsay.myanimequiz.domain.model.AuthState
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val authUseCase: AuthUseCase) : ViewModel() {
    lateinit var authorizationLauncher: ActivityResultLauncher<Intent>

    fun startAuthFlow() {
        authUseCase.startAuthFlow(authorizationLauncher)
    }

    fun handleAuthResponse(result: ActivityResult, updateAuthState: (AuthState) -> Unit) {
        authUseCase.handleAuthResponse(result, viewModelScope) { state ->
            updateAuthState(state)
        }
    }
}