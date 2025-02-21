package com.animeboynz.kmd.ui.screens.tools

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.database.entities.StockCountEntity
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.domain.StockCountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StockManagementScreenModel(
    private val stockCountRepository: StockCountRepository,
    private val barcodesRepository: BarcodesRepository
) : ScreenModel {

    var offsiteItems = MutableStateFlow<List<StockCountEntity>>(emptyList())

    init {
        loadOffsiteInventory()
    }

    fun loadOffsiteInventory() {
        screenModelScope.launch(Dispatchers.IO) {
            stockCountRepository.getOffsiteInventory().collect { inventory ->
                offsiteItems.value = inventory
            }
        }
    }

    fun addOrIncrementProduct(productBarcode: String) {
        screenModelScope.launch(Dispatchers.IO) {
            val existingStock = offsiteItems.value.find { it.productBarcode == productBarcode }

            if (existingStock != null) {
                stockCountRepository.incrementStockCount("", productBarcode, 1)
            } else {
                fetchProductDetails(productBarcode)
                stockCountRepository.insertStockCount("Offsite", "", productBarcode, 1)
            }
            loadOffsiteInventory() // Refresh UI
        }
    }

    private val _productNames = MutableStateFlow<Map<String, BarcodesEntity>>(emptyMap())
    val productNames: StateFlow<Map<String, BarcodesEntity>> = _productNames.asStateFlow()

    fun fetchProductDetails(barcode: String) {
        screenModelScope.launch(Dispatchers.IO) {
            if (!_productNames.value.containsKey(barcode)) {
                val productName = barcodesRepository.getByBarcode(barcode)
                //R.string.item_name_unknown
                _productNames.value = _productNames.value + (barcode to productName)
            }
        }
    }

    fun decrementProduct(productBarcode: String) {
        screenModelScope.launch(Dispatchers.IO) {
            stockCountRepository.decrementStockCount("", productBarcode, 1)
            loadOffsiteInventory()
        }
    }

    fun removeProduct(productBarcode: String) {
        screenModelScope.launch(Dispatchers.IO) {
            stockCountRepository.clearOffsiteInventoryByBarcode(productBarcode)
            loadOffsiteInventory()
        }
    }
}