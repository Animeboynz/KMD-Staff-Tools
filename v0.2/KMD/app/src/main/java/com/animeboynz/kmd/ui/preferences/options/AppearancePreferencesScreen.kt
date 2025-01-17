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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SwitchPreference
import org.koin.compose.koinInject
import com.animeboynz.kmd.R
import com.animeboynz.kmd.preferences.AppearancePreferences
import com.animeboynz.kmd.preferences.preference.collectAsState
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.preferences.MultiChoiceSegmentedButton
import com.animeboynz.kmd.ui.theme.DarkMode

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
                    val darkMode by preferences.darkMode.collectAsState()
                    MultiChoiceSegmentedButton(
                        choices = DarkMode.entries.map { context.getString(it.titleRes) }.toImmutableList(),
                        selectedIndices = persistentListOf(DarkMode.entries.indexOf(darkMode)),
                        onClick = { preferences.darkMode.set(DarkMode.entries[it]) },
                    )

                    val materialYou by preferences.materialYou.collectAsState()
                    val isMaterialYouAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    SwitchPreference(
                        value = materialYou,
                        onValueChange = { preferences.materialYou.set(it) },
                        title = { Text(text = stringResource(R.string.pref_appearance_material_you_title)) },
                        summary = {
                            Text(
                                text = stringResource(
                                    if (isMaterialYouAvailable) {
                                        R.string.pref_appearance_material_you_summary
                                    } else {
                                        R.string.pref_appearance_material_you_summary_disabled
                                    },
                                ),
                            )
                        },
                        enabled = isMaterialYouAvailable,
                    )

                    PreferenceCategory(
                        title = { Text(text = stringResource(id = R.string.pref_appearance_category_display)) },
                    )
                }
            }
        }
    }
}
