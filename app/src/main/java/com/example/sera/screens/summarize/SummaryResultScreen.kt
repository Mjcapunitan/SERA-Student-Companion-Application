package com.example.sera.screens.summarize

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryResultScreen(
    summary: String,
    fileName: String,
    onBack: () -> Unit,
    onSaveDone: () -> Unit = {}
) {
    val decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString())
    val viewModel: SummaryResultViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$decodedFileName Summary") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.saveSummary(decodedFileName, summary)
                            snackbarHostState.showSnackbar("Summary saved")
                            onSaveDone()
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save Summary")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val paragraphs = summary.split("\n\n")
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
                        Row(modifier = Modifier.padding(bottom = 8.dp)) {
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
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
