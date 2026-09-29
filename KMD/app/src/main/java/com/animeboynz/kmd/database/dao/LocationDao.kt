package com.animeboynz.kmd.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.animeboynz.kmd.database.entities.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert
    suspend fun insert(location: LocationEntity): Long

    @Query("SELECT * FROM LocationEntity WHERE id = :id")
    suspend fun getById(id: Long): LocationEntity?

    @Query("SELECT * FROM LocationEntity ORDER BY name")
    fun getAll(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM LocationEntity WHERE parentId IS NULL ORDER BY name")
    fun getRoots(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM LocationEntity WHERE parentId = :parentId ORDER BY name")
    fun getChildren(parentId: Long): Flow<List<LocationEntity>>
}
