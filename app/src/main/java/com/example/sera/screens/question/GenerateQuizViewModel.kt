package com.example.sera.screens.question

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sera.BuildConfig
import com.example.sera.common.value_objects.entities.SummaryEntity
import com.example.sera.data.SummaryRepository
import com.example.sera.utils.GeneratedQuestion
import com.example.sera.utils.QuestionGenerationService
import com.example.sera.utils.QuizDataHolder
import com.example.sera.utils.QuotaExceededException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateQuizViewModel @Inject constructor(
    private val repository: SummaryRepository,
    private val questionService: QuestionGenerationService
) : ViewModel() {

    @Inject
    lateinit var quizDataHolder: QuizDataHolder

    val summaries: StateFlow<List<SummaryEntity>> = repository.getAllSummaries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gemini_api = BuildConfig.GEMINI_API_KEY

    var selectedQuizSelection by mutableStateOf(QuizSelection())
        private set

    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState = _generationState.asStateFlow()

    private val _generatedQuestions = MutableStateFlow<List<GeneratedQuestion>>(emptyList())
    val generatedQuestions = _generatedQuestions.asStateFlow()

    private val previouslyGeneratedQuestions = mutableSetOf<String>()

    // Track current generation job for cancellation
    private var currentGenerationJob: Job? = null

    init {
        questionService.initialize(gemini_api)
    }

    fun onSummarySelected(title: String) {
        // Check if the summary has changed before clearing the cache
        val summaryChanged = selectedQuizSelection.title != title

        // Update the selection
        selectedQuizSelection = selectedQuizSelection.copy(title = title)

        // Clear the cache if the summary changed
        if (summaryChanged) {
            previouslyGeneratedQuestions.clear()
        }
    }

    fun setTimeoutError() {
        _generationState.value = GenerationState.Error("Request timed out. Please try again later.")
    }

    fun onQuestionTypeSelected(type: String) {
        selectedQuizSelection = selectedQuizSelection.copy(questionType = type)
    }

    fun onDifficultySelected(level: String) {
        selectedQuizSelection = selectedQuizSelection.copy(difficulty = level)
    }

    fun getSelectedSummaryContent(): String? {
        return summaries.value.find { it.title == selectedQuizSelection.title }?.content
    }

    fun onNumberOfQuestionsSelected(number: Int) {
        selectedQuizSelection = selectedQuizSelection.copy(numberOfQuestions = number)
    }

    // New function to handle duration selection
    fun onDurationSelected(duration: String) {
        selectedQuizSelection = selectedQuizSelection.copy(duration = duration)
    }

    fun generateQuestions() {
        val content = getSelectedSummaryContent() ?: return
        val quizSelection = selectedQuizSelection

        // Cancel any ongoing job first
        cancelGeneration()

        currentGenerationJob = viewModelScope.launch {
            _generationState.value = GenerationState.Loading

            questionService.generateQuestions(
                content = content,
                questionType = quizSelection.questionType ?: return@launch,
                difficulty = quizSelection.difficulty ?: return@launch,
                numberOfQuestions = quizSelection.numberOfQuestions ?: return@launch
            ).fold(
                onSuccess = { questions ->
                    // Filter out exact duplicates from previous generations
                    val newQuestions = questions.filter { question ->
                        !previouslyGeneratedQuestions.contains(question.question)
                    }

                    // Store new questions in our tracking set
                    newQuestions.forEach { previouslyGeneratedQuestions.add(it.question) }

                    // Use what we have, even if it's fewer than requested
                    _generatedQuestions.value = if (newQuestions.isNotEmpty()) {
                        newQuestions
                    } else {
                        // If all were duplicates, use the original questions
                        questions
                    }
                    _generationState.value = GenerationState.Success
                },
                onFailure = { error ->
                    if (error is QuotaExceededException) {
                        _generationState.value = GenerationState.QuotaExceeded
                    } else if (error is CancellationException) {
                        _generationState.value = GenerationState.Error("Request cancelled")
                    } else {
                        _generationState.value = GenerationState.Error(error.message ?: "Failed to generate questions")
                    }
                }
            )
        }
    }

    fun clearQuestionCache() {
        previouslyGeneratedQuestions.clear()
    }

    fun cancelGeneration() {
        currentGenerationJob?.let { job ->
            if (job.isActive) {
                job.cancel()
                // Check if we're actually in loading state before changing the state
                if (_generationState.value is GenerationState.Loading) {
                    _generationState.value = GenerationState.Error("")
                }
            }
        }
        currentGenerationJob = null

        questionService.cancelOngoingOperations()
    }

    fun resetState() {
        cancelGeneration() // Cancel any ongoing operations first
        _generatedQuestions.value = emptyList()
        _generationState.value = GenerationState.Idle
        selectedQuizSelection = QuizSelection()
    }

    fun navigateToQuiz(navController: NavController) {
        // Extract duration in minutes (default to 10 minutes if not selected)
        val durationMinutes = selectedQuizSelection.duration?.split(" ")?.firstOrNull()?.toIntOrNull() ?: 10

        // Store the questions in the shared data holder
        quizDataHolder.currentQuestions = _generatedQuestions.value

        // Update the quiz data holder with the selected duration
        quizDataHolder.quizDurationMinutes = durationMinutes

        // Navigate to the quiz answering screen
        navController.navigate("quiz_answering")
    }

    private fun convertQuestionsToJson(questions: List<GeneratedQuestion>): String {
        val gson = Gson()
        return gson.toJson(questions)
    }

    sealed class GenerationState {
        object Idle : GenerationState()
        object Loading : GenerationState()
        object Success : GenerationState()
        object QuotaExceeded : GenerationState()
        data class Error(val message: String) : GenerationState()
    }
}

data class QuizSelection(
    val title: String? = null,
    val questionType: String? = null,
    val difficulty: String? = null,
    val numberOfQuestions: Int? = null,
    val duration: String? = null
)