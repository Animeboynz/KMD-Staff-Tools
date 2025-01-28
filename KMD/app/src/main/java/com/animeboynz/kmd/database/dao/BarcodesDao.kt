package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BarcodesDao {
    @Query("SELECT * FROM BarcodesEntity")
    fun getAllProducts(): Flow<List<BarcodesEntity>>

    @Query("SELECT * FROM BarcodesEntity WHERE pieceBarcode = :barcode OR gtin = :barcode LIMIT 1")
    fun getByBarcode(barcode: String): BarcodesEntity

    @Query("SELECT * FROM BarcodesEntity WHERE sku = :sku")
    fun getBySku(sku: String): Flow<List<BarcodesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<BarcodesEntity>)
}