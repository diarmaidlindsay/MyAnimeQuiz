package com.github.diarmaidlindsay.myanimequiz.data.model.manga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Author(
    @SerialName("node")
    val node: AuthorNode,
    @SerialName("role")
    val role: String
)