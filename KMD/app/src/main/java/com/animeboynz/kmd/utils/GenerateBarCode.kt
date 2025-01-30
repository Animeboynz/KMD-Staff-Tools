package com.animeboynz.kmd.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

fun generateBarCode(text: String): Bitmap {
    val width = 500
    val height = 150
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val codeWriter = MultiFormatWriter()
    try {
        val bitMatrix = codeWriter.encode(
            text,
            BarcodeFormat.CODE_128,
            width,
            height
        )
        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                bitmap.setPixel(x, y, color)
            }
        }
    } catch (e: WriterException) {
        Log.d("TAG", "generateBarCode: ${e.message}")
    }
    return bitmap
}