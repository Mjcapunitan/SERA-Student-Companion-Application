package com.example.sera.components

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.sera.ui.theme.SeraTheme.colors

@Composable
fun AgendaGreeting(@StringRes greeting: Int, nickname: String?) {
    Text(
        text = "${stringResource(id = greeting)}${if (!nickname.isNullOrBlank()) ", $nickname!" else "!"}",
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
fun AgendaDateToday(date: String) {
    Text(
        text = date,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}