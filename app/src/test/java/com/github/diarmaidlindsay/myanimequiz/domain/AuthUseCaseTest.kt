package com.github.diarmaidlindsay.myanimequiz.domain

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.github.diarmaidlindsay.myanimequiz.QuizApplication
import com.github.diarmaidlindsay.myanimequiz.data.factory.AuthorizationExceptionFactory
import com.github.diarmaidlindsay.myanimequiz.data.factory.AuthorizationResponseFactory
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.domain.service.IAuthorizationService
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import com.github.diarmaidlindsay.myanimequiz.ui.callbacks.AuthCodeExchangedCallback
import com.github.diarmaidlindsay.myanimequiz.ui.callbacks.AuthResponseHandledCallback
import com.github.diarmaidlindsay.myanimequiz.utils.Constants
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
class AuthUseCaseTest {

    private lateinit var authUseCase: AuthUseCase
    private lateinit var authService: IAuthorizationService
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var authResponseHandledCallback: AuthResponseHandledCallback
    private lateinit var authCodeExchangedCallback: AuthCodeExchangedCallback
    private lateinit var authorizationResponseFactory: AuthorizationResponseFactory
    private lateinit var authorizationExceptionFactory: AuthorizationExceptionFactory
    private lateinit var testScope: TestScope

    @Before
    fun setUp() {
        authService = mockk()
        userPreferencesRepository = mockk()
        authResponseHandledCallback = mockk()
        authCodeExchangedCallback = mockk()
        authorizationResponseFactory = mockk()
        authorizationExceptionFactory = mockk()
        testScope = TestScope()

        authUseCase = AuthUseCase(
            authService,
            userPreferencesRepository,
            authorizationResponseFactory,
            authorizationExceptionFactory
        )

        // Mock static fields
        mockkObject(QuizApplication.Companion)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `handleAuthResponse should handle successful authorization`() = testScope.runTest {
        val authResponse = getSampleAuthorizationResponse()
        val tokenResponse = getSampleTokenResponse()
        val result = createActivityResult(RESULT_OK)

        setupAuthResponseFactory(authResponse)
        setupAuthService(tokenResponse)
        setupCallbacks()

        // Call the method under test
        authUseCase.handleAuthResponse(
            result,
            authResponseHandledCallback,
            authCodeExchangedCallback,
            this
        )

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify interactions
        verifySuccessfulAuthorization()
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `handleAuthResponse should handle authorization exception`() = testScope.runTest {
        val result = createActivityResult(RESULT_OK)
        val authException = AuthorizationException.fromTemplate(
            AuthorizationException.GeneralErrors.SERVER_ERROR,
            null
        )

        setupAuthExceptionFactory(authException)
        every { authResponseHandledCallback.onAuthError(any()) } just Runs

        // Call the method under test
        authUseCase.handleAuthResponse(
            result,
            authResponseHandledCallback,
            authCodeExchangedCallback,
            this
        )

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify interactions
        verify { authResponseHandledCallback.onAuthError(any()) }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `handleAuthResponse should handle token exchange error`() = testScope.runTest {
        val authResponse = getSampleAuthorizationResponse()
        val tokenException = AuthorizationException.fromTemplate(
            AuthorizationException.TokenRequestErrors.INVALID_REQUEST,
            null
        )
        val result = createActivityResult(RESULT_OK)

        setupAuthResponseFactory(authResponse)
        setupAuthService(null, tokenException)
        every { authCodeExchangedCallback.onAuthCodeExchangedError(any()) } just Runs
        every { authResponseHandledCallback.onAuthSuccess() } just Runs

        // Call the method under test
        authUseCase.handleAuthResponse(
            result,
            authResponseHandledCallback,
            authCodeExchangedCallback,
            this
        )

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify interactions
        verify { authCodeExchangedCallback.onAuthCodeExchangedError(any()) }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `handleAuthResponse should handle token exchange unknown error`() = testScope.runTest {
        val authResponse = getSampleAuthorizationResponse()
        val result = createActivityResult(RESULT_OK)

        setupAuthResponseFactory(authResponse)
        setupAuthService(null)
        every { authCodeExchangedCallback.onAuthCodeExchangedError(any()) } just Runs
        every { authResponseHandledCallback.onAuthSuccess() } just Runs

        // Call the method under test
        authUseCase.handleAuthResponse(
            result,
            authResponseHandledCallback,
            authCodeExchangedCallback,
            this
        )

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify interactions
        verify { authCodeExchangedCallback.onAuthCodeExchangedError(any()) }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `refreshToken should save new access token on success`() = testScope.runTest {
        val refreshToken = "refreshToken"
        val tokenResponse = getSampleTokenResponse()

        setupAuthService(tokenResponse)
        coEvery { userPreferencesRepository.saveTokens(any()) } just Runs

        // Call the method under test
        authUseCase.refreshToken(refreshToken, this)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify interactions
        coVerify { userPreferencesRepository.saveTokens(any()) }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `checkTokenExpiry should refresh token if expiring soon`() = testScope.runTest {
        val refreshToken = "refreshToken"
        val tokenResponse = getSampleTokenResponse()
        val expiryDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -Constants.ONE_WEEK_DAYS + 1)
        }.time.time

        every { userPreferencesRepository.tokenExpiryDate } returns flowOf(expiryDate)
        every { userPreferencesRepository.refreshToken } returns flowOf(refreshToken)
        setupAuthService(tokenResponse)
        coEvery { userPreferencesRepository.saveTokens(any()) } just Runs

        // Call the method under test
        authUseCase.checkTokenExpiry(this)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify interactions
        coVerify { userPreferencesRepository.saveTokens(any()) }
    }

    private fun setupAuthResponseFactory(authResponse: AuthorizationResponse) {
        every { authorizationResponseFactory.fromIntent(any()) } returns authResponse
        every { authorizationExceptionFactory.fromIntent(any()) } returns null
    }

    private fun setupAuthExceptionFactory(authException: AuthorizationException) {
        every { authorizationResponseFactory.fromIntent(any()) } returns null
        every { authorizationExceptionFactory.fromIntent(any()) } returns authException
    }

    private fun setupAuthService(
        tokenResponse: TokenResponse?,
        tokenException: AuthorizationException? = null
    ) {
        every { authService.performTokenRequest(any(), any()) } answers {
            secondArg<AuthorizationService.TokenResponseCallback>().onTokenRequestCompleted(
                tokenResponse,
                tokenException
            )
        }
    }

    private fun setupCallbacks() {
        coEvery { userPreferencesRepository.saveTokens(any()) } just Runs
        every { QuizApplication.accessToken = "accessToken" } just Runs
        every { authCodeExchangedCallback.onAuthCodeExchangedSuccess() } just Runs
        every { authResponseHandledCallback.onAuthSuccess() } just Runs
    }

    private fun verifySuccessfulAuthorization() {
        verify { authCodeExchangedCallback.onAuthCodeExchangedSuccess() }
        coVerify { userPreferencesRepository.saveTokens(any()) }
        verify { QuizApplication.accessToken = "accessToken" }
    }

    private fun createActivityResult(resultCode: Int): ActivityResult {
        val intent: Intent = mockk()
        return ActivityResult(resultCode, intent)
    }

    private fun getSampleTokenResponse(): TokenResponse {
        // Step 1: Create a TokenRequest object
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("https://example.com/auth"),
            Uri.parse("https://example.com/token")
        )
        val tokenRequest = TokenRequest.Builder(serviceConfig, "clientId")
            .setGrantType("authorization_code")
            .setAuthorizationCode("authCode")
            .setRedirectUri(Uri.parse("https://example.com/redirect"))
            .build()

        // Step 2: Use the TokenResponse.Builder to build a TokenResponse object with actual values
        return TokenResponse.Builder(tokenRequest)
            .setTokenType("Bearer")
            .setAccessToken("accessToken")
            .setAccessTokenExpirationTime(System.currentTimeMillis() + 3600 * 1000)
            .setRefreshToken("refreshToken")
            .build()
    }

    private fun getSampleAuthorizationResponse(): AuthorizationResponse {
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("https://example.com/auth"),
            Uri.parse("https://example.com/token")
        )
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            "clientId",
            ResponseTypeValues.CODE,
            Uri.parse("https://example.com/redirect")
        )
            .setCodeVerifier(
                "a".repeat(128),
                "a".repeat(128),
                "plain"
            ) // Ensure codeVerifier is 128 characters long
            .build()

        return AuthorizationResponse.Builder(authRequest)
            .setAuthorizationCode("authCode")
            .setState("state")
            .build()
    }
}