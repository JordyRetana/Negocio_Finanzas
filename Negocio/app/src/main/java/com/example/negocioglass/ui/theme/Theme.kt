package com.example.negocioglass.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NegocioGlassDarkColors = darkColorScheme(
    primary = BlueAccent,
    onPrimary = TextPrimary,
    secondary = CyanAccent,
    onSecondary = Midnight,
    tertiary = GoldAccent,
    onTertiary = Midnight,

    background = Midnight,
    onBackground = TextPrimary,

    surface = SurfaceSoft,
    onSurface = TextPrimary,

    surfaceVariant = SurfaceGlassStrong,
    onSurfaceVariant = TextSecondary,

    error = Danger,
    onError = TextPrimary
)

private val NegocioGlassLightColors = lightColorScheme(
    primary = BlueAccent,
    onPrimary = TextPrimary,
    secondary = CyanAccent,
    onSecondary = Midnight,
    tertiary = GoldAccent,
    onTertiary = Midnight,

    background = Midnight,
    onBackground = TextPrimary,

    surface = SurfaceSoft,
    onSurface = TextPrimary,

    surfaceVariant = SurfaceGlassStrong,
    onSurfaceVariant = TextSecondary,

    error = Danger,
    onError = TextPrimary
)

@Composable
fun NegocioGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        NegocioGlassDarkColors
    } else {
        NegocioGlassLightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}