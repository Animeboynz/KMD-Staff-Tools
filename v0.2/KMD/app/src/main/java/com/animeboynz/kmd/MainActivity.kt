package com.animeboynz.kmd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.android.ext.android.inject
import com.animeboynz.kmd.preferences.AppearancePreferences
import com.animeboynz.kmd.preferences.preference.collectAsState
import com.animeboynz.kmd.ui.home.HomeScreen
import com.animeboynz.kmd.ui.theme.KMDTheme
import com.animeboynz.kmd.ui.theme.DarkMode

class MainActivity : ComponentActivity() {
    private val appearancePreferences by inject<AppearancePreferences>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dark by appearancePreferences.darkMode.collectAsState()
            val isSystemInDarkTheme = isSystemInDarkTheme()
            enableEdgeToEdge(
                SystemBarStyle.auto(
                    lightScrim = Color.White.toArgb(),
                    darkScrim = Color.White.toArgb(),
                ) { dark == DarkMode.Dark || (dark == DarkMode.System && isSystemInDarkTheme) },
            )

            KMDTheme {
                Navigator(screen = HomeScreen) {
                    SlideTransition(navigator = it)
                }
            }
        }
    }
}
