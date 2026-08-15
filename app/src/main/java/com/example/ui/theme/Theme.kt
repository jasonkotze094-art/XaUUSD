package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAppAccentColor = staticCompositionLocalOf { AccentGold }

private val DarkColorScheme = darkColorScheme(
    primary = AccentGold,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF2C2415),
    onPrimaryContainer = AccentGold,
    secondary = AccentHotPink,
    onSecondary = Color.White,
    background = CyberDarkBg,
    onBackground = CyberTextPrimary,
    surface = CyberCardBg,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberCardBgElevated,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberCardBorder
)

@Composable
fun GoldBeastTheme(
    accentColor: Color = AccentGold,
    content: @Composable () -> Unit
) {
    val dynamicDark = DarkColorScheme.copy(
        primary = accentColor,
        onPrimary = if (accentColor == AccentWhite) Color.Black else Color.White,
        primaryContainer = accentColor.copy(alpha = 0.15f),
        onPrimaryContainer = accentColor
    )

    CompositionLocalProvider(LocalAppAccentColor provides accentColor) {
        MaterialTheme(
            colorScheme = dynamicDark,
            typography = Typography,
            content = content
        )
    }
}
