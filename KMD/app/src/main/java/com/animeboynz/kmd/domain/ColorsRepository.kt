package com.animeboynz.kmd.domain

import com.animeboynz.kmd.database.entities.ColorsEntity
import kotlinx.coroutines.flow.Flow

interface ColorsRepository {
    fun getAllColors(): Flow<List<ColorsEntity>>

    fun getColorName(code: String): ColorsEntity

    fun insertAll(products: List<ColorsEntity>)

    fun deleteAllColors()
}