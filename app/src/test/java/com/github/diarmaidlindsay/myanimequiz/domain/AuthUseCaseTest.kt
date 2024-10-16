package com.github.diarmaidlindsay.myanimequiz.domain

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResult
import com.github.diarmaidlindsay.myanimequiz.data.factory.AuthorizationExceptionFactory
import com.github.diarmaidlindsay.myanimequiz.data.factory.AuthorizationResponseFactory
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.domain.model.AuthState
import com.github.diarmaidlindsay.myanimequiz.domain.service.IAuthorizationService
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import com.github.diarmaidlindsay.myanimequiz.utils.Constants
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
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
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class AuthUseCaseTest {

    private lateinit var authUseCase: AuthUseCase
    private lateinit var authService: IAuthorizationService
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var authorizationResponseFactory: AuthorizationResponseFactory
    private lateinit var authorizationExceptionFactory: AuthorizationExceptionFactory
    private lateinit var testScope: TestScope

    private var authState: AuthState? = null

    @Before
    fun setUp() {
        authService = mockk()
        userPreferencesRepository = mockk()
        authorizationResponseFactory = mockk()
        authorizationExceptionFactory = mockk()
        testScope = TestScope()

        authUseCase = AuthUseCase(
            authService,
            userPreferencesRepository,
            authorizationResponseFactory,
            authorizationExceptionFactory
        )
    }

    private val updateAuthState: (AuthState) -> Unit = { state ->
        authState = state
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `handleAuthResponse should handle successful authorization`() = testScope.runTest {
        val authResponse = getSampleAuthorizationResponse()
        val tokenResponse = getSampleTokenResponse()
        val result = createActivityResult(RESULT_OK)

        setupAuthResponseFactory(authResponse)
        setupAuthService(tokenResponse)

        coEvery { userPreferencesRepository.saveTokens(any()) } just Runs

        // Call the method under test
        authUseCase.handleAuthResponse(result, this, updateAuthState)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify state
        assert(authState is AuthState.Success)

        //Verify tokens saved
        coVerify { userPreferencesRepository.saveTokens(any()) }
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

        // Call the method under test
        authUseCase.handleAuthResponse(result, this, updateAuthState)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify state
        assert(authState is AuthState.Error)
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

        // Call the method under test
        authUseCase.handleAuthResponse(result, this, updateAuthState)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify state
        assert(authState is AuthState.Error)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `handleAuthResponse should handle token exchange unknown error`() = testScope.runTest {
        val authResponse = getSampleAuthorizationResponse()
        val result = createActivityResult(RESULT_OK)

        setupAuthResponseFactory(authResponse)
        setupAuthService(null)

        // Call the method under test
        authUseCase.handleAuthResponse(result, this, updateAuthState)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify state
        assert(authState is AuthState.Error)
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
    fun `handleAuthResponse should handle authorization flow cancellation`() = testScope.runTest {
        val result = createActivityResult(RESULT_CANCELED)

        // Call the method under test
        authUseCase.handleAuthResponse(result, this, updateAuthState)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify state
        assert(authState is AuthState.Error)
        assert((authState as AuthState.Error).message == "Authorization flow was cancelled")
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `handleAuthResponse should handle unexpected result code`() = testScope.runTest {
        val result = createActivityResult(12345) // Some unexpected result code

        // Call the method under test
        authUseCase.handleAuthResponse(result, this, updateAuthState)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify state
        assert(authState is AuthState.Error)
        assert((authState as AuthState.Error).message == "Unexpected result code: 12345")
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `checkTokenExpiry should refresh token if expiring soon`() = testScope.runTest {
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val tokenResponse = getSampleTokenResponse()
        val expiryDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, Constants.ONE_WEEK_DAYS - 1)
        }.time.time

        every { userPreferencesRepository.tokenExpiryDate } returns flowOf(expiryDate)
        every { userPreferencesRepository.refreshToken } returns flowOf(refreshToken)
        every { userPreferencesRepository.accessToken } returns flowOf(accessToken)
        setupAuthService(tokenResponse)
        coEvery { userPreferencesRepository.saveTokens(any()) } just Runs

        // Call the method under test
        authUseCase.checkTokenExpiry(this)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify interactions
        coVerify { userPreferencesRepository.saveTokens(any()) }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `checkTokenExpiry should log out if token has already expired`() = testScope.runTest {
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val expiryDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1) // Set expiry date to yesterday
        }.time.time

        every { userPreferencesRepository.tokenExpiryDate } returns flowOf(expiryDate)
        every { userPreferencesRepository.refreshToken } returns flowOf(refreshToken)
        every { userPreferencesRepository.accessToken } returns flowOf(accessToken)
        coEvery { userPreferencesRepository.removeTokens() } just Runs

        // Call the method under test
        authUseCase.checkTokenExpiry(this)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify interactions
        coVerify { userPreferencesRepository.removeTokens() }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `checkTokenExpiry should not refresh token if not expiring soon`() = testScope.runTest {
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val expiryDate = Calendar.getInstance().apply {
            add(
                Calendar.DAY_OF_YEAR,
                Constants.ONE_WEEK_DAYS + 1
            ) // Set expiry date to more than one week from now
        }.time.time

        every { userPreferencesRepository.tokenExpiryDate } returns flowOf(expiryDate)
        every { userPreferencesRepository.refreshToken } returns flowOf(refreshToken)
        every { userPreferencesRepository.accessToken } returns flowOf(accessToken)

        // Call the method under test
        authUseCase.checkTokenExpiry(this)

        // Ensure all coroutines complete
        advanceUntilIdle()

        // Verify no interactions with saveTokens
        coVerify(exactly = 0) { userPreferencesRepository.saveTokens(any()) }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `checkTokenExpiry should log out if access token exists but expiry date or refresh token is null`() =
        testScope.runTest {
            val accessToken = "accessToken"

            every { userPreferencesRepository.tokenExpiryDate } returns flowOf(null)
            every { userPreferencesRepository.refreshToken } returns flowOf(null)
            every { userPreferencesRepository.accessToken } returns flowOf(accessToken)
            coEvery { userPreferencesRepository.removeTokens() } just Runs

            // Call the method under test
            authUseCase.checkTokenExpiry(this)

            // Ensure all coroutines complete
            advanceUntilIdle()

            // Verify interactions
            coVerify { userPreferencesRepository.removeTokens() }
        }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `checkTokenExpiry should log error if expiry date or refresh token is null`() =
        testScope.runTest {
            every { userPreferencesRepository.tokenExpiryDate } returns flowOf(null)
            every { userPreferencesRepository.refreshToken } returns flowOf(null)
            every { userPreferencesRepository.accessToken } returns flowOf(null)

            // Call the method under test
            authUseCase.checkTokenExpiry(this)

            // Ensure all coroutines complete
            advanceUntilIdle()

            // Verify no interactions with saveTokens or removeTokens
            coVerify(exactly = 0) { userPreferencesRepository.saveTokens(any()) }
            coVerify(exactly = 0) { userPreferencesRepository.removeTokens() }
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