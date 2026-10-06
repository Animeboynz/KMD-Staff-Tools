package com.animeboynz.kmd.database.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.provider.Settings
import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.PendingTransactionEntity
import com.animeboynz.kmd.sync.StockSyncWorker
import com.animeboynz.kmd.database.entities.BoxEntity
import com.animeboynz.kmd.database.entities.LocationEntity
import com.animeboynz.kmd.database.entities.StockCountEntity
import com.animeboynz.kmd.domain.BoxContentMatch
import com.animeboynz.kmd.domain.StockCountRepository
import com.animeboynz.kmd.domain.StockImportResult
import com.animeboynz.kmd.domain.StockImportRow
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

class StockCountRepositoryImpl(
    private val database: ALMDatabase,
    private val context: Context,
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

    override suspend fun deleteLocation(id: Long) {
        val all = database.locationDao().getAllOnce()
        val toDelete = mutableListOf<Long>()
        fun collect(currentId: Long) {
            all.filter { it.parentId == currentId }.forEach { collect(it.id) }
            toDelete += currentId
        }
        collect(id)
        val boxes = database.boxDao().getByLocationIds(toDelete)
        database.withTransaction {
            boxes.forEach { database.stockCountDao().clearInventoryByBoxId(it.barcode) }
            toDelete.forEach { database.locationDao().deleteById(it) }
        }
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
        val counts = database.stockCountDao().findByProductSearch("%$trimmed%")
        if (counts.isEmpty()) return emptyList()
        val codes = counts.map { it.productBarcode }.distinct()
        val products = codes.chunked(SQL_VARIABLE_CHUNK).flatMap { chunk ->
            database.barcodesDao().findByBarcodes(chunk)
        }
        val boxes = counts.map { it.boxId }.distinct().chunked(SQL_VARIABLE_CHUNK).flatMap { chunk ->
            database.boxDao().getByBarcodes(chunk)
        }.associateBy { it.barcode }
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

    override suspend fun findBoxesForProduct(sku: String, color: String, size: String): List<BoxContentMatch> {
        val variants = database.barcodesDao().findBySkuColorSize(sku, color, size)
        val codes = variants.flatMap { listOfNotNull(it.pieceBarcode, it.gtin) }
            .filter { it.isNotBlank() }
            .distinct()
        if (codes.isEmpty()) return emptyList()
        val counts = database.stockCountDao().findByProductBarcodes(codes).filter { it.quantity > 0 }
        if (counts.isEmpty()) return emptyList()
        val boxes = counts.map { it.boxId }.distinct().chunked(SQL_VARIABLE_CHUNK).flatMap { chunk ->
            database.boxDao().getByBarcodes(chunk)
        }.associateBy { it.barcode }
        val paths = mutableMapOf<Long, String>()
        return counts.mapNotNull { count ->
            val box = boxes[count.boxId] ?: return@mapNotNull null
            val product = variants.find {
                it.pieceBarcode == count.productBarcode || it.gtin == count.productBarcode
            } ?: return@mapNotNull null
            BoxContentMatch(
                boxBarcode = box.barcode,
                locationPath = paths.getOrPut(box.locationId) { locationPath(box.locationId) },
                productName = product.name,
                sku = product.sku,
                color = product.color,
                size = product.size,
                quantity = count.quantity,
            )
        }
    }

    override suspend fun seedDemoInventory(products: List<BarcodesEntity>) {
        val demoSkus = products.asSequence()
            .filter { barcodeOf(it) != null }
            .map { it.sku }
            .distinct()
            .take(DEMO_PRODUCT_COUNT)
            .toSet()
        val variants = products.asSequence()
            .filter { it.sku in demoSkus && barcodeOf(it) != null }
            .distinctBy { barcodeOf(it) }
            .toList()
        if (variants.isEmpty()) return

        val newZealand = insertLocation("New Zealand", null)
        val northIsland = insertLocation("North Island", newZealand)
        val southIsland = insertLocation("South Island", newZealand)
        val newmarket = insertLocation("Newmarket", northIsland)
        val christchurch = insertLocation("Christchurch CBD", southIsland)
        val countedAt = System.currentTimeMillis()
        val boxesPerStore = (variants.size / 6).coerceIn(4, 12)

        data class DemoBox(val barcode: String, val locationId: Long, val locationName: String)
        fun storeBoxes(prefix: String, locationId: Long, locationName: String): List<DemoBox> {
            return List(boxesPerStore) { index ->
                DemoBox(
                    barcode = "$prefix-${(index + 1).toString().padStart(2, '0')}",
                    locationId = locationId,
                    locationName = locationName,
                )
            }
        }
        val newmarketBoxes = storeBoxes("BOX-NM", newmarket, "Newmarket")
        val christchurchBoxes = storeBoxes("BOX-CHC", christchurch, "Christchurch CBD")

        database.withTransaction {
            for (box in newmarketBoxes + christchurchBoxes) {
                database.boxDao().insert(
                    BoxEntity(barcode = box.barcode, locationId = box.locationId, lastCountedAt = countedAt),
                )
            }
            variants.forEachIndexed { index, variant ->
                val code = barcodeOf(variant) ?: return@forEachIndexed
                val newmarketBox = newmarketBoxes[index % boxesPerStore]
                val christchurchBox = christchurchBoxes[(index + boxesPerStore / 2) % boxesPerStore]
                database.stockCountDao().insertStockCount(
                    newmarketBox.locationName,
                    newmarketBox.barcode,
                    code,
                    (index % 6) + 1,
                )
                database.stockCountDao().insertStockCount(
                    christchurchBox.locationName,
                    christchurchBox.barcode,
                    code,
                    (index % 4) + 2,
                )
            }
        }
    }

    private fun barcodeOf(product: BarcodesEntity): String? {
        return product.pieceBarcode?.takeIf { it.isNotBlank() }
            ?: product.gtin?.takeIf { it.isNotBlank() }
    }

    override suspend fun importRows(rows: List<StockImportRow>): StockImportResult {
        var applied = 0
        var skipped = 0
        for (row in rows) {
            if (row.boxBarcode.isBlank() || row.locationName.isBlank() || row.productBarcode.isBlank() || row.quantity <= 0) {
                skipped++
                continue
            }
            if (database.barcodesDao().findByBarcode(row.productBarcode) == null) {
                skipped++
                continue
            }
            val location = database.locationDao().getByName(row.locationName)
                ?: run {
                    val id = insertLocation(row.locationName, null)
                    LocationEntity(id = id, parentId = null, name = row.locationName)
                }
            val existingBox = database.boxDao().getByBarcode(row.boxBarcode)
            if (existingBox == null) {
                try {
                    insertBox(row.boxBarcode, location.id)
                } catch (_: SQLiteConstraintException) {
                    skipped++
                    continue
                }
            }
            addQuantity(location.name, row.boxBarcode, row.productBarcode, row.quantity)
            applied++
        }
        return StockImportResult(applied, skipped)
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

    override fun getBoxesDueForRecount(cutoffMillis: Long): Flow<List<BoxEntity>> {
        return database.boxDao().getDueForRecount(cutoffMillis)
    }

    override suspend fun confirmBoxCount(barcode: String) {
        database.boxDao().markCounted(barcode, System.currentTimeMillis())
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
        addQuantity(location, boxId, productBarcode, 1)
    }

    private fun addQuantity(location: String, boxId: String, productBarcode: String, quantity: Int) {
        database.runInTransaction {
            val updated = database.stockCountDao().incrementStockCount(boxId, productBarcode, quantity)
            if (updated == 0) {
                try {
                    database.stockCountDao().insertStockCount(location, boxId, productBarcode, quantity)
                } catch (_: SQLiteConstraintException) {
                    database.stockCountDao().incrementStockCount(boxId, productBarcode, quantity)
                }
            }
            enqueue(boxId, productBarcode, quantity)
        }
        StockSyncWorker.scheduleSoon(context)
    }

    override fun decrementStockCount(
        boxId: String,
        productBarcode: String,
        decrement: Int
    ) {
        database.stockCountDao().decrementStockCount(boxId, productBarcode, decrement)
    }

    override fun removeOneFromBox(boxId: String, productBarcode: String) {
        var changed = false
        database.runInTransaction {
            val existing = database.stockCountDao().findItem(boxId, productBarcode) ?: return@runInTransaction
            if (existing.quantity <= 0) return@runInTransaction
            database.stockCountDao().decrementStockCount(boxId, productBarcode, 1)
            database.stockCountDao().deleteIfEmpty(boxId, productBarcode)
            enqueue(boxId, productBarcode, -1)
            changed = true
        }
        if (changed) StockSyncWorker.scheduleSoon(context)
    }

    override fun deleteItem(boxId: String, productBarcode: String) {
        var changed = false
        database.runInTransaction {
            val existing = database.stockCountDao().findItem(boxId, productBarcode) ?: return@runInTransaction
            database.stockCountDao().deleteItem(boxId, productBarcode)
            enqueue(boxId, productBarcode, -existing.quantity)
            changed = true
        }
        if (changed) StockSyncWorker.scheduleSoon(context)
    }

    private fun enqueue(boxBarcode: String, productBarcode: String, delta: Int) {
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
        database.pendingTransactionDao().insert(
            PendingTransactionEntity(
                boxBarcode = boxBarcode,
                productBarcode = productBarcode,
                delta = delta,
                createdAt = System.currentTimeMillis(),
                deviceId = deviceId,
            ),
        )
    }

    override fun clearOffsiteInventoryByBarcode(barcode: String) {
        database.stockCountDao().clearOffsiteInventoryByBarcode(barcode)
    }

    override fun clearInventoryByBoxId(boxId: String) {
        database.stockCountDao().clearInventoryByBoxId(boxId)
    }

    private companion object {
        // findByBarcodes binds the list twice, and SQLite allows 999 variables.
        const val SQL_VARIABLE_CHUNK = 400
        const val DEMO_PRODUCT_COUNT = 12
    }

}