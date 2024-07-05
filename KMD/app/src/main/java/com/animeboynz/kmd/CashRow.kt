package com.animeboynz.kmd

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val currencyList = listOf(
    "$100", "$50", "$20", "$10", "$5", "$2", "$1", "50c", "20c", "10c"
)


@Composable
fun CashRow(
    modifier: Modifier = Modifier,
    navigateToTakings: () -> Unit
) {
    var inputValues by rememberSaveable { mutableStateOf(currencyList.associateWith { "" }) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Cash counter",
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier.weight(1f)
            ) {
                items(currencyList) { currency ->
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
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
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

            Spacer(modifier = Modifier.height(20.dp))

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
                    navigateToTakings()
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Proceed to Takings")
            }

        }
    }
}
