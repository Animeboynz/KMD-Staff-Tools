package com.animeboynz.kmd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.presentation.Screen
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.text.SimpleDateFormat
import java.util.*
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.domain.Status
import com.animeboynz.kmd.presentation.components.EmployeeDropdownItem
import com.animeboynz.kmd.presentation.components.SimpleDropdown


class AddOrderScreen : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val customerOrderRepository = koinInject<CustomerOrderRepository>()
        val employeeRepository = koinInject<EmployeeRepository>()

        val screenModel = rememberScreenModel(tag = "manga") {
            AddOrderScreenModel(customerOrderRepository, employeeRepository)
        }

        // State variables for user input
        val employeeList by screenModel.employees.collectAsState()
        val dropdownItems = employeeList.map { EmployeeDropdownItem(it) }
        var selectedEmployee by remember { mutableStateOf<EmployeeDropdownItem?>(null) }

        var orderDate by remember { mutableStateOf("") }
        var customerName by remember { mutableStateOf(TextFieldValue("")) }
        var customerPhone by remember { mutableStateOf(TextFieldValue("")) }
        var customerMics by remember { mutableStateOf(TextFieldValue("")) }
        var notes by remember { mutableStateOf(TextFieldValue("")) }

        var status by remember { mutableStateOf(Status.NOT_ORDERED) }

        // Collect the status from the screen model if needed
        val collectedStatus by screenModel.status.collectAsState()
        var showDatePicker by remember { mutableStateOf(false) }

        // Error states
        var hasNameError by remember { mutableStateOf(false) }
        var hasPhoneError by remember { mutableStateOf(false) }
        var hasDateError by remember { mutableStateOf(false) }
        var hasEmployeeError by remember { mutableStateOf(false) }

        LaunchedEffect(collectedStatus) {
            status = collectedStatus ?: Status.NOT_ORDERED
        }

        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { selectedDateMillis ->
                    selectedDateMillis?.let {
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        orderDate = dateFormat.format(Date(it))
                    }
                },
                onDismiss = { showDatePicker = false }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    title = {
                        Text(stringResource(R.string.orders_new))
                    },
                    actions = {},
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

                SimpleDropdown(
                    label = stringResource(R.string.orders_field_empid),
                    selectedItem = selectedEmployee,
                    items = dropdownItems,
                    modifier = maxWidth,
                    onSelected = { employee ->
                        selectedEmployee = employee
                    }
                )

                OutlinedTextField(
                    value = orderDate,
                    onValueChange = { /* No-op */ },
                    label = { Text(if (hasDateError) "Date is Required" else stringResource(R.string.orders_field_date)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false, // Disable manual input
                    isError = hasDateError
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text(if (hasNameError) "Name is Required" else stringResource(R.string.orders_field_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasNameError
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text(if (hasPhoneError) "Phone is Required" else stringResource(R.string.orders_field_phone)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasPhoneError
                )

                OutlinedTextField(
                    value = customerMics,
                    onValueChange = { customerMics = it },
                    label = { Text(stringResource(R.string.orders_field_misc)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.orders_field_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )

                SimpleDropdown(
                    label = stringResource(R.string.orders_status),
                    selectedItem = status,
                    items = Status.entries,
                    modifier = maxWidth,
                    onSelected = { selectedStatus ->
                        status = selectedStatus ?: Status.NOT_ORDERED
                    },
                )

                Button(
                    onClick = {
                        // Reset error states
                        hasNameError = false
                        hasPhoneError = false
                        hasDateError = false
                        hasEmployeeError = false

                        // Validate required fields
                        if (customerName.text.isBlank()) {
                            hasNameError = true
                        }
                        if (customerPhone.text.isBlank()) {
                            hasPhoneError = true
                        }
                        if (orderDate.isBlank()) {
                            hasDateError = true
                        }
                        if (selectedEmployee == null) {
                            hasEmployeeError = true
                        }
                        if (hasNameError || hasPhoneError || hasDateError || hasEmployeeError) {
                            Toast.makeText(context, "Complete all required fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Create the order entity
                        val order = CustomerOrderEntity(
                            orderDate = orderDate,
                            employeeId = selectedEmployee!!.extraData.toString(),
                            customerName = customerName.text,
                            customerPhone = customerPhone.text,
                            customerMics = customerMics.text,
                            notes = notes.text,
                            status = status.displayName
                        )

                        screenModel.addOrder(order)
                        //customerOrderRepository.insertOrder(order)
                        navigator.pop()
                        Toast.makeText(context, R.string.orders_insert_success, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.orders_add))
                }
            }

        }
    }

    @Composable
    fun DatePickerModal(
        onDateSelected: (Long?) -> Unit,
        onDismiss: () -> Unit
    ) {
        val datePickerState = rememberDatePickerState()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }) {
                    Text(stringResource(R.string.generic_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.generic_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}