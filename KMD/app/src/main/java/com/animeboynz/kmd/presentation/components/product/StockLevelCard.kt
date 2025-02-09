package com.animeboynz.kmd.presentation.components.product

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.animeboynz.kmd.domain.StockAvailability

@Composable
fun StockLevels(stockList: List<StockAvailability>) {
    val inStockStores = stockList.filter { it.quantity > 0 }
    val outOfStockStores = stockList.filter { it.quantity == 0 }

    Column {
        inStockStores.forEach { stock ->
            StockLevelCard(stock)
        }

        if (outOfStockStores.isNotEmpty()) {
            OutOfStockCard(outOfStockStores)
        }
    }
}

@Composable
fun StockLevelCard(stock: StockAvailability) {
    val lowStockColor = MaterialTheme.colorScheme.error
    val normalStockColor = MaterialTheme.colorScheme.primary

    val stockStatus = if (stock.quantity < 2 && stock.quantity > 0) {
        "Low Stock"
    } else if (stock.quantity > 1) {
        "In Stock"
    } else "Out of Stock"

    val stockColor = if (stock.quantity < 1) lowStockColor else normalStockColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Store,
                    contentDescription = "Store Icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stock.storeName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            Text("Store No: ${stock.storeCode}", style = MaterialTheme.typography.bodySmall)
            Text("Address: ${stock.storeAddress}", style = MaterialTheme.typography.bodySmall)
            Text("SKU: ${stock.sku}", style = MaterialTheme.typography.bodySmall)
            Text("Quantity: ${stock.quantity}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = stockColor)
            Text(stockStatus, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = stockColor)
        }
    }
}

@Composable
fun OutOfStockCard(outOfStockStores: List<StockAvailability>) {
    val storeNames = outOfStockStores.joinToString(", ") { it.storeName }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Out of Stock in ${outOfStockStores.size} stores", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.error)
            Text("Stores: $storeNames", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

