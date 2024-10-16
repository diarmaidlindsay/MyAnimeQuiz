package com.github.diarmaidlindsay.myanimequiz.data.repository

import com.github.diarmaidlindsay.myanimequiz.data.model.Response
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.AnimeList
import com.github.diarmaidlindsay.myanimequiz.data.service.ApiService
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import com.github.diarmaidlindsay.myanimequiz.scopes.DatastoreScope
import javax.inject.Inject

class QuizRepository @Inject constructor(
    private val apiService: ApiService,
    authUseCase: AuthUseCase,
    defaultPreferencesRepository: UserPreferencesRepository,
    datastoreScope: DatastoreScope
) : BaseRepository(authUseCase, defaultPreferencesRepository, datastoreScope) {

    suspend fun getAnimeQuestions(): Response<List<AnimeList>> = apiService.getAnimeList(
        query = "Naruto", limit = 100, offset = 0, nsfw = 0, fields = null
    )
}