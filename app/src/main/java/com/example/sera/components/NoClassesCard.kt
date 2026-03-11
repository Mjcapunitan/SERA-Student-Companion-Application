package com.example.sera.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sera.ui.theme.SeraTheme
import com.example.sera.utils.Icons
import com.example.sera.R

@ExperimentalMaterial3Api
@Composable
fun NoClassesCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                imageVector = Icons.CalendarToday,
                contentDescription = null,
                tint = SeraTheme.colors.green,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.no_classes_today),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}