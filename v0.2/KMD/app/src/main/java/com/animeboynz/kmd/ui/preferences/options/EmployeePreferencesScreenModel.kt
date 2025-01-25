package com.animeboynz.kmd.ui.preferences.options

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.domain.EmployeeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EmployeePreferencesScreenModel(
    private val employeeRepository: EmployeeRepository,
) : StateScreenModel<EmployeePreferencesScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data object Finished : State()
    }
    var employeesActive = MutableStateFlow<List<EmployeeEntity>>(emptyList())
    var employeesDisabled = MutableStateFlow<List<EmployeeEntity>>(emptyList())

    init {
        getActiveEmployees()
        getDisabledEmployee()
    }

    fun getActiveEmployees() {
        screenModelScope.launch(Dispatchers.IO) {
            employeeRepository.getActiveEmployee().collect { employeeList ->
                employeesActive.value = employeeList.filterNotNull() // Filter out nulls if any
            }
        }
    }

    fun getDisabledEmployee() {
        screenModelScope.launch(Dispatchers.IO) {
            employeeRepository.getDisabledEmployee().collect { employeeList ->
                employeesDisabled.value = employeeList.filterNotNull() // Filter out nulls if any
            }
        }
    }

    fun changeEmployeeStatus(id:String, status: String) {
        screenModelScope.launch(Dispatchers.IO) {
            employeeRepository.updateEmployeeStatus(id, status)
        }
    }
}
