package com.animeboynz.kmd.ui.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.animeboynz.kmd.database.entities.OrderItemEntity
import com.animeboynz.kmd.database.entities.ProductsEntity
import com.animeboynz.kmd.domain.OrderItemRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.domain.Status

class AddItemScreenModel(
    private val orderItemRepository: OrderItemRepository,
    private val productsRepository: ProductsRepository,
) : StateScreenModel<AddItemScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data object Finished : State()
    }

    var status = MutableStateFlow<Status?>(null)

    var productName = MutableStateFlow<ProductsEntity?>(null)

    fun addOrderItem(orderItem: OrderItemEntity) {
        screenModelScope.launch(Dispatchers.IO) {
            orderItemRepository.insertOrderItem(orderItem)
        }
    }

    fun getProductName(sku: String) {
        screenModelScope.launch(Dispatchers.IO) {
            productName.value = productsRepository.getProductName(sku)
        }
    }
}
