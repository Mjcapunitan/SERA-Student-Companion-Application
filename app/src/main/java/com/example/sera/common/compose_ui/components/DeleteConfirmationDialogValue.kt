package com.example.sera.common.compose_ui.components

sealed class DeleteConfirmationDialogValue<out T> {
    data class Visible<T>(val data: T) : DeleteConfirmationDialogValue<T>()
    object Hidden : DeleteConfirmationDialogValue<Nothing>()
}