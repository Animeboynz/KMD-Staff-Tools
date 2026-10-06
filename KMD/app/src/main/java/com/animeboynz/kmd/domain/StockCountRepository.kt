package com.animeboynz.kmd.domain

import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.BoxEntity
import com.animeboynz.kmd.database.entities.LocationEntity
import com.animeboynz.kmd.database.entities.StockCountEntity
import kotlinx.coroutines.flow.Flow

interface StockCountRepository {
    fun getOffsiteInventory(): Flow<List<StockCountEntity>>

    fun getInventoryByBoxId(boxId: String): Flow<List<StockCountEntity>>

    suspend fun insertLocation(name: String, parentId: Long?): Long

    suspend fun deleteLocation(id: Long)

    fun getLocations(): Flow<List<LocationEntity>>

    fun getRootLocations(): Flow<List<LocationEntity>>

    fun getChildLocations(parentId: Long): Flow<List<LocationEntity>>

    suspend fun getLocation(id: Long): LocationEntity?

    suspend fun locationPath(locationId: Long): String

    suspend fun searchBoxes(query: String): List<BoxContentMatch>

    suspend fun findBoxesForProduct(sku: String, color: String, size: String): List<BoxContentMatch>

    suspend fun seedDemoInventory(products: List<BarcodesEntity>)

    suspend fun importRows(rows: List<StockImportRow>): StockImportResult

    suspend fun insertBox(barcode: String, locationId: Long): Long

    suspend fun getBoxByBarcode(barcode: String): BoxEntity?

    fun getBoxes(): Flow<List<BoxEntity>>

    fun getBoxesByLocation(locationId: Long): Flow<List<BoxEntity>>

    fun getBoxesDueForRecount(cutoffMillis: Long): Flow<List<BoxEntity>>

    suspend fun confirmBoxCount(barcode: String)

    fun insertStockCount(location: String, boxId: String, productBarcode: String, quantity: Int)

    fun incrementStockCount(boxId: String, productBarcode: String, increment: Int)

    fun addOneToBox(location: String, boxId: String, productBarcode: String)

    fun decrementStockCount(boxId: String, productBarcode: String, decrement: Int)

    fun removeOneFromBox(boxId: String, productBarcode: String)

    fun deleteItem(boxId: String, productBarcode: String)

    fun clearOffsiteInventoryByBarcode(barcode: String)

    fun clearInventoryByBoxId(boxId: String)
}