package com.github.diarmaidlindsay.myanimequiz.ui.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.AnimeList
import com.github.diarmaidlindsay.myanimequiz.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _animeQuestions = MutableLiveData<List<AnimeList>>()
    val animeQuestions: LiveData<List<AnimeList>> get() = _animeQuestions

    init {
        getAnimeQuestions()
    }

    fun getAnimeQuestions() {
        viewModelScope.launch {
            val response = quizRepository.getAnimeQuestions()
            if (response.isSuccess) {
                _animeQuestions.postValue(response.data ?: emptyList())
            } else {
                // Handle the error case as needed
                _animeQuestions.postValue(emptyList())
            }
        }
    }
}