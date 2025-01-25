package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerOrderDao {
    @Insert
    fun insertOrder(order: CustomerOrderEntity)

    @Query("DELETE FROM CustomerOrderEntity WHERE orderId = :orderId")
    fun deleteOrder(orderId: Long)

    @Query("DELETE FROM CustomerOrderEntity")
    fun deleteAllOrders()

    @Query("SELECT * FROM CustomerOrderEntity")
    fun getAllOrders(): Flow<List<CustomerOrderEntity>>

    @Query("SELECT * FROM CustomerOrderEntity WHERE orderId = :orderId LIMIT 1")
    fun getOrderById(orderId: Long): CustomerOrderEntity

    @Query("UPDATE CustomerOrderEntity SET notes = :newNotes WHERE orderId = :orderId")
    fun updateNotes(orderId: Long, newNotes: String)

}