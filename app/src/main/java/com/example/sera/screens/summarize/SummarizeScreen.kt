package com.example.sera.screens.summarize

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sera.components.FilePickerTopBar
import com.ml.quaterion.text2summary.Text2Summary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import android.content.Context
import android.net.Uri

fun getFileNameFromUri(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && it.moveToFirst()) {
            it.getString(nameIndex)
        } else {
            null
        }
    }
}

@Composable
fun SummarizeScreen(
    navController: NavController,
    viewModel: SummarizeViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val text by viewModel.text.collectAsState()
    var compressionRate by remember { mutableStateOf(0.6f) }
    val context = LocalContext.current
    val fileTextExtractor = remember { FileTextExtractor(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsState()
    val wordCount = text.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
    var showInfoDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            coroutineScope.launch(Dispatchers.IO) {
                viewModel.setLoading(true, 0f)
                try {
                    if (uris.isNotEmpty()) {
                        val extractedText = fileTextExtractor.extractTextFromFiles(uris)
                        val fileName = getFileNameFromUri(context, uris.first()) ?: "Untitled"

                        withContext(Dispatchers.Main) {
                            viewModel.setText(extractedText)
                            viewModel.setFileName(fileName)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        snackbarHostState.showSnackbar("Error extracting text: ${e.message}")
                    }
                } finally {
                    viewModel.setLoading(false)
                }
            }
        }
    )

    Scaffold(
        topBar = {
            FilePickerTopBar(
                title = "Summarize Lecture",
                onPickPdf = {
                    if (!isLoading) filePickerLauncher.launch(arrayOf(
                        "application/pdf",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "image/jpeg",
                        "image/png",
                        "image/jpg"
                    ))
                },
                onBack = onBack,
                isAddEnabled = !isLoading
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Input text area with transparent background
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    TextField(
                        value = text,
                        onValueChange = { viewModel.setText(it) },
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        placeholder = { Text("Input text to summarize (minimum 100 words)") },
                        enabled = !isLoading,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        )
                    )
                }
            }

            // Summary length control with info icon
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Column {
                        Row (verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)){
                            Text(
                                text = if (compressionRate == 1.0f) "Summary Length: 100% (Original Text)" else "Summary Length: ${(compressionRate * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            IconButton(
                                onClick = { showInfoDialog = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Information about summary length",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = if (compressionRate == 1.0f)
                                    "Note: At 100%, the original text is preserved with all formatting intact."
                                else
                                    "Note: This percentage represents how much of the original content is kept. Higher values preserve more information.",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Slider(
                    value = compressionRate,
                    onValueChange = { compressionRate = it.coerceIn(0.4f, 1.0f) },
                    valueRange = 0.4f..1.0f,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "40%",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Shorter",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "100%",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Original",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.setText("") },
                    enabled = !isLoading
                ) {
                    Text("Clear")
                }
                Button(
                    onClick = {
                        if (text.trim().isEmpty()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("There is no text to process")
                            }
                        } else if (wordCount < 100) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Text must be at least 100 words")
                            }
                        } else {
                            coroutineScope.launch(Dispatchers.IO) {
                                viewModel.setLoading(true, 0f)

                                try {
                                    // Check if fileName is empty (meaning user typed directly)
                                    if (viewModel.fileName.value.isEmpty()) {
                                        // Generate title from first few words of text
                                        val generatedTitle = viewModel.generateDefaultTitle(text)
                                        viewModel.setFileName(generatedTitle)
                                    }

                                    if (compressionRate == 1.0f) {
                                        // For 100%, bypass all processing to preserve formatting
                                        SummaryDataStore.setSummary(text)
                                        SummaryDataStore.setFileName(viewModel.fileName.value)
                                    } else {
                                        // For summarization, process as before
                                        val rawSummary = Text2Summary.summarize(text, compressionRate)
                                        val cleanedSummary = viewModel.setSummarizedText(rawSummary)
                                        SummaryDataStore.setSummary(cleanedSummary)
                                        SummaryDataStore.setFileName(viewModel.fileName.value)
                                    }

                                    withContext(Dispatchers.Main) {
                                        val encodedFileName = URLEncoder.encode(viewModel.fileName.value, StandardCharsets.UTF_8.toString())
                                        navController.navigate("summaryResult/$encodedFileName")
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        snackbarHostState.showSnackbar("Error processing text: ${e.message}")
                                    }
                                } finally {
                                    viewModel.setLoading(false)
                                }
                            }
                        }
                    },
                    enabled = !isLoading && wordCount >= 100
                ) {
                    Text(if (compressionRate == 1.0f) "Use Original" else "Summarize")
                }
            }
        }
    }

    // Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("About Summary Length") },
            text = {
                Column {
                    Text(
                        text = "Higher percentage = longer, more detailed summary",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "This percentage represents how much of the original content is kept:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "• Below 50%: Very concise summary with only essential information",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "• 50% - 70%: Balanced summary retaining important details",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "• 70% - 90%: Comprehensive summary preserving most content",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "• 100%: Original text with all formatting preserved",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "When set to 100%, the text will be used exactly as entered without any processing or summarization, preserving all original formatting.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }

    // Loading overlay
    if (isLoading) {
        var loadingText by remember { mutableStateOf("Processing Text") }

        LaunchedEffect(isLoading) {
            while (isLoading) {
                loadingText = when (loadingText) {
                    "Processing Text" -> "Processing Text."
                    "Processing Text." -> "Processing Text.."
                    "Processing Text.." -> "Processing Text..."
                    else -> "Processing Text"
                }
                delay(500) // Update text every 500ms
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = loadingText,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}