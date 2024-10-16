package com.github.diarmaidlindsay.myanimequiz.data.model.media

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class AlternativeTitles(
    @SerialName("synonyms")
    val synonyms: List<String>?,
    @SerialName("en")
    val en: String,
    @SerialName("ja")
    val ja: String
)