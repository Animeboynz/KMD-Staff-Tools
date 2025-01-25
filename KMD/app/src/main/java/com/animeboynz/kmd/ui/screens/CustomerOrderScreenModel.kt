package com.animeboynz.kmd.ui.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.OrderItemRepository
import com.animeboynz.kmd.domain.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerOrderScreenModel(
    private val customerOrderRepository: CustomerOrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productsRepository: ProductsRepository,
    private val orderId: Long,
) : ScreenModel {

    var orderedItems = MutableStateFlow<List<OrderItemEntity>>(emptyList())

    private val _order = MutableStateFlow<CustomerOrderEntity>(CustomerOrderEntity(0, "", "", "", "", "", "", "")) // Use nullable type
    val order: StateFlow<CustomerOrderEntity> = _order.asStateFlow() // Expose as StateFlow

    //var productName = MutableStateFlow<ProductsEntity?>(null)

    private val _productNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val productNames: StateFlow<Map<String, String>> = _productNames.asStateFlow()

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

//    fun getProductName(sku: String) {
//        screenModelScope.launch(Dispatchers.IO) {
//            productName.value = productsRepository.getProductName(sku)
//        }
//    }

    fun fetchProductName(sku: String) {
        screenModelScope.launch(Dispatchers.IO) {
            if (!_productNames.value.containsKey(sku)) {
                val productName = productsRepository.getProductName(sku)?.name ?: "Name Not Found"
                _productNames.value = _productNames.value + (sku to productName)
            }
        }
    }

    fun updateOrderNotes(orderId: Long, newNotes: String) {
        screenModelScope.launch(Dispatchers.IO) {
            customerOrderRepository.updateNotes(orderId, newNotes)
        }
    }
}
