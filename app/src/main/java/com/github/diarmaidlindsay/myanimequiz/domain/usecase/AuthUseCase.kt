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
import com.github.diarmaidlindsay.myanimequiz.domain.model.AuthState
import com.github.diarmaidlindsay.myanimequiz.domain.service.IAuthorizationService
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
        scope: CoroutineScope,
        updateAuthState: (AuthState) -> Unit
    ) {
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val response = data?.let { authorizationResponseFactory.fromIntent(it) }
            val ex = data?.let { authorizationExceptionFactory.fromIntent(it) }

            if (response != null) {
                updateAuthState(AuthState.Success)
                exchangeAuthorizationCode(response, scope, updateAuthState)
            } else if (ex != null) {
                Timber.e("AuthorizationException: $ex")
                updateAuthState(AuthState.Error(ex.error ?: ex.errorDescription ?: "Unknown Error"))
            } else {
                "AuthorizationResponse is null".let { errorMessage ->
                    Timber.e(errorMessage)
                    updateAuthState(AuthState.Error(errorMessage))
                }

            }
        } else if (result.resultCode == RESULT_CANCELED) {
            "Authorization flow was cancelled".let { errorMessage ->
                Timber.d(errorMessage)
                updateAuthState(AuthState.Error(errorMessage))
            }
        } else {
            "Unexpected result code: ${result.resultCode}".let { errorMessage ->
                Timber.e(errorMessage)
                updateAuthState(AuthState.Error(errorMessage))
            }
        }
    }

    private fun exchangeAuthorizationCode(
        response: AuthorizationResponse,
        scope: CoroutineScope,
        updateAuthState: (AuthState) -> Unit
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
                updateAuthState(AuthState.Success)
                Timber.d("AccessToken: $accessToken")
            } else {
                val error = ex?.error ?: ex?.errorDescription ?: "Unknown error"
                Timber.e("Token exchange failed: $error")
                updateAuthState(AuthState.Error(error))
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