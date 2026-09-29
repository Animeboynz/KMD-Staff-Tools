package com.animeboynz.kmd.ui.screens.tools

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.StockCountEntity
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.StockCountRepository
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.utils.BarcodeScanner
import org.koin.compose.koinInject

class StockManagementScreen(val initialBoxBarcode: String? = null) : Screen() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val scanner = remember { BarcodeScanner(context) }
        val scannedBarcode by remember { scanner.scannedBarcode }

        val stockCountRepository = koinInject<StockCountRepository>()
        val barcodesRepository = koinInject<BarcodesRepository>()
        val screenModel = rememberScreenModel {
            StockManagementScreenModel(stockCountRepository, barcodesRepository)
        }

        val openBox by screenModel.openBox.collectAsState()
        val items by screenModel.items.collectAsState()
        val locationPath by screenModel.locationPath.collectAsState()
        val message by screenModel.message.collectAsState()

        LaunchedEffect(initialBoxBarcode) {
            val barcode = initialBoxBarcode
            if (!barcode.isNullOrBlank()) {
                screenModel.openBoxByBarcode(barcode)
            }
        }

        DisposableEffect(Unit) {
            scanner.startScanning()
            onDispose { scanner.stopScanning() }
        }

        LaunchedEffect(scannedBarcode) {
            if (scannedBarcode.isNotEmpty()) {
                screenModel.addOrIncrementProduct(scannedBarcode)
                //scanner.clearScannedBarcode()
            }
        }

        LaunchedEffect(message) {
            val text = message ?: return@LaunchedEffect
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            screenModel.consumeMessage()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = { Text(openBox?.barcode ?: "Scan a box") },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                if (locationPath.isNotEmpty()) {
                    Text(
                        text = locationPath,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    )
                }
                Text(
                    text = if (openBox == null) {
                        "Scan a box barcode. Item scans after that are added to that box only."
                    } else {
                        "Scan an item to add it to ${openBox?.barcode}. Scan another box to switch."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                )
                if (openBox != null) {
                    Button(
                        onClick = { screenModel.confirmCount() },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Text("Confirm count")
                    }
                }
                StockItemsList(items, screenModel)
            }
        }
    }

    @Composable
    fun StockItemsList(orderItems: List<StockCountEntity>, screenModel: StockManagementScreenModel) {
        val barcodeEntity by screenModel.productNames.collectAsState()

        orderItems.forEach { item ->
            if (!barcodeEntity.containsKey(item.productBarcode)) {
                screenModel.fetchProductDetails(item.productBarcode)
            }
            StockCard(barcodeEntity[item.productBarcode], item, screenModel)
        }
    }

    @Composable
    fun StockCard(
        productDetails: BarcodesEntity?,
        stock: StockCountEntity,
        screenModel: StockManagementScreenModel,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = productDetails?.name ?: stock.productBarcode,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "${productDetails?.color ?: ""} | ${productDetails?.size ?: ""}".trim(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { screenModel.decrementProduct(stock.productBarcode) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Text(
                        text = stock.quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    IconButton(onClick = { screenModel.addOrIncrementProduct(stock.productBarcode) }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }

                IconButton(onClick = { screenModel.removeProduct(stock.productBarcode) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
