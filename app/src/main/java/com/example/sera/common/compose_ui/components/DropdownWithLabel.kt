package com.example.sera.common.compose_ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DropdownWithLabel(
    title: String,
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
    onOpen: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    // Track the size of the TextField to match the dropdown width to it
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val density = LocalDensity.current

    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
                if (expanded) onOpen?.invoke()
            }
        ) {
            TextField(
                value = selected ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(title) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .onSizeChanged { size ->
                        // Store the size of the TextField
                        textFieldSize = Size(size.width.toFloat(), size.height.toFloat())
                    }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                // Use the width of the text field for the dropdown menu
                modifier = Modifier.width(with(density) { textFieldSize.width.toDp() })
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}