package com.animeboynz.kmd.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class CameraBarcodeScanner(
    private val context: Context,
) : StockScanner {
    private val _scannedBarcode = mutableStateOf("")
    override val scannedBarcode: State<String> = _scannedBarcode

    private val mainHandler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private val detector = BarcodeScanning.getClient()
    private var cameraProvider: ProcessCameraProvider? = null
    private var lastValue = ""
    private var lastAt = 0L

    fun bind(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            val provider = future.get()
            cameraProvider = provider
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analysis.setAnalyzer(executor, ::analyze)
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis,
            )
        }, ContextCompat.getMainExecutor(context))
    }

    fun stop() {
        cameraProvider?.unbindAll()
        detector.close()
        executor.shutdown()
    }

    override fun clearScannedBarcode() {
        _scannedBarcode.value = ""
    }

    private fun analyze(image: ImageProxy) {
        val media = image.image
        if (media == null) {
            image.close()
            return
        }
        val input = InputImage.fromMediaImage(media, image.imageInfo.rotationDegrees)
        detector.process(input)
            .addOnSuccessListener { codes ->
                val value = codes.firstOrNull()?.rawValue ?: return@addOnSuccessListener
                val now = SystemClock.elapsedRealtime()
                if (value == lastValue && now - lastAt < 1500) return@addOnSuccessListener
                lastValue = value
                lastAt = now
                mainHandler.post { _scannedBarcode.value = value }
            }
            .addOnCompleteListener { image.close() }
    }
}
