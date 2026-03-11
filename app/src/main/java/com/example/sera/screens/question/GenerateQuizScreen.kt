package com.example.sera.screens.question

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sera.common.compose_ui.components.DropdownWithLabel
import com.example.sera.utils.GeneratedQuestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Function to check internet connectivity
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// Function to check if internet connection is slow
suspend fun isConnectionSlow(context: Context): Boolean {
    // Simple ping test to check connection speed
    val startTime = System.currentTimeMillis()
    try {

        delay(100) // Small delay to simulate network operation
        val endTime = System.currentTimeMillis()
        val pingTime = endTime - startTime


        return pingTime > 1500
    } catch (e: Exception) {
        return true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQuizScreen(
    navController: NavController,
    viewModel: GenerateQuizViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNavigateToSummarize: () -> Unit = {}
) {
    // Collecting states from the ViewModel
    val summaries by viewModel.summaries.collectAsState()
    val selectedQuizSelection = viewModel.selectedQuizSelection
    val generationState by viewModel.generationState.collectAsState()
    val generatedQuestions by viewModel.generatedQuestions.collectAsState()

    // State for timeout and slow connection handling
    var isProcessingRequest by remember { mutableStateOf(false) }
    var showSlowConnectionWarning by remember { mutableStateOf(false) }
    var progressValue by remember { mutableFloatStateOf(0f) }
    var timeoutCounter by remember { mutableIntStateOf(0) }
    val maxTimeout = 30 // 30 seconds timeout

    val coroutineScope = rememberCoroutineScope()

    // Get the current context for connectivity check
    val context = LocalContext.current

    // State for showing connectivity error dialog
    var showNoInternetDialog by remember { mutableStateOf(false) }
    var showSlowConnectionDialog by remember { mutableStateOf(false) }
    var showTimeoutWarningDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // No Internet Connection Dialog
    if (showNoInternetDialog) {
        AlertDialog(
            onDismissRequest = { showNoInternetDialog = false },
            icon = { Icon(Icons.Outlined.WifiOff, contentDescription = "No Internet") },
            title = { Text("No Internet Connection") },
            text = { Text("Please check your internet connection and try again. A stable internet connection is required to generate quiz questions.") },
            confirmButton = {
                Button(onClick = { showNoInternetDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Slow Connection Dialog
    if (showSlowConnectionDialog) {
        AlertDialog(
            onDismissRequest = { showSlowConnectionDialog = false },
            icon = { Icon(Icons.Outlined.NetworkCheck, contentDescription = "Slow Connection") },
            title = { Text("Slow Internet Connection") },
            text = {
                Column {
                    Text("Your internet connection appears to be slow. This may affect the question generation process.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("You can continue with the current connection or try again later when you have a better connection.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSlowConnectionDialog = false
                        // Continue with generation despite slow connection
                        coroutineScope.launch {
                            isProcessingRequest = true
                            progressValue = 0f
                            timeoutCounter = 0
                            viewModel.generateQuestions()

                            // Start timeout counter
                            while (isProcessingRequest && timeoutCounter < maxTimeout) {
                                delay(1000)
                                timeoutCounter++
                                progressValue = timeoutCounter.toFloat() / maxTimeout

                                // Check if generation completed
                                if (generationState is GenerateQuizViewModel.GenerationState.Success ||
                                    generationState is GenerateQuizViewModel.GenerationState.Error) {
                                    isProcessingRequest = false
                                    break
                                }

                                // Show warning after 15 seconds
                                if (timeoutCounter == 15 && isProcessingRequest) {
                                    showTimeoutWarningDialog = true
                                }

                                // If timeout reached, cancel the request
                                if (timeoutCounter >= maxTimeout && isProcessingRequest) {
                                    viewModel.cancelGeneration()
                                    isProcessingRequest = false
                                }
                            }
                        }
                    }
                ) {
                    Text("Continue Anyway")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSlowConnectionDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Time Warning Dialog - Replaces the inline card with a popup dialog
    if (showTimeoutWarningDialog) {
        AlertDialog(
            onDismissRequest = { },  // Empty to prevent dismissal by tapping outside
            icon = { Icon(Icons.Outlined.NetworkCheck, contentDescription = "Slow Connection") },
            title = { Text("Still working...") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "This is taking longer than expected. Your internet connection may be slow.",
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelGeneration()
                        showTimeoutWarningDialog = false
                        isProcessingRequest = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generate Questions") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Show form if no questions have been generated yet or in error state
            if (generationState !is GenerateQuizViewModel.GenerationState.Success) {
                // Show message if no summaries are available
                if (summaries.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Information",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "No summaries available",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Please create a summary first before generating quiz questions.",
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = onNavigateToSummarize,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Go to Summarize")
                                }
                            }
                        }
                    }
                } else {
                    // Handle QuotaExceeded state at the top level
                    if (generationState is GenerateQuizViewModel.GenerationState.QuotaExceeded) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = "Quota Exceeded",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(32.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Daily Quota Reached",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "You have reached the Daily Quiz Generation Quota. Try Again Tomorrow.",
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { viewModel.resetState() },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Reset")
                                    }
                                }
                            }
                        }
                    }

                    // Continue showing the form even in quota exceeded state
                    item {
                        // --- Summary Dropdown ---
                        DropdownWithLabel(
                            title = "Select Summary",
                            options = summaries.map { it.title },
                            selected = selectedQuizSelection.title,
                            onSelect = { viewModel.onSummarySelected(it) }
                        )
                    }

                    item {
                        // --- Question Type Dropdown ---
                        DropdownWithLabel(
                            title = "Question Type",
                            options = listOf("Multiple Choice", "True or False", "Fill in the Blanks"),
                            selected = selectedQuizSelection.questionType,
                            onSelect = { viewModel.onQuestionTypeSelected(it) }
                        )
                    }

                    item {
                        // --- Number of Questions Dropdown ---
                        DropdownWithLabel(
                            title = "Number of Questions",
                            options = listOf("5", "10", "15", "20", "25"),
                            selected = selectedQuizSelection.numberOfQuestions?.toString(),
                            onSelect = { viewModel.onNumberOfQuestionsSelected(it.toInt()) },

                        )
                    }


                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // --- Difficulty Dropdown ---
                            DropdownWithLabel(
                                title = "Difficulty",
                                options = listOf("Easy", "Medium", "Hard"),
                                selected = selectedQuizSelection.difficulty,
                                onSelect = { viewModel.onDifficultySelected(it) },
                                modifier = Modifier.weight(1f)
                            )

                            // --- Quiz Duration Dropdown ---
                            DropdownWithLabel(
                                title = "Quiz Duration",
                                options = listOf("5 minutes", "10 minutes", "15 minutes", "20 minutes"),
                                selected = selectedQuizSelection.duration,
                                onSelect = { viewModel.onDurationSelected(it) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        // Show loading or generate button based on state
                        when (generationState) {
                            is GenerateQuizViewModel.GenerationState.Loading -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Generating questions...",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    // Only show the cancel button if we're in a potentially slow connection situation
                                    if (isProcessingRequest) {
                                        Spacer(modifier = Modifier.height(16.dp))

                                        OutlinedButton(
                                            onClick = {
                                                viewModel.cancelGeneration()
                                                isProcessingRequest = false
                                                showTimeoutWarningDialog = false
                                            }
                                        ) {
                                            Text("Cancel")
                                        }
                                    }
                                }
                            }
                            is GenerateQuizViewModel.GenerationState.Error -> {
                                val errorState = generationState as GenerateQuizViewModel.GenerationState.Error

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Info,
                                            contentDescription = "Error",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(32.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Error Generating Questions",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = if (errorState.message.contains("timeout") ||
                                                errorState.message.contains("timed out"))
                                                "Request timed out. Your internet connection might be too slow."
                                            else
                                                errorState.message,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = {
                                                // Check internet connectivity before retrying
                                                if (isInternetAvailable(context)) {
                                                    coroutineScope.launch {
                                                        // Check if connection is slow
                                                        val isSlow = isConnectionSlow(context)
                                                        if (isSlow) {
                                                            showSlowConnectionDialog = true
                                                        } else {
                                                            // If connection is good, proceed with generation
                                                            isProcessingRequest = true
                                                            progressValue = 0f
                                                            timeoutCounter = 0
                                                            viewModel.generateQuestions()

                                                            // Start timeout counter
                                                            while (isProcessingRequest && timeoutCounter < maxTimeout) {
                                                                delay(1000)
                                                                timeoutCounter++
                                                                progressValue = timeoutCounter.toFloat() / maxTimeout

                                                                // Check if generation completed
                                                                if (generationState is GenerateQuizViewModel.GenerationState.Success ||
                                                                    (generationState is GenerateQuizViewModel.GenerationState.Error &&
                                                                            (generationState as GenerateQuizViewModel.GenerationState.Error).message != "Request timed out")) {
                                                                    isProcessingRequest = false
                                                                    break
                                                                }

                                                                // Show warning after 15 seconds
                                                                if (timeoutCounter == 15 && isProcessingRequest) {
                                                                    showTimeoutWarningDialog = true
                                                                }

                                                                // If timeout reached, cancel the request
                                                                if (timeoutCounter >= maxTimeout && isProcessingRequest) {
                                                                    viewModel.cancelGeneration()
                                                                    isProcessingRequest = false
                                                                    showTimeoutWarningDialog = false

                                                                    if (generationState is GenerateQuizViewModel.GenerationState.Loading) {
                                                                        viewModel.setTimeoutError()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    showNoInternetDialog = true
                                                }
                                            },
                                            enabled = selectedQuizSelection.title != null &&
                                                    selectedQuizSelection.questionType != null &&
                                                    selectedQuizSelection.difficulty != null &&
                                                    selectedQuizSelection.numberOfQuestions != null &&
                                                    selectedQuizSelection.duration != null,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            }
                            is GenerateQuizViewModel.GenerationState.QuotaExceeded -> {
                                // The button is disabled when quota is exceeded
                                Button(
                                    onClick = { },
                                    enabled = false,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Generate Questions")
                                }
                            }
                            else -> {
                                Button(
                                    onClick = {
                                        // Check internet connectivity before generating questions
                                        if (isInternetAvailable(context)) {
                                            coroutineScope.launch {
                                                // Check if connection is slow
                                                val isSlow = isConnectionSlow(context)
                                                if (isSlow) {
                                                    showSlowConnectionDialog = true
                                                } else {
                                                    // If connection is good, proceed with generation
                                                    isProcessingRequest = true
                                                    progressValue = 0f
                                                    timeoutCounter = 0
                                                    viewModel.generateQuestions()

                                                    // Start timeout counter
                                                    while (isProcessingRequest && timeoutCounter < maxTimeout) {
                                                        delay(1000)
                                                        timeoutCounter++
                                                        progressValue = timeoutCounter.toFloat() / maxTimeout

                                                        // Check if generation completed
                                                        if (generationState is GenerateQuizViewModel.GenerationState.Success ||
                                                            generationState is GenerateQuizViewModel.GenerationState.Error) {
                                                            isProcessingRequest = false
                                                            break
                                                        }

                                                        // Show warning after 15 seconds
                                                        if (timeoutCounter == 15 && isProcessingRequest) {
                                                            showTimeoutWarningDialog = true
                                                        }

                                                        // If timeout reached, cancel the request
                                                        if (timeoutCounter >= maxTimeout && isProcessingRequest) {
                                                            viewModel.cancelGeneration()
                                                            isProcessingRequest = false
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            showNoInternetDialog = true
                                        }
                                    },
                                    enabled = selectedQuizSelection.title != null &&
                                            selectedQuizSelection.questionType != null &&
                                            selectedQuizSelection.difficulty != null &&
                                            selectedQuizSelection.numberOfQuestions != null &&
                                            selectedQuizSelection.duration != null,

                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Generate Questions")
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "Generated Questions Preview",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Display questions based on their type
                items(generatedQuestions) { question ->
                    QuestionCard(
                        question = question,
                        index = generatedQuestions.indexOf(question),
                        showAnswers = false
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.navigateToQuiz(navController) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Quiz")
                    }
                }
            }
        }
    }
}
@Composable
fun QuestionCard(
    question: GeneratedQuestion,
    index: Int,
    showAnswers: Boolean = false // parameter to control answer visibility
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "${index + 1}. ${question.question}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (question.type) {
                "Multiple Choice" -> {
                    question.options.forEachIndexed { optionIndex, option ->
                        // Each option gets its own distinct row with proper spacing
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            // Enforce proper radio button spacing and layout
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 4.dp)
                            ) {
                                RadioButton(
                                    selected = showAnswers && optionIndex == question.correctAnswerIndex,
                                    onClick = null,
                                )
                            }

                            // Display option text with proper letter prefix
                            Text(
                                text = "${('A' + optionIndex)}) ${option.text}",
                                color = if (showAnswers && optionIndex == question.correctAnswerIndex)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Add small divider between options for visual clarity
                        if (optionIndex < question.options.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
                "True or False" -> {
                    // True option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 4.dp)
                        ) {
                            RadioButton(
                                selected = showAnswers && question.correctAnswerIndex == 0,
                                onClick = null
                            )
                        }
                        Text(
                            text = "True",
                            color = if (showAnswers && question.correctAnswerIndex == 0)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Add divider for visual separation
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    // False option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 4.dp)
                        ) {
                            RadioButton(
                                selected = showAnswers && question.correctAnswerIndex == 1,
                                onClick = null
                            )
                        }
                        Text(
                            text = "False",
                            color = if (showAnswers && question.correctAnswerIndex == 1)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                "Fill in the Blanks" -> {
                    if (showAnswers) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Answer: ${question.options.firstOrNull()?.text ?: ""}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}