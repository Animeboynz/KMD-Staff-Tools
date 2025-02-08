package com.animeboynz.kmd.domain

data class StockAvailability(
    val storeName: String,
    val storeCode: String,
    val storeAddress: String,
    val sku: String,
    val quantity: Int
)