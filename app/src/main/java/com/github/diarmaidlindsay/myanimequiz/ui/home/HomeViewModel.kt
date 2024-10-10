package com.github.diarmaidlindsay.myanimequiz.ui.home

import androidx.lifecycle.ViewModel
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val authUseCase: AuthUseCase) : ViewModel() {
}