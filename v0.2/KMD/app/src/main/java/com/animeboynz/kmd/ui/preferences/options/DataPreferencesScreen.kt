package com.animeboynz.kmd.ui.preferences.options

import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.koin.compose.koinInject
import com.animeboynz.kmd.R
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.ConfirmDialog

object DataPreferencesScreen : Screen() {
    private fun readResolve(): Any = DataPreferencesScreen

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val preferences = koinInject<GeneralPreferences>()

        val employeeRepository = koinInject<EmployeeRepository>()
        val customerOrderRepository = koinInject<CustomerOrderRepository>()
        val screenModel = rememberScreenModel { DataPreferencesScreenModel(preferences, employeeRepository, customerOrderRepository) }

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
                    Preference(
                        title = { Text(text = stringResource(R.string.pref_data_delete_all_orders)) },
                        onClick = {
                            screenModel.deleteAllOrders()
                            Toast.makeText(
                                context,
                                context.getString(R.string.pref_data_orders_cleared),
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                    )

                    Preference(
                        title = { Text(text = stringResource(R.string.pref_data_delete_all_employees)) },
                        onClick = {
                            screenModel.deleteAllEmployees()
                            Toast.makeText(
                                context,
                                context.getString(R.string.pref_data_employees_cleared),
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                    )
                }
            }
        }
    }
}
