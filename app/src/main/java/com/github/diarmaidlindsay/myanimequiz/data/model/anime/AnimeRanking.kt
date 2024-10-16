package com.github.diarmaidlindsay.myanimequiz.data.model.anime

import com.github.diarmaidlindsay.myanimequiz.data.model.media.BaseRanking
import com.github.diarmaidlindsay.myanimequiz.data.model.media.RankingType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeRanking(
    @SerialName("node")
    override val node: AnimeNode,
    @SerialName("ranking")
    override val ranking: Ranking? = null,
    @SerialName("ranking_type")
    override val rankingType: RankingType? = null,
) : BaseRanking

