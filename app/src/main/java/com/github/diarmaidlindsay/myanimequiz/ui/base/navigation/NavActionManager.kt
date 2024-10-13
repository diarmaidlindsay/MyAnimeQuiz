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

    fun toMain(clearBackStack: Boolean = false) {
        navigateTo(Route.Home, clearBackStack)
    }

    fun toLogin(clearBackStack: Boolean = false) {
        navigateTo(Route.Login, clearBackStack)
    }

    fun toQuiz(clearBackStack: Boolean = false) {
        navigateTo(Route.Quiz, clearBackStack)
    }

    fun toHighScores(clearBackStack: Boolean = false) {
        navigateTo(Route.HighScores, clearBackStack)
    }

    private fun navigateTo(route: Route, clearBackStack: Boolean) {
        navController.navigate(route) {
            if (clearBackStack) {
                popUpTo(0) { inclusive = true }
            }
            launchSingleTop = true
        }
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