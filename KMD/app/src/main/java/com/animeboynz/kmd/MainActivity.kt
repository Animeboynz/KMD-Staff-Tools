package com.animeboynz.kmd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.animeboynz.kmd.preferences.AppearancePreferences
import com.animeboynz.kmd.preferences.preference.collectAsState
import com.animeboynz.kmd.presentation.components.preferences.TachiyomiTheme
import com.animeboynz.kmd.ui.home.HomeScreen
import com.animeboynz.kmd.ui.theme.ThemeMode
import com.animeboynz.kmd.utils.FirebaseConfig
import org.koin.android.ext.android.inject
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.luminance

class MainActivity : BaseActivity() {
    //private val appearancePreferences by inject<AppearancePreferences>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseConfig.init(applicationContext)
        FirebaseConfig.setAnalyticsEnabled(true)
        FirebaseConfig.setCrashlyticsEnabled(true)

        setContent {
            val dark by appearancePreferences.themeMode.collectAsState()
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val statusBarBackgroundColor = MaterialTheme.colorScheme.surface

            LaunchedEffect(isSystemInDarkTheme, statusBarBackgroundColor) {
                // Draw edge-to-edge and set system bars color to transparent
                val lightStyle = SystemBarStyle.light(Color.Transparent.toArgb(), Color.Black.toArgb())
                val darkStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
                enableEdgeToEdge(
                    statusBarStyle = if (isSystemInDarkTheme) darkStyle else lightStyle,
                    navigationBarStyle = if (isSystemInDarkTheme) darkStyle else lightStyle,
                )
            }

            TachiyomiTheme() {
                Navigator(screen = HomeScreen) {
                    SlideTransition(navigator = it)
                }
            }
        }
    }
}
