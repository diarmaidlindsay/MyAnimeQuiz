package com.github.diarmaidlindsay.myanimequiz.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.diarmaidlindsay.myanimequiz.QuizApplication
import com.github.diarmaidlindsay.myanimequiz.domain.model.AuthState
import com.github.diarmaidlindsay.myanimequiz.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var authorizationLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check token expiry
        mainViewModel.checkTokenExpiry()

        // Set the Compose UI
        setContent {
            val accessTokenState =
                mainViewModel.accessToken.collectAsStateWithLifecycle(QuizApplication.accessToken)
            val accessToken = accessTokenState.value
            val authState = mainViewModel.authState.collectAsStateWithLifecycle().value

            AnimeQuizAppUI(mainViewModel, accessToken, authState)
        }

        authorizationLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            mainViewModel.handleAuthResponse(result)
        }
    }

    @Composable
    fun AnimeQuizAppUI(mainViewModel: MainViewModel, accessToken: String?, authState: AuthState) {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = { mainViewModel.startAuthFlow(authorizationLauncher) }) {
                    Text("Login with MyAnimeList")
                }
                Text("Access Token: ${if (accessToken.isNullOrEmpty()) "Not available" else "Available"}")
                when (authState) {
                    is AuthState.Success -> {
                        // Handle success state
                    }

                    is AuthState.Error -> {
                        showToast(authState.message ?: "Error in AuthState")
                    }

                    AuthState.Idle -> {
                        // Handle idle state
                    }
                }
            }
        }
    }
}