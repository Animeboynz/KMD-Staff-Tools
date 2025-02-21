package com.animeboynz.kmd.ui.theme

import android.app.Activity
import com.animeboynz.kmd.R
import com.animeboynz.kmd.preferences.AppearancePreferences
import org.koin.android.ext.android.inject
import kotlin.getValue

interface ThemingDelegate {
    fun applyAppTheme(activity: Activity, appearancePreferences: AppearancePreferences)

    companion object {
        fun getThemeResIds(appTheme: AppTheme, isAmoled: Boolean): List<Int> {
            return buildList(2) {
                add(themeResources.getOrDefault(appTheme, R.style.Theme_Tachiyomi))
                if (isAmoled) add(R.style.ThemeOverlay_Tachiyomi_Amoled)
            }
        }
    }
}

class ThemingDelegateImpl : ThemingDelegate {
    override fun applyAppTheme(activity: Activity, uiPreferences: AppearancePreferences) {
        ThemingDelegate.getThemeResIds(uiPreferences.appTheme.get(), uiPreferences.themeDarkAmoled.get())
            .forEach(activity::setTheme)
    }
}

private val themeResources: Map<AppTheme, Int> = mapOf(
    AppTheme.MONET to R.style.Theme_Tachiyomi_Monet,
    AppTheme.GREEN_APPLE to R.style.Theme_Tachiyomi_GreenApple,
    AppTheme.LAVENDER to R.style.Theme_Tachiyomi_Lavender,
    AppTheme.MIDNIGHT_DUSK to R.style.Theme_Tachiyomi_MidnightDusk,
    AppTheme.NORD to R.style.Theme_Tachiyomi_Nord,
    AppTheme.STRAWBERRY_DAIQUIRI to R.style.Theme_Tachiyomi_StrawberryDaiquiri,
    AppTheme.TAKO to R.style.Theme_Tachiyomi_Tako,
    AppTheme.TEALTURQUOISE to R.style.Theme_Tachiyomi_TealTurquoise,
    AppTheme.YINYANG to R.style.Theme_Tachiyomi_YinYang,
    AppTheme.YOTSUBA to R.style.Theme_Tachiyomi_Yotsuba,
    AppTheme.TIDAL_WAVE to R.style.Theme_Tachiyomi_TidalWave,
)