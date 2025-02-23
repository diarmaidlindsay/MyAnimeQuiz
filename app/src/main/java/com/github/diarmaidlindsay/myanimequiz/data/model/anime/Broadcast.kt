package com.github.diarmaidlindsay.myanimequiz.data.model.anime

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.diarmaidlindsay.myanimequiz.R
import com.github.diarmaidlindsay.myanimequiz.data.model.media.WeekDay
import com.github.diarmaidlindsay.myanimequiz.utils.DateUtils
import com.github.diarmaidlindsay.myanimequiz.utils.DateUtils.getNextDayOfWeek
import com.github.diarmaidlindsay.myanimequiz.utils.DateUtils.secondsToLegibleText
import com.github.diarmaidlindsay.myanimequiz.utils.SeasonCalendar
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.absoluteValue

@Serializable
data class Broadcast(
    @SerialName("day_of_the_week")
    val dayOfTheWeek: WeekDay? = null,
    @SerialName("start_time")
    val startTime: String? = null
) {

    @Composable
    fun timeText(isAiring: Boolean) = buildString {
        if (dayOfTheWeek != null) {
            append(dayOfTheWeek.localized())
            append(" ")
            if (startTime != null) {
                append(startTime)
                append(" (JST)")
                if (isAiring) {
                    val airingIn = airingInString()
                    if (airingIn.isNotEmpty()) {
                        append("\n$airingIn")
                    }
                }
            }
        } else append(stringResource(R.string.unknown))
    }

    @Composable
    fun dayTimeText() = buildString {
        if (dayOfTheWeek != null) append(dayOfTheWeek.localized())
        if (startTime != null) append(" $startTime")
        if (dayOfTheWeek == null && startTime == null)
            append(stringResource(R.string.unknown))
    }

    @Composable
    fun airingInString() = if (startTime != null && dayOfTheWeek != null) {
        val remaining = remaining()
        if (remaining > 0) {
            stringResource(R.string.airing_in).format(remaining.secondsToLegibleText())
        } else stringResource(R.string.aired_ago).format(remaining.absoluteValue.secondsToLegibleText())
    } else ""

    @Composable
    fun airingInShortString() = if (startTime != null && dayOfTheWeek != null) {
        val remaining = remaining()
        if (remaining > 0) remaining.secondsToLegibleText()
        else stringResource(R.string.ago).format(remaining.absoluteValue.secondsToLegibleText())
    } else ""

    fun nextAiringDayFormatted() = try {
        dateTimeUntilNextBroadcast()?.format(
            DateTimeFormatter.ofPattern(
                DateFormat.getBestDateTimePattern(Locale.getDefault(), "EE, d MMM HH:mm")
            )
        )
    } catch (e: Exception) {
        null
    }

    private fun remaining() =
        secondsUntilNextBroadcast() - LocalDateTime.now().toEpochSecond(DateUtils.defaultZoneOffset)

    fun secondsUntilNextBroadcast() =
        dateTimeUntilNextBroadcast()?.toEpochSecond(DateUtils.defaultZoneOffset) ?: 0

    private fun dateTimeUntilNextBroadcast(): LocalDateTime? =
        if (startTime != null && dayOfTheWeek != null) {
            val airingDay = LocalDate.now().getNextDayOfWeek(DayOfWeek.of(dayOfTheWeek.ordinal + 1))
            val airingTime = LocalTime.parse(startTime)
            LocalDateTime.of(airingDay, airingTime)
                .atZone(SeasonCalendar.japanZoneId)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
        } else null
}