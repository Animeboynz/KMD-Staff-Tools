package com.animeboynz.kmd.ui.screens.tools

interface CashCountData {
    val currencyList: List<String>
    var totalValue: Double
    var bankingValue: Double
    val wholeSymbol: String
    val decimalSymbol: String
    var cashRowQuantities: Map<String, Int>
    var takingsQuantities: Map<String, Int>
}

object CashCountDataNZ : CashCountData {
    override val currencyList = listOf(
        "$100", "$50", "$20", "$10", "$5", "$2", "$1", "50c", "20c", "10c"
    )

    override var totalValue: Double = 0.0
    override var bankingValue: Double = 0.0
    override val wholeSymbol: String = "$"
    override val decimalSymbol: String = "c"
    override var cashRowQuantities: Map<String, Int> = currencyList.associateWith { 0 }
    override var takingsQuantities: Map<String, Int> = currencyList.associateWith { 0 }
}

object CashCountDataAU : CashCountData {
    override val currencyList = listOf(
        "$100", "$50", "$20", "$10", "$5", "$2", "$1", "50c", "20c", "10c", "5c"
    )

    override var totalValue: Double = 0.0
    override var bankingValue: Double = 0.0
    override val wholeSymbol: String = "$"
    override val decimalSymbol: String = "c"
    override var cashRowQuantities: Map<String, Int> = currencyList.associateWith { 0 }
    override var takingsQuantities: Map<String, Int> = currencyList.associateWith { 0 }
}

object CashCountDataUS : CashCountData {
    override val currencyList = listOf(
        "$100", "$50", "$20", "$10", "$5", "$2", "$1", "50c", "25c", "10c", "5c", "1c"
    )

    //US CA

    override var totalValue: Double = 0.0
    override var bankingValue: Double = 0.0
    override val wholeSymbol: String = "$"
    override val decimalSymbol: String = "c"
    override var cashRowQuantities: Map<String, Int> = currencyList.associateWith { 0 }
    override var takingsQuantities: Map<String, Int> = currencyList.associateWith { 0 }
}

object CashCountDataGB : CashCountData {
    override val currencyList = listOf(
        "£500", "£200" ,"£100", "£50", "£20", "£10", "£5", "£2", "£1", "50p", "20p", "10p", "5p", "2p", "1p"
    )
    //Pounds £ & Pence p

    override var totalValue: Double = 0.0
    override var bankingValue: Double = 0.0
    override val wholeSymbol: String = "£"
    override val decimalSymbol: String = "p"
    override var cashRowQuantities: Map<String, Int> = currencyList.associateWith { 0 }
    override var takingsQuantities: Map<String, Int> = currencyList.associateWith { 0 }
}

object CashCountDataDE : CashCountData {
    override val currencyList = listOf(
        "€500", "€200" ,"€100", "€50", "€20", "€10", "€5", "€2", "€1", "50c", "20c", "10c", "5c", "2c", "1c"
    )
    //Euro € & Cents c
    //Germany France

    override var totalValue: Double = 0.0
    override var bankingValue: Double = 0.0
    override val wholeSymbol: String = "€"
    override val decimalSymbol: String = "c"
    override var cashRowQuantities: Map<String, Int> = currencyList.associateWith { 0 }
    override var takingsQuantities: Map<String, Int> = currencyList.associateWith { 0 }
}
