package com.animeboynz.kmd.presentation.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.animeboynz.kmd.database.entities.ProductsEntity

@Composable
fun ProductRow(
    product: ProductsEntity,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
            ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = product.sku,
                modifier = Modifier.weight(0.6f)
            )
            Text(
                text = product.name,
                modifier = Modifier.weight(2f)
            )
        }
    }
}