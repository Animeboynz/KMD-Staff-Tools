package com.animeboynz.kmd.Cash_Count

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val currencyList2 = listOf(
    "$100", "$50", "$20", "$10", "$5", "$2", "$1", "50c", "20c", "10c"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakingsScreen(
    modifier: Modifier = Modifier,
    navigateToRemainingFloat: () -> Unit,
    navigateBack: () -> Unit
) {
    var inputValues by rememberSaveable { mutableStateOf(currencyList2.associateWith { "" }) }
    var errorMessages by remember { mutableStateOf(currencyList2.associateWith { "" }) }

    val totalTakings = inputValues.entries.sumByDouble { (currency, quantity) ->
        val denominationValue = when {
            currency.endsWith("c") -> currency.replace("c", "").toDouble() / 100
            else -> currency.replace("$", "").toDouble()
        }
        (quantity.toIntOrNull() ?: 0) * denominationValue
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Left to take: $${"%.2f".format(AppData.bankingValue - totalTakings)}",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.weight(1f)
        ) {
            items(currencyList2) { currency ->
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
                                val maxQuantity = AppData.cashRowQuantities[currency] ?: 0

                                if (newQuantity <= maxQuantity) {
                                    inputValues = inputValues.toMutableMap().apply { put(currency, newValue) }
                                    errorMessages = errorMessages.toMutableMap().apply { put(currency, "") }
                                } else {
                                    errorMessages = errorMessages.toMutableMap().apply { put(currency, "Cannot take more than $maxQuantity") }
                                }
                            }
                        },
                        label = { Text(if (hasError) errorMessages[currency] ?: "" else "Qty") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        isError = hasError,
                        colors = TextFieldDefaults.textFieldColors(
                            errorLabelColor = Color.Red,
                            errorCursorColor = Color.Red,
                            errorIndicatorColor = Color.Red
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

        // Display expected and current takings
        Text(
            text = "Expected Takings: $${"%.2f".format(AppData.bankingValue)}",
            fontSize = 20.sp
        )

        Text(
            text = "Current Takings: $${"%.2f".format(totalTakings)}",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Button to navigate back
        Button(
            onClick = { navigateBack() },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 0.dp)
        ) {
            Text(text = "Back to Cash Counter")
        }

        // Button to navigate to RemainingFloat
        Button(
            onClick = {
                AppData.takingsQuantities = inputValues.mapValues { it.value.toIntOrNull() ?: 0 }
                navigateToRemainingFloat()
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(text = "Calculate Remaining Float")
        }
    }
}
