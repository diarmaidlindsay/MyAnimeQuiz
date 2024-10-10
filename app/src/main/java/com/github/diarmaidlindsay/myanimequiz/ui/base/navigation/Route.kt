package com.github.diarmaidlindsay.myanimequiz.ui.base.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Quiz : Route

    @Serializable
    data object HighScores : Route
}