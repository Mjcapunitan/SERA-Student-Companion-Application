package com.example.sera.common.compose_ui.components

import androidx.compose.runtime.*

class DeleteConfirmationDialogState<T>(initialValue: DeleteConfirmationDialogValue<T>) {

    var value by mutableStateOf<DeleteConfirmationDialogValue<T>>(initialValue)

    fun isVisible() = value is DeleteConfirmationDialogValue.Visible
    fun isHidden() = value == DeleteConfirmationDialogValue.Hidden

    fun show(data: T) {
        value = DeleteConfirmationDialogValue.Visible(data)
    }

    fun hide() {
        value = DeleteConfirmationDialogValue.Hidden
    }
}

@Composable
fun <T> rememberDeleteConfirmationDialogState(initialValue: DeleteConfirmationDialogValue<T> = DeleteConfirmationDialogValue.Hidden) =
    remember {
        DeleteConfirmationDialogState(initialValue)
    }