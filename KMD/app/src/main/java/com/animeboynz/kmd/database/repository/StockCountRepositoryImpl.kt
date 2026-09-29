package com.animeboynz.kmd.database.repository

import android.database.sqlite.SQLiteConstraintException
import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.BoxEntity
import com.animeboynz.kmd.database.entities.LocationEntity
import com.animeboynz.kmd.database.entities.StockCountEntity
import com.animeboynz.kmd.domain.BoxContentMatch
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

    override suspend fun locationPath(locationId: Long): String {
        val parts = ArrayDeque<String>()
        val seen = mutableSetOf<Long>()
        var current = getLocation(locationId)
        while (current != null && seen.add(current.id)) {
            parts.addFirst(current.name)
            val parentId = current.parentId ?: break
            current = getLocation(parentId)
        }
        return parts.joinToString(" / ")
    }

    override suspend fun searchBoxes(query: String): List<BoxContentMatch> {
        val trimmed = query.trim().replace("%", "").replace("_", "")
        if (trimmed.isEmpty()) return emptyList()
        val products = database.barcodesDao().search("%$trimmed%")
        val codes = products.flatMap { listOfNotNull(it.pieceBarcode, it.gtin) }.distinct()
        if (codes.isEmpty()) return emptyList()
        val counts = database.stockCountDao().findByProductBarcodes(codes)
        if (counts.isEmpty()) return emptyList()
        val boxes = database.boxDao().getByBarcodes(counts.map { it.boxId }.distinct()).associateBy { it.barcode }
        val paths = mutableMapOf<Long, String>()
        return counts.mapNotNull { count ->
            val box = boxes[count.boxId] ?: return@mapNotNull null
            val product = products.find {
                it.pieceBarcode == count.productBarcode || it.gtin == count.productBarcode
            } ?: return@mapNotNull null
            val path = paths.getOrPut(box.locationId) { locationPath(box.locationId) }
            BoxContentMatch(
                boxBarcode = box.barcode,
                locationPath = path,
                productName = product.name,
                sku = product.sku,
                color = product.color,
                size = product.size,
                quantity = count.quantity,
            )
        }.distinctBy { "${it.boxBarcode}|${it.sku}|${it.color}|${it.size}" }
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

    override fun addOneToBox(location: String, boxId: String, productBarcode: String) {
        val updated = database.stockCountDao().incrementStockCount(boxId, productBarcode, 1)
        if (updated > 0) return
        try {
            database.stockCountDao().insertStockCount(location, boxId, productBarcode, 1)
        } catch (_: SQLiteConstraintException) {
            database.stockCountDao().incrementStockCount(boxId, productBarcode, 1)
        }
    }

    override fun decrementStockCount(
        boxId: String,
        productBarcode: String,
        decrement: Int
    ) {
        database.stockCountDao().decrementStockCount(boxId, productBarcode, decrement)
    }

    override fun removeOneFromBox(boxId: String, productBarcode: String) {
        database.stockCountDao().decrementStockCount(boxId, productBarcode, 1)
        database.stockCountDao().deleteIfEmpty(boxId, productBarcode)
    }

    override fun deleteItem(boxId: String, productBarcode: String) {
        database.stockCountDao().deleteItem(boxId, productBarcode)
    }

    override fun clearOffsiteInventoryByBarcode(barcode: String) {
        database.stockCountDao().clearOffsiteInventoryByBarcode(barcode)
    }

    override fun clearInventoryByBoxId(boxId: String) {
        database.stockCountDao().clearInventoryByBoxId(boxId)
    }

}