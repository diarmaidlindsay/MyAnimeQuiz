package com.github.diarmaidlindsay.myanimequiz.data.model.manga

import com.github.diarmaidlindsay.myanimequiz.data.model.media.BaseUserMediaList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserMangaList(
    @SerialName("node")
    override val node: MangaNode,
    @SerialName("list_status")
    override val listStatus: MyMangaListStatus? = null,
) : BaseUserMediaList<MangaNode>()