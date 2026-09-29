package com.animeboynz.kmd.database.repository

import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.BoxEntity
import com.animeboynz.kmd.database.entities.LocationEntity
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

    override suspend fun insertLocation(name: String, parentId: Long?): Long {
        return database.locationDao().insert(LocationEntity(parentId = parentId, name = name.trim()))
    }

    override fun getLocations(): Flow<List<LocationEntity>> {
        return database.locationDao().getAll()
    }

    override fun getRootLocations(): Flow<List<LocationEntity>> {
        return database.locationDao().getRoots()
    }

    override fun getChildLocations(parentId: Long): Flow<List<LocationEntity>> {
        return database.locationDao().getChildren(parentId)
    }

    override suspend fun getLocation(id: Long): LocationEntity? {
        return database.locationDao().getById(id)
    }

    override suspend fun insertBox(barcode: String, locationId: Long): Long {
        return database.boxDao().insert(BoxEntity(barcode = barcode.trim(), locationId = locationId))
    }

    override suspend fun getBoxByBarcode(barcode: String): BoxEntity? {
        return database.boxDao().getByBarcode(barcode.trim())
    }

    override fun getBoxes(): Flow<List<BoxEntity>> {
        return database.boxDao().getAll()
    }

    override fun getBoxesByLocation(locationId: Long): Flow<List<BoxEntity>> {
        return database.boxDao().getByLocation(locationId)
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