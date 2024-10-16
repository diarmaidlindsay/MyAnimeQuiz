package com.github.diarmaidlindsay.myanimequiz.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.github.diarmaidlindsay.myanimequiz.QuizApplication
import com.github.diarmaidlindsay.myanimequiz.R
import com.github.diarmaidlindsay.myanimequiz.data.model.BaseResponse
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.AnimeDetails
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.Recommendations
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.RelatedAnime
import com.github.diarmaidlindsay.myanimequiz.data.model.manga.MangaDetails
import com.github.diarmaidlindsay.myanimequiz.data.model.manga.RelatedManga
import com.github.diarmaidlindsay.myanimequiz.utils.Constants.ANIME_URL
import com.github.diarmaidlindsay.myanimequiz.utils.Constants.MANGA_URL

abstract class BaseMediaDetails : BaseResponse {
    abstract val id: Int
    abstract val title: String?
    abstract val mainPicture: MainPicture?
    abstract val alternativeTitles: AlternativeTitles?
    abstract val startDate: String?
    abstract val endDate: String?
    abstract val synopsis: String?
    abstract val mean: Float?
    abstract val rank: Int?
    abstract val popularity: Int?
    abstract val numListUsers: Int?
    abstract val numScoringUsers: Int?
    abstract val nsfw: String?
    abstract val createdAt: String?
    abstract val updatedAt: String?
    abstract val mediaFormat: MediaFormat?
    abstract val status: MediaStatus?
    abstract val genres: List<Genre>?
    abstract val pictures: List<MainPicture>?
    abstract val background: String?
    abstract val relatedAnime: List<RelatedAnime>?
    abstract val relatedManga: List<RelatedManga>?
    abstract val recommendations: List<Recommendations<*>>?

    abstract val myListStatus: BaseMyListStatus?

    override val error: String? = null
    override val message: String? = null

    val mediaType
        get() = if (this is MangaDetails) MediaType.MANGA else MediaType.ANIME

    val malUrl
        get() = if (this is MangaDetails) MANGA_URL + id else ANIME_URL + id

    fun userPreferredTitle() = title(QuizApplication.titleLanguage)

    fun title(language: TitleLanguage) = when (language) {
        TitleLanguage.ROMAJI -> title
        TitleLanguage.ENGLISH ->
            if (alternativeTitles?.en.isNullOrBlank()) title
            else alternativeTitles?.en ?: title

        TitleLanguage.JAPANESE ->
            if (alternativeTitles?.ja.isNullOrBlank()) title
            else alternativeTitles?.ja ?: title
    }

    @Composable
    fun durationText() = when (this) {
        is AnimeDetails -> {
            if (numEpisodes != null && numEpisodes > 0) {
                pluralStringResource(
                    id = R.plurals.num_episodes,
                    count = numEpisodes,
                    numEpisodes
                )
            } else stringResource(R.string.unknown)
        }

        is MangaDetails -> {
            if (numChapters != null && numChapters > 0) {
                pluralStringResource(
                    id = R.plurals.num_chapters,
                    count = numChapters,
                    numChapters
                )
            } else stringResource(R.string.unknown)
        }

        else -> stringResource(R.string.unknown)
    }

    @Composable
    fun rankText() = if (rank == null) "N/A" else "#$rank"

    @Composable
    fun synonymsJoined(): String? {
        val joined = alternativeTitles?.synonyms?.joinToString(",\n")
        return if (joined?.isNotBlank() == true) joined
        else null
    }

    @Composable
    fun synopsisAndBackground() = buildAnnotatedString {
        val hasSynopsis = !synopsis.isNullOrBlank()
        if (hasSynopsis) append(synopsis)
        if (!background.isNullOrBlank()) {
            if (hasSynopsis) append("\n\n")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stringResource(R.string.synopsis_background))
            }
            append("\n$background")
        }
    }
}