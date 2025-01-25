package com.animeboynz.kmd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.animeboynz.kmd.database.dao.CustomerOrderDao
import com.animeboynz.kmd.database.dao.EmployeeDao
import com.animeboynz.kmd.database.dao.OrderItemDao
import com.animeboynz.kmd.database.dao.ProductsDao
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.database.entities.ProductsEntity

@Database(entities = [EmployeeEntity::class, ProductsEntity::class, CustomerOrderEntity::class, OrderItemEntity::class], version = 1)
abstract class ALMDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun productsDao(): ProductsDao

    abstract fun customerOrderDao(): CustomerOrderDao
    abstract fun orderItemDao(): OrderItemDao
}
