package com.animeboynz.kmd.ui.screens

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.ColorsEntity
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.ColorsRepository
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.domain.OrderItemRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.domain.Status
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.DropdownItem
import com.animeboynz.kmd.presentation.components.EmployeeDropdownItem
import com.animeboynz.kmd.presentation.components.SimpleDropdown
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.text.SimpleDateFormat
import java.util.*
import org.koin.compose.koinInject


class ProductScreen(val sku: String) : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val barcodesRepository = koinInject<BarcodesRepository>()
        val colorsRepository = koinInject<ColorsRepository>()

        val screenModel = rememberScreenModel {
            ProductScreenModel(barcodesRepository, colorsRepository, sku)
        }

        val items by screenModel.product.collectAsState()
        val colors by screenModel.colors.collectAsState()

        var hasColorError by remember { mutableStateOf(false) }
        var hasSizeError by remember { mutableStateOf(false) }

        var selectedItems by remember { mutableStateOf<List<BarcodesEntity>>(emptyList()) }

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
                        Text("Product Details")
                    },
                    actions = {},
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

                SimpleDropdown(
                    label = "Colour",
                    selectedItem = selectedColor,
                    items = dropdownColorItems,
                    modifier = maxWidth,
                    onSelected = { color ->
                        selectedColor = color
                    }
                )

                SimpleDropdown(
                    label = "Size",
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
                            Toast.makeText(context, "Select both Colour & Size", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Filter items based on selected color and size
                        val selectedItem = items.filter { item ->
                            item.color == selectedColor?.item?.color && item.size == selectedSize?.item?.size
                        }

                        if (selectedItem.isNotEmpty()) {
                            Toast.makeText(context, "Selected Item Found", Toast.LENGTH_SHORT).show()
                            selectedItems = selectedItem

                        } else {
                            Toast.makeText(context, "No items match the selected criteria", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Get Barcode")
                }

            }

        }
    }

    @Composable
    fun PrintBarcodes(items: List<BarcodesEntity>) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items.forEach { item ->
                if (!item.pieceBarcode.isNullOrEmpty()) {
                    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
                    bitmap.value = generateBarCode(item.pieceBarcode)

                    Card(
                        modifier = Modifier.padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Piece Barcode: ${item.pieceBarcode}", style = MaterialTheme.typography.bodyMedium)
                            bitmap.value?.asImageBitmap()?.let { bitmapImage ->
                                Image(
                                    bitmap = bitmapImage,
                                    contentDescription = "Generate Barcode Image for ${item.pieceBarcode}",
                                    modifier = Modifier.size(250.dp, 100.dp)
                                )
                            }
                        }
                    }
                }
                if (!item.gtin.isNullOrEmpty()) {
                    val bitmapGtin = remember { mutableStateOf<Bitmap?>(null) }
                    bitmapGtin.value = generateBarCode(item.gtin)

                    Card(
                        modifier = Modifier.padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("GTIN Barcode: ${item.gtin}", style = MaterialTheme.typography.bodyMedium)
                            bitmapGtin.value?.asImageBitmap()?.let { bitmapImage ->
                                Image(
                                    bitmap = bitmapImage,
                                    contentDescription = "Generate Barcode Image for ${item.gtin}",
                                    modifier = Modifier.size(250.dp, 100.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun generateBarCode(text: String): Bitmap {
        val width = 500
        val height = 150
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(
                text,
                BarcodeFormat.CODE_128,
                width,
                height
            )
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    bitmap.setPixel(x, y, color)
                }
            }
        } catch (e: WriterException) {
            Log.d("TAG", "generateBarCode: ${e.message}")
        }
        return bitmap
    }

    data class ColorDropdownItem(
        val item: BarcodesEntity,
        val colors: List<ColorsEntity>
    ) : DropdownItem {
        override val displayName: String
            get() = item.color
        override val id: Int
            get() = item.color.hashCode() // Or any unique integer ID
        override val extraData: Int? = null
        override val extraString: String? = colors.find { it.colorCode == item.color }?.colorName
    }

    data class SizeDropdownItem(
        val item: BarcodesEntity
    ) : DropdownItem {
        override val displayName: String
            get() = item.size
        override val id: Int
            get() = item.size.hashCode() // Or any unique integer ID
        override val extraData: Int? = null
        override val extraString: String? = null
    }

}
