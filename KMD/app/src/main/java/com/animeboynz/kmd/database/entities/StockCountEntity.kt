package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["boxId", "productBarcode"], unique = true),
    ],
)
data class StockCountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val location: String,
    val boxId: String,

    val productBarcode: String,
    val quantity: Int
)