package com.animeboynz.kmd.presentation.components.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.ui.home.tabs.SkuTabScreenModel
import com.animeboynz.kmd.ui.screens.ProductScreen
import com.animeboynz.kmd.utils.BarcodeScanner

@Composable
fun ScanBarcodeDialog(
    screenModel: SkuTabScreenModel,
    productsListState: Boolean,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val barcodeScanner = remember { BarcodeScanner(context) }
    val scannedBarcode by remember { barcodeScanner.scannedBarcode }
    val navigator = LocalNavigator.currentOrThrow

    val sku by screenModel.skuResult.collectAsState()

    DisposableEffect(Unit) {
        barcodeScanner.startScanning()

        onDispose {
            barcodeScanner.stopScanning()
        }
    }

    LaunchedEffect(scannedBarcode) {
        scannedBarcode.let { barcode ->
            screenModel.fetchByBarcode(barcode) // Fetch SKU
        }
    }

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = {
                sku?.let { productSku ->
                    navigator.push(ProductScreen(productSku.sku, productSku.name, productSku.color, productSku.size))
                    onCancel() // Close the dialog after navigating
                }
                onCancel()
            }) {
                Text(text = stringResource(R.string.generic_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(R.string.generic_cancel))
            }
        },
        title = {
            Text(text = "Scan a Barcode")
        },
        text = {
            if (sku == null) {
                if (productsListState) {
                    if (scannedBarcode.isNotEmpty()){
                        Column {
                            Text(text = "Scanned Barcode: $scannedBarcode")
                            PrintBarcodes(scannedBarcode)
                        }
                    }
                } else {
                    Text(text = "Import a products list with barcodes to use this feature")
                }
            } else {
                Column {
                    Text(text = "SKU: ${sku?.sku}\nName: ${sku?.name}\nColour: ${sku?.color}\nSize: ${sku?.size}")
                    PrintBarcodes(scannedBarcode)
                }
            }
        },
    )
}