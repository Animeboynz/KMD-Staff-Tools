package com.animeboynz.kmd.ui.screens.tools

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
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

        val CashCountData: CashCountData = when (countryCode) {
            "AU" -> CashCountDataAU
            "US", "CA" -> CashCountDataUS
            "DE", "FR" -> CashCountDataDE
            "GB" -> CashCountDataGB
            else -> CashCountDataNZ
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(stringResource(R.string.tools_cash_count_remaining_float))
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

            val scrollState = rememberScrollState()

            Column(
                modifier = paddingModifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                ) {
                    items(remainingQuantities.entries.toList()) { (currency, quantity) ->
                        val denominationValue = if (currency.endsWith(CashCountData.decimalSymbol)) {
                            currency.replace(CashCountData.decimalSymbol, "").toDouble() / 100
                        } else {
                            currency.replace(CashCountData.wholeSymbol, "").toDouble()
                        }
                        val total = quantity * denominationValue

                        RemainingFloatCard(currency, quantity, total, CashCountData.wholeSymbol)
                    }
                }


                Spacer(modifier = Modifier.height(10.dp))

                // Display total sum at the bottom
                Text(
                    text = "${stringResource(R.string.tools_cash_count_total)}: ${CashCountData.wholeSymbol}${"%.2f".format(totalSum)}",
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Button to navigate back
                Button(
                    onClick = { navigator.pop() },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(text = stringResource(R.string.tools_cash_count_prev_takings))
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
                    Text(text = stringResource(R.string.tools_cash_count_reset))
                }

            }
        }
    }

    @Composable
    fun RemainingFloatCard(
        currency: String,
        quantity: Int,
        total: Double,
        wholeSymbol: String
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currency,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Qty: $quantity",
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "= ${wholeSymbol}${"%.2f".format(total)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}