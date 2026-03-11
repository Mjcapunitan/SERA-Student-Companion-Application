package com.example.sera.common.compose_ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sera.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldWithError(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onKeyboardAction: (() -> Unit)? = null,
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(text = labelText, color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            singleLine = true,
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions { onKeyboardAction?.invoke() },
            modifier = modifier.fillMaxWidth()
        )
        AnimatedVisibility(visible = isError) {
            Text(
                text = stringResource(id = R.string.this_field_is_required),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}