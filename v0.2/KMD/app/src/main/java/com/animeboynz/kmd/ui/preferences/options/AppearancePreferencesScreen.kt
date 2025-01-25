package com.animeboynz.kmd.ui.preferences.options

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.preferences.AppearancePreferences
import com.animeboynz.kmd.preferences.preference.collectAsState
import com.animeboynz.kmd.presentation.AppThemeModePreferenceWidget
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.preferences.AppThemePreferenceWidget
import com.animeboynz.kmd.presentation.components.preferences.MultiChoiceSegmentedButton
import com.animeboynz.kmd.ui.theme.DarkMode
import com.animeboynz.kmd.ui.theme.setAppCompatDelegateThemeMode
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SwitchPreference
import org.koin.compose.koinInject

object AppearancePreferencesScreen : Screen() {
    private fun readResolve(): Any = AppearancePreferencesScreen

    @Composable
    override fun Content() {
        val preferences = koinInject<AppearancePreferences>()
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_appearance_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                )
            },
        ) { paddingValues ->
            ProvidePreferenceLocals {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues),
                ) {
                    PreferenceCategory(
                        title = { Text(text = stringResource(id = R.string.pref_appearance_category_theme)) },
                    )

                    val themeModePref = preferences.themeMode
                    val themeMode by themeModePref.collectAsState()

                    val appThemePref = preferences.appTheme
                    val appTheme by appThemePref.collectAsState()

                    val amoledPref = preferences.themeDarkAmoled
                    val amoled by amoledPref.collectAsState()

                    AppThemeModePreferenceWidget(
                        value = themeMode,
                        onItemClick = {
                            themeModePref.set(it)
                            setAppCompatDelegateThemeMode(it)
                        },
                    )

                    AppThemePreferenceWidget(
                        value = appTheme,
                        amoled = amoled,
                        onItemClick = { appThemePref.set(it) },
                    )

                    PreferenceCategory(
                        title = { Text(text = stringResource(id = R.string.pref_appearance_category_display)) },
                    )
                }
            }
        }
    }
}
