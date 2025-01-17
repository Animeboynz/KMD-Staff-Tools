package com.animeboynz.kmd.ui.preferences.options

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataPreferencesScreenModel(
    private val preferences: GeneralPreferences,
    private val employeeRepository: EmployeeRepository,
    private val customerOrderRepository: CustomerOrderRepository,
) : ScreenModel {

    fun deleteAllOrders() {
        screenModelScope.launch(Dispatchers.IO) {
            customerOrderRepository.deleteAllOrders()
        }
    }

    fun deleteAllEmployees() {
        screenModelScope.launch(Dispatchers.IO) {
            employeeRepository.deleteAllEmployees()
        }
    }
}
