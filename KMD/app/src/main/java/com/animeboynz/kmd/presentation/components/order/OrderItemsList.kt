package com.animeboynz.kmd.presentation.components.order

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.Navigator
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.ui.screens.AddItemScreen
import com.animeboynz.kmd.ui.screens.CustomerOrderScreenModel
import kotlin.collections.forEach

@Composable
fun OrderItemsList(orderId: Long, orderItems: List<OrderItemEntity>, screenModel: CustomerOrderScreenModel, navigator: Navigator) {

    val productNames by screenModel.productNames.collectAsState()

    orderItems.forEach { item ->
        if (!productNames.containsKey(item.sku)) {
            screenModel.fetchProductName(item.sku)
        }
        val name = productNames[item.sku] ?: stringResource(R.string.orders_loading)
        OrderItemCard(item, name, {
            navigator.push(AddItemScreen(orderId, true, item.orderItemId))
        })
    }
}