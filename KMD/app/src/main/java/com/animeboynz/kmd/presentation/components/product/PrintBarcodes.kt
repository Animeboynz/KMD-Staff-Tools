package com.animeboynz.kmd.presentation.components.product

import android.graphics.Bitmap
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.utils.generateBarCode
import kotlin.collections.forEach

@Composable
fun PrintBarcodes(items: List<BarcodesEntity>) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val barcode = mutableSetOf<BarcodeItem>()
        items.forEach { item ->
            if (!item.pieceBarcode.isNullOrEmpty()) {
                barcode.add(BarcodeItem("Piece Barcode", item.pieceBarcode))
            }
            if (!item.gtin.isNullOrEmpty()) {
                barcode.add(BarcodeItem("GTIN Barcode", item.gtin))
            }
        }
        if (barcode.isEmpty()) {
            Toast.makeText(context, R.string.product_no_barcodes, Toast.LENGTH_SHORT).show()
        }
        barcode.forEach{ barcode ->
            val bitmapGtin = remember { mutableStateOf<Bitmap?>(null) }
            bitmapGtin.value = generateBarCode(barcode.barcode)

            Card(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${barcode.type}: ${barcode.barcode}", style = MaterialTheme.typography.bodyMedium)
                    bitmapGtin.value?.asImageBitmap()?.let { bitmapImage ->
                        Image(
                            bitmap = bitmapImage,
                            contentDescription = "Generate Barcode Image for ${barcode.barcode}",
                            modifier = Modifier.size(250.dp, 100.dp)
                        )
                    }
                }
            }
        }
    }
}

data class BarcodeItem(
    val type: String,
    val barcode: String
)

@Composable
fun PrintBarcodes(barcode: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val bitmapGtin = remember { mutableStateOf<Bitmap?>(null) }
        bitmapGtin.value = generateBarCode(barcode)

        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                bitmapGtin.value?.asImageBitmap()?.let { bitmapImage ->
                    Image(
                        bitmap = bitmapImage,
                        contentDescription = "Generate Barcode Image for $barcode",
                        modifier = Modifier.size(250.dp, 100.dp)
                    )
                }
            }
        }
    }
}