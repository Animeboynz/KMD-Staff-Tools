package com.animeboynz.kmd.domain

import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import kotlinx.coroutines.flow.Flow

interface CustomerOrderRepository {

    fun insertOrder(order: CustomerOrderEntity)

    fun deleteOrder(orderId: Long)

    fun deleteAllOrders()

    fun getAllOrders(): Flow<List<CustomerOrderEntity>>

    fun getOrderById(orderId: Long): CustomerOrderEntity

    fun updateNotes(orderId: Long, newNotes: String)
}