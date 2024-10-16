package com.github.diarmaidlindsay.myanimequiz.data.model.manga

import com.github.diarmaidlindsay.myanimequiz.data.model.media.BaseMediaList
import kotlinx.serialization.Serializable

@Serializable
data class MangaList(
    override val node: MangaNode
) : BaseMediaList

