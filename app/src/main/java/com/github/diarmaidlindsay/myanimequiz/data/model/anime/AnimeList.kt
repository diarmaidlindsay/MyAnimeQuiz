package com.github.diarmaidlindsay.myanimequiz.data.model.anime

import com.github.diarmaidlindsay.myanimequiz.data.model.media.BaseMediaList
import kotlinx.serialization.Serializable

@Serializable
data class AnimeList(
    override val node: AnimeNode
) : BaseMediaList
