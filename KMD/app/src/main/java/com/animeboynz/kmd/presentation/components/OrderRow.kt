package com.animeboynz.kmd.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.animeboynz.kmd.R
import com.animeboynz.kmd.database.entities.CustomerOrderEntity
import com.animeboynz.kmd.ui.theme.spacing

@Composable
fun OrderRow(
    order: CustomerOrderEntity,
    storeNumber: String,
    orderNumberDigits: String,
    onClick: () -> Unit,
) {
    var color = Color.DarkGray

    when (order.status) {
        "Not Ordered" -> color = Color.Red
        "Ordered" -> color = Color.Green
        "Arrived - Not Notified" -> color = Color.Red
        "Waiting for Pickup" -> color = Color.Yellow
        "Cancelled" -> color = Color.Gray
        "Completed" -> color = Color.DarkGray
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp)
            //.padding(horizontal = MaterialTheme.spacing.small)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .combinedClickable(
                onClick = onClick,
            )
            .padding(MaterialTheme.spacing.medium),
    ) {
        Column {
            Text(
                text = order.customerName + " • KMD-${storeNumber}-" + String.format("%0${orderNumberDigits}d", order.orderId),
                style = MaterialTheme.typography.titleMedium,
            )
            Row {
                Text(
                    text = stringResource(R.string.orders_customer_phone),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = " ",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = order.customerPhone,
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                )
            }
            Row {
                Text(
                    text = stringResource(R.string.orders_status),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = " ",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd),
            tint = MaterialTheme.colorScheme.outline,
        )
    }
}