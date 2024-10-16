package com.github.diarmaidlindsay.myanimequiz.utils.extensions

import android.text.Html

object StringExtensions {
    /**
     * Returns a string representation of the object.
     * Can be called with a null receiver, in which case it returns `null`.
     */
    fun Any?.toStringOrNull() = this.toString().let { if (it == "null") null else it }

    /**
     * Returns a string representation of the object.
     * Can be called with a null receiver, in which case it returns an empty String.
     */
    fun Any?.toStringOrEmpty() = this.toString().let { if (it == "null") "" else it }

    /**
     * Format the opening/ending text from MAL to use it on YouTube search
     */
    fun String.buildQueryFromThemeText() = this
        .replace(" ", "+")
        .replace("\"", "")
        .replaceFirst(Regex("#?\\w+:"), "") // theme number
        .replace(Regex("\\(ep.*\\)"), "") // episodes
        .trim()


    /***
     * Unescape HTML entities in a string
     *
     * NOTE : This is untested since the original class used StringEscapeUtils from Apache Commons Text
     */
    fun String.unescapeHtml(): String {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
    }
}