package com.github.diarmaidlindsay.myanimequiz.ui.main

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.diarmaidlindsay.myanimequiz.BuildConfig
import com.github.diarmaidlindsay.myanimequiz.MyAnimeListConfig
import com.github.diarmaidlindsay.myanimequiz.QuizApplication
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.utils.PkceGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authService: AuthorizationService,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private lateinit var codeVerifier: String
    val accessToken = userPreferencesRepository.accessToken

    // Function to start the authorization flow
    fun startAuthFlow(authorizationLauncher: ActivityResultLauncher<Intent>) {
        codeVerifier = PkceGenerator.generateVerifier(length = 128)

        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse(MyAnimeListConfig.AUTH_ENDPOINT),
            Uri.parse(MyAnimeListConfig.TOKEN_ENDPOINT)
        )

        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            BuildConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(MyAnimeListConfig.REDIRECT_URI)
        )
            .setCodeVerifier(codeVerifier, codeVerifier, "plain")
            .build()

        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        authorizationLauncher.launch(authIntent)
    }

    // Handle the OAuth2 response
    fun handleAuthResponse(
        result: ActivityResult,
        authResponseHandledCallback: AuthResponseHandledCallback,
        authCodeExchangedCallback: AuthCodeExchangedCallback
    ) {
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val response = data?.let { AuthorizationResponse.fromIntent(it) }
            val ex = AuthorizationException.fromIntent(data)

            if (response != null) {
                Log.d("MainViewModel", "AuthorizationResponse: ${response.jsonSerializeString()}")
                exchangeAuthorizationCode(response, authCodeExchangedCallback)
            } else if (ex != null) {
                Log.e("MainViewModel", "AuthorizationException: $ex")
                authResponseHandledCallback.onAuthError(
                    ex.error ?: ex.errorDescription ?: "Unknown error"
                )
            } else {
                authResponseHandledCallback.onAuthError("AuthorizationResponse is null")
            }
        } else if (result.resultCode == RESULT_CANCELED) {
            authResponseHandledCallback.onAuthError("Authorization flow was cancelled")
        } else {
            Log.e("MainViewModel", "Unexpected result code: ${result.resultCode}")
        }
    }

    // Exchange the authorization code for an access token
    private fun exchangeAuthorizationCode(
        response: AuthorizationResponse,
        authCodeExchangedCallback: AuthCodeExchangedCallback
    ) {
        val tokenRequest = response.createTokenExchangeRequest()

        Log.d("MainViewModel", "TokenRequest: ${tokenRequest.jsonSerializeString()}")

        authService.performTokenRequest(tokenRequest) { tokenResponse, ex ->
            if (tokenResponse != null) {
                val accessToken = tokenResponse.accessToken
                Log.d("MainViewModel", "AccessToken: $accessToken")
                accessToken?.let { saveAccessToken(it) }
            } else {
                val error = ex?.error ?: ex?.errorDescription ?: "Unknown error"
                Log.e("MainViewModel", "Token exchange failed: $error")
                authCodeExchangedCallback.onAuthCodeExchangedError(error)
            }
        }
    }

    private fun saveAccessToken(accessToken: String) = viewModelScope.launch {
        userPreferencesRepository.saveTokens(accessToken)
        QuizApplication.accessToken = accessToken
    }
}

interface AuthResponseHandledCallback {
    fun onAuthError(errorMessage: String?)
}

interface AuthCodeExchangedCallback {
    fun onAuthCodeExchangedError(errorMessage: String?)
}