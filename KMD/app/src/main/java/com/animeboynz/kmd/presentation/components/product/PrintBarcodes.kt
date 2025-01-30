package com.animeboynz.kmd.presentation.components.product

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.utils.generateBarCode
import kotlin.collections.forEach

@Composable
fun PrintBarcodes(items: List<BarcodesEntity>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items.forEach { item ->
            if (!item.pieceBarcode.isNullOrEmpty()) {
                val bitmap = remember { mutableStateOf<Bitmap?>(null) }
                bitmap.value = generateBarCode(item.pieceBarcode)

                Card(
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Piece Barcode: ${item.pieceBarcode}", style = MaterialTheme.typography.bodyMedium)
                        bitmap.value?.asImageBitmap()?.let { bitmapImage ->
                            Image(
                                bitmap = bitmapImage,
                                contentDescription = "Generate Barcode Image for ${item.pieceBarcode}",
                                modifier = Modifier.size(250.dp, 100.dp)
                            )
                        }
                    }
                }
            }
            if (!item.gtin.isNullOrEmpty()) {
                val bitmapGtin = remember { mutableStateOf<Bitmap?>(null) }
                bitmapGtin.value = generateBarCode(item.gtin)

                Card(
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("GTIN Barcode: ${item.gtin}", style = MaterialTheme.typography.bodyMedium)
                        bitmapGtin.value?.asImageBitmap()?.let { bitmapImage ->
                            Image(
                                bitmap = bitmapImage,
                                contentDescription = "Generate Barcode Image for ${item.gtin}",
                                modifier = Modifier.size(250.dp, 100.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}