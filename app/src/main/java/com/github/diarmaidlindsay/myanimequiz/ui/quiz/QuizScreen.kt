package com.github.diarmaidlindsay.myanimequiz.ui.quiz

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.AnimeList

@Composable
fun QuizScreen() {
    val quizViewModel: QuizViewModel = hiltViewModel()
    val animeQuestions by quizViewModel.animeQuestions.observeAsState(emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(animeQuestions) { animeList ->
            AnimeItem(animeList)
        }
    }
}

@Composable
fun AnimeItem(animeList: AnimeList) {
    Text(text = animeList.node.title)
}