package com.github.diarmaidlindsay.myanimequiz.data.model.manga

import com.github.diarmaidlindsay.myanimequiz.data.model.media.AlternativeTitles
import com.github.diarmaidlindsay.myanimequiz.data.model.media.BaseMediaNode
import com.github.diarmaidlindsay.myanimequiz.data.model.media.MainPicture
import com.github.diarmaidlindsay.myanimequiz.data.model.media.MediaFormat
import com.github.diarmaidlindsay.myanimequiz.data.model.media.MediaStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaNode(
    @SerialName("id")
    override val id: Int,
    @SerialName("title")
    override val title: String,
    @SerialName("alternative_titles")
    override val alternativeTitles: AlternativeTitles? = null,
    @SerialName("main_picture")
    override val mainPicture: MainPicture? = null,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("num_volumes")
    val numVolumes: Int? = null,
    @SerialName("num_chapters")
    val numChapters: Int? = null,
    @SerialName("num_list_users")
    override val numListUsers: Int? = null,
    @SerialName("media_type")
    override val mediaFormat: MediaFormat? = null,
    @SerialName("status")
    override val status: MediaStatus? = null,
    @SerialName("mean")
    override val mean: Float? = null
) : BaseMediaNode()