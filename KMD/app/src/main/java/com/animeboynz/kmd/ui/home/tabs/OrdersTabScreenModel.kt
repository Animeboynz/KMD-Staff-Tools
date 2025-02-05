package com.animeboynz.kmd.ui.home.tabs

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OrdersTabScreenModel(
    private val preferences: GeneralPreferences,
    private val customerOrderRepository: CustomerOrderRepository,
    private val employeeRepository: EmployeeRepository,
) : ScreenModel {

    var customerOrders = MutableStateFlow<List<CustomerOrderEntity>>(emptyList())
    var employees = MutableStateFlow<List<EmployeeEntity>>(emptyList())

    init {
        getCustomerOrders()
        getAllEmployees()
    }

    fun getCustomerOrders() {
        screenModelScope.launch(Dispatchers.IO) {
            customerOrderRepository.getAllOrders().collect() { employeeList ->
                customerOrders.value = employeeList.filterNotNull() // Filter out nulls if any
            }
        }
    }

    fun getAllEmployees() {
        screenModelScope.launch(Dispatchers.IO) {
            employeeRepository.getAllEmployees().collect { employeeList ->
                employees.value = employeeList.filterNotNull()
            }
        }
    }

}
