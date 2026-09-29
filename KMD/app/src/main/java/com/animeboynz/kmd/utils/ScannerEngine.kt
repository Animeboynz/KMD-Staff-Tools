package com.animeboynz.kmd.utils
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

interface ScannerEngine {
    fun startScanning()
    fun stopScanning()
    fun triggerScan(pressed: Boolean)
    fun getScannerDetails(): String
    val scannedBarcode: State<String>
}