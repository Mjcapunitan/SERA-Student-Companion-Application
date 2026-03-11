package com.example.sera.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.sera.utils.QuizAttempt
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizDetailScreen(
    navController: NavController,
    quizId: String,
    onBack: () -> Unit,
    viewModel: QuizDetailViewModel = hiltViewModel()
) {
    // Load quiz attempt based on ID
    LaunchedEffect(quizId) {
        viewModel.loadQuizAttempt(quizId)
    }

    val quizAttempt by viewModel.quizAttempt.collectAsState()
    val showDeleteDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Details") },
                navigationIcon = {
                    IconButton(onClick =onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog.value = true }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        quizAttempt?.let { attempt ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quiz summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = attempt.quizTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Date and time
                            val dateFormatter = SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                            Text(
                                text = "Taken on ${dateFormatter.format(attempt.timestamp)}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Score info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Score",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "${attempt.correctAnswers}/${attempt.totalQuestions}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Percentage",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    val percentage = if (attempt.totalQuestions > 0) {
                                        (attempt.correctAnswers.toFloat() / attempt.totalQuestions) * 100
                                    } else 0f
                                    Text(
                                        text = String.format("%.1f%%", percentage),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Time info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                val minutes = attempt.completionTimeSeconds / 60
                                val seconds = attempt.completionTimeSeconds % 60

                                Text(
                                    text = "Completion time: ${minutes}m ${seconds}s",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                if (attempt.timeoutExpired) {
                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            text = "Time Expired",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Question details header
                item {
                    Text(
                        text = "Question Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Question results
                itemsIndexed(attempt.questionResults) { index, result ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Question number and type
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Question ${index + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = result.questionType,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Question text
                            Text(
                                text = result.questionText,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Correct answer
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column {
                                    Text(
                                        text = "Correct Answer:",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = result.correctAnswer,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // User answer
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = if (result.isCorrect) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel,
                                    contentDescription = null,
                                    tint = if (result.isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column {
                                    Text(
                                        text = "Your Answer:",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = result.userAnswer ?: "Not answered",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (result.isCorrect)
                                            MaterialTheme.colorScheme.onSurface
                                        else
                                            MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            // Show time taken if available
                            result.timeTakenSeconds?.let { timeTaken ->
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Timer,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Text(
                                        text = "Time taken: ${timeTaken}s",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } ?: run {
            // Show loading or not found state if quiz attempt is null
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Delete Quiz Record") },
            text = { Text("Are you sure you want to delete this quiz record? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        quizAttempt?.id?.let { id ->
                            viewModel.deleteQuizAttempt(id)
                            navController.navigateUp()
                        }
                        showDeleteDialog.value = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}