package com.animeboynz.kmd.ui.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.domain.EmployeeRepository
import com.animeboynz.kmd.domain.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddOrderScreenModel(
    private val customerOrderRepository: CustomerOrderRepository,
    private val employeeRepository: EmployeeRepository
) : StateScreenModel<AddOrderScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data object Finished : State()
    }

    var status = MutableStateFlow<Status?>(null)

    var employees = MutableStateFlow<List<EmployeeEntity>>(emptyList())

    init {
        getActiveEmployees()
    }

    fun getActiveEmployees() {
        screenModelScope.launch(Dispatchers.IO) {
            employeeRepository.getActiveEmployee().collect { employeeList ->
                employees.value = employeeList.filterNotNull() // Filter out nulls if any
            }
        }
    }

    fun addOrder(order: CustomerOrderEntity) {
        screenModelScope.launch(Dispatchers.IO) {
            customerOrderRepository.insertOrder(order)
        }
    }
}
