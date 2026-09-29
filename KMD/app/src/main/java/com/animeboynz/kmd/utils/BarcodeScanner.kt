package com.animeboynz.kmd.utils

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class BarcodeScanner(context: Context) : StockScanner {
    private val engine: ScannerEngine? = when {
        isHoneywellDevice() -> HoneywellScannerEngine(context)
        isPanasonicDevice() -> PanasonicScannerEngine(context)
        else -> null
    }

    private val _hardwareUnavailable = mutableStateOf(engine == null)
    val hardwareUnavailable: State<Boolean> = _hardwareUnavailable

    private val emptyBarcode = mutableStateOf("")
    override val scannedBarcode: State<String> = engine?.scannedBarcode ?: emptyBarcode

    fun startScanning() {
        engine?.startScanning()
    }

    fun stopScanning() {
        engine?.stopScanning()
    }

    fun triggerScan(pressed: Boolean) {
        engine?.triggerScan(pressed)
    }

    fun getScannerDetails(): String {
        return engine?.getScannerDetails() ?: "No hardware scanner"
    }

    override fun clearScannedBarcode() {
        engine?.clearScannedBarcode()
    }

    private fun isHoneywellDevice(): Boolean {
        return android.os.Build.MANUFACTURER.contains("Honeywell", true)
    }

    private fun isPanasonicDevice(): Boolean {
        return android.os.Build.MANUFACTURER.contains("Panasonic", true)
    }
}
