package com.cielo.cielopass.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CieloBlue,
    onPrimary = White,
    primaryContainer = Nightfall,
    onPrimaryContainer = Cloud,
    secondary = Pistachio,
    onSecondary = Nightfall,
    secondaryContainer = Nightfall,
    onSecondaryContainer = Cloud,
    tertiary = Pistachio,
    onTertiary = Nightfall,
    error = Sunset,
    onError = White,
    background = Nightfall,
    onBackground = Cloud,
    surface = Nightfall,
    onSurface = Cloud,
    surfaceVariant = Rain,
    onSurfaceVariant = Cloud,
)

private val LightColorScheme = lightColorScheme(
    primary = CieloBlue,
    onPrimary = White,
    primaryContainer = Cloud,
    onPrimaryContainer = Nightfall,
    secondary = Nightfall,
    onSecondary = White,
    secondaryContainer = Cloud,
    onSecondaryContainer = Nightfall,
    tertiary = Pistachio,
    onTertiary = Nightfall,
    error = Sunset,
    onError = White,
    background = White,
    onBackground = Rain,
    surface = Cloud,
    onSurface = Nightfall,
    surfaceVariant = Cloud,
    onSurfaceVariant = Rain,
)

@Composable
fun CieloPassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
