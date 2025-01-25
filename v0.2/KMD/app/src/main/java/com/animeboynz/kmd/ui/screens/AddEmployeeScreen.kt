package com.animeboynz.kmd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.presentation.Screen
import org.koin.compose.koinInject


class AddEmployeeScreen : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val employeeRepository = koinInject<EmployeeRepository>()

        val screenModel = rememberScreenModel(tag = "manga") {
            AddEmployeeScreenModel(employeeRepository)
        }

        // State variables for user input
        var employeeId by remember { mutableStateOf(TextFieldValue("")) }
        var employeeName by remember { mutableStateOf(TextFieldValue("")) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.pref_employees_add))
                    },
                    actions = {
//
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                )
            }
        ) { paddingValues ->
            val paddingModifier = Modifier.padding(paddingValues)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val maxWidth = Modifier.fillMaxWidth()

                OutlinedTextField(
                    value = employeeId,
                    onValueChange = { employeeId = it },
                    label = { Text(stringResource(R.string.orders_field_empid)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = employeeName,
                    onValueChange = { employeeName = it },
                    label = { Text(stringResource(R.string.orders_field_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        val emp = EmployeeEntity(
                            employeeId = employeeId.text,
                            employeeName = employeeName.text,
                            employeeStatus = "Active"
                        )
                        screenModel.addEmployees(emp)
                        navigator.pop()
                        Toast.makeText(context, R.string.employee_insert_success, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.pref_employees_add))
                }
            }

        }
    }
}