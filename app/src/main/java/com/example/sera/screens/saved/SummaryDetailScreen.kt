package com.example.sera.screens.saved

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDetailScreen(
    summaryId: Long,
    onBack: () -> Unit
) {
    val viewModel: SummaryDetailViewModel = hiltViewModel()
    val summaryState by viewModel.summaryState.collectAsState()
    val deleteError by viewModel.deleteError.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Handle delete error
    LaunchedEffect(deleteError) {
        deleteError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearDeleteError()
        }
    }

    // Load summary data
    LaunchedEffect(summaryId) {
        viewModel.loadSummary(summaryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(summaryState.title.ifEmpty { "Loading..." }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = !summaryState.isLoading && summaryState.error.isEmpty()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Summary")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            when {
                summaryState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally))
                }

                summaryState.error.isNotEmpty() -> {
                    Text(
                        text = summaryState.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    // retry button if there's an error
                    Button(
                        onClick = { viewModel.loadSummary(summaryId) },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Retry")
                    }
                }

                else -> {
                    val paragraphs = summaryState.content.split("\n\n")
                    paragraphs.forEach { paragraph ->
                        when {
                            paragraph.startsWith("## ") -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = paragraph.substring(3),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            paragraph.startsWith("• ") -> {
                                Row(
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                                    )
                                    Text(
                                        text = paragraph.substring(2),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }

                            else -> {
                                Text(
                                    text = paragraph,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete \"${summaryState.title}\"?") },
            text = { Text("Are you sure you want to delete \"${summaryState.title}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteSummary(summaryId)
                    // Only navigate back if delete was successful
                    // The actual navigation will happen after the deletion is confirmed via LaunchedEffect
                    if (viewModel.deleteError.value == null) {
                        onBack()
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}