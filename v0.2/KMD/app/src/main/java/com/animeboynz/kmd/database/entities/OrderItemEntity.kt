package com.animeboynz.kmd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val orderItemId: Long = 0, // Auto-generated ID for the order item
    val orderId: Long, // Foreign key referencing the order

    val productName: String?, // Name of the product
    val productColor: String?,

    val sku: String, // SKU of the product
    val color: String,
    val size: String,

    val price: String,
    val store: String,
    val status: String,

    val quantity: Int // Quantity of the product ordered
)