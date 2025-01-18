package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import com.animeboynz.kmd.database.entities.ProductsEntity

@Dao
interface ProductsDao {
    @Upsert
    suspend fun upsert(productsEntity: ProductsEntity)

    @Query("SELECT * FROM ProductsEntity")
    fun getAllProducts(): Flow<List<ProductsEntity>>

    @Query("SELECT * FROM ProductsEntity WHERE sku = :sku")
    fun getProductName(sku: String): ProductsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<ProductsEntity>)
}