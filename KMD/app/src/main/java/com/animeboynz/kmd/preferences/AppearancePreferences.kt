package com.animeboynz.kmd.preferences

import android.os.Build
import com.animeboynz.kmd.preferences.preference.PreferenceStore
import com.animeboynz.kmd.preferences.preference.getEnum
import com.animeboynz.kmd.ui.theme.AppTheme
import com.animeboynz.kmd.ui.theme.DarkMode
import com.animeboynz.kmd.ui.theme.ThemeMode
import com.animeboynz.kmd.utils.DeviceUtil
import com.animeboynz.kmd.utils.isDynamicColorAvailable

class AppearancePreferences(preferenceStore: PreferenceStore) {

    val themeMode = preferenceStore.getEnum("pref_theme_mode_key", ThemeMode.SYSTEM)

    val appTheme = preferenceStore.getEnum(
        "pref_app_theme",
        if (DeviceUtil.isDynamicColorAvailable) {
            AppTheme.MONET
        } else {
            AppTheme.DEFAULT
        },
    )

    val themeDarkAmoled = preferenceStore.getBoolean("pref_theme_dark_amoled_key", false)
}
