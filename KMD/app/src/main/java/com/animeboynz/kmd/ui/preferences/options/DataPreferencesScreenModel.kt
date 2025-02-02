package com.animeboynz.kmd.ui.preferences.options

import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.ColorsEntity
import com.animeboynz.kmd.domain.BackupData
import com.animeboynz.kmd.domain.BarcodesRepository
import com.animeboynz.kmd.domain.ColorsRepository
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.domain.ProductsRepository
import com.animeboynz.kmd.domain.toEntity
import com.animeboynz.kmd.domain.toSKUEntity
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

    fun importProducts(backupData: BackupData) {
        screenModelScope.launch(Dispatchers.IO) {
            colorsRepository.insertAll(backupData.colors.map { it.toEntity() })
            productsRepository.insertAll(backupData.barcodes.map { it.toSKUEntity() })
            barcodesRepository.insertAll(backupData.barcodes.map { it.toEntity() })
        }
    }
}
