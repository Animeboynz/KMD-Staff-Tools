package com.animeboynz.kmd.presentation.components.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import com.animeboynz.kmd.ui.theme.spacing
import me.zhanghai.compose.preference.TextFieldPreference

@Composable
fun <T> ReplacingTextFieldPreference(
    value: T,
    onValueChange: (T) -> Unit,
    title: String,
    description: String?,
    textToValue: (String) -> T?,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
) {
    TextFieldPreference(
        value = value,
        onValueChange = onValueChange,
        title = { Text(text = title) },
        summary = { Text(text = value.toString()) },
        textField = { value, onValueChange, onOk ->
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                Text(text = description ?: "")

                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    keyboardActions = KeyboardActions(onDone = { onOk() }),
                )
            }
        },
        textToValue = textToValue,
    )
}