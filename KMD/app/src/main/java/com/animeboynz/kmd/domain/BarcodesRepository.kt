package com.animeboynz.kmd.domain

import com.animeboynz.kmd.database.entities.BarcodesEntity
import kotlinx.coroutines.flow.Flow

interface BarcodesRepository {
    fun getAllProducts(): Flow<List<BarcodesEntity>>

    fun getByBarcode(barcode: String): BarcodesEntity

    fun getBySku(sku: String): Flow<List<BarcodesEntity>>

    suspend fun insertAll(products: List<BarcodesEntity>)

    fun deleteAllLines()

}