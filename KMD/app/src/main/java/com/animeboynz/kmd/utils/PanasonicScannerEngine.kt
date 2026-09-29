package com.animeboynz.kmd.utils

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import com.panasonic.toughpad.android.api.ToughpadApi
import com.panasonic.toughpad.android.api.ToughpadApiListener
import com.panasonic.toughpad.android.api.barcode.*
import kotlinx.coroutines.*

class PanasonicScannerEngine(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ScannerEngine, ToughpadApiListener, BarcodeListener {

    private var barcodeReader: BarcodeReader? = null
    private var isInitialized = false

    private var _scannedBarcode = mutableStateOf("")
    override val scannedBarcode: State<String> get() = _scannedBarcode

    init {
        initialize()
    }

    // -------------------------
    // Initialization
    // -------------------------
    private fun initialize() {
        if (!ToughpadApi.isAlreadyInitialized()) {
            try {
                ToughpadApi.initialize(context, this)
            } catch (e: RuntimeException) {
                Log.e("PanasonicScanner", "Init error: ${e.message}")
            }
        } else {
            // API already alive → mark initialized + start
            isInitialized = true
            setupReader()
        }
    }

    override fun onApiConnected(version: Int) {
        Log.d("PanasonicScanner", "Toughpad API connected (v$version)")
        isInitialized = true
        setupReader()
    }

    override fun onApiDisconnected() {
        Log.d("PanasonicScanner", "Toughpad API disconnected")
        cleanup()
    }

    // -------------------------
    // Reader setup
    // -------------------------
    private fun setupReader() {
        val readers = BarcodeReaderManager.getBarcodeReaders()

        if (readers.isNullOrEmpty()) {
            Log.e("PanasonicScanner", "No barcode readers found")
            return
        }

        barcodeReader = readers.first()

        coroutineScope.launch {
            try {
                barcodeReader?.apply {
                    addBarcodeListener(this@PanasonicScannerEngine)
                    enable(3000) // 3-second timeout (same behavior as your original)
                }
            } catch (e: BarcodeException) {
                Log.e("PanasonicScanner", "Enable error: ${e.message}")
            }
        }
    }

    // -------------------------
    // Scanning controls
    // -------------------------
    override fun startScanning() {
        if (!isInitialized) {
            Log.e("PanasonicScanner", "API not initialized")
            return
        }

        coroutineScope.launch {
            try {
                barcodeReader?.enable(3000)
            } catch (e: BarcodeException) {
                Log.e("PanasonicScanner", "Start scan error: ${e.message}")
            }
        }
    }

    override fun stopScanning() {
        cleanup()
    }

    override fun triggerScan(pressed: Boolean) {
        coroutineScope.launch {
            try {
                barcodeReader?.pressSoftwareTrigger(pressed)
            } catch (e: BarcodeException) {
                Log.e("PanasonicScanner", "Trigger error: ${e.message}")
            }
        }
    }

    // -------------------------
    // Callback
    // -------------------------
    override fun onRead(reader: BarcodeReader?, data: BarcodeData?) {
        val text = data?.textData ?: return
        Log.d("PanasonicScanner", "Scanned: $text")
        _scannedBarcode.value = text
    }

    // -------------------------
    // Info
    // -------------------------
    override fun getScannerDetails(): String {
        return barcodeReader?.let {
            "Panasonic Scanner\n" +
                    "Model: ${it.deviceName}\n" +
                    "Firmware: ${it.deviceFirmwareVersion}\n" +
                    "Serial: ${it.deviceSerialNumber}"
        } ?: "Scanner not initialized"
    }

    // -------------------------
    // Cleanup
    // -------------------------
    private fun cleanup() {
        coroutineScope.launch {
            try {
                barcodeReader?.let {
                    it.removeBarcodeListener(this@PanasonicScannerEngine)
                    it.disable()
                }
            } catch (e: BarcodeException) {
                Log.e("PanasonicScanner", "Cleanup error: ${e.message}")
            }

            try {
                ToughpadApi.destroy()
            } catch (e: Exception) {
                Log.e("PanasonicScanner", "API destroy error: ${e.message}")
            }

            barcodeReader = null
            isInitialized = false
        }
    }
}