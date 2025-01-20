package com.animeboynz.kmd.presentation.components.preferences

import com.animeboynz.kmd.preferences.AppearancePreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.preferences.preference.collectAsState
import com.animeboynz.kmd.presentation.BaseColorScheme
import com.animeboynz.kmd.presentation.GreenAppleColorScheme
import com.animeboynz.kmd.presentation.LavenderColorScheme
import com.animeboynz.kmd.presentation.MidnightDuskColorScheme
import com.animeboynz.kmd.presentation.MonetColorScheme
import com.animeboynz.kmd.presentation.NordColorScheme
import com.animeboynz.kmd.presentation.StrawberryColorScheme
import com.animeboynz.kmd.presentation.TachiyomiColorScheme
import com.animeboynz.kmd.presentation.TakoColorScheme
import com.animeboynz.kmd.presentation.TealTurqoiseColorScheme
import com.animeboynz.kmd.presentation.TidalWaveColorScheme
import com.animeboynz.kmd.presentation.YinYangColorScheme
import com.animeboynz.kmd.presentation.YotsubaColorScheme
import com.animeboynz.kmd.ui.theme.AppTheme
import org.koin.compose.koinInject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.animeboynz.kmd.ui.theme.ThemeMode


@Composable
fun TachiyomiTheme(
    appTheme: AppTheme? = null,
    amoled: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val uiPreferences = koinInject<AppearancePreferences>()

    BaseTachiyomiTheme(
        appTheme = (appTheme ?: uiPreferences.appTheme.get()),
        isAmoled = (amoled ?: uiPreferences.themeDarkAmoled.get()),
        content = content,
    )
}

@Composable
fun TachiyomiPreviewTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    isAmoled: Boolean = false,
    content: @Composable () -> Unit,
) = BaseTachiyomiTheme(appTheme, isAmoled, content)

@Composable
private fun BaseTachiyomiTheme(
    appTheme: AppTheme,
    isAmoled: Boolean,
    content: @Composable () -> Unit,
) {
    val preferences = koinInject<AppearancePreferences>()
    val themeMode by preferences.themeMode.collectAsState()

    MaterialTheme(
        colorScheme = getThemeColorScheme(appTheme, themeMode, isAmoled),
        content = content,
    )
}

@Composable
@ReadOnlyComposable
private fun getThemeColorScheme(
    appTheme: AppTheme,
    themeMode: ThemeMode,
    isAmoled: Boolean,
): ColorScheme {

    val colorScheme = if (appTheme == AppTheme.MONET) {
        MonetColorScheme(LocalContext.current)
    } else {
        colorSchemes.getOrDefault(appTheme, TachiyomiColorScheme)
    }

    var dark: Boolean = false

    if (themeMode == ThemeMode.DARK) {
        dark = true
    } else if (themeMode == ThemeMode.LIGHT) {
        dark = false
    } else if (themeMode == ThemeMode.SYSTEM) {
        dark = isSystemInDarkTheme()
    }

    return colorScheme.getColorScheme(
        dark,
        isAmoled,
    )
}

private val colorSchemes: Map<AppTheme, BaseColorScheme> = mapOf(
    AppTheme.DEFAULT to TachiyomiColorScheme,
    AppTheme.GREEN_APPLE to GreenAppleColorScheme,
    AppTheme.LAVENDER to LavenderColorScheme,
    AppTheme.MIDNIGHT_DUSK to MidnightDuskColorScheme,
    AppTheme.NORD to NordColorScheme,
    AppTheme.STRAWBERRY_DAIQUIRI to StrawberryColorScheme,
    AppTheme.TAKO to TakoColorScheme,
    AppTheme.TEALTURQUOISE to TealTurqoiseColorScheme,
    AppTheme.TIDAL_WAVE to TidalWaveColorScheme,
    AppTheme.YINYANG to YinYangColorScheme,
    AppTheme.YOTSUBA to YotsubaColorScheme,
)