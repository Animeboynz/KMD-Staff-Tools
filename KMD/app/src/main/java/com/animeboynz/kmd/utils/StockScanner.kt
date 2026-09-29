package com.animeboynz.kmd.utils

import androidx.compose.runtime.State

interface StockScanner {
    val scannedBarcode: State<String>

    fun clearScannedBarcode()
}
