package com.animeboynz.kmd.ui.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.domain.OrderItemRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.domain.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddItemScreenModel(
    private val orderItemRepository: OrderItemRepository,
    private val productsRepository: ProductsRepository,
    private val itemId: Long?
) : StateScreenModel<AddItemScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data object Finished : State()
    }

    var status = MutableStateFlow<Status?>(null)

    var productName = MutableStateFlow<ProductsEntity?>(null)

    private val _orderItem = MutableStateFlow<OrderItemEntity>(OrderItemEntity(0, 0, "", "", "", "", "", "", "", "", 4)) // Use nullable type
    val orderItem: StateFlow<OrderItemEntity> = _orderItem.asStateFlow() // Expose as StateFlow

    init {
        if (itemId != null) {getOrderItem(itemId)}
    }

    fun addOrderItem(orderItem: OrderItemEntity) {
        screenModelScope.launch(Dispatchers.IO) {
            orderItemRepository.insertOrderItem(orderItem)
        }
    }

    fun getOrderItem(itemId: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            val orderEntity = orderItemRepository.getOrderItems(itemId)
            _orderItem.value = orderEntity // Update the StateFlow
        }
    }

    fun updateOrderItem(orderItem: OrderItemEntity) {
        screenModelScope.launch(Dispatchers.IO) {
            orderItemRepository.updateOrderItem(orderItem)
        }
    }

//    fun getProductName(sku: String) {
//        screenModelScope.launch(Dispatchers.IO) {
//            productName.value = productsRepository.getProductName(sku)
//        }
//    }
}
