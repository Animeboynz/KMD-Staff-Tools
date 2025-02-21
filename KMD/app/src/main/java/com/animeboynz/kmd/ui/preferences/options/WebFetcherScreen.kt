package com.animeboynz.kmd.ui.preferences.options

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.preferences.PrefsHorizontalPadding
import com.animeboynz.kmd.presentation.components.preferences.TextPreferenceWidget
import com.animeboynz.kmd.ui.theme.spacing
import com.animeboynz.kmd.utils.openInBrowser
import com.animeboynz.kmd.utils.secondaryItemAlpha
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.koin.compose.koinInject
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.ColorsRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.domain.importProtobufData
import com.animeboynz.kmd.presentation.components.preferences.InfoWidget
import com.animeboynz.kmd.ui.screens.CustomerOrderScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

object WebFetcherScreen : Screen() {
    private fun readResolve(): Any = WebFetcherScreen

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val productsRepository = koinInject<ProductsRepository>()

        var statusMessage by remember { mutableStateOf("Press the button to start fetching.") }
        var fetchedCount by remember { mutableStateOf(0) }
        var isFetching by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_data_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
            ) {
                Button(
                    onClick = {
                        isFetching = true
                        statusMessage = "Fetching NZ products..."
                        fetchedCount = 0

                        CoroutineScope(Dispatchers.IO).launch {
                            val success = fetchAndStoreProducts(
                                productsRepository = productsRepository, countryCode = "NZ",
                                updateStatus = { count, message ->
                                    fetchedCount = count
                                    statusMessage = message
                                }
                            )
                            withContext(Dispatchers.Main) {
                                isFetching = false
                                statusMessage = if (success) "Products fetched successfully!" else "Failed to fetch products"
                                Toast.makeText(context, statusMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    enabled = !isFetching
                ) {
                    Text(if (isFetching) "Fetching..." else "Fetch Products from NZ Website")
                }

                Button(
                    onClick = {
                        isFetching = true
                        statusMessage = "Fetching AU products..."
                        fetchedCount = 0

                        CoroutineScope(Dispatchers.IO).launch {
                            val success = fetchAndStoreProducts(
                                productsRepository = productsRepository, countryCode = "AU",
                                updateStatus = { count, message ->
                                    fetchedCount = count
                                    statusMessage = message
                                }
                            )
                            withContext(Dispatchers.Main) {
                                isFetching = false
                                statusMessage = if (success) "Products fetched successfully!" else "Failed to fetch products"
                                Toast.makeText(context, statusMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    enabled = !isFetching
                ) {
                    Text(if (isFetching) "Fetching..." else "Fetch Products from AU Website")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (fetchedCount > 0) {
                    Text(
                        text = "Fetched Products: $fetchedCount",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }

    private suspend fun fetchAndStoreProducts(
        productsRepository: ProductsRepository,
        updateStatus: (Int, String) -> Unit,
        countryCode: String
    ): Boolean {
        val client = OkHttpClient()
        val jsonParser = Json { ignoreUnknownKeys = true }
        var page = 1
        var totalPages: Int
        val fetchedProducts = mutableListOf<Product>()
        val fetchedSkus = mutableListOf<String>()

        do {
            val urls = mapOf(
                "AU" to "https://3vlhyg.a.searchspring.io/api/search/search.json?resultsFormat=native&resultsPerPage=100&page=$page&siteId=3vlhyg",
                "NZ" to "https://8r21li.a.searchspring.io/api/search/search.json?resultsFormat=native&resultsPerPage=100&page=$page&siteId=8r21li",
                "FR" to "https://rvz9yn.a.searchspring.io/api/search/search.json?resultsFormat=native&resultsPerPage=100&page=$page&siteId=rvz9yn",
                "DE" to "https://6v27dr.a.searchspring.io/api/search/search.json?resultsFormat=native&resultsPerPage=100&page=$page&siteId=6v27dr",
                "UK" to "https://q8l5y3.a.searchspring.io/api/search/search.json?resultsFormat=native&resultsPerPage=100&page=$page&siteId=q8l5y3",
                "US" to "https://fcgway.a.searchspring.io/api/search/search.json?resultsFormat=native&resultsPerPage=100&page=$page&siteId=fcgway"
            )

            val url = urls[countryCode] ?: urls["NZ"]!!
            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    updateStatus(fetchedProducts.size, "Failed to fetch page $page")
                    return false
                }
                response.body.string().let { responseBody ->
                    val apiResponse = jsonParser.decodeFromString<ApiResponse>(responseBody)
                    totalPages = apiResponse.pagination.totalPages
                    fetchedProducts.addAll(apiResponse.results)

                    // Update UI with progress
                    updateStatus(fetchedProducts.size, "Fetched ${fetchedProducts.size} products so far...")
                }
            }
            page++
        } while (page <= totalPages)

        // Insert into database and update status
        fetchedProducts.forEachIndexed { index, product ->
            fetchedSkus.add(product.sku)
            productsRepository.upsert(ProductsEntity(product.sku.removeSuffix("/").removeSuffix("-CLR"), product.name))
            if (index % 10 == 0) {  // Update UI every 10 products
                updateStatus(index, "Storing products... ($index/${fetchedProducts.size})")
            }
        }

        updateStatus(fetchedProducts.size, "All products stored successfully!")
        return true
    }
}

@Serializable
data class Pagination(val totalPages: Int)

@Serializable
data class Product(val sku: String, val name: String)

@Serializable
data class ApiResponse(val pagination: Pagination, val results: List<Product>)

