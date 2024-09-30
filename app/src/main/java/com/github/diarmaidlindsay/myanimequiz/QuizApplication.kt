package com.github.diarmaidlindsay.myanimequiz

import android.app.Application
import com.github.diarmaidlindsay.myanimequiz.data.model.media.TitleLanguage
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuizApplication : Application() {
    companion object {
        var accessToken: String? = null
        var titleLanguage = TitleLanguage.ROMAJI
    }
}