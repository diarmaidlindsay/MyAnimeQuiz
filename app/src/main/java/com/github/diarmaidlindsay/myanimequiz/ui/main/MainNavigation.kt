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

@Composable
fun MainNavigation(
    navController: NavHostController,
    navActionManager: NavActionManager,
    isLoggedIn: Boolean,
    authState: AuthState,
    startAuthFlow: () -> Unit,
    showToast: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        if (isLoggedIn) {
            navActionManager.toMain()
        } else {
            navActionManager.toLogin()
        }
    }

    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> { HomeScreen(navActionManager) }
        composable<Route.Login> {
            LoginScreen(
                navActionManager = navActionManager,
                authState = authState,
                startAuthFlow = startAuthFlow,
                showToast = showToast
            )
        }
        composable<Route.Quiz> { QuizScreen() }
        composable<Route.HighScores> { HighScoresScreen() }

    }
}