package com.animeboynz.kmd.database.repository

import com.animeboynz.kmd.domain.OrderItemRepository
import kotlinx.coroutines.flow.Flow
import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository

class OrderItemRepositoryImpl(
    private val database: ALMDatabase,
) : OrderItemRepository {
    //    override suspend fun upsert(employeeEntity: EmployeeEntity) {
//        database.employeeDao().upsert(employeeEntity)
//    }
    override fun insertOrderItem(orderItem: OrderItemEntity) {
        database.orderItemDao().insertOrderItem(orderItem)
    }

    override fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>> {
        return database.orderItemDao().getOrderItemsForOrder(orderId)
    }

}
