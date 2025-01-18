package com.animeboynz.kmd.ui.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.OrderItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerOrderScreenModel(
    private val customerOrderRepository: CustomerOrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val orderId: Long,
) : ScreenModel {

    var orderedItems = MutableStateFlow<List<OrderItemEntity>>(emptyList())

    private val _order = MutableStateFlow<CustomerOrderEntity>(CustomerOrderEntity(0, "", "", "", "", "", "", "")) // Use nullable type
    val order: StateFlow<CustomerOrderEntity> = _order.asStateFlow() // Expose as StateFlow

    init {
        getOrder(orderId)
        getOrderItems(orderId)
    }

    fun deleteOrder(orderId: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            customerOrderRepository.deleteOrder(orderId)
        }
    }

    fun getOrderItems(orderId: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            orderItemRepository.getOrderItemsForOrder(orderId).collect { employeeList ->
                orderedItems.value = employeeList
            }
        }
    }

    fun getOrder(orderId: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            val orderEntity = customerOrderRepository.getOrderById(orderId)
            _order.value = orderEntity // Update the StateFlow
        }
    }

}
