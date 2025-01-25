package com.animeboynz.kmd.domain

import com.animeboynz.kmd.database.entities.OrderItemEntity
import kotlinx.coroutines.flow.Flow

interface OrderItemRepository {
    fun insertOrderItem(orderItem: OrderItemEntity)

    fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>>
}