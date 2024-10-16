package com.github.diarmaidlindsay.myanimequiz.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.diarmaidlindsay.myanimequiz.R
import com.github.diarmaidlindsay.myanimequiz.data.model.base.Localizable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WeekDay : Localizable {
    @SerialName("monday")
    MONDAY,

    @SerialName("tuesday")
    TUESDAY,

    @SerialName("wednesday")
    WEDNESDAY,

    @SerialName("thursday")
    THURSDAY,

    @SerialName("friday")
    FRIDAY,

    @SerialName("saturday")
    SATURDAY,

    @SerialName("sunday")
    SUNDAY;

    @Composable
    override fun localized() = stringResource(stringRes)

    val stringRes
        get() = when (this) {
            MONDAY -> R.string.monday
            TUESDAY -> R.string.tuesday
            WEDNESDAY -> R.string.wednesday
            THURSDAY -> R.string.thursday
            FRIDAY -> R.string.friday
            SATURDAY -> R.string.saturday
            SUNDAY -> R.string.sunday
        }
}