package com.github.diarmaidlindsay.myanimequiz.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Calendar
import java.util.Date

@Serializable
data class AccessToken(
    @SerialName("token_type")
    val tokenType: String = "",
    @SerialName("expires_in")
    val expiresIn: Long = 0,
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null,

    override val error: String? = null,
    override val message: String? = null,
) : BaseResponse {
    val expiryDate: Date
        get() {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis() + (expiresIn * 1000)
            return calendar.time
        }
}