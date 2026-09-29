package com.animeboynz.kmd.utils

import androidx.compose.runtime.State

interface ScannerEngine {
    fun startScanning()
    fun stopScanning()
    fun triggerScan(pressed: Boolean)
    fun getScannerDetails(): String
    fun clearScannedBarcode()
    val scannedBarcode: State<String>
}