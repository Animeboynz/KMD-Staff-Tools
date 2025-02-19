package com.animeboynz.kmd.utils

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import com.panasonic.toughpad.android.api.ToughpadApi
import com.panasonic.toughpad.android.api.ToughpadApiListener
import com.panasonic.toughpad.android.api.barcode.*
import kotlinx.coroutines.*

class BarcodeScanner(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ToughpadApiListener, BarcodeListener {

    private var barcodeReader: BarcodeReader? = null
    private var _scannedBarcode = mutableStateOf("")
    val scannedBarcode: State<String> get() = _scannedBarcode

    private var isInitialized = false  // Flag to check if the API is initialized

    init {
        initializeBarcodeReader()
    }

    private fun initializeBarcodeReader() {
        if (!ToughpadApi.isAlreadyInitialized()) {
            try {
                ToughpadApi.initialize(context, this)
            } catch (e: RuntimeException) {
                Log.e("BarcodeScanner", "Error initializing Toughpad API: ${e.message}")
            }
        }
    }

    fun startScanning() {
        if (isInitialized) {
            val readers = BarcodeReaderManager.getBarcodeReaders()
            if (readers.isNullOrEmpty()) {
                Log.e("BarcodeScanner", "No barcode readers found.")
                return
            }

            barcodeReader = readers.first().apply {
                coroutineScope.launch {
                    try {
                        enable(3000)  // Timeout: 3 seconds
                        addBarcodeListener(this@BarcodeScanner)
                    } catch (e: BarcodeException) {
                        Log.e("BarcodeScanner", "Error enabling barcode reader: ${e.message}")
                    }
                }
            }
        } else {
            Log.e("BarcodeScanner", "Toughpad API not initialized yet.")
        }
    }

    override fun onApiConnected(version: Int) {
        Log.d("BarcodeScanner", "Toughpad API connected.")
        isInitialized = true  // Set the flag to true when the API is connected
        startScanning()
    }

    override fun onApiDisconnected() {
        cleanupBarcodeReader()
    }

    override fun onRead(reader: BarcodeReader?, data: BarcodeData?) {
        Log.d("BarcodeScanner", "onRead() Triggered!")

        data?.let {
            val barcodeText = it.textData
            Log.d("BarcodeScanner", "Scanned Barcode: $barcodeText")

            // Update the scanned barcode state
            _scannedBarcode.value = barcodeText
        }
    }

    fun clearScannedBarcode() {
        _scannedBarcode.value = ""
    }

    private fun cleanupBarcodeReader() {
        coroutineScope.launch {
            barcodeReader?.let {
                try {
                    it.disable()
                    it.removeBarcodeListener(this@BarcodeScanner)
                } catch (e: BarcodeException) {
                    Log.e("BarcodeScanner", "Error disabling barcode reader: ${e.message}")
                }
            }
            ToughpadApi.destroy()
        }
    }

    fun stopScanning() {
        cleanupBarcodeReader()
    }
}
