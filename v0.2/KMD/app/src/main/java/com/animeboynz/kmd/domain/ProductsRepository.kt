package com.animeboynz.kmd.domain

import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.database.entities.ProductsEntity

interface ProductsRepository {
    @Upsert
    suspend fun upsert(productsEntity: ProductsEntity)

    fun getAllProducts(): Flow<List<ProductsEntity>>

    suspend fun insertAll(products: List<ProductsEntity>)
}

