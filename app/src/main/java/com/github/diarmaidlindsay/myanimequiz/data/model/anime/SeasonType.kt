package com.github.diarmaidlindsay.myanimequiz.data.model.anime

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.diarmaidlindsay.myanimequiz.R
import com.github.diarmaidlindsay.myanimequiz.data.model.base.Localizable
import com.github.diarmaidlindsay.myanimequiz.utils.SeasonCalendar

enum class SeasonType : Localizable {
    PREVIOUS,
    CURRENT,
    NEXT;

    @Composable
    override fun localized() = when (this) {
        PREVIOUS -> stringResource(R.string.previous_season)
        CURRENT -> stringResource(R.string.current_season)
        NEXT -> stringResource(R.string.next_season)
    }

    val season
        get() = when (this) {
            PREVIOUS -> SeasonCalendar.prevStartSeason
            CURRENT -> SeasonCalendar.currentStartSeason
            NEXT -> SeasonCalendar.nextStartSeason
        }
}