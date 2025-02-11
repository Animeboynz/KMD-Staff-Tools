package com.animeboynz.kmd.ui.preferences.options

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.preferences.PrefsHorizontalPadding
import com.animeboynz.kmd.presentation.components.preferences.TextPreferenceWidget
import com.animeboynz.kmd.ui.theme.spacing
import com.animeboynz.kmd.utils.openInBrowser
import com.animeboynz.kmd.utils.secondaryItemAlpha
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.koin.compose.koinInject

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.ColorsRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.domain.importProtobufData
import com.animeboynz.kmd.presentation.components.preferences.InfoWidget

object DataPreferencesScreen : Screen() {
    private fun readResolve(): Any = DataPreferencesScreen

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val employeeRepository = koinInject<EmployeeRepository>()
        val customerOrderRepository = koinInject<CustomerOrderRepository>()
        val barcodesRepository = koinInject<BarcodesRepository>()
        val productsRepository = koinInject<ProductsRepository>()
        val colorsRepository = koinInject<ColorsRepository>()
        val screenModel = rememberScreenModel {
            DataPreferencesScreenModel(
                employeeRepository,
                customerOrderRepository,
                barcodesRepository,
                productsRepository,
                colorsRepository
            )
        }

        val importFileLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                var importedData = importProtobufData(context, uri)
                if (importedData != null) {
                    screenModel.importProducts(importedData)
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_data_title)) },
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
                    //////////////////
                    PreferenceCategory(
                        title = { Text(text = "Products") },
                    )

                    TextPreferenceWidget(
                        title = "Import products file",
                        onPreferenceClick = {
                            importFileLauncher.launch("application/gzip")
                        },
                    )

                    TextPreferenceWidget(
                        title = "Fetch products from website",
                        onPreferenceClick = {
                            navigator.push(WebFetcherScreen)
                        },
                    )

                    PreferenceCategory(
                        title = { Text(text = "Advanced") },
                    )

                    TextPreferenceWidget(
                        title = stringResource(R.string.pref_data_delete_all_orders),
                        widget = {},
                        onPreferenceClick = {
                            screenModel.deleteAllOrders()
                            Toast.makeText(
                                context,
                                context.getString(R.string.pref_data_orders_cleared),
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                    )

                    TextPreferenceWidget(
                        title = stringResource(R.string.pref_data_delete_all_employees),
                        widget = {},
                        onPreferenceClick = {
                            screenModel.deleteAllEmployees()
                            Toast.makeText(
                                context,
                                context.getString(R.string.pref_data_employees_cleared),
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                    )

                    TextPreferenceWidget(
                        title = "Delete all products",
                        widget = {},
                        onPreferenceClick = {
                            screenModel.deleteAllProducts()
                            Toast.makeText(
                                context,
                                "All products deleted",
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                    )

                    InfoWidget("All actions performed here are final and cannot be undone.")
                }
            }
        }
    }
}
