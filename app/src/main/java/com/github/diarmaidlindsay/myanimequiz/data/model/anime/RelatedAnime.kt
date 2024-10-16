package com.github.diarmaidlindsay.myanimequiz.data.model.anime

import com.github.diarmaidlindsay.myanimequiz.data.model.media.BaseRelated
import com.github.diarmaidlindsay.myanimequiz.data.model.media.RelationType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelatedAnime(
    @SerialName("node")
    override val node: AnimeNode,
    @SerialName("relation_type")
    override val relationType: RelationType,
) : BaseRelated
