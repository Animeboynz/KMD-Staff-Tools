package com.animeboynz.kmd.domain

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.util.zip.GZIPInputStream

fun importProtobufData(context: Context, uri: Uri?, assetFileName: String? = null): BackupData? {
    var showToast: Boolean = false
    try {
        val inputStream: InputStream?
        if (assetFileName != null) {
            inputStream = context.assets.open(assetFileName)
        } else {
            inputStream = context.contentResolver.openInputStream(uri!!)
            showToast = true
        }

        if (inputStream != null) {
            val gzipInputStream = GZIPInputStream(inputStream)
            val byteArray = gzipInputStream.readBytes()
            val backupData = ProtoBuf.decodeFromByteArray(BackupData.serializer(), byteArray)

            if (showToast) Toast.makeText(context, "Import successful!", Toast.LENGTH_SHORT).show()
            return backupData
        } else {
            Toast.makeText(context, "Failed to open file", Toast.LENGTH_SHORT).show()
            return null
        }
    } catch (e: Exception) {
        if (showToast) Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
        return null
    }
}
