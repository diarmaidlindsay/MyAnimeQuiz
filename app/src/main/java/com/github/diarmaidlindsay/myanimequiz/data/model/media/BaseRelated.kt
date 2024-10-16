package com.github.diarmaidlindsay.myanimequiz.data.model.media

interface BaseRelated {
    val node: BaseMediaNode
    val relationType: RelationType
}