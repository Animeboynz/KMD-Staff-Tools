package com.animeboynz.kmd.domain

import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.animeboynz.kmd.database.entities.OrderItemEntity

interface OrderItemRepository {
    fun insertOrderItem(orderItem: OrderItemEntity)

    fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>>
}