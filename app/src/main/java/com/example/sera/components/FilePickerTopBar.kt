package com.example.sera.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilePickerTopBar(
    title: String,
    onPickPdf: () -> Unit,
    onBack: () -> Unit,
    isAddEnabled: Boolean = true
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { if (isAddEnabled) onBack() },) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(
                onClick = onPickPdf,
                enabled = isAddEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add File"
                )
            }
        }
    )
}