package com.github.diarmaidlindsay.myanimequiz.data.model.manga

import com.github.diarmaidlindsay.myanimequiz.data.model.media.BaseRelated
import com.github.diarmaidlindsay.myanimequiz.data.model.media.RelationType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelatedManga(
    @SerialName("node")
    override val node: MangaNode,
    @SerialName("relation_type")
    override val relationType: RelationType,
) : BaseRelated
