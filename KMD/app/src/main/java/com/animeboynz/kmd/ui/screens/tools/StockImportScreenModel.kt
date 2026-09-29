package com.animeboynz.kmd.ui.screens.tools

import android.content.Context
import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.domain.StockCountRepository
import com.animeboynz.kmd.domain.StockImportRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StockImportScreenModel(
    private val context: Context,
    private val stockCountRepository: StockCountRepository,
) : ScreenModel {
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun import(uri: Uri) {
        screenModelScope.launch(Dispatchers.IO) {
            val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            if (text.isNullOrBlank()) {
                _message.value = "Could not read that file"
                return@launch
            }
            val parsed = parseCsv(text)
            val result = stockCountRepository.importRows(parsed.rows)
            val skipped = parsed.skipped + result.skipped
            _message.value = "Imported ${result.applied} rows, skipped $skipped"
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    private fun parseCsv(text: String): ParsedCsv {
        var skipped = 0
        val rows = mutableListOf<StockImportRow>()
        text.lineSequence().forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEach
            val parts = line.split(',').map { it.trim().trim('"') }
            if (parts.size < 4) {
                skipped++
                return@forEach
            }
            val header = parts[0].equals("box barcode", ignoreCase = true) || parts[0].equals("box", ignoreCase = true)
            if (header) return@forEach
            val quantity = parts[3].toIntOrNull()
            if (quantity == null) {
                skipped++
                return@forEach
            }
            rows += StockImportRow(parts[0], parts[1], parts[2], quantity)
        }
        return ParsedCsv(rows, skipped)
    }

    private data class ParsedCsv(val rows: List<StockImportRow>, val skipped: Int)
}
