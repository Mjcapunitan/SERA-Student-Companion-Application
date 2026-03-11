package com.example.sera.screens.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.data.QuizStatisticsRepository
import com.example.sera.utils.GeneratedQuestion
import com.example.sera.utils.QuestionResult
import com.example.sera.utils.QuizAttempt
import com.example.sera.utils.QuizDataHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class QuizAnsweringViewModel @Inject constructor(
    private val quizDataHolder: QuizDataHolder,
    private val quizStatisticsRepository: QuizStatisticsRepository
) : ViewModel() {

    private val _questions = MutableStateFlow<List<GeneratedQuestion>>(emptyList())
    val questions: StateFlow<List<GeneratedQuestion>> = _questions.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val selectedAnswers: StateFlow<Map<Int, Int>> = _selectedAnswers.asStateFlow()

    private val _quizSubmitted = MutableStateFlow(false)
    val quizSubmitted: StateFlow<Boolean> = _quizSubmitted.asStateFlow()

    private val _correctAnswers = MutableStateFlow(0)
    val correctAnswers: StateFlow<Int> = _correctAnswers.asStateFlow()

    private val _inReviewMode = MutableStateFlow(false)
    val inReviewMode = _inReviewMode.asStateFlow()

    // Timer-related state
    private val _remainingTimeInSeconds = MutableStateFlow(0)
    val remainingTimeInSeconds: StateFlow<Int> = _remainingTimeInSeconds.asStateFlow()

    private var timerJob: Job? = null
    private val startTime = LocalDateTime.now()
    private val initialTimeSeconds: Int

    // Track question timing
    private val questionStartTimes = mutableMapOf<Int, Long>()
    private val questionTimeTaken = mutableMapOf<Int, Int>()
    private var currentQuestionIndex = 0

    init {
        loadQuestions()
        initialTimeSeconds = quizDataHolder.quizDurationMinutes * 60
        initializeTimer(quizDataHolder.quizDurationMinutes)
        // Start tracking time for first question
        startTrackingQuestionTime(0)
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val currentQuestions = quizDataHolder.currentQuestions
            if (currentQuestions.isNotEmpty()) {
                _questions.value = currentQuestions
            }
        }
    }

    private fun initializeTimer(durationMinutes: Int) {
        _remainingTimeInSeconds.value = durationMinutes * 60
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (_remainingTimeInSeconds.value > 0 && !_quizSubmitted.value && !_inReviewMode.value) {
                delay(1000)
                _remainingTimeInSeconds.value -= 1
            }

            if (_remainingTimeInSeconds.value <= 0 && !_quizSubmitted.value && !_inReviewMode.value) {
                forceSubmitOnTimeout()
            }
        }
    }

    private fun startTrackingQuestionTime(questionIndex: Int) {
        questionStartTimes[questionIndex] = System.currentTimeMillis()
    }

    private fun stopTrackingQuestionTime(questionIndex: Int) {
        questionStartTimes[questionIndex]?.let { startTime ->
            val timeTaken = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            questionTimeTaken[questionIndex] = timeTaken
        }
    }

    fun selectAnswer(questionIndex: Int, answerIndex: Int) {
        if (!_quizSubmitted.value) {
            // Track time spent on each question when switching between questions
            if (currentQuestionIndex != questionIndex) {
                stopTrackingQuestionTime(currentQuestionIndex)
                startTrackingQuestionTime(questionIndex)
                currentQuestionIndex = questionIndex
            }

            val updatedAnswers = _selectedAnswers.value.toMutableMap()
            updatedAnswers[questionIndex] = answerIndex
            _selectedAnswers.value = updatedAnswers
        }
    }

    fun submitQuiz() {
        timerJob?.cancel()
        stopTrackingQuestionTime(currentQuestionIndex)

        if (_selectedAnswers.value.size == _questions.value.size) {
            var correctCount = 0
            val questionResults = mutableListOf<QuestionResult>()

            _questions.value.forEachIndexed { index, question ->
                val selectedAnswerIndex = _selectedAnswers.value[index]
                var isCorrect = false
                var userAnswerText: String? = null

                if (question.type == "Fill in the Blanks") {
                    val userAnswer = question.userAnswer?.trim() ?: ""
                    val correctAnswer = question.options.firstOrNull()?.text?.trim() ?: ""
                    userAnswerText = userAnswer

                    if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
                        correctCount++
                        isCorrect = true
                    }
                } else {
                    if (selectedAnswerIndex == question.correctAnswerIndex) {
                        correctCount++
                        isCorrect = true
                    }

                    userAnswerText = if (selectedAnswerIndex != null) {
                        if (question.type == "True or False") {
                            if (selectedAnswerIndex == 0) "True" else "False"
                        } else {
                            question.options.getOrNull(selectedAnswerIndex)?.text ?: ""
                        }
                    } else ""
                }

                // Get correct answer text
                val correctAnswerText = when (question.type) {
                    "Fill in the Blanks" -> question.options.firstOrNull()?.text ?: ""
                    "True or False" -> if (question.correctAnswerIndex == 0) "True" else "False"
                    else -> question.options.getOrNull(question.correctAnswerIndex)?.text ?: ""
                }

                questionResults.add(
                    QuestionResult(
                        questionIndex = index,
                        questionText = question.question,
                        questionType = question.type,
                        correctAnswer = correctAnswerText,
                        userAnswer = userAnswerText,
                        isCorrect = isCorrect,
                        timeTakenSeconds = questionTimeTaken[index]
                    )
                )
            }

            _correctAnswers.value = correctCount
            _quizSubmitted.value = true

            // Create a title based on the first question or date if no questions
            val quizTitle = if (_questions.value.isNotEmpty()) {
                // Use first question topic or first words as title
                val firstQuestion = _questions.value.first().question
                if (firstQuestion.length > 30) {
                    firstQuestion.substring(0, 30) + "..."
                } else {
                    firstQuestion
                }
            } else {
                "Quiz ${LocalDateTime.now()}"
            }

            // Create and save quiz attempt
            val timeSpent = initialTimeSeconds - _remainingTimeInSeconds.value
            val quizAttempt = QuizAttempt(
                quizTitle = quizTitle,
                totalQuestions = _questions.value.size,
                correctAnswers = correctCount,
                completionTimeSeconds = timeSpent,
                timeoutExpired = false,
                questionResults = questionResults
            )

            saveQuizStatistics(quizAttempt)
        }
    }

    fun setQuizReviewMode() {
        _quizSubmitted.value = false
        _inReviewMode.value = true
    }

    fun forceSubmitOnTimeout() {
        stopTrackingQuestionTime(currentQuestionIndex)

        var correctCount = 0
        val questionResults = mutableListOf<QuestionResult>()

        _questions.value.forEachIndexed { index, question ->
            val selectedAnswerIndex = _selectedAnswers.value[index]
            var isCorrect = false
            var userAnswerText: String? = null

            if (selectedAnswerIndex != null) {
                if (question.type == "Fill in the Blanks") {
                    val userAnswer = question.userAnswer?.trim() ?: ""
                    val correctAnswer = question.options.firstOrNull()?.text?.trim() ?: ""
                    userAnswerText = userAnswer

                    if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
                        correctCount++
                        isCorrect = true
                    }
                } else {
                    if (selectedAnswerIndex == question.correctAnswerIndex) {
                        correctCount++
                        isCorrect = true
                    }

                    userAnswerText = if (question.type == "True or False") {
                        if (selectedAnswerIndex == 0) "True" else "False"
                    } else {
                        question.options.getOrNull(selectedAnswerIndex)?.text ?: ""
                    }
                }
            }

            // Get correct answer text
            val correctAnswerText = when (question.type) {
                "Fill in the Blanks" -> question.options.firstOrNull()?.text ?: ""
                "True or False" -> if (question.correctAnswerIndex == 0) "True" else "False"
                else -> question.options.getOrNull(question.correctAnswerIndex)?.text ?: ""
            }

            questionResults.add(
                QuestionResult(
                    questionIndex = index,
                    questionText = question.question,
                    questionType = question.type,
                    correctAnswer = correctAnswerText,
                    userAnswer = userAnswerText,
                    isCorrect = isCorrect,
                    timeTakenSeconds = questionTimeTaken[index]
                )
            )
        }

        _correctAnswers.value = correctCount
        _quizSubmitted.value = true

        // Create a title based on the first question or date if no questions
        val quizTitle = if (_questions.value.isNotEmpty()) {
            // Use first question topic or first words as title
            val firstQuestion = _questions.value.first().question
            if (firstQuestion.length > 30) {
                firstQuestion.substring(0, 30) + "..."
            } else {
                firstQuestion
            }
        } else {
            "Quiz ${LocalDateTime.now()}"
        }

        // Create and save quiz attempt
        val quizAttempt = QuizAttempt(
            quizTitle = quizTitle,
            totalQuestions = _questions.value.size,
            correctAnswers = correctCount,
            completionTimeSeconds = initialTimeSeconds, // Used all available time
            timeoutExpired = true,
            questionResults = questionResults
        )

        saveQuizStatistics(quizAttempt)
    }

    private fun saveQuizStatistics(quizAttempt: QuizAttempt) {
        viewModelScope.launch {
            quizStatisticsRepository.saveQuizAttempt(quizAttempt)
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}