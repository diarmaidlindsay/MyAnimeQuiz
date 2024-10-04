package com.github.diarmaidlindsay.myanimequiz.data.repository

import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import com.github.diarmaidlindsay.myanimequiz.scopes.DatastoreScope
import javax.inject.Inject

class QuizRepository @Inject constructor(
    authUseCase: AuthUseCase,
    defaultPreferencesRepository: UserPreferencesRepository,
    datastoreScope: DatastoreScope
) : BaseRepository(authUseCase, defaultPreferencesRepository, datastoreScope) {
}