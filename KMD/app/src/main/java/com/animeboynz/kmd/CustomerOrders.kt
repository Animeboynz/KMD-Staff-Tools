// CustomerOrders.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.animeboynz.kmd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrders(
    navController: NavController,
    viewModel: OrdersViewModel
) {
    val tabTitles = listOf("Created", "Ordered", "Arrived", "Completed")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Orders") },
                actions = {
                    IconButton(
                        onClick = {
                            // Navigate to add order screen
                            navController.navigate("add_order_route")
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Add Order"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                //backgroundColor = MaterialTheme.colorScheme.primarySurface,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            // Display orders based on selected tab
            when (selectedTabIndex) {
                0 -> OrderList(orders = viewModel.createdOrders)
                1 -> OrderList(orders = emptyList()) // Placeholder for other tabs
                2 -> OrderList(orders = emptyList()) // Placeholder for other tabs
                3 -> OrderList(orders = emptyList()) // Placeholder for other tabs
            }
        }
    }
}

@Composable
fun OrderList(orders: List<Order>) {
    LazyColumn {
        items(orders) { order ->
            OrderListItem(order = order)
        }
    }
}

@Composable
fun OrderListItem(order: Order) {
    ListItem(
        text = { Text("Order ID: ${order.orderId}") },
        secondaryText = { Text("Customer: ${order.customerName}, Employee: ${order.employeeName}") },
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun AddOrderScreen(
    onAddOrder: (Order) -> Unit,
    onClose: () -> Unit
) {
    // Implement the modal form here as described (fields, items + area, save button)
    // Use TextField, DropdownMenu, DatePicker, etc. for form elements
    // Ensure state management for form fields and items list

    // Example close button
    Button(
        onClick = onClose,
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Close")
    }
}
