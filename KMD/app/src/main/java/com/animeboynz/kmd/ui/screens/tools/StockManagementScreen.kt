package com.animeboynz.kmd.ui.screens.tools

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.StockCountEntity
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.domain.StockCountRepository
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.ui.home.tabs.OrdersTabScreenModel
import com.animeboynz.kmd.utils.BarcodeScanner
import org.koin.compose.koinInject

class StockManagementScreen : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val scanner = remember { BarcodeScanner(context) }
        val scannedBarcode by remember { scanner.scannedBarcode }

        val stockCountRepository = koinInject<StockCountRepository>()
        val barcodesRepository = koinInject<BarcodesRepository>()
        val screenModel = rememberScreenModel { StockManagementScreenModel(stockCountRepository, barcodesRepository) }

        DisposableEffect(Unit) {
            scanner.startScanning()
            onDispose { scanner.stopScanning() }
        }

        // Automatically add/increment product when a barcode is scanned
        LaunchedEffect(scannedBarcode) {
            if (scannedBarcode.isNotEmpty()) {
                screenModel.addOrIncrementProduct(scannedBarcode)
                scanner.clearScannedBarcode()
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
                Text("Manage Offsite Stock", style = MaterialTheme.typography.titleMedium)

                // Display scanned barcode
                Text("Scanned Barcode: $scannedBarcode", style = MaterialTheme.typography.bodyLarge)

                // Display current stock list in styled cards
                LazyColumn {
                    items(screenModel.stockList.value) { stock ->
                        screenModel.fetchProductDetails(stock.productBarcode)
                        val productDetails = screenModel.skuResult.collectAsState()
                        StockCard(productDetails.value, stock, screenModel)
                    }
                }
            }
        }
    }

    @Composable
    fun StockCard(productDetails:BarcodesEntity?, stock: StockCountEntity, screenModel: StockManagementScreenModel) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Product: ${productDetails?.name ?: "Unknown"}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Color: ${productDetails?.color ?: "Unknown"}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Size: ${productDetails?.size ?: "Unknown"}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Quantity: ${stock.quantity}", style = MaterialTheme.typography.bodyLarge)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { screenModel.decrementProduct(stock.productBarcode) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    IconButton(onClick = { screenModel.addOrIncrementProduct(stock.productBarcode) }) {
                        Icon(Icons.Default.Add, contentDescription = "Decrease")
                    }
                    IconButton(onClick = { screenModel.removeProduct(stock.productBarcode) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}