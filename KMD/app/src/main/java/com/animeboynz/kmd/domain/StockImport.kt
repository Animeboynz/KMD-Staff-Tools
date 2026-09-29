package com.animeboynz.kmd.domain

data class StockImportRow(
    val boxBarcode: String,
    val locationName: String,
    val productBarcode: String,
    val quantity: Int,
)

data class StockImportResult(
    val applied: Int,
    val skipped: Int,
)
