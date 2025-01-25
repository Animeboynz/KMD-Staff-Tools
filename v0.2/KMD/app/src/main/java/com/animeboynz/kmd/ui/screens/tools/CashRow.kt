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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.ui.home.tabs.ToolsTab
import com.animeboynz.kmd.ui.home.tabs.ToolsTab.rememberImeState
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.text.toIntOrNull

class CashRow : Screen() {

    val currencyList = listOf(
        "$100", "$50", "$20", "$10", "$5", "$2", "$1", "50c", "20c", "10c"
    )

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Cash counter")
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

            val imeState = rememberImeState()
            val scrollState = rememberScrollState()
            var inputValues by rememberSaveable { mutableStateOf(ToolsTab.currencyList.associateWith { "" }) }

            Column(
                modifier = paddingModifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .imePadding(), // Ensures padding to handle the keyboard
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.weight(1f)
                ) {
                    items(ToolsTab.currencyList) { currency ->
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

                            TextField(
                                value = inputValues[currency] ?: "",
                                onValueChange = { newValue ->
                                    if (newValue.all { char -> char.isDigit() }) {
                                        inputValues = inputValues.toMutableMap().apply { put(currency, newValue) }
                                    }
                                },
                                label = { Text("Qty") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                ),
                                modifier = Modifier.weight(2f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            val denominationValue = when {
                                currency.endsWith("c") -> currency.replace("c", "").toDouble() / 100
                                else -> currency.replace("$", "").toDouble()
                            }
                            val total = (inputValues[currency]?.toIntOrNull() ?: 0) * denominationValue
                            Text(
                                text = "= $${"%.2f".format(total)}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Calculate total sum of all entered values
                val totalSum = inputValues.entries.sumByDouble { (currency, quantity) ->
                    val denominationValue = when {
                        currency.endsWith("c") -> currency.replace("c", "").toDouble() / 100
                        else -> currency.replace("$", "").toDouble()
                    }
                    (quantity.toIntOrNull() ?: 0) * denominationValue
                }

                // Display total sum at the bottom
                Text(
                    text = "Total: $${"%.2f".format(totalSum)}",
                    fontSize = 20.sp
                )

                // Calculate banking value
                AppData.bankingValue = (totalSum - 300).coerceAtLeast(0.0)

                // Display banking value
                Text(
                    text = "Banking: $${"%.2f".format(AppData.bankingValue)}",
                    fontSize = 20.sp
                )

                // Button to proceed to Takings
                Button(
                    onClick = {
                        AppData.totalValue = totalSum
                        AppData.cashRowQuantities = inputValues.mapValues { it.value.toIntOrNull() ?: 0 }
                        navigator.push(Takings())
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(text = "Proceed to Takings")
                }

            }

        }
    }
}