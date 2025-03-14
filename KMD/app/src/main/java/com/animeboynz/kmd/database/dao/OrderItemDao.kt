package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    @Insert
    fun insertOrderItem(orderItem: OrderItemEntity)

    @Query("SELECT * FROM OrderItemEntity WHERE orderId = :orderId")
    fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM OrderItemEntity WHERE orderItemId = :itemId")
    fun getOrderItems(itemId: Long): OrderItemEntity

    @Update
    fun updateOrderItem(orderItem: OrderItemEntity)

}