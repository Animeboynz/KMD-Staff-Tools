package com.animeboynz.kmd.ui.preferences.options

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.ColorsRepository
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.preferences.GeneralPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataPreferencesScreenModel(
    private val employeeRepository: EmployeeRepository,
    private val customerOrderRepository: CustomerOrderRepository,
    private val barcodesRepository: BarcodesRepository,
    private val productsRepository: ProductsRepository,
    private val colorsRepository: ColorsRepository,
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

    fun deleteAllProducts() {
        screenModelScope.launch(Dispatchers.IO) {
            barcodesRepository.deleteAllLines()
            productsRepository.deleteAllProducts()
            colorsRepository.deleteAllColors()
        }
    }
}
