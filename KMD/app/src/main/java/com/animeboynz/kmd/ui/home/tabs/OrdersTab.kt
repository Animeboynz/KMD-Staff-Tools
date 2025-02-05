package com.animeboynz.kmd.ui.home.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.components.EmptyScreen
import com.animeboynz.kmd.presentation.components.order.OrderRow
import com.animeboynz.kmd.presentation.components.order.tabs.OrderScrollableTabs
import com.animeboynz.kmd.presentation.util.Tab
import com.animeboynz.kmd.ui.preferences.PreferencesScreen
import com.animeboynz.kmd.ui.screens.AddOrderScreen
import com.animeboynz.kmd.ui.screens.CustomerOrderScreen
import com.animeboynz.kmd.ui.theme.spacing
import kotlinx.coroutines.launch
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
        val employeeRepository = koinInject<EmployeeRepository>()
        val screenModel = rememberScreenModel { OrdersTabScreenModel(preferences, orderRepository, employeeRepository) }

        val allOrders by screenModel.customerOrders.collectAsState()
        val allEmployees by screenModel.employees.collectAsState()

        val statusOrder = listOf(
            stringResource(R.string.orders_state_not_ordered),
            stringResource(R.string.orders_state_not_notified),
            stringResource(R.string.orders_state_waiting_pickup),
            stringResource(R.string.orders_state_ordered),
            stringResource(R.string.orders_state_cancelled),
            stringResource(R.string.orders_state_completed)
        )

        val pagerState = rememberPagerState { statusOrder.size }
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.orders_title)) },
                    actions = {
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
            Column(modifier = Modifier.padding(paddingValues)) {

                OrderScrollableTabs(
                    categories = statusOrder, // Assuming Status takes a name
                    pagerState = pagerState,
                    getNumberOfOrdersForCategory = { status ->
                        allOrders.count { it.status == status } // Count orders per status
                    },
                    onTabItemClick = { index ->
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )


                // Pager for each order status
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val filteredOrders = allOrders.filter { it.status == statusOrder[page] }

                    if (filteredOrders.isEmpty()) {
                        EmptyScreen(R.string.orders_none)
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = MaterialTheme.spacing.smaller),
                        ) {
                            items(filteredOrders) { order ->
                                OrderRow(
                                    order,
                                    allEmployees.find { it.employeeId == order.employeeId }?.employeeName ?: "Not Found",
                                    preferences.storeNumber.get(),
                                    preferences.orderNumberPadding.get().toString(),
                                    { navigator.push(CustomerOrderScreen(order.orderId)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}
