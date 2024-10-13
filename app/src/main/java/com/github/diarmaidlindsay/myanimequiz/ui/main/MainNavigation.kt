package com.github.diarmaidlindsay.myanimequiz.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.diarmaidlindsay.myanimequiz.domain.model.AuthState
import com.github.diarmaidlindsay.myanimequiz.ui.base.navigation.NavActionManager
import com.github.diarmaidlindsay.myanimequiz.ui.base.navigation.Route
import com.github.diarmaidlindsay.myanimequiz.ui.highscore.HighScoresScreen
import com.github.diarmaidlindsay.myanimequiz.ui.home.HomeScreen
import com.github.diarmaidlindsay.myanimequiz.ui.login.LoginScreen
import com.github.diarmaidlindsay.myanimequiz.ui.quiz.QuizScreen
import timber.log.Timber

@Composable
fun MainNavigation(
    navController: NavHostController,
    navActionManager: NavActionManager,
    isLoggedIn: Boolean,
    authState: AuthState,
    startAuthFlow: () -> Unit,
    showToast: (String) -> Unit
) {
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            Timber.d("User is logged in, navigating to main screen")
            navActionManager.toMain(clearBackStack = true)
        } else {
            Timber.d("User is not logged in, navigating to login screen")
            navActionManager.toLogin(clearBackStack = true)
        }
    }

    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> { HomeScreen(navActionManager) }
        composable<Route.Login> {
            LoginScreen(
                authState = authState,
                startAuthFlow = startAuthFlow,
                showToast = showToast
            )
        }
        composable<Route.Quiz> { QuizScreen() }
        composable<Route.HighScores> { HighScoresScreen() }

    }
}