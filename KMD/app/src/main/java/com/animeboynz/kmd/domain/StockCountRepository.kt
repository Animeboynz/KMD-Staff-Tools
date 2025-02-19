package com.animeboynz.kmd.domain

import com.animeboynz.kmd.database.entities.StockCountEntity
import kotlinx.coroutines.flow.Flow

interface StockCountRepository {
    fun getOffsiteInventory(): Flow<List<StockCountEntity>>

    fun getInventoryByBoxId(boxId: String): Flow<List<StockCountEntity>>

    fun insertStockCount(location: String, boxId: String, productBarcode: String, quantity: Int)

    fun incrementStockCount(boxId: String, productBarcode: String, increment: Int)

    fun decrementStockCount(boxId: String, productBarcode: String, decrement: Int)

    fun clearOffsiteInventoryByBarcode(barcode: String)

    fun clearInventoryByBoxId(boxId: String)
}