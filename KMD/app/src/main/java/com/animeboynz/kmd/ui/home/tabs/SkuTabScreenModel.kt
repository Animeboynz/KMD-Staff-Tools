package com.animeboynz.kmd.ui.home.tabs

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.preferences.preference.asState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SkuTabScreenModel(
    private val productsRepository: ProductsRepository,
) : ScreenModel {

    var allProducts = MutableStateFlow<List<ProductsEntity>>(emptyList())

    init {
        getProducts()
    }

    fun getProducts() {
        screenModelScope.launch(Dispatchers.IO) {
            productsRepository.getAllProducts().collect() { productsList ->
                allProducts.value = productsList
            }
        }
    }
}
