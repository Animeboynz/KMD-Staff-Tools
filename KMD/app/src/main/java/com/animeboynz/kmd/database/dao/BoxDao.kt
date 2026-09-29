package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.animeboynz.kmd.database.entities.BoxEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxDao {
    @Insert
    suspend fun insert(box: BoxEntity): Long

    @Query("SELECT * FROM BoxEntity WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): BoxEntity?

    @Query("SELECT * FROM BoxEntity WHERE barcode IN (:barcodes)")
    suspend fun getByBarcodes(barcodes: List<String>): List<BoxEntity>

    @Query("SELECT * FROM BoxEntity WHERE id = :id")
    suspend fun getById(id: Long): BoxEntity?

    @Query("SELECT * FROM BoxEntity ORDER BY barcode")
    fun getAll(): Flow<List<BoxEntity>>

    @Query("SELECT * FROM BoxEntity WHERE locationId = :locationId ORDER BY barcode")
    fun getByLocation(locationId: Long): Flow<List<BoxEntity>>
}
