package com.github.diarmaidlindsay.myanimequiz.data.model.manga

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource
import com.github.diarmaidlindsay.myanimequiz.R
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.Recommendations
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.RelatedAnime
import com.github.diarmaidlindsay.myanimequiz.data.model.media.AlternativeTitles
import com.github.diarmaidlindsay.myanimequiz.data.model.media.BaseMediaDetails
import com.github.diarmaidlindsay.myanimequiz.data.model.media.Genre
import com.github.diarmaidlindsay.myanimequiz.data.model.media.MainPicture
import com.github.diarmaidlindsay.myanimequiz.data.model.media.MediaFormat
import com.github.diarmaidlindsay.myanimequiz.data.model.media.MediaStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class MangaDetails(
    override val id: Int = 0,
    override val title: String? = null,
    @SerialName("main_picture")
    override val mainPicture: MainPicture? = null,
    @SerialName("alternative_titles")
    override val alternativeTitles: AlternativeTitles? = null,
    @SerialName("start_date")
    override val startDate: String? = null,
    @SerialName("end_date")
    override val endDate: String? = null,
    override val synopsis: String? = null,
    override val mean: Float? = null,
    override val rank: Int? = null,
    override val popularity: Int? = null,
    @SerialName("num_list_users")
    override val numListUsers: Int? = null,
    @SerialName("num_scoring_users")
    override val numScoringUsers: Int? = null,
    override val nsfw: String? = null,
    @SerialName("created_at")
    override val createdAt: String? = null,
    @SerialName("updated_at")
    override val updatedAt: String? = null,
    @SerialName("media_type")
    override val mediaFormat: MediaFormat? = null,
    override val status: MediaStatus? = null,
    override val genres: List<Genre>? = null,
    override val pictures: List<MainPicture>? = null,
    override val background: String? = null,
    @SerialName("related_anime")
    override val relatedAnime: List<RelatedAnime>? = null,
    @SerialName("related_manga")
    override val relatedManga: List<RelatedManga>? = null,
    override val recommendations: List<Recommendations<MangaNode>>? = null,
    @SerialName("my_list_status")
    override val myListStatus: MyMangaListStatus? = null,
    @SerialName("num_volumes")
    val numVolumes: Int? = null,
    @SerialName("num_chapters")
    val numChapters: Int? = null,
    @SerialName("authors")
    val authors: List<Author>? = null,
    @SerialName("serialization")
    val serialization: List<Serialization>? = null,
) : BaseMediaDetails() {

    val hasVolumes = numVolumes != null && numVolumes > 0

    @Composable
    fun volumesText() = if (hasVolumes) {
        "$numVolumes ${stringResource(R.string.volumes)}"
    } else {
        stringResource(R.string.unknown)
    }

    fun toMangaNode() = MangaNode(
        id = id,
        title = title.orEmpty(),
        alternativeTitles = alternativeTitles,
        mainPicture = mainPicture,
        startDate = startDate,
        numVolumes = numVolumes,
        numChapters = numChapters,
        numListUsers = numListUsers,
        mediaFormat = mediaFormat,
        status = status,
        mean = mean,
    )
}