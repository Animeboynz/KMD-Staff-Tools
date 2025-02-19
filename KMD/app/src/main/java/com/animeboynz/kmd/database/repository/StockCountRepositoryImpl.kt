package com.animeboynz.kmd.database.repository

import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.StockCountEntity
import com.animeboynz.kmd.domain.StockCountRepository
import kotlinx.coroutines.flow.Flow

class StockCountRepositoryImpl(
    private val database: ALMDatabase,
) : StockCountRepository {
    override fun getOffsiteInventory(): Flow<List<StockCountEntity>> {
        return database.stockCountDao().getOffsiteInventory()
    }

    override fun getInventoryByBoxId(boxId: String): Flow<List<StockCountEntity>> {
        return database.stockCountDao().getInventoryByBoxId(boxId)
    }

    override fun insertStockCount(
        location: String,
        boxId: String,
        productBarcode: String,
        quantity: Int
    ) {
        database.stockCountDao().insertStockCount(location, boxId, productBarcode, quantity)
    }

    override fun incrementStockCount(
        boxId: String,
        productBarcode: String,
        increment: Int
    ) {
        database.stockCountDao().incrementStockCount(boxId, productBarcode, increment)
    }

    override fun decrementStockCount(
        boxId: String,
        productBarcode: String,
        decrement: Int
    ) {
        database.stockCountDao().decrementStockCount(boxId, productBarcode, decrement)
    }

    override fun clearOffsiteInventoryByBarcode(barcode: String) {
        database.stockCountDao().clearOffsiteInventoryByBarcode(barcode)
    }

    override fun clearInventoryByBoxId(boxId: String) {
        database.stockCountDao().clearInventoryByBoxId(boxId)
    }

}