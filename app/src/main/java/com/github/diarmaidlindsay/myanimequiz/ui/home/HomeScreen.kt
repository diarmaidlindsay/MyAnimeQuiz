package com.github.diarmaidlindsay.myanimequiz.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.github.diarmaidlindsay.myanimequiz.ui.base.navigation.NavActionManager

@Composable
fun HomeScreen(navActionManager: NavActionManager) {
    val quizAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.quiz))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Space for the app logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = quizAnimation,
                modifier = Modifier.fillMaxWidth(),
                speed = 0.5f,
                reverseOnRepeat = true,
                iterations = LottieConstants.IterateForever
            )
        }

        // Main menu buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeButton(text = "Start Quiz", onClick = { navActionManager.toQuiz() })
            HomeButton(text = "High Scores", onClick = { navActionManager.toHighScores() })
            HomeButton(text = "Logout", onClick = { navActionManager.toLogin() })
            HomeButton(text = "Settings", onClick = { /* Navigate to Settings */ })
            HomeButton(text = "About", onClick = { /* Navigate to About */ })
            HomeButton(text = "Privacy Policy", onClick = { /* Navigate to Privacy Policy */ })
        }
    }
}

@Composable
fun HomeButton(
    text: String,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text(text = text)
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(navActionManager = NavActionManager.rememberNavActionManager())
}