package com.animeboynz.kmd.ui.screens.tools

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.domain.BoxContentMatch
import com.animeboynz.kmd.domain.StockCountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StockSearchScreenModel(
    private val stockCountRepository: StockCountRepository,
) : ScreenModel {
    private val _results = MutableStateFlow<List<BoxContentMatch>>(emptyList())
    val results: StateFlow<List<BoxContentMatch>> = _results.asStateFlow()

    private val _searched = MutableStateFlow(false)
    val searched: StateFlow<Boolean> = _searched.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _results.value = emptyList()
            _searched.value = false
            return
        }
        searchJob = screenModelScope.launch(Dispatchers.IO) {
            _results.value = stockCountRepository.searchBoxes(query)
            _searched.value = true
        }
    }
}
