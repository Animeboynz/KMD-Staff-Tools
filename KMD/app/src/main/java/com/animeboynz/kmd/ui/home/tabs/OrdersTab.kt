package com.animeboynz.kmd.ui.home.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.animeboynz.kmd.R
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.components.OrderRow
import com.animeboynz.kmd.presentation.util.Tab
import com.animeboynz.kmd.ui.preferences.PreferencesScreen
import com.animeboynz.kmd.ui.screens.AddOrderScreen
import com.animeboynz.kmd.ui.screens.CustomerOrderScreen
import org.koin.compose.koinInject

object OrdersTab : Tab {
    private fun readResolve(): Any = OrdersTab

    override val options: TabOptions
        @Composable
        get() {
            val image = rememberVectorPainter(Icons.AutoMirrored.Filled.ReceiptLong)
            return TabOptions(
                index = 0u,
                title = stringResource(R.string.orders_tab),
                icon = image,
            )
        }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val preferences = koinInject<GeneralPreferences>()
        val orderRepository = koinInject<CustomerOrderRepository>()
        val screenModel = rememberScreenModel { OrdersTabScreenModel(preferences, orderRepository) }

        val allOrders by screenModel.customerOrders.collectAsState()

        val statusOrder = mapOf(
            stringResource(R.string.orders_state_not_ordered) to 0,
            stringResource(R.string.orders_state_ordered) to 1,
            stringResource(R.string.orders_state_not_notified) to 2,
            stringResource(R.string.orders_state_waiting_pickup) to 3,
            stringResource(R.string.orders_state_cancelled) to 4,
            stringResource(R.string.orders_state_completed) to 5
        )

        val sortedOrders = allOrders.sortedBy { statusOrder[it.status] ?: Int.MAX_VALUE }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.orders_title))
                    },
                    actions = {
                        IconButton(onClick = { navigator.push(PreferencesScreen) }) {
                            Icon(Icons.Default.FilterList, null)
                        }
                        IconButton(onClick = { navigator.push(PreferencesScreen) }) {
                            Icon(Icons.Default.Settings, null)
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navigator.push(AddOrderScreen())
                }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.orders_add))
                }
            }
        ) { paddingValues ->
            val paddingModifier = Modifier.padding(paddingValues)
            Column(modifier = paddingModifier) {
                if (allOrders.isEmpty()) {
                    Surface {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.orders_none),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Button(
                                onClick = { navigator.push(AddOrderScreen()) },
                            ) {
                                Text(text = stringResource(R.string.orders_new))
                            }
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(sortedOrders) { order ->
                            OrderRow(order,
                                preferences.storeNumber.get(),
                                { navigator.push(CustomerOrderScreen(order.orderId)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
