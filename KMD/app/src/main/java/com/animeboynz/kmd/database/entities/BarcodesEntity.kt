package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BarcodesEntity(

    @PrimaryKey(autoGenerate = true)
    val uniqueId: Long = 0,

    val sku: String,
    val color: String,
    val size: String,
    val name: String,
    val pieceBarcode: String?,
    val gtin: String?,
)

