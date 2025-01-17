package com.animeboynz.kmd.ui.home.tabs

/////
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.koin.compose.koinInject
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.presentation.util.Tab
import com.animeboynz.kmd.ui.preferences.PreferencesScreen
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items


object SkuTab : Tab {
    private fun readResolve(): Any = OrdersTab

    override val options: TabOptions
        @Composable
        get() {
            val image = rememberVectorPainter(Icons.AutoMirrored.Filled.ManageSearch)
            return TabOptions(
                index = 0u,
                title = stringResource(R.string.sku_tab),
                icon = image,
            )
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val productsRepository = koinInject<ProductsRepository>()
        val allProductsFlow = productsRepository.getAllProducts() // This should return a Flow<List<ProductsEntity>>
        val allProducts by allProductsFlow.collectAsState(initial = emptyList()) // Collect it as state

        var searchQuery by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        R.string.sku_tab
                    },
                    actions = {
                        IconButton(onClick = { navigator.push(PreferencesScreen) }) {
                            Icon(Icons.Default.Settings, null)
                        }
                    },
                )
            },
        ) { paddingValues ->
            val paddingModifier = Modifier.padding(paddingValues)

            Column(modifier = paddingModifier) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { R.string.sku_search_bar },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { /* Handle search action if needed */ }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                // Filtered product list
                val filteredProducts = allProducts.filter { product ->
                    product.name.contains(searchQuery, ignoreCase = true) ||
                            product.sku.contains(searchQuery, ignoreCase = true)
                }.sortedByDescending { it.sku }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredProducts) { product ->
                        ProductRow(product)
                    }
                }
            }
        }
    }

    @Composable
    fun ProductRow(product: ProductsEntity) {
        Card(
            modifier = Modifier
                .padding(vertical = 2.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = product.sku,
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    text = product.name,
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }
}
