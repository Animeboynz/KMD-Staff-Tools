package com.animeboynz.kmd
import Order
import OrdersViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.animeboynz.kmd.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderScreen(
    viewModel: OrdersViewModel,
    onAddOrder: (Order) -> Unit,
    onClose: () -> Unit,
    navController: NavController
) {
    // State for form fields
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerMembership by remember { mutableStateOf("") }
    var employeeName by remember { mutableStateOf("") }
    var dateRequiredBy by remember { mutableStateOf("") }

    // State for items
    var items by remember { mutableStateOf(mutableListOf("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Order ID - Auto generated and not changeable
        Text("Order ID: KMD-183-${viewModel.createdOrders.size + 1}", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Customer Name
        TextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Customer Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Customer Phone
        TextField(
            value = customerPhone,
            onValueChange = { customerPhone = it },
            label = { Text("Customer Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Customer Membership
        TextField(
            value = customerMembership,
            onValueChange = { customerMembership = it },
            label = { Text("Customer Membership") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Employee Name (Dropdown)
        // Example list of employees
        val employees = listOf("Alice", "Bob", "Charlie")
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = employeeName,
                onValueChange = { employeeName = it },
                label = { Text("Employee Name") },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(painterResource(id = R.drawable.ic_baseline_arrow_drop_down_circle_24), contentDescription = "Dropdown")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )


        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Required By (DatePicker)
        // You need to implement a DatePicker composable function
        // DatePicker(
        //     value = dateRequiredBy,
        //     onValueChange = { dateRequiredBy = it },
        //     label = { Text("Date Required By") },
        //     modifier = Modifier.fillMaxWidth()
        // )

        Spacer(modifier = Modifier.height(16.dp))

        // Items + Area
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items.size) { index ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = items[index],
                        onValueChange = { items[index] = it },
                        label = { Text("SKU ${index + 1}") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    TextField(
                        value = "", // Placeholder for store field
                        onValueChange = { /* TODO: Implement store field */ },
                        label = { Text("Store") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            items.add("")
                        }
                    ) {
                        Icon(painterResource(id = R.drawable.ic_add), contentDescription = "Add Item")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                val newOrder = Order(
                    orderId = "KMD-183-${viewModel.createdOrders.size + 1}",
                    customerName = customerName,
                    customerPhone = customerPhone,
                    customerMembership = customerMembership,
                    employeeName = employeeName,
                    dateRequiredBy = dateRequiredBy,
                    items = items.filter { it.isNotBlank() }
                )
                onAddOrder(newOrder)
                onClose()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}