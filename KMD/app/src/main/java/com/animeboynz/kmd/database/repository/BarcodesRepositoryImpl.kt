package com.animeboynz.kmd.database.repository

import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.ProductsRepository
import kotlinx.coroutines.flow.Flow

class BarcodesRepositoryImpl(
    private val database: ALMDatabase,
) : BarcodesRepository {
    override fun getAllProducts(): Flow<List<BarcodesEntity>> {
        return database.barcodesDao().getAllProducts()
    }

    override fun getByBarcode(barcode: String): BarcodesEntity {
        return database.barcodesDao().getByBarcode(barcode)
    }

    override fun getBySku(sku: String): Flow<List<BarcodesEntity>> {
        return database.barcodesDao().getBySku(sku)
    }

    override suspend fun insertAll(products: List<BarcodesEntity>) {
        database.barcodesDao().insertAll(products)
    }
}