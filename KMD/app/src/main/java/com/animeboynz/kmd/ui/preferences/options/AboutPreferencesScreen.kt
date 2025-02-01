package com.animeboynz.kmd.ui.preferences.options

import android.provider.Settings.Global.getString
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.BuildConfig
import com.animeboynz.kmd.R
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.LogoHeader
import com.animeboynz.kmd.presentation.components.icons.CustomIcons
import com.animeboynz.kmd.presentation.components.icons.Github
import com.animeboynz.kmd.presentation.components.preferences.TextPreferenceWidget
import com.animeboynz.kmd.ui.preferences.options.OpenSourceLicensesScreen
import com.animeboynz.kmd.utils.CrashLogUtil
import com.animeboynz.kmd.utils.copyToClipboard
import com.animeboynz.kmd.utils.toast
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

object AboutPreferencesScreen : Screen() {
    private fun readResolve(): Any = AboutPreferencesScreen

    @Composable
    override fun Content() {
        //val scope = rememberCoroutineScope()
        val context = LocalContext.current
        //val uriHandler = LocalUriHandler.current
        //val handleBack = LocalBackPress.current
        val navigator = LocalNavigator.currentOrThrow
        //var isCheckingUpdates by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_about_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    }
                )
            },
        ) { contentPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding),
            ){
                LogoHeader()

                TextPreferenceWidget(
                    title = stringResource(R.string.version),
                    subtitle = getVersionName(true),
                    onPreferenceClick = {
                        val deviceInfo = CrashLogUtil(context).getDebugInfo()
                        context.copyToClipboard("Debug information", deviceInfo)
                    },
                )

                TextPreferenceWidget(
                    title = stringResource(R.string.check_for_updates),
                    widget = {},
                    onPreferenceClick = {
                        Toast.makeText(context, R.string.check_for_updates_not_implemented, Toast.LENGTH_SHORT).show()
                    },
                )

                TextPreferenceWidget(
                    title = stringResource(R.string.licenses),
                    onPreferenceClick = { navigator.push(OpenSourceLicensesScreen()) },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    LinkIcon(
                        label = "GitHub",
                        icon = CustomIcons.Github,
                        url = "https://github.com/Animeboynz/KMD-Staff-Tools",
                    )
                }

            }
        }
    }

    fun getVersionName(withBuildDate: Boolean): String {
        return when {
            BuildConfig.DEBUG -> {
                "KMD Staff Tools Debug r${BuildConfig.COMMIT_COUNT}".let {
                    if (withBuildDate) {
                        "$it (${BuildConfig.BUILD_TIME})"
                    } else {
                        it
                    }
                }
            }
//            BuildConfig.DEBUG -> {
//                "Debug ${BuildConfig.COMMIT_SHA}".let {
//                    if (withBuildDate) {
//                        "$it (${BuildConfig.BUILD_TIME})"
//                    } else {
//                        it
//                    }
//                }
//            }
//            BuildConfig.PREVIEW -> {
//                "Beta r${BuildConfig.COMMIT_COUNT}".let {
//                    if (withBuildDate) {
//                        "$it (${BuildConfig.COMMIT_SHA}, ${getFormattedBuildTime()})"
//                    } else {
//                        "$it (${BuildConfig.COMMIT_SHA})"
//                    }
//                }
//            }
            else -> {
                "KMD Staff Tools Stable ${BuildConfig.VERSION_NAME}".let {
                    if (withBuildDate) {
                        "$it (${BuildConfig.BUILD_TIME})"
                    } else {
                        it
                    }
                }
            }
        }
    }

    @Composable
    fun LinkIcon(
        label: String,
        icon: ImageVector,
        url: String,
        modifier: Modifier = Modifier,
    ) {
        val uriHandler = LocalUriHandler.current
        IconButton(
            modifier = modifier.padding(4.dp),
            onClick = { uriHandler.openUri(url) },
        ) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = label,
            )
        }
    }

}