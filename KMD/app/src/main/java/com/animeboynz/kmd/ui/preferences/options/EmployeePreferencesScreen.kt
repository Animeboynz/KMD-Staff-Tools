package com.animeboynz.kmd.ui.preferences.options

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.EmployeeRow
import com.animeboynz.kmd.ui.screens.AddEmployeeScreen
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.koin.compose.koinInject

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
        val employeeDisabledList by screenModel.employeesDisabled.collectAsState()

        var showDialog by remember { mutableStateOf(false) }
        var selectedEmployeeName by remember { mutableStateOf("") }
        var selectedEmployeeId by remember { mutableStateOf("") }
        var selectedEmployeeStatus by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_employees_title)) },
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
                        .padding(paddingValues)
                ) {
                    Button(
                        onClick = {
                            navigator.push(AddEmployeeScreen())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                    ) {
                        Text(stringResource(R.string.pref_employees_add))
                    }

                    if (employeeActiveList.isNotEmpty()) {
                        PreferenceCategory(
                            title = { Text(text = stringResource(id = R.string.pref_employees_current)) },
                        )

                        employeeActiveList.forEach { employee ->
                            EmployeeRow(employee, {
                                selectedEmployeeName = employee.employeeName
                                selectedEmployeeId = employee.employeeId
                                selectedEmployeeStatus = employee.employeeStatus
                                showDialog = true
                            })
                        }
                    }

                    if (employeeDisabledList.isNotEmpty()) {
                        PreferenceCategory(
                            title = { Text(text = stringResource(id = R.string.pref_employees_deactivated)) },
                        )

                        employeeDisabledList.forEach { employee ->
                            EmployeeRow(employee, {
                                selectedEmployeeName = employee.employeeName
                                selectedEmployeeId = employee.employeeId
                                selectedEmployeeStatus = employee.employeeStatus
                                showDialog = true
                            })
                        }
                    }
                }
            }
        }

        if (showDialog) {
            EmpChangeDialog(
                onDismissRequest = { showDialog = false },
                onConfirm = {
                    val newStatus: String = when (selectedEmployeeStatus) {
                        context.getString(R.string.employee_active) -> context.getString(R.string.employee_disabled)
                        context.getString(R.string.employee_disabled) -> context.getString(R.string.employee_active)
                        else -> selectedEmployeeStatus
                    }
                    screenModel.changeEmployeeStatus(selectedEmployeeId, newStatus)
                    Toast.makeText(context, context.getString(R.string.employee_update_success, selectedEmployeeName, selectedEmployeeId), Toast.LENGTH_SHORT).show()
                },
                empName = selectedEmployeeName,
                empId = selectedEmployeeId
            )
        }

    }

    @Composable
    fun EmpChangeDialog(
        onDismissRequest: () -> Unit,
        onConfirm: () -> Unit,
        empName: String,
        empId: String
    ) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    onDismissRequest()
                }) {
                    Text(text = stringResource(R.string.generic_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(R.string.generic_cancel))
                }
            },
            title = {
                Text(text = stringResource(R.string.pref_employees_modify))
            },
            text = {
                Text(text = stringResource(R.string.employee_update_prompt, empName))
            },
        )
    }
}
