package com.animeboynz.kmd.domain

import kotlinx.coroutines.flow.Flow
import com.animeboynz.kmd.database.entities.CustomerOrderEntity

interface CustomerOrderRepository {

    fun insertOrder(order: CustomerOrderEntity)

    fun deleteOrder(orderId: Long)

    fun deleteAllOrders()

    fun getAllOrders(): Flow<List<CustomerOrderEntity>>

    fun getOrderById(orderId: Long): CustomerOrderEntity

    fun updateNotes(orderId: Long, newNotes: String)
}