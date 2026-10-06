package com.animeboynz.kmd.ui.screens.tools

import android.database.sqlite.SQLiteConstraintException
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.domain.StockCountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BoxInventoryHubScreenModel(
    private val stockCountRepository: StockCountRepository,
) : ScreenModel {
    val locations = stockCountRepository.getLocations()
    val boxes = stockCountRepository.getBoxes()
    val dueBoxes = stockCountRepository.getBoxesDueForRecount(
        System.currentTimeMillis() - RECOUNT_AFTER_MS,
    )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun addBox(barcode: String, locationId: Long?) {
        val trimmed = barcode.trim()
        if (trimmed.isEmpty() || locationId == null) {
            _message.value = "Enter a box barcode and choose a location"
            return
        }
        screenModelScope.launch(Dispatchers.IO) {
            try {
                stockCountRepository.insertBox(trimmed, locationId)
                _message.value = "Box added"
            } catch (_: SQLiteConstraintException) {
                _message.value = "That box barcode is already in use"
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    companion object {
        const val RECOUNT_AFTER_MS = 30L * 24 * 60 * 60 * 1000
    }
}
