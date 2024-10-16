package com.github.diarmaidlindsay.myanimequiz.data.model.media

import com.github.diarmaidlindsay.myanimequiz.data.model.anime.Ranking

interface BaseRanking {
    val node: BaseMediaNode
    val ranking: Ranking?
    val rankingType: RankingType?
}