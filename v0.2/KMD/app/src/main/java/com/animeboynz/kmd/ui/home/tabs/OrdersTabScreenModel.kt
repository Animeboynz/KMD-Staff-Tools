package com.animeboynz.kmd.ui.home.tabs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.preferences.preference.asState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OrdersTabScreenModel(
    private val preferences: GeneralPreferences,
    private val customerOrderRepository: CustomerOrderRepository,
) : ScreenModel {

    var customerOrders = MutableStateFlow<List<CustomerOrderEntity>>(emptyList())

    init {
        getCustomerOrders()
    }

    fun getCustomerOrders() {
        screenModelScope.launch(Dispatchers.IO) {
            customerOrderRepository.getAllOrders().collect() { employeeList ->
                customerOrders.value = employeeList.filterNotNull() // Filter out nulls if any
            }
        }
    }

}
