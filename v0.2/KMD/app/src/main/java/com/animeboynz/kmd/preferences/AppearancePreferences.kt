package com.animeboynz.kmd.preferences

import android.os.Build
import com.animeboynz.kmd.preferences.preference.PreferenceStore
import com.animeboynz.kmd.preferences.preference.getEnum
import com.animeboynz.kmd.ui.theme.DarkMode

class AppearancePreferences(preferenceStore: PreferenceStore) {
    val darkMode = preferenceStore.getEnum("dark_mode", DarkMode.System)
    val materialYou = preferenceStore.getBoolean("material_you", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
}
