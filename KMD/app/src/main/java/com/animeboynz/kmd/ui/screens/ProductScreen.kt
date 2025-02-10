package com.animeboynz.kmd.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.ColorDropdownItem
import com.animeboynz.kmd.domain.ColorsRepository
import com.animeboynz.kmd.domain.SizeDropdownItem
import com.animeboynz.kmd.domain.StockAvailability
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.SimpleDropdown
import com.animeboynz.kmd.presentation.components.product.ImageCarousel
import com.animeboynz.kmd.presentation.components.product.PrintBarcodes
import com.animeboynz.kmd.presentation.components.product.StockLevels
import com.animeboynz.kmd.presentation.components.product.fetchImageUrls
import com.animeboynz.kmd.utils.openInBrowser
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.koin.compose.koinInject

class ProductScreen(val sku: String, val name: String) : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val barcodesRepository = koinInject<BarcodesRepository>()
        val colorsRepository = koinInject<ColorsRepository>()

        val screenModel = rememberScreenModel {
            ProductScreenModel(barcodesRepository, colorsRepository, sku)
        }

        val preferences = koinInject<GeneralPreferences>()
        val countryCode = preferences.countryCode.get()

        val items by screenModel.product.collectAsState()
        val colors by screenModel.colors.collectAsState()

        var hasColorError by remember { mutableStateOf(false) }
        var hasSizeError by remember { mutableStateOf(false) }

        var selectedItems by remember { mutableStateOf<List<BarcodesEntity>>(emptyList()) }

        var stockData by remember { mutableStateOf<List<StockAvailability>>(emptyList()) }
        var isLoading by remember { mutableStateOf(false) }
        var fetchStockDataTrigger by remember { mutableStateOf(false) }

        var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    title = {
                        Text(stringResource(R.string.product_details))
                    },
                    actions = {
                        IconButton(onClick = {
                            val domainMap = mapOf(
                                "NZ" to Pair(R.string.search_url_a, "co.nz"),
                                "AU" to Pair(R.string.search_url_a, "com.au"),
                                "GB" to Pair(R.string.search_url_a, "co.uk"),
                                "FR" to Pair(R.string.search_url_b, "fr"),
                                "DE" to Pair(R.string.search_url_b, "de"),
                                "US" to Pair(R.string.search_url_b, "com")
                            )

                            val url = when (countryCode) {
                                "CA" -> context.getString(R.string.search_url_c, sku) // Canada has a unique format
                                else -> domainMap[countryCode]?.let { (urlRes, domain) ->
                                    context.getString(urlRes, domain, sku)
                                }
                            }

                            url?.let { context.openInBrowser(it) }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.TravelExplore,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                )
            }
        ) { paddingValues ->
            val paddingModifier = Modifier.padding(paddingValues)
            Column(
                modifier = paddingModifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val maxWidth = Modifier.fillMaxWidth()

                val filteredByColor: List<BarcodesEntity> = items.distinctBy { it.color }
                val filteredBySize: List<BarcodesEntity> = items.distinctBy { it.size }

                var selectedColor by remember { mutableStateOf<ColorDropdownItem?>(null) }
                val dropdownColorItems = filteredByColor.map { ColorDropdownItem(it, colors) }

                var selectedSize by remember { mutableStateOf<SizeDropdownItem?>(null) }
                val dropdownSizeItems = filteredBySize.map { SizeDropdownItem(it) }

                var region = preferences.stockCheckRegion.get()
                var stockCheckURL = "https://app.kathmandu.co.nz/graphql?query=query+getStoreInventory%28%24childSku%3AString%21%24region%3AString%24locationId%3AInt%29%7BStockAvailabilities%28childSku%3A%24childSku+region%3A%24region+locationId%3A%24locationId%29%7Binventory%7Bquantity+sku+__typename%7DisPossible+isPossibleMessage+magentoRetailerId+orderMethods%7BclickAndCollect%7BcollectionTime+deliveryScope+isPossible+name+__typename%7DpickupInStore%7BcollectionTime+deliveryScope+isPossible+name+__typename%7D__typename%7DstoreAddress%7Bcity+country+latitude+longitude+postcode+state+street+__typename%7DstoreCode+storeName+tripDistance+__typename%7D%7D&operationName=getStoreInventory&variables=%7B%22childSku%22%3A%22${sku}%2F${selectedColor?.displayName}%2F${selectedSize?.displayName}%22%2C%22region%22%3A%22${region}%22%7D"


                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)) // Optional: Add a background
                )

                LaunchedEffect(Unit) {
                    imageUrls = fetchImageUrls(sku)
                }

                if (imageUrls.isNotEmpty())
                {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)) {
                        ImageCarousel(imageUrls)
                    }
                }

                SimpleDropdown(
                    label = stringResource(R.string.color),
                    selectedItem = selectedColor,
                    items = dropdownColorItems,
                    modifier = maxWidth,
                    onSelected = { color ->
                        selectedColor = color
                    }
                )

                SimpleDropdown(
                    label = stringResource(R.string.size),
                    selectedItem = selectedSize,
                    items = dropdownSizeItems,
                    modifier = maxWidth,
                    onSelected = { size ->
                        selectedSize = size
                    }
                )

                if (selectedItems.isNotEmpty()) {
                    PrintBarcodes(selectedItems)
                }


                Button(
                    onClick = {
                        // Reset error states
                        hasColorError = false
                        hasSizeError = false

                        if (selectedColor == null) {
                            hasColorError = true
                        }
                        if (selectedSize == null) {
                            hasSizeError = true
                        }

                        if (hasColorError || hasSizeError) {
                            Toast.makeText(context, R.string.product_input_errors_warning, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Filter items based on selected color and size
                        val selectedItem = items.filter { item ->
                            item.color == selectedColor?.item?.color && item.size == selectedSize?.item?.size
                        }

                        if (selectedItem.isNotEmpty()) {
                            Toast.makeText(context, R.string.product_found, Toast.LENGTH_SHORT).show()
                            selectedItems = selectedItem

                        } else {
                            Toast.makeText(context, R.string.product_not_found, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.action_get_barcode))
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                } else {
                    StockLevels(stockData)
                    fetchStockDataTrigger = false
                }

                Button(
                    onClick = {
                        hasColorError = false
                        hasSizeError = false

                        if (selectedColor == null) {
                            hasColorError = true
                        }
                        if (selectedSize == null) {
                            hasSizeError = true
                        }

                        if (hasColorError || hasSizeError) {
                            Toast.makeText(context, R.string.product_input_errors_warning, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        fetchStockDataTrigger = !fetchStockDataTrigger // Toggle to trigger LaunchedEffect
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Get Stock")
                }

                LaunchedEffect(fetchStockDataTrigger) {
                    if (fetchStockDataTrigger) {
                        val response = fetchStockData(stockCheckURL)
                        response?.let {
                            val stockList = parseStockJson(it)
                            stockData = stockList
                            isLoading = false
                        }
                    }
                }

            }

        }
    }

    suspend fun fetchStockData(url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpsURLConnection
                connection.requestMethod = "GET"
                connection.inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                Log.e("StockCheck", "Error fetching stock data", e)
                null
            }
        }
    }

    fun parseStockJson(jsonString: String): List<StockAvailability> {
        val stockList = mutableListOf<StockAvailability>()
        try {
            val jsonObject = JSONObject(jsonString)

            // Accessing the 'data' object
            val dataObject = jsonObject.getJSONObject("data")

            // Now, getting the 'StockAvailabilities' array from inside 'data'
            val stockArray = dataObject.getJSONArray("StockAvailabilities")

            for (i in 0 until stockArray.length()) {
                val store = stockArray.getJSONObject(i)

                val storeName = store.getString("storeName")
                val storeCode = store.getInt("storeCode") // Note: Store code might be an integer
                val storeAddress = store.getJSONObject("storeAddress").getString("street")
                val inventory = store.getJSONArray("inventory").getJSONObject(0)
                val quantity = inventory.getInt("quantity")
                val sku = inventory.getString("sku")

                stockList.add(StockAvailability(storeName, storeCode.toString(), storeAddress, sku, quantity))
            }
        } catch (e: Exception) {
            Log.e("StockParsing", "Error parsing stock JSON", e)
        }
        return stockList
    }
}
