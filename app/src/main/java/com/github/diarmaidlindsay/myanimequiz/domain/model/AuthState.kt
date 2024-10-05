package com.github.diarmaidlindsay.myanimequiz.domain.model

sealed class AuthState {
    data object Idle : AuthState()
    data object Success : AuthState()
    data class Error(val message: String?) : AuthState()
}