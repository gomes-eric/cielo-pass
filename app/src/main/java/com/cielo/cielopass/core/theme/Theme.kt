package com.cielo.cielopass.core.theme

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
    onPrimary = White100,
    primaryContainer = Nightfall,
    onPrimaryContainer = White100,
    secondary = White100,
    onSecondary = Nightfall,
    secondaryContainer = DeepNightfall,
    onSecondaryContainer = Cloud,
    tertiary = Pistachio,
    onTertiary = Nightfall,
    error = Sunset,
    onError = White100,
    background = Nightfall,
    onBackground = White100,
    surface = Nightfall,
    onSurface = White100,
    surfaceVariant = DeepNightfall,
    onSurfaceVariant = Cloud,
    outline = CieloBlue,
    outlineVariant = Cloud,
)

private val LightColorScheme = lightColorScheme(
    primary = CieloBlue,
    onPrimary = White100,
    primaryContainer = Cloud,
    onPrimaryContainer = Nightfall,
    secondary = Rain,
    onSecondary = White100,
    secondaryContainer = Cloud,
    onSecondaryContainer = Rain,
    tertiary = Pistachio,
    onTertiary = Nightfall,
    error = Sunset,
    onError = White100,
    background = White100,
    onBackground = Rain,
    surface = Cloud,
    onSurface = Nightfall,
    surfaceVariant = Cloud,
    onSurfaceVariant = Rain,
    outline = Rain,
    outlineVariant = Cloud,
)

@Composable
fun CieloPassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> {
            DarkColorScheme
        }

        else -> {
            LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
