package com.example.sera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun DayItem(day: String, isSelected: Boolean) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 32.dp)
            .clip(shape)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = shape,
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
            .padding(12.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClickableDayItem(
    day: String,
    isSelected: Boolean,
    isDisabled: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 32.dp)
            .clip(shape)
            .clickable(enabled = !isDisabled, onClick = onClick)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = shape,
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = shape,
            )
            .padding(12.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}