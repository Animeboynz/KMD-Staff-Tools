package com.animeboynz.kmd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.Alignment
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
import com.animeboynz.kmd.domain.BoxContentMatch
import com.animeboynz.kmd.domain.ColorDropdownItem
import com.animeboynz.kmd.domain.ColorsRepository
import com.animeboynz.kmd.domain.SizeDropdownItem
import com.animeboynz.kmd.domain.StockCountRepository
import com.animeboynz.kmd.domain.WebColorDropdownItem
import com.animeboynz.kmd.domain.WebSizeDropdownItem
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.DropdownItem
import com.animeboynz.kmd.presentation.components.SimpleDropdown
import com.animeboynz.kmd.presentation.components.product.ColorsEntity
import com.animeboynz.kmd.presentation.components.product.ImageCarousel
import com.animeboynz.kmd.presentation.components.product.PrintBarcodes
import com.animeboynz.kmd.presentation.components.product.fetchImageUrls
import com.animeboynz.kmd.ui.screens.tools.StockManagementScreen
import com.animeboynz.kmd.utils.openInBrowser
import org.koin.compose.koinInject

class ProductScreen(
    val sku: String,
    val name: String,
    val color: String? = null,
    val size: String? = null,
) : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val barcodesRepository = koinInject<BarcodesRepository>()
        val colorsRepository = koinInject<ColorsRepository>()
        val stockCountRepository = koinInject<StockCountRepository>()

        val screenModel = rememberScreenModel {
            ProductScreenModel(barcodesRepository, colorsRepository, stockCountRepository, sku)
        }

        val preferences = koinInject<GeneralPreferences>()
        val countryCode = preferences.countryCode.get()

        val items by screenModel.product.collectAsState()
        val colors by screenModel.colors.collectAsState()
        var isWebProduct = false

        if (items.isEmpty())
        {
            isWebProduct = true
        }

        var hasColorError by remember { mutableStateOf(false) }
        var hasSizeError by remember { mutableStateOf(false) }

        var selectedItems by remember { mutableStateOf<List<BarcodesEntity>>(emptyList()) }

        val boxStock by screenModel.boxStock.collectAsState()

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

                var selectedColor by remember { mutableStateOf<DropdownItem?>(null) }
                val dropdownColorItems = filteredByColor.map { ColorDropdownItem(it, colors) }

                var selectedSize by remember { mutableStateOf<DropdownItem?>(null) }
                val dropdownSizeItems = filteredBySize.map { SizeDropdownItem(it) }

                LaunchedEffect(dropdownColorItems, dropdownSizeItems) {
                    selectedColor = dropdownColorItems.find { it.displayName == color }
                    selectedSize = dropdownSizeItems.find { it.displayName == size }
                }

                var dropdownColorItemsWeb by remember { mutableStateOf<List<DropdownItem>>(emptyList()) }
                var dropdownSizeItemsWeb by remember { mutableStateOf<List<DropdownItem>>(emptyList()) }

                LaunchedEffect(Unit) {
                    val productDetails = fetchImageUrls(sku)
                    imageUrls = productDetails.imageUrls.flatMap { it.imageUrls }.distinct() // Combine and remove duplicates
                    if (isWebProduct) {
                        val sizeList = productDetails.sizes
                        val colorList = productDetails.colors
                        dropdownSizeItemsWeb = sizeList.map { WebSizeDropdownItem(it) }
                        dropdownColorItemsWeb = colorList.map { WebColorDropdownItem(it.colorCode, it.colorName) }
                    }
                }

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
                    items = if(isWebProduct) dropdownColorItemsWeb else dropdownColorItems,
                    modifier = maxWidth,
                    onSelected = { color ->
                        selectedColor = color
                    }
                )

                SimpleDropdown(
                    label = stringResource(R.string.size),
                    selectedItem = selectedSize,
                    items = if(isWebProduct) dropdownSizeItemsWeb else dropdownSizeItems,
                    modifier = maxWidth,
                    onSelected = { size ->
                        selectedSize = size
                    }
                )

                if (selectedItems.isNotEmpty()) {
                    PrintBarcodes(selectedItems)
                }

                if(!isWebProduct)
                {
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
                                item.color == selectedColor?.displayName && item.size == selectedSize?.displayName
                            }

                            if (selectedItem.isNotEmpty()) {
                                selectedItems = selectedItem

                            } else {
                                Toast.makeText(context, R.string.product_no_barcodes, Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.action_get_barcode))
                    }
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

                        screenModel.findBoxStock(
                            selectedColor?.displayName.orEmpty(),
                            selectedSize?.displayName.orEmpty(),
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Get Stock")
                }

                val stockMatches = boxStock
                when {
                    stockMatches == null -> Unit
                    stockMatches.isEmpty() -> {
                        Text("This colour and size is not in any box.")
                    }
                    else -> {
                        for (match in stockMatches) {
                            BoxStockRow(match) {
                                navigator.push(StockManagementScreen(match.boxBarcode))
                            }
                        }
                    }
                }

            }

        }
    }
}

@Composable
private fun BoxStockRow(match: BoxContentMatch, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
    ) {
        Text(match.locationPath, style = MaterialTheme.typography.titleMedium)
        Text("${match.boxBarcode} · Qty ${match.quantity}", style = MaterialTheme.typography.bodyMedium)
    }
}
