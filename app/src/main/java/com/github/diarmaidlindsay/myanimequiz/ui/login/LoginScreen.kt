// LoginScreen.kt
package com.github.diarmaidlindsay.myanimequiz.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.diarmaidlindsay.myanimequiz.R
import com.github.diarmaidlindsay.myanimequiz.domain.model.AuthState
import com.github.diarmaidlindsay.myanimequiz.ui.composables.LoadingScreen
import com.github.diarmaidlindsay.myanimequiz.ui.theme.MyAnimeQuizTheme
import timber.log.Timber

// LoginScreen.kt
@Composable
fun LoginScreen(
    authState: AuthState,
    startAuthFlow: () -> Unit,
    showToast: (String) -> Unit
) {
    LoginScreenContent(
        startAuthFlow = startAuthFlow,
        authState = authState,
        showToast = showToast
    )
}

@Composable
fun LoginScreenContent(
    startAuthFlow: () -> Unit,
    authState: AuthState,
    showToast: (String) -> Unit
) {
    if (authState is AuthState.Success) {
        LoadingScreen() //While saving the authToken asynchronously
    } else {
        val jellyfishAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.jellyfish))
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = jellyfishAnimation,
                        modifier = Modifier.fillMaxWidth(),
                        iterations = LottieConstants.IterateForever,
                        clipToCompositionBounds = true
                    )
                }

                Button(onClick = startAuthFlow) {
                    Text("Login with MyAnimeList")
                }
                if (authState is AuthState.Error) {
                    Timber.d("Login Screen : Error in AuthState")
                    showToast(authState.message ?: "Error in AuthState")
                }
            }
        }
    }
}

@Composable
@Preview(apiLevel = 35)
fun LoginScreenPreview() {
    MyAnimeQuizTheme {
        LoginScreenContent(
            startAuthFlow = { },
            authState = AuthState.Idle,
            showToast = { }
        )
    }
}