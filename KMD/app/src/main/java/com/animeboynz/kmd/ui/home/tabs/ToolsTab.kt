package com.animeboynz.kmd.ui.home.tabs

import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.animeboynz.kmd.R
import com.animeboynz.kmd.presentation.components.tools.ToolRow
import com.animeboynz.kmd.presentation.util.Tab
import com.animeboynz.kmd.ui.preferences.PreferencesScreen
import com.animeboynz.kmd.ui.screens.tools.BarcodeScanTest
import com.animeboynz.kmd.ui.screens.tools.CashRow
import com.animeboynz.kmd.ui.screens.tools.StockManagementScreen
import com.animeboynz.kmd.ui.theme.spacing


object ToolsTab : Tab {
    private fun readResolve(): Any = ToolsTab

    override val options: TabOptions
        @Composable
        get() {
            val image = rememberVectorPainter(Icons.Filled.Construction)
            return TabOptions(
                index = 0u,
                title = stringResource(R.string.tools_tab),
                icon = image,
            )
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.tools_tab))
                    },
                    actions = {
                        IconButton(onClick = { navigator.push(PreferencesScreen) }) {
                            Icon(Icons.Default.Settings, null)
                        }
                    },
                )
            },
        ) { paddingValues ->
            val paddingModifier = Modifier.padding(paddingValues)

            Column(modifier = paddingModifier) {
                ToolRow(stringResource(R.string.tools_cash_count), {
                    navigator.push(CashRow())
                })
                ToolRow("Offsite Inventory") {
                    navigator.push(StockManagementScreen())
                }
            }
        }
    }

    @Composable
    fun rememberImeState(): State<Boolean> {
        val imeState = remember {
            mutableStateOf(false)
        }

        val view = LocalView.current
        DisposableEffect(view) {
            val listener = ViewTreeObserver.OnGlobalLayoutListener {
                val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
                imeState.value = isKeyboardOpen
            }

            view.viewTreeObserver.addOnGlobalLayoutListener(listener)
            onDispose {
                view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        }
        return imeState
    }
}