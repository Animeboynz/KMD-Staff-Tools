package com.animeboynz.kmd.ui.screens.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
import kotlin.text.toIntOrNull

class Takings : Screen() {

    @OptIn(ExperimentalMaterial3Api::class)
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

        var inputValues by rememberSaveable { mutableStateOf(CashCountData.currencyList.associateWith { "" }) }
        var errorMessages by remember { mutableStateOf(CashCountData.currencyList.associateWith { "" }) }

        val totalTakings = inputValues.entries.sumByDouble { (currency, quantity) ->
            val denominationValue = when {
                currency.endsWith(CashCountData.decimalSymbol) -> currency.replace(CashCountData.decimalSymbol, "").toDouble() / 100
                else -> currency.replace(CashCountData.wholeSymbol, "").toDouble()
            }
            (quantity.toIntOrNull() ?: 0) * denominationValue
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("${stringResource(R.string.tools_cash_count_remaining_takings)}: ${CashCountData.wholeSymbol}${"%.2f".format(CashCountData.bankingValue - totalTakings)}", fontSize = 24.sp)
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

                            val hasError = errorMessages[currency]?.isNotEmpty() == true

                            TextField(
                                value = inputValues[currency] ?: "",
                                onValueChange = { newValue ->
                                    if (newValue.all { char -> char.isDigit() }) {
                                        val newQuantity = newValue.toIntOrNull() ?: 0
                                        val maxQuantity = CashCountData.cashRowQuantities[currency] ?: 0

                                        if (newQuantity <= maxQuantity) {
                                            inputValues = inputValues.toMutableMap().apply { put(currency, newValue) }
                                            errorMessages = errorMessages.toMutableMap().apply { put(currency, "") }
                                        } else {
                                            errorMessages = errorMessages.toMutableMap().apply { put(currency, context.getString(R.string.tools_cash_count_max_takings, maxQuantity)) }
                                        }
                                    }
                                },
                                label = { Text(if (hasError) errorMessages[currency] ?: "" else stringResource(R.string.tools_cash_count_quantity)) },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                isError = hasError,
                                colors = TextFieldDefaults.colors(
                                    errorIndicatorColor = Color.Red,
                                    errorLabelColor = Color.Red,
                                    errorCursorColor = Color.Red,
                                ),
                                modifier = Modifier.weight(2f)
                            )


                            Spacer(modifier = Modifier.width(8.dp))

                            val denominationValue = when {
                                currency.endsWith(CashCountData.decimalSymbol) -> currency.replace(CashCountData.decimalSymbol, "").toDouble() / 100
                                else -> currency.replace(CashCountData.wholeSymbol, "").toDouble()
                            }
                            val total = (inputValues[currency]?.toIntOrNull() ?: 0) * denominationValue
                            Text(
                                text = "= ${CashCountData.wholeSymbol}${"%.2f".format(total)}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Display expected and current takings
                Text(
                    text = "${stringResource(R.string.tools_cash_count_expected_takings)}: ${CashCountData.wholeSymbol}${"%.2f".format(CashCountData.bankingValue)}",
                    fontSize = 20.sp
                )

                Text(
                    text = "${stringResource(R.string.tools_cash_count_current_takings)}: ${CashCountData.wholeSymbol}${"%.2f".format(totalTakings)}",
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
                    Text(text = stringResource(R.string.tools_cash_count_prev_count))
                }

                // Button to navigate to RemainingFloat
                Button(
                    onClick = {
                        CashCountData.takingsQuantities = inputValues.mapValues { it.value.toIntOrNull() ?: 0 }
                        navigator.push(RemainingFloat())
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(text = stringResource(R.string.tools_cash_count_calc_float))
                }
            }

        }
    }
}