package com.example.sera.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.sera.utils.QuizAttempt
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizStatisticsScreen(
    navController: NavController,
    onBack: () -> Unit,
    viewModel: QuizStatisticsViewModel = hiltViewModel()
) {
    val quizAttempts by viewModel.quizAttempts.collectAsState(initial = emptyList())
    val totalQuizzesCompleted by viewModel.totalQuizzesCompleted.collectAsState(initial = 0)
    val averageScore by viewModel.averageScore.collectAsState(initial = 0.0)
    val totalQuestionsAnswered by viewModel.totalQuestionsAnswered.collectAsState(initial = 0)
    val totalCorrectAnswers by viewModel.totalCorrectAnswers.collectAsState(initial = 0)
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Clear Statistics") },
            text = { Text("Are you sure you want to clear all quiz statistics? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack ) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats grid with 2 rows, 2 columns
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Total quizzes
                            StatCard(
                                icon = Icons.Outlined.Quiz,
                                title = "Total Quizzes",
                                value = totalQuizzesCompleted.toString(),
                                modifier = Modifier.weight(1f)
                            )

                            // Average score
                            StatCard(
                                icon = Icons.Outlined.BarChart,
                                title = "Average Score",
                                value = String.format("%.1f%%", averageScore * 100),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Questions answered
                            StatCard(
                                icon = Icons.Outlined.QuestionAnswer,
                                title = "Questions",
                                value = "$totalCorrectAnswers/$totalQuestionsAnswered",
                                modifier = Modifier.weight(1f)
                            )

                            // Success rate
                            StatCard(
                                icon = Icons.Outlined.CheckCircle,
                                title = "Success Rate",
                                value = if (totalQuestionsAnswered > 0) {
                                    String.format("%.1f%%",
                                        (totalCorrectAnswers.toDouble() / totalQuestionsAnswered) * 100)
                                } else "0%",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Recent quizzes header
            item {
                Text(
                    text = "Recent Quizzes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // List of quiz attempts
            if (quizAttempts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No quiz attempts yet",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            } else {
                items(quizAttempts) { quizAttempt ->
                    QuizAttemptCard(quizAttempt = quizAttempt) {
                        // Navigate to quiz detail screen
                        navController.navigate("quiz_detail/${quizAttempt.id}")
                    }
                }
            }

            // Add clear data option at the bottom
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Clear Data")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear All Statistics")
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuizAttemptCard(
    quizAttempt: QuizAttempt,
    onClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = quizAttempt.quizTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = dateFormatter.format(quizAttempt.timestamp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Score: ${quizAttempt.correctAnswers}/${quizAttempt.totalQuestions}",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Calculate percentage
                val percentage = if (quizAttempt.totalQuestions > 0) {
                    (quizAttempt.correctAnswers.toFloat() / quizAttempt.totalQuestions) * 100
                } else 0f

                Text(
                    text = String.format("%.1f%%", percentage),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(4.dp))

                val minutes = quizAttempt.completionTimeSeconds / 60
                val seconds = quizAttempt.completionTimeSeconds % 60

                Text(
                    text = String.format("%d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (quizAttempt.timeoutExpired) {
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
