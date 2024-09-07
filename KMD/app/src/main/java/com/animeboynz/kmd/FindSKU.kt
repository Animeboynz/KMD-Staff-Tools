package com.animeboynz.kmd

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.withContext


data class Product(val sku: String, val name: String)

class FindSKUViewModel(context: Context) : ViewModel() {
    var productList by mutableStateOf(listOf<Product>())

    init {
        loadCSV(context)
    }

    private fun loadCSV(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val products = mutableListOf<Product>()
            try {
                val inputStream = context.assets.open("MPL.csv")
                val reader = BufferedReader(InputStreamReader(inputStream))
                reader.useLines { lines ->
                    lines.drop(1).forEach { line ->
                        val tokens = line.split(",")
                        if (tokens.size == 2) {
                            val sku = tokens[0].trim()
                            val name = tokens[1].trim()
                            products.add(Product(sku, name))
                        }
                    }
                }
                // Sort products in descending order by SKU
                products.sortByDescending { it.sku }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Switch to the main thread before updating the state
            withContext(Dispatchers.Main) {
                productList = products
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindSKUPage(viewModel: FindSKUViewModel) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Use derivedStateOf to recompute filteredProducts whenever searchQuery or viewModel.productList changes
    val filteredProducts by remember(searchQuery, viewModel.productList) {
        derivedStateOf {
            if (searchQuery.text.isBlank()) {
                viewModel.productList
            } else {
                viewModel.productList.filter {
                    it.sku.contains(searchQuery.text, ignoreCase = true) ||
                            it.name.contains(searchQuery.text, ignoreCase = true) ||
                            it.name.split(" ").containsAll(searchQuery.text.split(" ").filter { queryWord -> queryWord.isNotBlank() })
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        // Search Box
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search SKU or Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Product List
        LazyColumn {
            items(filteredProducts) { product ->
                Column {
                    ProductRow(product = product)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ProductRow(product: Product) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        headlineContent = { Text(text = product.sku) },
        supportingContent = { Text(text = product.name) }
    )
}
