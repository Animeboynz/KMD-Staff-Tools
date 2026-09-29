package com.animeboynz.kmd.domain

import com.animeboynz.kmd.database.entities.BoxEntity
import com.animeboynz.kmd.database.entities.LocationEntity
import com.animeboynz.kmd.database.entities.StockCountEntity
import kotlinx.coroutines.flow.Flow

interface StockCountRepository {
    fun getOffsiteInventory(): Flow<List<StockCountEntity>>

    fun getInventoryByBoxId(boxId: String): Flow<List<StockCountEntity>>

    suspend fun insertLocation(name: String, parentId: Long?): Long

    fun getLocations(): Flow<List<LocationEntity>>

    fun getRootLocations(): Flow<List<LocationEntity>>

    fun getChildLocations(parentId: Long): Flow<List<LocationEntity>>

    suspend fun getLocation(id: Long): LocationEntity?

    suspend fun insertBox(barcode: String, locationId: Long): Long

    suspend fun getBoxByBarcode(barcode: String): BoxEntity?

    fun getBoxes(): Flow<List<BoxEntity>>

    fun getBoxesByLocation(locationId: Long): Flow<List<BoxEntity>>

    fun insertStockCount(location: String, boxId: String, productBarcode: String, quantity: Int)

    fun incrementStockCount(boxId: String, productBarcode: String, increment: Int)

    fun decrementStockCount(boxId: String, productBarcode: String, decrement: Int)

    fun clearOffsiteInventoryByBarcode(barcode: String)

    fun clearInventoryByBoxId(boxId: String)
}