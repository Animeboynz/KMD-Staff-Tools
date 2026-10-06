package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.animeboynz.kmd.database.entities.StockCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockCountDao {
    @Query("SELECT * FROM StockCountEntity WHERE location = 'Offsite'")
    fun getOffsiteInventory(): Flow<List<StockCountEntity>>

    @Query("SELECT * FROM StockCountEntity WHERE boxId = :boxId")
    fun getInventoryByBoxId(boxId: String): Flow<List<StockCountEntity>>

    @Query("SELECT * FROM StockCountEntity WHERE productBarcode IN (:barcodes)")
    fun findByProductBarcodes(barcodes: List<String>): List<StockCountEntity>

    @Query(
        """
        SELECT * FROM StockCountEntity
        WHERE productBarcode IN (
            SELECT pieceBarcode FROM BarcodesEntity
            WHERE pieceBarcode IS NOT NULL
                AND (
                    sku LIKE :query
                    OR name LIKE :query
                    OR pieceBarcode LIKE :query
                    OR IFNULL(gtin, '') LIKE :query
                )
            UNION
            SELECT gtin FROM BarcodesEntity
            WHERE gtin IS NOT NULL
                AND (
                    sku LIKE :query
                    OR name LIKE :query
                    OR IFNULL(pieceBarcode, '') LIKE :query
                    OR gtin LIKE :query
                )
        )
        """,
    )
    fun findByProductSearch(query: String): List<StockCountEntity>

    @Query("SELECT * FROM StockCountEntity WHERE boxId = :boxId AND productBarcode = :productBarcode LIMIT 1")
    fun findItem(boxId: String, productBarcode: String): StockCountEntity?

    @Query("INSERT INTO StockCountEntity (location, boxId, productBarcode, quantity) VALUES (:location, :boxId, :productBarcode, :quantity)")
    fun insertStockCount(location: String, boxId: String, productBarcode: String, quantity: Int)

    @Query("UPDATE StockCountEntity SET quantity = quantity + :increment WHERE boxId = :boxId AND productBarcode = :productBarcode")
    fun incrementStockCount(boxId: String, productBarcode: String, increment: Int): Int

    @Query("UPDATE StockCountEntity SET quantity = quantity - :decrement WHERE boxId = :boxId AND productBarcode = :productBarcode")
    fun decrementStockCount(boxId: String, productBarcode: String, decrement: Int)

    @Query("DELETE FROM StockCountEntity WHERE boxId = :boxId AND productBarcode = :productBarcode AND quantity <= 0")
    fun deleteIfEmpty(boxId: String, productBarcode: String)

    @Query("DELETE FROM StockCountEntity WHERE boxId = :boxId AND productBarcode = :productBarcode")
    fun deleteItem(boxId: String, productBarcode: String)

    @Query("DELETE FROM StockCountEntity WHERE location = 'Offsite' AND productBarcode = :barcode")
    fun clearOffsiteInventoryByBarcode(barcode: String)

    @Query("SELECT * FROM StockCountEntity WHERE boxId = :boxId")
    fun getInventoryOnce(boxId: String): List<StockCountEntity>

    @Query("DELETE FROM StockCountEntity WHERE boxId = :boxId")
    fun clearInventoryByBoxId(boxId: String)
}