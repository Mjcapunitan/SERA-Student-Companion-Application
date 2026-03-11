package com.example.sera.common.compose_ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.sera.R

@Composable
fun <T> DeleteConfirmationDialog(
    title: @Composable (T) -> String,
    text: @Composable (T) -> String,
    state: DeleteConfirmationDialogState<T>,
    onConfirmDelete: (T) -> Unit
) {
    if (state.isVisible()) {
        (state.value as? DeleteConfirmationDialogValue.Visible<T>)?.let {
            AlertDialog(
                onDismissRequest = { state.hide() },
                title = { Text(text = title(it.data)) },
                text = { Text(text = text(it.data)) },
                confirmButton = {
                    TextButton(onClick = {
                        onConfirmDelete(it.data)
                        state.hide()
                    }) { Text(text = stringResource(id = R.string.delete)) }
                },
                dismissButton = {
                    TextButton(onClick = { state.hide() }) {
                        Text(
                            text = stringResource(
                                id = R.string.cancel
                            )
                        )
                    }
                },
            )
        }
    }
}