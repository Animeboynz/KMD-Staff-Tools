package com.animeboynz.kmd.ui.screens

import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.github.k1rakishou.fsaf.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomerOrderScreenModel(
    private val customerOrderRepository: CustomerOrderRepository,
) : ScreenModel {

    fun deleteOrder(orderId: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            customerOrderRepository.deleteOrder(orderId)
        }
    }
}
