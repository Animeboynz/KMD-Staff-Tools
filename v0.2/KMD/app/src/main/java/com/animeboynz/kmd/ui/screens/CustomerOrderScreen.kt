package com.animeboynz.kmd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.presentation.Screen
import org.koin.compose.koinInject
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.domain.OrderItemRepository
import com.animeboynz.kmd.presentation.components.CustomerOrderCard
import com.animeboynz.kmd.presentation.components.NotesItem
import com.animeboynz.kmd.ui.theme.spacing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.components.OrderItemCard

class CustomerOrderScreen(val orderId: Long) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val preferences = koinInject<GeneralPreferences>()

        val customerOrderRepository = koinInject<CustomerOrderRepository>()
        val orderItemRepository = koinInject<OrderItemRepository>()
        val productsRepository = koinInject<ProductsRepository>()
        val screenModel = rememberScreenModel { CustomerOrderScreenModel(customerOrderRepository, orderItemRepository, productsRepository, orderId) }
        val order by screenModel.order.collectAsState()
        val orderItems by screenModel.orderedItems.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "KMD-${preferences.storeNumber.get()}-${order.orderId}")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                screenModel.deleteOrder(order.orderId);
                                navigator.pop()
                                Toast.makeText(context, R.string.orders_delete_success, Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.Delete, null)
                        }
                        IconButton(
                            onClick = {
                            }
                        ) {
                            Icon(Icons.Default.EditNote, null)
                        }
                    },
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                //verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                CustomerOrderCard(order)

                if (order.notes.isNotEmpty())
                {
                    NotesItem(
                        note = order.notes,
                        onClick = { },
                    )
                }

                OrderItemsHeader({navigator.push(AddItemScreen(orderId))})

                OrderItemsList(orderItems, screenModel)


                //Spacer(modifier = Modifier.weight(1f))
                //Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
            }
        }
    }

    @Composable
    fun OrderItemsHeader(onAddItemClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = MaterialTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Space between title and button
        ) {
            Text(
                text = "Order Items",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(end = MaterialTheme.spacing.large)
                    .weight(1f) // Allows the title to take remaining space
            )
            IconButton(onClick = onAddItemClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    }

    @Composable
    fun OrderItemsList(orderItems: List<OrderItemEntity>, screenModel: CustomerOrderScreenModel) {

        val productNames by screenModel.productNames.collectAsState()

        orderItems.forEach { item ->
            if (!productNames.containsKey(item.sku)) {
                screenModel.fetchProductName(item.sku)
            }
            val name = productNames[item.sku] ?: "Loading..."
            OrderItemCard(item, name)
        }
    }
}
