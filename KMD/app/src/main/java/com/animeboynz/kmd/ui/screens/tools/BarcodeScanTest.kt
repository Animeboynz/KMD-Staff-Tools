package com.animeboynz.kmd.ui.screens.tools

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.utils.BarcodeScanner

class BarcodeScanTest : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        // Initialize BarcodeScanner
        val barcodeScanner = remember { BarcodeScanner(context) }

        // Observe scannedBarcode
        //val scannedBarcode by barcodeScanner.scannedBarcode.observeAsState("")
        val scannedBarcode by remember { barcodeScanner.scannedBarcode }

        DisposableEffect(Unit) {
            barcodeScanner.startScanning()

            onDispose {
                barcodeScanner.stopScanning()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = { Text(stringResource(R.string.product_details)) }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Scanned Barcode: $scannedBarcode",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}