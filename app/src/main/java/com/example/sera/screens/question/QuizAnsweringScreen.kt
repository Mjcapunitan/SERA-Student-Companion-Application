package com.example.sera.screens.question

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sera.utils.GeneratedQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizAnsweringScreen(
    navController: NavController,
    viewModel: QuizAnsweringViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val questions by viewModel.questions.collectAsState()
    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
    val quizSubmitted by viewModel.quizSubmitted.collectAsState()
    val correctAnswers by viewModel.correctAnswers.collectAsState()
    val inReviewMode by viewModel.inReviewMode.collectAsState()
    val remainingTimeInSeconds by viewModel.remainingTimeInSeconds.collectAsState()

    // Format remaining time as mm:ss
    val formattedTime = remember(remainingTimeInSeconds) {
        val minutes = remainingTimeInSeconds / 60
        val seconds = remainingTimeInSeconds % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    // Define a back action that navigates to generate_quiz with reset flag
    val navigateBackToGenerator = {
        navController.navigate("generate_quiz?reset=true") {
            // Clear the back stack to prevent building up navigation history
            popUpTo("generate_quiz") { inclusive = true }
        }
    }

    // Show results dialog when quiz is submitted
    if (quizSubmitted) {
        ResultsDialog(
            correctCount = correctAnswers,
            totalCount = questions.size,
            onDismiss = navigateBackToGenerator,
            onCancel = {
                // Close the dialog but keep showing the results screen
                viewModel.setQuizReviewMode()
            }
        )
    }

    // Effect to handle timer expiration
    LaunchedEffect(remainingTimeInSeconds) {
        if (remainingTimeInSeconds <= 0 && !quizSubmitted && !inReviewMode) {
            // Time's up! Submit the quiz automatically
            viewModel.forceSubmitOnTimeout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    // Only show back button when quiz is submitted
                    if (quizSubmitted || inReviewMode) {
                        IconButton(onClick = navigateBackToGenerator) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    // Show timer only when quiz is active
                    if (!quizSubmitted && !inReviewMode) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = "Time Remaining"
                            )
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (remainingTimeInSeconds < 60)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // Add back handler to intercept system back button
        BackHandler(enabled = !quizSubmitted) {
            // Do nothing when back is pressed if quiz is not submitted
            // This prevents user from going back before completing the quiz
        }

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header section
            item {
                Text(
                    text = "Answer the following questions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Questions section - display each question with selection capability
            items(questions) { question ->
                val questionIndex = questions.indexOf(question)
                AnswerableQuestionCard(
                    question = question,
                    index = questionIndex,
                    selectedAnswerIndex = selectedAnswers[questionIndex] ?: -1,
                    showCorrectAnswer = quizSubmitted,
                    inReviewMode = inReviewMode,  // Pass the inReviewMode parameter here
                    onAnswerSelected = { answerIndex ->
                        viewModel.selectAnswer(questionIndex, answerIndex)
                    }
                )
            }

            // Footer section - Submit button only (results are in dialog)
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Only show Submit button when NOT in review mode and quiz is NOT submitted
                if (!quizSubmitted && !inReviewMode) {
                    Button(
                        onClick = { viewModel.submitQuiz() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedAnswers.size == questions.size
                    ) {
                        Text("Submit Answers")
                    }
                }


                if (inReviewMode) {
                    Button(
                        onClick = navigateBackToGenerator,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Try New Quiz")
                    }
                }
            }
        }
    }
}

@Composable
fun ResultsDialog(
    correctCount: Int,
    totalCount: Int,
    onDismiss: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Quiz Results",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$correctCount / $totalCount correct",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                val percentage = (correctCount.toFloat() / totalCount) * 100
                Text(
                    text = "Score: ${percentage.toInt()}%",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Button row for multiple actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cancel button - stays on current screen
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Review")
                    }

                    // Original button - navigates back
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("New Quiz")
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerableQuestionCard(
    question: GeneratedQuestion,
    index: Int,
    selectedAnswerIndex: Int,
    showCorrectAnswer: Boolean,
    inReviewMode: Boolean = false,
    onAnswerSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${index + 1}. ${question.question}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Create a local variable to track if we should show the correct/wrong answer highlighting
            val showResults = showCorrectAnswer || inReviewMode

            when (question.type) {
                "Multiple Choice" -> {
                    question.options.forEachIndexed { optionIndex, option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedAnswerIndex == optionIndex,
                                onClick = {
                                    if (!showResults) {
                                        onAnswerSelected(optionIndex)
                                    }
                                },
                                enabled = !showResults  // Disable radio buttons in review mode
                            )
                            Text(
                                text = "${('A' + optionIndex)}) ${option.text}",
                                color = when {
                                    showResults && optionIndex == question.correctAnswerIndex ->
                                        MaterialTheme.colorScheme.primary
                                    showResults && selectedAnswerIndex == optionIndex &&
                                            optionIndex != question.correctAnswerIndex ->
                                        MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
                "True or False" -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedAnswerIndex == 0,
                            onClick = {
                                if (!showResults) {
                                    onAnswerSelected(0)
                                }
                            },
                            enabled = !showResults  // Disable radio buttons in review mode
                        )
                        Text(
                            text = "True",
                            color = when {
                                showResults && question.correctAnswerIndex == 0 ->
                                    MaterialTheme.colorScheme.primary
                                showResults && selectedAnswerIndex == 0 &&
                                        question.correctAnswerIndex != 0 ->
                                    MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedAnswerIndex == 1,
                            onClick = {
                                if (!showResults) {
                                    onAnswerSelected(1)
                                }
                            },
                            enabled = !showResults  // Disable radio buttons in review mode
                        )
                        Text(
                            text = "False",
                            color = when {
                                showResults && question.correctAnswerIndex == 1 ->
                                    MaterialTheme.colorScheme.primary
                                showResults && selectedAnswerIndex == 1 &&
                                        question.correctAnswerIndex != 1 ->
                                    MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
                "Fill in the Blanks" -> {
                    // Initialize the TextFied with the user's saved answer if in review mode
                    var answerText by remember {
                        mutableStateOf(if (showResults) question.userAnswer ?: "" else "")
                    }

                    OutlinedTextField(
                        value = answerText,
                        onValueChange = {
                            if (!showResults) {
                                answerText = it
                                question.userAnswer = it  // Store the user's answer
                                // Mark as selected with index 0 when text is entered
                                if (it.isNotEmpty()) {
                                    onAnswerSelected(0)
                                }
                            }
                        },
                        enabled = !showResults,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Your answer") },
                        singleLine = true
                    )

                    if (showResults) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Correct answer: ${question.options.firstOrNull()?.text ?: ""}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        // Show the user's answer for comparison
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your answer: ${question.userAnswer ?: ""}",
                            color = if ((question.userAnswer?.trim() ?: "").equals(question.options.firstOrNull()?.text?.trim() ?: "", ignoreCase = true))
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}