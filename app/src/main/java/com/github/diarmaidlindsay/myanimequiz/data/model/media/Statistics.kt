package com.github.diarmaidlindsay.myanimequiz.data.model.media

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Statistics(
    @SerialName("status")
    val status: StatisticsStatus,
    @SerialName("num_list_users")
    val numListUsers: Int
)