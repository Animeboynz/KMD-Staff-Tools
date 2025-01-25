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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddOrderScreenModel(
    private val customerOrderRepository: CustomerOrderRepository,
    private val employeeRepository: EmployeeRepository,
    private val orderId: Long?
) : StateScreenModel<AddOrderScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data object Finished : State()
    }

    var status = MutableStateFlow<Status?>(null)

    var employees = MutableStateFlow<List<EmployeeEntity>>(emptyList())

    private val _order = MutableStateFlow<CustomerOrderEntity>(CustomerOrderEntity(0, "", "", "", "", "", "", "")) // Use nullable type
    val order: StateFlow<CustomerOrderEntity> = _order.asStateFlow() // Expose as StateFlow

    init {
        getActiveEmployees()
        if (orderId != null) {getOrder(orderId)}
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

    fun getOrder(orderId: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            val orderEntity = customerOrderRepository.getOrderById(orderId)
            _order.value = orderEntity // Update the StateFlow
        }
    }

    fun updateOrder(order: CustomerOrderEntity) {
        screenModelScope.launch(Dispatchers.IO) {
            customerOrderRepository.updateOrder(order)
        }
    }
}
