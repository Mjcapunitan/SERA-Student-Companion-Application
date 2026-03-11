package com.example.sera.screens.saved

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sera.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedSummariesScreen(
    viewModel: SavedSummariesViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSummaryClick: (Long) -> Unit
) {
    val summaries by viewModel.summaries.collectAsState(initial = emptyList())
    var selectedSummaryId by remember { mutableStateOf<Long?>(null) }
    val selectedSummary = summaries.find { it.id == selectedSummaryId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Summaries") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (summaries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_summaries_yet),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(summaries) { summary ->
                    SummaryCard(
                        title = summary.title,
                        date = summary.createdAt,
                        onClick = { onSummaryClick(summary.id) },
                        onLongPress = { selectedSummaryId = summary.id }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    selectedSummary?.let { summary ->
        AlertDialog(
            onDismissRequest = { selectedSummaryId = null },
            title = { Text("Delete \"${summary.title}\"?") },
            text = { Text("Are you sure you want to delete this summary? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSummary(summary.id)
                    selectedSummaryId = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSummaryId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    date: Date,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onClick() }
                )
            },
        colors = CardDefaults.outlinedCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateFormat.format(date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
