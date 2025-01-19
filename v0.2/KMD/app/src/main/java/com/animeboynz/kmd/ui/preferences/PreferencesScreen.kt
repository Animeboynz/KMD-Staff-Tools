package com.animeboynz.kmd.ui.preferences

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preference
import com.animeboynz.kmd.R
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.ui.preferences.options.AppearancePreferencesScreen
import com.animeboynz.kmd.ui.preferences.options.DataPreferencesScreen
import com.animeboynz.kmd.ui.preferences.options.EmployeePreferencesScreen
import com.animeboynz.kmd.ui.preferences.options.GeneralPreferencesScreen

object PreferencesScreen : Screen() {
    private fun readResolve(): Any = PreferencesScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_settings_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                )
            },
        ) { paddingValues ->
            ProvidePreferenceLocals {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    preference(
                        key = "general",
                        title = { Text(text = stringResource(R.string.pref_general_title)) },
                        summary = { Text(text = stringResource(R.string.pref_general_summary)) },
                        icon = { Icon(Icons.Default.Settings, null) },
                        onClick = { navigator.push(GeneralPreferencesScreen) },
                    )
                    preference(
                        key = "employees",
                        title = { Text(text = stringResource(R.string.pref_employees_title)) },
                        summary = { Text(text = stringResource(R.string.pref_employees_summary)) },
                        icon = { Icon(Icons.Outlined.Person, null) },
                        onClick = { navigator.push(EmployeePreferencesScreen) },
                    )
                    preference(
                        key = "appearance",
                        title = { Text(text = stringResource(R.string.pref_appearance_title)) },
                        summary = { Text(text = stringResource(R.string.pref_appearance_summary)) },
                        icon = { Icon(Icons.Outlined.Palette, null) },
                        onClick = { navigator.push(AppearancePreferencesScreen) },
                    )
                    preference(
                        key = "data",
                        title = { Text(text = stringResource(R.string.pref_data_title)) },
                        summary = { Text(text = stringResource(R.string.pref_data_summary)) },
                        icon = { Icon(ImageVector.vectorResource(R.drawable.database_24px), null) },
                        onClick = { navigator.push(DataPreferencesScreen) },
                    )
                }
            }
        }
    }
}
