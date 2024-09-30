package com.github.diarmaidlindsay.myanimequiz.data.model

import kotlinx.serialization.SerialName

interface BaseResponse {
    @SerialName("error")
    val error: String?

    @SerialName("message")
    val message: String?
}