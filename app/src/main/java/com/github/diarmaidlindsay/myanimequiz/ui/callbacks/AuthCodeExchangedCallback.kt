package com.github.diarmaidlindsay.myanimequiz.ui.callbacks

interface AuthCodeExchangedCallback {
    fun onAuthCodeExchangedSuccess()
    fun onAuthCodeExchangedError(errorMessage: String?)
}