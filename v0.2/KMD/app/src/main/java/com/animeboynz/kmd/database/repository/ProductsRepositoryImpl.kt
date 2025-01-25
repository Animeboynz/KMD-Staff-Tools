package com.animeboynz.kmd.database.repository

import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.domain.ProductsRepository
import kotlinx.coroutines.flow.Flow

class ProductsRepositoryImpl(
    private val database: ALMDatabase,
) : ProductsRepository {
    override suspend fun upsert(productsEntity: ProductsEntity) {
        database.productsDao().upsert(productsEntity)
    }

    override fun getAllProducts(): Flow<List<ProductsEntity>> {
        return database.productsDao().getAllProducts()
    }

    override fun getProductName(sku: String): ProductsEntity {
        return database.productsDao().getProductName(sku)
    }

    override suspend fun insertAll(products: List<ProductsEntity>) {
        database.productsDao().insertAll(products)
    }
}
