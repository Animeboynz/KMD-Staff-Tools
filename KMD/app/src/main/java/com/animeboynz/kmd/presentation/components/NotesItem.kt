package com.animeboynz.kmd.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.animeboynz.kmd.R
import com.animeboynz.kmd.ui.theme.spacing

@Composable
fun NotesItem(
    note: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.small)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .combinedClickable(
                onClick = onClick,
            )
            .padding(MaterialTheme.spacing.medium),
    ) {
        Column {
            if (note.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.orders_field_notes),
                    style = MaterialTheme.typography.titleMedium,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(), // Ensures the row spans the entire width
                    horizontalArrangement = Arrangement.SpaceBetween // This helps separate the text and icon
                ) {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f) // Ensures the text takes all available space
                    )
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.outline,
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.orders_field_notes_add),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        if (note.isEmpty()) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd),
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}