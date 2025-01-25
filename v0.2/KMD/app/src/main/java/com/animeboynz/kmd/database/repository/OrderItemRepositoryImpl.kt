package com.animeboynz.kmd.database.repository

import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.domain.OrderItemRepository
import kotlinx.coroutines.flow.Flow

class OrderItemRepositoryImpl(
    private val database: ALMDatabase,
) : OrderItemRepository {
    override fun insertOrderItem(orderItem: OrderItemEntity) {
        database.orderItemDao().insertOrderItem(orderItem)
    }

    override fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>> {
        return database.orderItemDao().getOrderItemsForOrder(orderId)
    }
}
