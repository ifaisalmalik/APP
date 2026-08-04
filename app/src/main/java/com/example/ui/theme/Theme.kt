package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF381E72),
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = EmeraldGreen,
    onSecondary = Color(0xFF003915),
    tertiary = GoldAccent,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = CoralRed,
    onError = Color(0xFF601410)
)

private val LightColorScheme = lightColorScheme(
    primary = GoldPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = GoldLight,
    onPrimaryContainer = Color(0xFF3B2B00),
    secondary = EmeraldGreen,
    onSecondary = Color.White,
    tertiary = GoldDark,
    background = LightBackground,
    onBackground = Color(0xFF1F1B16),
    surface = LightSurface,
    onSurface = Color(0xFF1F1B16),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF52483C),
    error = CoralRed,
    onError = Color.White
)

@Composable
fun LordsFarmhouseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent luxury dark/gold look
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Default to luxury dark aesthetic
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
