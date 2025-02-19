package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StockCountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val location: String,
    val boxId: String,

    val productBarcode: String,
    val quantity: Int
)