package com.animeboynz.kmd.ui.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.BarcodesEntity
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.OrderItemRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.domain.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProductScreenModel(
    private val barcodesRepository: BarcodesRepository,
    private val sku: String,
) : StateScreenModel<ProductScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data object Finished : State()
    }

    var product = MutableStateFlow<List<BarcodesEntity>>(emptyList())

    init {
        getProductName(sku)
    }

    fun getProductName(sku: String) {
        screenModelScope.launch(Dispatchers.IO) {
            barcodesRepository.getBySku(sku).collect { items ->
                product.value = items
            }
        }
    }
}
