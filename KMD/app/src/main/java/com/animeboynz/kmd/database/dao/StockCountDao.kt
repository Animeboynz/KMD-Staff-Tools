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

    @Query("INSERT INTO StockCountEntity (location, boxId, productBarcode, quantity) VALUES (:location, :boxId, :productBarcode, :quantity)")
    fun insertStockCount(location: String, boxId: String, productBarcode: String, quantity: Int)

    @Query("UPDATE StockCountEntity SET quantity = quantity + :increment WHERE boxId = :boxId AND productBarcode = :productBarcode")
    fun incrementStockCount(boxId: String, productBarcode: String, increment: Int)

    @Query("UPDATE StockCountEntity SET quantity = quantity - :decrement WHERE boxId = :boxId AND productBarcode = :productBarcode")
    fun decrementStockCount(boxId: String, productBarcode: String, decrement: Int)

    @Query("DELETE FROM StockCountEntity WHERE location = 'Offsite' AND productBarcode = :barcode")
    fun clearOffsiteInventoryByBarcode(barcode: String)

    @Query("DELETE FROM StockCountEntity WHERE boxId = :boxId")
    fun clearInventoryByBoxId(boxId: String)
}