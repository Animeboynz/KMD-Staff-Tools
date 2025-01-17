package com.animeboynz.kmd.ui.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.domain.CustomerOrderRepository
import com.animeboynz.kmd.presentation.components.DropdownItem

class AddOrderScreenModel(
    private val customerOrderRepository: CustomerOrderRepository,
) : StateScreenModel<AddOrderScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data object Finished : State()
    }

    enum class Status(
        override val displayName: String,
        override val id: Int,
        override val extraData: Int?,
    ) : DropdownItem {
        NOT_ORDERED("Not Ordered", 0, null),
        ORDERED("Ordered", 1, null),
        ARRIVED_NOT_NOTIFIED("Arrived - Not Notified", 2, null),
        WAITING_FOR_PICKUP("Waiting for Pickup", 3, null),
        CANCELLED("Cancelled", 4, null),
        COMPLETED("Completed", 5, null);

        companion object {
            val entries: List<Status> = values().toList()
        }
    }

    var status = MutableStateFlow<Status?>(null)

    fun addOrder(order: CustomerOrderEntity) {
        screenModelScope.launch(Dispatchers.IO) {
            customerOrderRepository.insertOrder(order)
        }
    }


}
