package com.animeboynz.kmd.database.repository

import com.animeboynz.kmd.database.ALMDatabase
import com.animeboynz.kmd.database.entities.ColorsEntity
import com.animeboynz.kmd.domain.ColorsRepository
import kotlinx.coroutines.flow.Flow

class ColorsRepositoryImpl(
    private val database: ALMDatabase,
) : ColorsRepository {
    override fun getAllColors(): Flow<List<ColorsEntity>> {
        return database.colorsDao().getAllColors()
    }

    override fun getColorName(code: String): ColorsEntity {
        return database.colorsDao().getColorName(code)
    }

    override fun insertAll(products: List<ColorsEntity>) {
        database.colorsDao().insertAll(products)
    }

    override fun deleteAllColors() {
        database.colorsDao().deleteAllColors()
    }

}