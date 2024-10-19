package com.github.diarmaidlindsay.myanimequiz.data.repository

import com.github.diarmaidlindsay.myanimequiz.data.model.media.ListStatus
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

    suspend fun getUserAnimeList() = apiService.getUserAnimeList(
        status = ListStatus.COMPLETED.value, sort = "", limit = 1000, nsfw = 0, fields = null
    )
}