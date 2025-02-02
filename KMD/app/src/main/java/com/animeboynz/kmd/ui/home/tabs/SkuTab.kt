package com.animeboynz.kmd.ui.home.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.animeboynz.kmd.R
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.presentation.components.EmptyScreen
import com.animeboynz.kmd.presentation.components.EmptyScreenAction
import com.animeboynz.kmd.presentation.components.order.ProductRow
import com.animeboynz.kmd.presentation.util.Tab
import com.animeboynz.kmd.ui.preferences.PreferencesScreen
import com.animeboynz.kmd.ui.preferences.options.DataPreferencesScreen
import com.animeboynz.kmd.ui.screens.ProductScreen
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.koinInject


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
        val screenModel = rememberScreenModel { SkuTabScreenModel(productsRepository) }

        val allProducts by screenModel.allProducts.collectAsState()

        var searchQuery by remember { mutableStateOf("") }

        val filteredProducts = allProducts.filter { product ->
            product.name.contains(searchQuery, ignoreCase = true) ||
                    product.sku.contains(searchQuery, ignoreCase = true)
        }.sortedByDescending { it.sku }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.sku_tab))
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

            if (allProducts.isEmpty())
            {
                EmptyScreen(
                    "No products found",
                    actions = persistentListOf(
                        EmptyScreenAction(
                            stringRes = "Import Products",
                            icon = Icons.Outlined.UploadFile,
                            onClick = { navigator.push(DataPreferencesScreen) },
                        ),
                    )
                )
            } else {
                Column(modifier = paddingModifier) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(stringResource(R.string.sku_search_bar)) },
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

                    if (filteredProducts.isEmpty())
                    {
                        EmptyScreen("No matching products found")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filteredProducts) { product ->
                                ProductRow(product, {
                                    navigator.push(ProductScreen(product.sku, product.name))
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}
