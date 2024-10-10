package com.github.diarmaidlindsay.myanimequiz.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = maq_theme_light_primary,
    onPrimary = maq_theme_light_onPrimary,
    primaryContainer = maq_theme_light_primaryContainer,
    onPrimaryContainer = maq_theme_light_onPrimaryContainer,
    secondary = maq_theme_light_secondary,
    onSecondary = maq_theme_light_onSecondary,
    secondaryContainer = maq_theme_light_secondaryContainer,
    onSecondaryContainer = maq_theme_light_onSecondaryContainer,
    tertiary = maq_theme_light_tertiary,
    onTertiary = maq_theme_light_onTertiary,
    tertiaryContainer = maq_theme_light_tertiaryContainer,
    onTertiaryContainer = maq_theme_light_onTertiaryContainer,
    error = maq_theme_light_error,
    errorContainer = maq_theme_light_errorContainer,
    onError = maq_theme_light_onError,
    onErrorContainer = maq_theme_light_onErrorContainer,
    background = maq_theme_light_background,
    onBackground = maq_theme_light_onBackground,
    surface = maq_theme_light_surface,
    onSurface = maq_theme_light_onSurface,
    surfaceVariant = maq_theme_light_surfaceVariant,
    onSurfaceVariant = maq_theme_light_onSurfaceVariant,
    outline = maq_theme_light_outline,
    inverseOnSurface = maq_theme_light_inverseOnSurface,
    inverseSurface = maq_theme_light_inverseSurface,
    inversePrimary = maq_theme_light_inversePrimary,
    surfaceTint = maq_theme_light_surfaceTint,
    outlineVariant = maq_theme_light_outlineVariant,
    scrim = maq_theme_light_scrim,
)

private val DarkColors = darkColorScheme(
    primary = maq_theme_dark_primary,
    onPrimary = maq_theme_dark_onPrimary,
    primaryContainer = maq_theme_dark_primaryContainer,
    onPrimaryContainer = maq_theme_dark_onPrimaryContainer,
    secondary = maq_theme_dark_secondary,
    onSecondary = maq_theme_dark_onSecondary,
    secondaryContainer = maq_theme_dark_secondaryContainer,
    onSecondaryContainer = maq_theme_dark_onSecondaryContainer,
    tertiary = maq_theme_dark_tertiary,
    onTertiary = maq_theme_dark_onTertiary,
    tertiaryContainer = maq_theme_dark_tertiaryContainer,
    onTertiaryContainer = maq_theme_dark_onTertiaryContainer,
    error = maq_theme_dark_error,
    errorContainer = maq_theme_dark_errorContainer,
    onError = maq_theme_dark_onError,
    onErrorContainer = maq_theme_dark_onErrorContainer,
    background = maq_theme_dark_background,
    onBackground = maq_theme_dark_onBackground,
    surface = maq_theme_dark_surface,
    onSurface = maq_theme_dark_onSurface,
    surfaceVariant = maq_theme_dark_surfaceVariant,
    onSurfaceVariant = maq_theme_dark_onSurfaceVariant,
    outline = maq_theme_dark_outline,
    inverseOnSurface = maq_theme_dark_inverseOnSurface,
    inverseSurface = maq_theme_dark_inverseSurface,
    inversePrimary = maq_theme_dark_inversePrimary,
    surfaceTint = maq_theme_dark_surfaceTint,
    outlineVariant = maq_theme_dark_outlineVariant,
    scrim = maq_theme_dark_scrim,
)

private fun ColorScheme.toBlack() = this.copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = surfaceVariant.copy(alpha = 0.4f).compositeOver(Color.Black),
    surfaceContainer = Color.Black,
    surfaceContainerHigh = surfaceContainerHigh.copy(alpha = 0.5f).compositeOver(Color.Black),
    surfaceContainerHighest = surfaceContainerHighest.copy(alpha = 0.6f).compositeOver(Color.Black)
)

@Composable
fun MyAnimeQuizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    useBlackColors: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = remember(dynamicColor, darkTheme, useBlackColors) {
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (darkTheme) dynamicDarkColorScheme(context).let {
                    return@let if (useBlackColors) it.toBlack() else it
                }
                else dynamicLightColorScheme(context)
            }

            darkTheme -> if (useBlackColors) DarkColors.toBlack() else DarkColors
            else -> LightColors
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
