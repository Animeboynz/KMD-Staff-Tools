package com.animeboynz.kmd.ui.preferences.options

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.animeboynz.kmd.domain.EmployeeRepository

class EmployeePreferencesScreenModel(
    private val employeeRepository: EmployeeRepository,
) : StateScreenModel<EmployeePreferencesScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data object Finished : State()
    }

    fun getActiveEmployees() {
        screenModelScope.launch(Dispatchers.IO) {
            employeeRepository.getActiveEmployee()
        }
    }

    fun getDisabledEmployee() {
        screenModelScope.launch(Dispatchers.IO) {
            employeeRepository.getDisabledEmployee()
        }
    }
}
