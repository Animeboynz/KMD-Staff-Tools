package com.animeboynz.kmd.utils

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import com.honeywell.aidc.BarcodeReader
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.AidcManager
//import com.honeywell.aidc.*
import kotlinx.coroutines.*

class HoneywellScannerEngine(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ScannerEngine {

    private var barcodeReader: BarcodeReader? = null
    private var aidcManager: AidcManager? = null

    private var _scannedBarcode = mutableStateOf("")
    override val scannedBarcode: State<String> get() = _scannedBarcode

    private val barcodeListener = object : BarcodeReader.BarcodeListener {

        override fun onBarcodeEvent(event: BarcodeReadEvent) {
            val data = event.barcodeData
            Log.d("HoneywellScanner", "Scanned: $data")
            _scannedBarcode.value = data ?: ""
        }

        override fun onFailureEvent(event: BarcodeFailureEvent) {
            Log.e("HoneywellScanner", "Scan failed")
        }
    }

    init {
        initialize()
    }

    private fun initialize() {
        AidcManager.create(context) { manager ->
            aidcManager = manager
            barcodeReader = manager.createBarcodeReader().apply {
                addBarcodeListener(barcodeListener)

                try {
                    claim()
                    setProperty(
                        BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL
                    )

                    setProperty(
                        BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED,
                        true
                    )
                } catch (e: Exception) {
                    Log.e("HoneywellScanner", "Error claiming scanner: ${e.message}")
                }
            }
        }
    }

    override fun startScanning() {
        try {
            barcodeReader?.softwareTrigger(true)
        } catch (e: Exception) {
            Log.e("HoneywellScanner", "Start scan error: ${e.message}")
        }
    }

    override fun stopScanning() {
        try {
            barcodeReader?.softwareTrigger(false)
            barcodeReader?.release()
            barcodeReader?.close()
            aidcManager?.close()
        } catch (e: Exception) {
            Log.e("HoneywellScanner", "Stop scan error: ${e.message}")
        }
    }

    override fun triggerScan(pressed: Boolean) {
        try {
            barcodeReader?.softwareTrigger(pressed)
        } catch (e: Exception) {
            Log.e("HoneywellScanner", "Trigger error: ${e.message}")
        }
    }

    override fun getScannerDetails(): String {
        return if (barcodeReader != null) {
            "Honeywell scanner connected"
        } else {
            "Scanner not initialized"
        }
    }
}