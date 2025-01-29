package com.animeboynz.kmd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.domain.OrderItemRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.domain.Status
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.EmployeeDropdownItem
import com.animeboynz.kmd.presentation.components.SimpleDropdown
import java.text.SimpleDateFormat
import java.util.*
import org.koin.compose.koinInject


class AddItemScreen(
    val orderId: Long,
    val editMode: Boolean = false,
    val itemId: Long? = null) : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val orderItemRepository = koinInject<OrderItemRepository>()
        val productsRepository = koinInject<ProductsRepository>()

        val screenModel = rememberScreenModel {
            AddItemScreenModel(orderItemRepository, productsRepository, itemId)
        }

        var sku by remember { mutableStateOf(TextFieldValue("")) }
        var color by remember { mutableStateOf(TextFieldValue("")) }
        var size by remember { mutableStateOf(TextFieldValue("")) }
        var price by remember { mutableStateOf(TextFieldValue("")) }
        var store by remember { mutableStateOf(TextFieldValue("")) }
        var quantity by remember { mutableStateOf(TextFieldValue("")) }
        var status by remember { mutableStateOf(Status.NOT_ORDERED) }

        if (editMode) {
            val order by screenModel.orderItem.collectAsState()

            LaunchedEffect(order) {
                sku = TextFieldValue(order.sku)
                color = TextFieldValue(order.color)
                size = TextFieldValue(order.size)
                price = TextFieldValue(order.price)
                quantity = TextFieldValue(order.quantity.toString())
                store = TextFieldValue(order.store)
                status = Status.fromDisplayName(order.status) ?: Status.NOT_ORDERED

            }
        }

        // Error states
        var hasSkuError by remember { mutableStateOf(false) }
        var hasColorError by remember { mutableStateOf(false) }
        var hasSizeError by remember { mutableStateOf(false) }
        var hasPriceError by remember { mutableStateOf(false) }
        var hasQuantityError by remember { mutableStateOf(false) }

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
                        Text(stringResource(if (editMode) R.string.orders_item_edit else R.string.orders_item))
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

                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text(if (hasSkuError) stringResource(R.string.orders_errors_sku) else stringResource(R.string.sku)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasSkuError
                )

                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text(if (hasColorError) stringResource(R.string.orders_errors_color) else stringResource(R.string.color)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasColorError
                )

                OutlinedTextField(
                    value = size,
                    onValueChange = { size = it },
                    label = { Text(if (hasSizeError) stringResource(R.string.orders_errors_size) else stringResource(R.string.size)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasSizeError
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(if (hasPriceError) stringResource(R.string.orders_errors_price) else stringResource(R.string.price)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasPriceError
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(if (hasQuantityError) stringResource(R.string.orders_errors_quantity) else stringResource(R.string.quantity)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = hasQuantityError
                )

                OutlinedTextField(
                    value = store,
                    onValueChange = { store = it },
                    label = { Text(stringResource(R.string.store)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
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
                        hasSkuError = false
                        hasColorError = false
                        hasSizeError = false
                        hasPriceError = false
                        hasQuantityError = false

                        // Validate required fields
                        if (sku.text.isBlank()) {
                            hasSkuError = true
                        }
                        if (color.text.isBlank()) {
                            hasColorError = true
                        }
                        if (size.text.isBlank()) {
                            hasSizeError = true
                        }
                        if (price.text.isBlank()) {
                            hasPriceError = true
                        }
                        if (quantity.text.isBlank()) {
                            hasQuantityError = true
                        }
                        if (hasSkuError || hasColorError || hasSizeError || hasPriceError || hasQuantityError) {
                            Toast.makeText(context, R.string.input_errors_warning, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val orderItem: OrderItemEntity
                        // Create the order entity
                        if (editMode) {
                            orderItem = OrderItemEntity(
                                orderItemId = itemId!!,
                                orderId = orderId,
                                productName = "",
                                productColor = "",
                                sku = sku.text,
                                color = color.text,
                                size = size.text,
                                price = price.text,
                                quantity = quantity.text.toInt(),
                                store = store.text,
                                status = status.displayName

                            )
                            screenModel.updateOrderItem(orderItem)
                            navigator.pop()
                            Toast.makeText(context, R.string.item_update_success, Toast.LENGTH_SHORT).show()

                        } else {
                            orderItem = OrderItemEntity(
                                orderId = orderId,
                                productName = "",
                                productColor = "",
                                sku = sku.text,
                                color = color.text,
                                size = size.text,
                                price = price.text,
                                quantity = quantity.text.toInt(),
                                store = store.text,
                                status = status.displayName

                            )

                            screenModel.addOrderItem(orderItem)
                            navigator.pop()
                            Toast.makeText(context, R.string.item_insert_success, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(if (editMode) R.string.orders_item_edit else R.string.orders_add))
                }
            }

        }
    }
}