package com.animeboynz.kmd.domain

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream

fun importProtobufData(context: Context, uri: Uri): BackupData? {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val byteArray = inputStream.readBytes()
            val backupData = ProtoBuf.decodeFromByteArray(BackupData.serializer(), byteArray)

            Toast.makeText(context, "Import successful!", Toast.LENGTH_SHORT).show()
            return backupData
        } else {
            Toast.makeText(context, "Failed to open file", Toast.LENGTH_SHORT).show()
            return null
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
        return null
    }
}
