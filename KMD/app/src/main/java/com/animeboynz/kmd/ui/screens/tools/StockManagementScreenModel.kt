package com.animeboynz.kmd.ui.screens.tools

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.BoxEntity
import com.animeboynz.kmd.database.entities.StockCountEntity
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.StockCountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StockManagementScreenModel(
    private val stockCountRepository: StockCountRepository,
    private val barcodesRepository: BarcodesRepository,
) : ScreenModel {
    private val _openBox = MutableStateFlow<BoxEntity?>(null)
    val openBox: StateFlow<BoxEntity?> = _openBox.asStateFlow()

    private val _items = MutableStateFlow<List<StockCountEntity>>(emptyList())
    val items: StateFlow<List<StockCountEntity>> = _items.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _productNames = MutableStateFlow<Map<String, BarcodesEntity>>(emptyMap())
    val productNames: StateFlow<Map<String, BarcodesEntity>> = _productNames.asStateFlow()

    private var contentsJob: Job? = null

    fun processScan(barcode: String) {
        val trimmed = barcode.trim()
        if (trimmed.isEmpty()) return
        screenModelScope.launch(Dispatchers.IO) {
            val box = stockCountRepository.getBoxByBarcode(trimmed)
            if (box != null) {
                openBox(box)
                return@launch
            }
            val current = _openBox.value
            if (current == null) {
                _message.value = "Scan a box barcode first"
                return@launch
            }
            val product = barcodesRepository.findByBarcode(trimmed)
            if (product == null) {
                _message.value = "Unknown barcode"
                return@launch
            }
            rememberProduct(trimmed, product)
            addOne(current, trimmed)
        }
    }

    fun addOrIncrementProduct(productBarcode: String) {
        val current = _openBox.value ?: return
        screenModelScope.launch(Dispatchers.IO) {
            addOne(current, productBarcode)
        }
    }

    fun decrementProduct(productBarcode: String) {
        val current = _openBox.value ?: return
        screenModelScope.launch(Dispatchers.IO) {
            stockCountRepository.removeOneFromBox(current.barcode, productBarcode)
        }
    }

    fun removeProduct(productBarcode: String) {
        val current = _openBox.value ?: return
        screenModelScope.launch(Dispatchers.IO) {
            stockCountRepository.deleteItem(current.barcode, productBarcode)
        }
    }

    fun fetchProductDetails(barcode: String) {
        screenModelScope.launch(Dispatchers.IO) {
            if (_productNames.value.containsKey(barcode)) return@launch
            val product = barcodesRepository.findByBarcode(barcode) ?: return@launch
            rememberProduct(barcode, product)
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    private suspend fun addOne(box: BoxEntity, productBarcode: String) {
        val location = stockCountRepository.getLocation(box.locationId)?.name ?: ""
        stockCountRepository.addOneToBox(location, box.barcode, productBarcode)
    }

    private fun openBox(box: BoxEntity) {
        _openBox.value = box
        _message.value = "Opened ${box.barcode}"
        contentsJob?.cancel()
        contentsJob = screenModelScope.launch(Dispatchers.IO) {
            stockCountRepository.getInventoryByBoxId(box.barcode).collect { inventory ->
                _items.value = inventory
            }
        }
    }

    private fun rememberProduct(barcode: String, product: BarcodesEntity) {
        _productNames.value = _productNames.value + (barcode to product)
    }
}
