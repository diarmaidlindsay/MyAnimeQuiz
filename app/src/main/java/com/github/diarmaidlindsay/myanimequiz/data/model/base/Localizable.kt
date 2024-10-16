package com.github.diarmaidlindsay.myanimequiz.data.model.base

import androidx.compose.runtime.Composable

fun interface Localizable {
    @Composable
    fun localized(): String
}