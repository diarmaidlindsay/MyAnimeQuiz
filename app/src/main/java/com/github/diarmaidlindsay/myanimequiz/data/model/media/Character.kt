package com.github.diarmaidlindsay.myanimequiz.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.diarmaidlindsay.myanimequiz.R
import com.github.diarmaidlindsay.myanimequiz.data.model.base.Localizable
import com.github.diarmaidlindsay.myanimequiz.utils.extensions.StringExtensions.unescapeHtml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val node: Node,
    val role: Role? = null,
) {
    @Serializable
    data class Node(
        @SerialName("id")
        val id: Int,
        @SerialName("first_name")
        val firstName: String? = null,
        @SerialName("last_name")
        val lastName: String? = null,
        @SerialName("alternative_name")
        val alternativeName: String? = null,
        @SerialName("main_picture")
        val mainPicture: MainPicture? = null,
        @SerialName("biography")
        val biography: String? = null,
    )

    @Serializable
    enum class Role : Localizable {
        @SerialName("Main")
        MAIN,

        @SerialName("Supporting")
        SUPPORTING;

        @Composable
        override fun localized() = when (this) {
            MAIN -> stringResource(R.string.role_main)
            SUPPORTING -> stringResource(R.string.role_supporting)
        }
    }

    fun fullName(): String {
        // MAL API returns special characters escaped
        val firstNameUnescaped = node.firstName.orEmpty().unescapeHtml()
        val lastNameUnescaped = node.lastName.orEmpty().unescapeHtml()
        return "$firstNameUnescaped $lastNameUnescaped"
    }
}
