package com.github.diarmaidlindsay.myanimequiz

import android.app.Application
import com.github.diarmaidlindsay.myanimequiz.data.model.media.TitleLanguage
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class QuizApplication : Application() {
    companion object {
        var titleLanguage = TitleLanguage.ROMAJI
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}