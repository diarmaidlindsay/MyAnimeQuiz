package com.github.diarmaidlindsay.myanimequiz.ui.base.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Immutable
class NavActionManager(
    private val navController: NavHostController
) {
    fun goBack() {
        navController.popBackStack()
    }

    fun toMain() {
        navController.navigate(Route.Home) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun toLogin() {
        navController.navigate(Route.Login)
    }

    fun toQuiz() {
        navController.navigate(Route.Quiz)
    }

    fun toHighScores() {
        navController.navigate(Route.HighScores)
    }

    companion object {
        @Composable
        fun rememberNavActionManager(
            navController: NavHostController = rememberNavController()
        ) = remember {
            NavActionManager(navController)
        }
    }
}