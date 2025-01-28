package com.animeboynz.kmd.ui.screens.tools

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.presentation.Screen
import org.koin.compose.koinInject
import kotlin.collections.component1
import kotlin.collections.component2

class RemainingFloat : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val preferences = koinInject<GeneralPreferences>()
        val countryCode = preferences.countryCode.get()

        val CashCountData: CashCountData = if (countryCode == "AU") {
            CashCountDataAU
        } else if (countryCode == "US" || countryCode == "CA") {
            CashCountDataUS
        } else if (countryCode == "DE" || countryCode == "FR") {
            CashCountDataDE
        } else if (countryCode == "GB") {
            CashCountDataGB
        } else {
            CashCountDataNZ
        }


        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Remaining Float")
                    },
                    actions = {
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                )
            }
        ) { paddingValues ->
            val paddingModifier = Modifier.padding(paddingValues)

            val remainingQuantities = CashCountData.cashRowQuantities.mapValues {
                it.value - (CashCountData.takingsQuantities[it.key] ?: 0)
            }

            val totalSum = remainingQuantities.entries.sumByDouble { (currency, quantity) ->
                val denominationValue = when {
                    currency.endsWith(CashCountData.decimalSymbol) -> currency.replace(CashCountData.decimalSymbol, "").toDouble() / 100
                    else -> currency.replace(CashCountData.wholeSymbol, "").toDouble()
                }
                quantity * denominationValue
            }

            Column(
                modifier = paddingModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.weight(1f)
                ) {
                    items(CashCountData.currencyList) { currency ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = currency,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = remainingQuantities[currency].toString(),
                                modifier = Modifier.weight(2f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            val denominationValue = when {
                                currency.endsWith(CashCountData.decimalSymbol) -> currency.replace(CashCountData.decimalSymbol, "").toDouble() / 100
                                else -> currency.replace(CashCountData.wholeSymbol, "").toDouble()
                            }
                            val total = remainingQuantities[currency]!! * denominationValue
                            Text(
                                text = "= ${CashCountData.wholeSymbol}${"%.2f".format(total)}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Display total sum at the bottom
                Text(
                    text = "Total: ${CashCountData.wholeSymbol}${"%.2f".format(totalSum)}",
                    fontSize = 20.sp
                )

                // Button to navigate back
                Button(
                    onClick = { navigator.pop() },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(text = "Back to Takings")
                }
                Button(
                    onClick = { navigator.pop(); navigator.pop();
                        CashCountData.takingsQuantities = CashCountData.currencyList.associateWith { 0 }.toMutableMap(); // Reset quantities
                        CashCountData.takingsQuantities = CashCountData.currencyList.associateWith { 0 }.toMutableMap();
                        CashCountData.totalValue = 0.0;
                        CashCountData.bankingValue = 0.0;
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(text = "Reset")
                }

            }
        }
    }
}