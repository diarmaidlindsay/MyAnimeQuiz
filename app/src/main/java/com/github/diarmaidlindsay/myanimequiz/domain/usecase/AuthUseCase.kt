package com.github.diarmaidlindsay.myanimequiz.domain.usecase

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.github.diarmaidlindsay.myanimequiz.BuildConfig
import com.github.diarmaidlindsay.myanimequiz.QuizApplication
import com.github.diarmaidlindsay.myanimequiz.data.factory.AuthorizationExceptionFactory
import com.github.diarmaidlindsay.myanimequiz.data.factory.AuthorizationResponseFactory
import com.github.diarmaidlindsay.myanimequiz.data.model.AccessToken
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.domain.service.IAuthorizationService
import com.github.diarmaidlindsay.myanimequiz.ui.callbacks.AuthCodeExchangedCallback
import com.github.diarmaidlindsay.myanimequiz.ui.callbacks.AuthResponseHandledCallback
import com.github.diarmaidlindsay.myanimequiz.utils.Constants
import com.github.diarmaidlindsay.myanimequiz.utils.PkceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import timber.log.Timber
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authService: IAuthorizationService,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authorizationResponseFactory: AuthorizationResponseFactory,
    private val authorizationExceptionFactory: AuthorizationExceptionFactory
) {

    private lateinit var codeVerifier: String

    fun startAuthFlow(authorizationLauncher: ActivityResultLauncher<Intent>) {
        codeVerifier = PkceGenerator.generateVerifier(length = 128)

        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse(Constants.AUTH_ENDPOINT),
            Uri.parse(Constants.TOKEN_ENDPOINT)
        )

        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            BuildConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(Constants.REDIRECT_URI)
        )
            .setCodeVerifier(codeVerifier, codeVerifier, "plain")
            .build()

        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        authorizationLauncher.launch(authIntent)
    }

    fun handleAuthResponse(
        result: ActivityResult,
        authResponseHandledCallback: AuthResponseHandledCallback,
        authCodeExchangedCallback: AuthCodeExchangedCallback,
        scope: CoroutineScope
    ) {
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val response = data?.let { authorizationResponseFactory.fromIntent(it) }
            val ex = data?.let { authorizationExceptionFactory.fromIntent(it) }

            if (response != null) {
                authResponseHandledCallback.onAuthSuccess()
                exchangeAuthorizationCode(response, authCodeExchangedCallback, scope)
            } else if (ex != null) {
                Timber.e("AuthorizationException: $ex")
                authResponseHandledCallback.onAuthError(
                    ex.error ?: ex.errorDescription ?: "Unknown Error"
                )
            } else {
                authResponseHandledCallback.onAuthError("AuthorizationResponse is null")
            }
        } else if (result.resultCode == RESULT_CANCELED) {
            Timber.d("Authorization flow was cancelled")
            authResponseHandledCallback.onAuthError("Authorization flow was cancelled")
        } else {
            Timber.e("Unexpected result code: ${result.resultCode}")
        }
    }

    private fun exchangeAuthorizationCode(
        response: AuthorizationResponse,
        authCodeExchangedCallback: AuthCodeExchangedCallback,
        scope: CoroutineScope
    ) {
        val tokenRequest = response.createTokenExchangeRequest()

        authService.performTokenRequest(tokenRequest) { tokenResponse, ex ->
            if (tokenResponse != null) {
                val accessToken = AccessToken(
                    tokenType = tokenResponse.tokenType ?: "",
                    expiresIn = tokenResponse.accessTokenExpirationTime ?: 0,
                    accessToken = tokenResponse.accessToken,
                    refreshToken = tokenResponse.refreshToken
                )
                scope.launch {
                    saveAccessToken(accessToken)
                }
                authCodeExchangedCallback.onAuthCodeExchangedSuccess()
                Timber.d("AccessToken: $accessToken")
            } else {
                val error = ex?.error ?: ex?.errorDescription ?: "Unknown error"
                Timber.e("Token exchange failed: $error")
                authCodeExchangedCallback.onAuthCodeExchangedError(error)
            }
        }
    }

    fun refreshToken(refreshToken: String, scope: CoroutineScope) {
        val tokenRequest = TokenRequest.Builder(
            AuthorizationServiceConfiguration(
                Uri.parse(Constants.AUTH_ENDPOINT),
                Uri.parse(Constants.TOKEN_ENDPOINT)
            ),
            BuildConfig.CLIENT_ID
        )
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setRefreshToken(refreshToken)
            .build()

        authService.performTokenRequest(tokenRequest) { tokenResponse, ex ->
            if (tokenResponse != null) {
                val newAccessToken = AccessToken(
                    tokenType = tokenResponse.tokenType ?: "",
                    expiresIn = tokenResponse.accessTokenExpirationTime ?: 0,
                    accessToken = tokenResponse.accessToken,
                    refreshToken = tokenResponse.refreshToken
                )
                scope.launch {
                    saveAccessToken(newAccessToken)
                }
                Timber.d("Refreshed token: $newAccessToken")
            } else {
                val error = ex?.error ?: ex?.errorDescription ?: "Unknown error"
                Timber.e("Token exchange failed: $error")
            }
        }
    }

    fun checkTokenExpiry(scope: CoroutineScope) {
        scope.launch {
            val expiryDate = userPreferencesRepository.tokenExpiryDate.first()
            if (expiryDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = Date(expiryDate)
                calendar.add(Calendar.DAY_OF_YEAR, -Constants.ONE_WEEK_DAYS)
                val oneWeekBeforeExpiry = calendar.time

                if (Date().after(oneWeekBeforeExpiry)) {
                    Timber.d("Token is due to expire in less than one week, refreshing token...")
                    val refreshToken = userPreferencesRepository.refreshToken.first()
                    if (refreshToken != null) {
                        refreshToken(refreshToken, scope)
                    } else {
                        Timber.e("Refresh token is null")
                    }
                }
            }
        }
    }

    private suspend fun saveAccessToken(accessToken: AccessToken) {
        QuizApplication.accessToken = accessToken.accessToken
        userPreferencesRepository.saveTokens(accessToken)
    }
}