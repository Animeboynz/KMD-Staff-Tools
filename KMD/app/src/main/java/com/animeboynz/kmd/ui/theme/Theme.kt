// ui/theme/Theme.kt
package com.animeboynz.kmd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    primaryContainer = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    secondaryContainer = Color(0xFF018786),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF2b2d30),
    onSurface = Color.White
)

@Composable
fun KMDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        DarkColorScheme // Keeping the same color scheme for dark mode
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
