package com.animeboynz.kmd.utils

import android.content.Context
import com.animeboynz.kmd.utils.HoneywellScannerEngine
import com.animeboynz.kmd.utils.PanasonicScannerEngine
import com.animeboynz.kmd.utils.ScannerEngine

class BarcodeScanner(context: Context) {

    val engine: ScannerEngine = when {
        isHoneywellDevice() -> HoneywellScannerEngine(context)
        isPanasonicDevice() -> PanasonicScannerEngine(context)
        else -> throw UnsupportedOperationException("Unsupported device")
    }

    val scannedBarcode get() = engine.scannedBarcode

    fun startScanning() = engine.startScanning()
    fun stopScanning() = engine.stopScanning()
    fun triggerScan(pressed: Boolean) = engine.triggerScan(pressed)
    fun getScannerDetails() = engine.getScannerDetails()

    private fun isHoneywellDevice(): Boolean {
        return android.os.Build.MANUFACTURER.contains("Honeywell", true)
    }

    private fun isPanasonicDevice(): Boolean {
        return android.os.Build.MANUFACTURER.contains("Panasonic", true)
    }
}