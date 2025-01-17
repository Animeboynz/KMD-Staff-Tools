package com.animeboynz.kmd.ui.home.tabs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ChromeReaderMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.flow.onEach
import org.koin.compose.koinInject
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.util.Tab
import com.animeboynz.kmd.ui.preferences.PreferencesScreen

import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.FloatingActionButton

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.presentation.components.OrderRow
import com.animeboynz.kmd.ui.screens.AddOrderScreen
import com.animeboynz.kmd.ui.screens.CustomerOrderScreen
import com.animeboynz.kmd.ui.theme.MissingColor
import com.animeboynz.kmd.ui.theme.PresentColor
import com.animeboynz.kmd.ui.theme.spacing

object OrdersTab : Tab {
    private fun readResolve(): Any = OrdersTab

    override val options: TabOptions
        @Composable
        get() {
            val image = rememberVectorPainter(Icons.AutoMirrored.Filled.ChromeReaderMode)
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

//        val statusOrder = mapOf(
//            "Not Ordered" to 0,
//            "Ordered" to 1,
//            "Arrived - Not Notified" to 2,
//            "Waiting for Pickup" to 3,
//            "Cancelled" to 4,
//            "Completed" to 5
//        )

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
                            OrderRow(order, {
                                navigator.push(CustomerOrderScreen(order))
                            })
                        }
                    }
                }
            }
        }
    }
}
