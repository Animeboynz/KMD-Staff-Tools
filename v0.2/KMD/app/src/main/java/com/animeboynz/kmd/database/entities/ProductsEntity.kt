package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductsEntity(
    @PrimaryKey
    val sku: String,
    val name: String,
)
