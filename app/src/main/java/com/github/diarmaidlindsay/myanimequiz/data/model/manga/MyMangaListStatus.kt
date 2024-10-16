package com.github.diarmaidlindsay.myanimequiz.data.model.manga

import androidx.annotation.IntRange
import androidx.compose.runtime.Immutable
import com.github.diarmaidlindsay.myanimequiz.data.model.media.BaseMyListStatus
import com.github.diarmaidlindsay.myanimequiz.data.model.media.ListStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class MyMangaListStatus(

    @SerialName("status")
    override val status: ListStatus,

    @SerialName("score")
    @IntRange(0, 10)
    override val score: Int = 0,

    @SerialName("updated_at")
    override val updatedAt: String? = null,

    @SerialName("start_date")
    override val startDate: String? = null,

    @SerialName("finish_date")
    override val finishDate: String? = null,

    @SerialName("num_chapters_read")
    override val progress: Int? = 0,

    @SerialName("num_volumes_read")
    val numVolumesRead: Int = 0,

    @SerialName("is_rereading")
    override val isRepeating: Boolean = false,

    @SerialName("num_times_reread")
    override val repeatCount: Int? = 0,

    @SerialName("reread_value")
    @IntRange(0, 5)
    override val repeatValue: Int? = 0,

    @SerialName("priority")
    @IntRange(0, 2)
    override val priority: Int = 0,

    @SerialName("tags")
    override val tags: List<String>? = null,

    @SerialName("comments")
    override val comments: String? = null

) : BaseMyListStatus() {

    fun isUsingVolumeProgress() = numVolumesRead > 0 && (progress == null || progress == 0)
}