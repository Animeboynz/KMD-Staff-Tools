package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.animeboynz.kmd.database.entities.ProductsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {
    @Upsert
    suspend fun upsert(productsEntity: ProductsEntity)

    @Query("SELECT * FROM ProductsEntity")
    fun getAllProducts(): Flow<List<ProductsEntity>>

    @Query(
        """
        SELECT DISTINCT ProductsEntity.sku AS sku, ProductsEntity.name AS name
        FROM StockCountEntity
        INNER JOIN BarcodesEntity
            ON BarcodesEntity.pieceBarcode = StockCountEntity.productBarcode
            OR BarcodesEntity.gtin = StockCountEntity.productBarcode
        INNER JOIN ProductsEntity ON ProductsEntity.sku = BarcodesEntity.sku
        WHERE StockCountEntity.quantity > 0
        ORDER BY ProductsEntity.sku DESC
        """,
    )
    fun getInStockProducts(): Flow<List<ProductsEntity>>

    @Query("SELECT * FROM ProductsEntity WHERE sku = :sku")
    fun getProductName(sku: String): ProductsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<ProductsEntity>)

    @Query("DELETE FROM ProductsEntity")
    fun deleteAllProducts()
}