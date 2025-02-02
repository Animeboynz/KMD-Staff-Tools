package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.animeboynz.kmd.database.entities.ColorsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorsDao {
    @Query("SELECT * FROM ColorsEntity")
    fun getAllColors(): Flow<List<ColorsEntity>>

    @Query("SELECT * FROM ColorsEntity WHERE colorCode = :code LIMIT 1")
    fun getColorName(code: String): ColorsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<ColorsEntity>)

    @Query("DELETE FROM ColorsEntity")
    fun deleteAllColors()
}