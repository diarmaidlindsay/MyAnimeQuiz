package com.github.diarmaidlindsay.myanimequiz.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.domain.model.AuthState
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import com.github.diarmaidlindsay.myanimequiz.ui.base.ThemeStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val accessToken = userPreferencesRepository.accessToken
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    val theme = userPreferencesRepository.theme
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeStyle.FOLLOW_SYSTEM)

    fun checkTokenExpiry(): Job {
        return authUseCase.checkTokenExpiry(viewModelScope)
    }

    fun updateAuthState(newState: AuthState) {
        _authState.value = newState
    }
}