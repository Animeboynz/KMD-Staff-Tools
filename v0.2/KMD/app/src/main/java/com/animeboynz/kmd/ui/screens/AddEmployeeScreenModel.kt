package com.animeboynz.kmd.ui.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.animeboynz.kmd.database.entities.EmployeeEntity
import com.animeboynz.kmd.domain.EmployeeRepository

class AddEmployeeScreenModel(
    private val employeeRepository: EmployeeRepository
) : StateScreenModel<AddEmployeeScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data object Finished : State()
    }

    fun addEmployees(employee: EmployeeEntity) {
        screenModelScope.launch(Dispatchers.IO) {
            employeeRepository.upsert(employee)
        }
    }
}
