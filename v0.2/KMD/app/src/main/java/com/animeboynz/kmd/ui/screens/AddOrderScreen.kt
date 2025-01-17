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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.k1rakishou.fsaf.FileManager
import nl.adaptivity.xmlutil.serialization.XML
import org.koin.compose.koinInject
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.presentation.Screen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.coroutineScope
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.text.SimpleDateFormat
import java.util.*
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.presentation.components.SimpleDropdown


class AddOrderScreen : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val customerOrderRepository = koinInject<CustomerOrderRepository>()

        val screenModel = rememberScreenModel(tag = "manga") {
            AddOrderScreenModel(customerOrderRepository)
        }

        // State variables for user input
        var orderDate by remember { mutableStateOf("") }
        var employeeId by remember { mutableStateOf(TextFieldValue("")) }
        var customerName by remember { mutableStateOf(TextFieldValue("")) }
        var customerPhone by remember { mutableStateOf(TextFieldValue("")) }
        var customerMics by remember { mutableStateOf(TextFieldValue("")) }
        var notes by remember { mutableStateOf(TextFieldValue("")) }
        //val status by screenModel.status.collectAsState()

        var status by remember { mutableStateOf(AddOrderScreenModel.Status.NOT_ORDERED) } // Replace DEFAULT with your default status

        // Collect the status from the screen model if needed
        val collectedStatus by screenModel.status.collectAsState()
        var showDatePicker by remember { mutableStateOf(false) }


        LaunchedEffect(collectedStatus) {
            status = collectedStatus ?: AddOrderScreenModel.Status.NOT_ORDERED
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
                    title = {
                        Text(stringResource(R.string.orders_new))
                    },
                    actions = {
//                        IconButton(onClick = { navigator.push(PreferencesScreen) }) {
//                            Icon(Icons.Default.FilterList, null)
//                        }
//                        IconButton(onClick = { navigator.push(PreferencesScreen) }) {
//                            Icon(Icons.Default.Settings, null)
//                        }
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

                OutlinedTextField(
                    value = orderDate,
                    onValueChange = { /* No-op */ },
                    label = { Text(stringResource(R.string.orders_field_date)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }, // Open date picker on click
                    enabled = false // Disable manual input
                )

                OutlinedTextField(
                    value = employeeId,
                    onValueChange = { employeeId = it },
                    label = { Text(stringResource(R.string.orders_field_empid)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text(stringResource(R.string.orders_field_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text(stringResource(R.string.orders_field_phone)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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

                val maxWidth = Modifier.fillMaxWidth()

                SimpleDropdown(
                    label = stringResource(R.string.orders_status),
                    selectedItem = status,
                    items = AddOrderScreenModel.Status.entries,
                    modifier = maxWidth,
                    onSelected = { selectedStatus ->
                        status = selectedStatus ?: AddOrderScreenModel.Status.NOT_ORDERED
                    },
                )

                Button(
                    onClick = {
                        // Generate order ID
                        // Create the order entity
                        val order = CustomerOrderEntity(
                            orderDate = orderDate,
                            employeeId = employeeId.text,
                            customerName = customerName.text,
                            customerPhone = customerPhone.text,
                            customerMics = customerMics.text,
                            notes = notes.text,
                            status = status.displayName
                        )
                        // Insert the order into the database
                        screenModel.addOrder(order)
                        //customerOrderRepository.insertOrder(order)
                        // Show a success message
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