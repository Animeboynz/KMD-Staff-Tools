package com.animeboynz.kmd.ui.screens.tools

object CashCountData {
    val currencyList = listOf(
        "$100", "$50", "$20", "$10", "$5", "$2", "$1", "50c", "20c", "10c"
    )
    var totalValue: Double = 0.0
    var bankingValue: Double = 0.0
    var cashRowQuantities: Map<String, Int> = currencyList.associateWith { 0 }
    var takingsQuantities: Map<String, Int> = currencyList.associateWith { 0 }
}