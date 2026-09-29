package com.animeboynz.kmd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.animeboynz.kmd.database.dao.BarcodesDao
import com.animeboynz.kmd.database.dao.BoxDao
import com.animeboynz.kmd.database.dao.ColorsDao
import com.animeboynz.kmd.database.dao.CustomerOrderDao
import com.animeboynz.kmd.database.dao.EmployeeDao
import com.animeboynz.kmd.database.dao.LocationDao
import com.animeboynz.kmd.database.dao.OrderItemDao
import com.animeboynz.kmd.database.dao.PendingTransactionDao
import com.animeboynz.kmd.database.dao.ProductsDao
import com.animeboynz.kmd.database.dao.StockCountDao
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.BoxEntity
import com.animeboynz.kmd.database.entities.ColorsEntity
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.database.entities.LocationEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.database.entities.PendingTransactionEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.database.entities.StockCountEntity

@Database(entities = [
    EmployeeEntity::class,
    ProductsEntity::class,
    CustomerOrderEntity::class,
    OrderItemEntity::class,
    BarcodesEntity::class,
    ColorsEntity::class,
    StockCountEntity::class,
    LocationEntity::class,
    BoxEntity::class,
    PendingTransactionEntity::class],
    version = 4
)
abstract class ALMDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun productsDao(): ProductsDao

    abstract fun customerOrderDao(): CustomerOrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun barcodesDao(): BarcodesDao
    abstract fun colorsDao(): ColorsDao
    abstract fun stockCountDao(): StockCountDao
    abstract fun locationDao(): LocationDao
    abstract fun boxDao(): BoxDao
    abstract fun pendingTransactionDao(): PendingTransactionDao
}
