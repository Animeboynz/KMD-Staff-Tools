package com.animeboynz.kmd.domain

import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.animeboynz.kmd.database.entities.CustomerOrderEntity

interface CustomerOrderRepository {

    fun insertOrder(order: CustomerOrderEntity)

    fun deleteOrder(orderId: Long)

    fun getAllOrders(): Flow<List<CustomerOrderEntity>>

    fun getOrderById(orderId: Long): CustomerOrderEntity
}