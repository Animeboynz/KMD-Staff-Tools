package com.animeboynz.kmd.database.repository

import kotlinx.coroutines.flow.Flow
import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository

class CustomerOrderRepositoryImpl(
    private val database: ALMDatabase,
) : CustomerOrderRepository {
    override fun insertOrder(order: CustomerOrderEntity) {
        database.customerOrderDao().insertOrder(order)
    }

    override fun deleteOrder(orderId: Long) {
        database.customerOrderDao().deleteOrder(orderId)
    }

    override fun deleteAllOrders() {
        database.customerOrderDao().deleteAllOrders()
    }

    override fun getAllOrders(): Flow<List<CustomerOrderEntity>> {
        return database.customerOrderDao().getAllOrders()
    }

    override fun getOrderById(orderId: Long): CustomerOrderEntity {
        return database.customerOrderDao().getOrderById(orderId)
    }

    override fun updateNotes(orderId: Long, newNotes: String) {
        database.customerOrderDao().updateNotes(orderId, newNotes)
    }
}
