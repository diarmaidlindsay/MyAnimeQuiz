package com.github.diarmaidlindsay.myanimequiz.ui.callbacks

interface AuthResponseHandledCallback {
    fun onAuthSuccess()
    fun onAuthError(errorMessage: String?)
}