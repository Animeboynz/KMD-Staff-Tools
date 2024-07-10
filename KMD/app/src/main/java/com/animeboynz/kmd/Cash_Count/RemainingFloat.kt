package com.animeboynz.kmd.Cash_Count

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RemainingFloatScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit
) {
    val remainingQuantities = AppData.cashRowQuantities.mapValues {
        it.value - (AppData.takingsQuantities[it.key] ?: 0)
    }

    val totalSum = remainingQuantities.entries.sumByDouble { (currency, quantity) ->
        val denominationValue = when {
            currency.endsWith("c") -> currency.replace("c", "").toDouble() / 100
            else -> currency.replace("$", "").toDouble()
        }
        quantity * denominationValue
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Remaining Float",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

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

                    Text(
                        text = remainingQuantities[currency].toString(),
                        modifier = Modifier.weight(2f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val denominationValue = when {
                        currency.endsWith("c") -> currency.replace("c", "").toDouble() / 100
                        else -> currency.replace("$", "").toDouble()
                    }
                    val total = remainingQuantities[currency]!! * denominationValue
                    Text(
                        text = "= $${"%.2f".format(total)}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Display total sum at the bottom
        Text(
            text = "Total: $${"%.2f".format(totalSum)}",
            fontSize = 20.sp
        )

        // Button to navigate back
        Button(
            onClick = { navigateBack() },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 0.dp)
        ) {
            Text(text = "Back to Takings")
        }
        Button(
            onClick = { navigateBack(); navigateBack();
                AppData.takingsQuantities = currencyList2.associateWith { 0 }.toMutableMap(); // Reset quantities
                AppData.takingsQuantities = currencyList2.associateWith { 0 }.toMutableMap();
                AppData.totalValue = 0.0;
                AppData.bankingValue = 0.0;
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