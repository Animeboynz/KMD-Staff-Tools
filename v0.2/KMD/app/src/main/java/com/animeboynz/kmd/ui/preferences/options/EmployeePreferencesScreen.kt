package com.animeboynz.kmd.ui.preferences.options

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SwitchPreference
import me.zhanghai.compose.preference.TextFieldPreference
import org.koin.compose.koinInject
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.preferences.AppearancePreferences
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.preferences.preference.collectAsState
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.EmployeeDropdownItem
import com.animeboynz.kmd.presentation.components.EmployeeRow
import com.animeboynz.kmd.presentation.components.preferences.MultiChoiceSegmentedButton
import com.animeboynz.kmd.ui.screens.AddEmployeeScreen
import com.animeboynz.kmd.ui.screens.CustomerOrderScreen
import com.animeboynz.kmd.ui.theme.DarkMode
import com.animeboynz.kmd.ui.theme.spacing
import com.animeboynz.kmd.utils.Constants

object EmployeePreferencesScreen : Screen() {
    private fun readResolve(): Any = EmployeePreferencesScreen

    @Composable
    override fun Content() {
        val preferences = koinInject<GeneralPreferences>()
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val employeeRepository = koinInject<EmployeeRepository>()
        val screenModel = rememberScreenModel { EmployeePreferencesScreenModel(employeeRepository) }

        val employeeActiveList by screenModel.employeesActive.collectAsState()
        val employeeDisablesList by screenModel.employeesDisabled.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_general_title)) },
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
                        //.verticalScroll(rememberScrollState())
                        .padding(paddingValues),
                ) {
                    Button(
                        onClick = {
                            navigator.push(AddEmployeeScreen())
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.pref_employees_add))
                    }

                    PreferenceCategory(
                        title = { Text(text = stringResource(id = R.string.pref_employees_current)) },
                    )

                    LazyColumn {
                        items(employeeActiveList) { employee ->
                            EmployeeRow(employee, {})
                        }
                    }

                    PreferenceCategory(
                        title = { Text(text = stringResource(id = R.string.pref_employees_deactivated)) },
                    )

                    LazyColumn {
                        items(employeeDisablesList) { employee ->
                            EmployeeRow(employee, {})
                        }
                    }


                }
            }
        }
    }
}
