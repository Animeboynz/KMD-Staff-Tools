package com.animeboynz.kmd.presentation.components.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.animeboynz.kmd.R
import com.animeboynz.kmd.ui.theme.spacing

@Composable
fun OrderItemsHeader(onAddItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = MaterialTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Space between title and button
    ) {
        Text(
            text = stringResource(R.string.orders_items),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(end = MaterialTheme.spacing.large)
                .weight(1f) // Allows the title to take remaining space
        )
        IconButton(onClick = onAddItemClick) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.items_add))
        }
    }
}