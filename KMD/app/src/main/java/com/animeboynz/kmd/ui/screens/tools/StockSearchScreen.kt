package com.animeboynz.kmd.ui.screens.tools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.domain.StockCountRepository
import com.animeboynz.kmd.presentation.Screen
import org.koin.compose.koinInject

class StockSearchScreen : Screen() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repository = koinInject<StockCountRepository>()
        val screenModel = rememberScreenModel { StockSearchScreenModel(repository) }
        val results by screenModel.results.collectAsState()
        val searched by screenModel.searched.collectAsState()
        var query by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = { Text("Search stock") },
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        screenModel.onQueryChange(it)
                    },
                    label = { Text("SKU, name, or barcode") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                if (searched && results.isEmpty()) {
                    Text(
                        text = "No boxes contain that product.",
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                results.forEach { match ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navigator.push(StockManagementScreen(match.boxBarcode)) }
                            .padding(vertical = 12.dp),
                    ) {
                        Text(match.productName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${match.sku} · ${match.color} · ${match.size}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(match.locationPath, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${match.boxBarcode} · Qty ${match.quantity}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}
