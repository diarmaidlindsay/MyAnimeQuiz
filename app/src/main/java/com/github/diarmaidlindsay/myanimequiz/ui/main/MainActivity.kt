package com.github.diarmaidlindsay.myanimequiz.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.diarmaidlindsay.myanimequiz.domain.model.AuthState
import com.github.diarmaidlindsay.myanimequiz.ui.base.ThemeStyle
import com.github.diarmaidlindsay.myanimequiz.ui.base.navigation.NavActionManager
import com.github.diarmaidlindsay.myanimequiz.ui.base.navigation.NavActionManager.Companion.rememberNavActionManager
import com.github.diarmaidlindsay.myanimequiz.ui.composables.LoadingScreen
import com.github.diarmaidlindsay.myanimequiz.ui.login.LoginViewModel
import com.github.diarmaidlindsay.myanimequiz.ui.theme.MyAnimeQuizTheme
import com.github.diarmaidlindsay.myanimequiz.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialTheme = runBlocking { mainViewModel.theme.first() }

        // Set the Compose UI
        setContent {
            val theme by mainViewModel.theme.collectAsStateWithLifecycle(initialValue = initialTheme)
            var isLoading by rememberSaveable { mutableStateOf(true) }
            val isDark =
                if (theme == ThemeStyle.FOLLOW_SYSTEM) isSystemInDarkTheme() else theme == ThemeStyle.DARK
            val navController = rememberNavController()
            val navActionManager = rememberNavActionManager(navController)
            val accessTokenState =
                mainViewModel.accessToken.collectAsStateWithLifecycle(initialValue = null)
            val accessToken = accessTokenState.value
            val authState by mainViewModel.authState.collectAsStateWithLifecycle()

            // Check token expiry
            LaunchedEffect(Unit) {
                Timber.d("Checking token expiry")
                mainViewModel.checkTokenExpiry().join()
                isLoading = false
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LoadingScreen()
            }

            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AnimeQuizAppUI(
                    navController,
                    navActionManager,
                    isLoggedIn = !accessToken.isNullOrEmpty(),
                    authState = authState,
                    startAuthFlow = { loginViewModel.startAuthFlow() },
                    showToast = { message -> showToast(message) },
                    isDark = isDark
                )
            }
        }

        loginViewModel.authorizationLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            loginViewModel.handleAuthResponse(result) { authState ->
                mainViewModel.updateAuthState(authState)
            }
        }
    }

    @Composable
    fun AnimeQuizAppUI(
        navController: NavHostController,
        navActionManager: NavActionManager,
        isLoggedIn: Boolean,
        authState: AuthState,
        startAuthFlow: () -> Unit,
        showToast: (String) -> Unit,
        isDark: Boolean
    ) {
        MyAnimeQuizTheme(darkTheme = isDark) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainNavigation(
                    navController = navController,
                    navActionManager = navActionManager,
                    isLoggedIn = isLoggedIn,
                    authState = authState,
                    startAuthFlow = startAuthFlow,
                    showToast = showToast
                )
            }
        }
    }
}