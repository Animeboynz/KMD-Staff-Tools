package com.animeboynz.kmd.domain

data class BoxContentMatch(
    val boxBarcode: String,
    val locationPath: String,
    val productName: String,
    val sku: String,
    val color: String,
    val size: String,
    val quantity: Int,
)
