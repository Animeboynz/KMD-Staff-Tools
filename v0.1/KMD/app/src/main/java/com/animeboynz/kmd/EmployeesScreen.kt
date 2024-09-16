package com.animeboynz.kmd

import KMD.Employees.Employees
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.animeboynz.kmd.data.EmployeeDataSourceImpl
import kotlinx.coroutines.flow.collect

@Composable
fun EmployeesScreen(employeeDataSource: EmployeeDataSourceImpl) {
    val coroutineScope = rememberCoroutineScope()
    val employees = remember { mutableStateOf<List<Employees>>(emptyList()) }
    val showAddDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        employeeDataSource  .getAllEmployees().collect { employeeList ->
            employees.value = employeeList
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { showAddDialog.value = true }) {
            Text("Add Employee")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(employees.value) { employee ->
                Text(text = "${employee.firstName} ${employee.lastName}")
            }
        }
    }

    if (showAddDialog.value) {
        AddEmployeeDialog(
            onDismiss = { showAddDialog.value = false },
            onSave = { firstName, lastName ->
                coroutineScope.launch {
                    employeeDataSource.insertEmployee(firstName, lastName, id = System.currentTimeMillis())
                }
                showAddDialog.value = false
            }
        )
    }
}

@Composable
fun AddEmployeeDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add Employee")
        },
        text = {
            Column {
                BasicTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (firstName.text.isEmpty()) {
                            Text(text = "First Name")
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (lastName.text.isEmpty()) {
                            Text(text = "Last Name")
                        }
                        innerTextField()
                    }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(firstName.text, lastName.text)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
