package com.example.sera.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.BuildConfig
import com.example.sera.data.QuizStatisticsRepository
import com.example.sera.utils.PerformanceAssessment
import com.example.sera.utils.PerformanceEvaluationService
import com.example.sera.utils.QuizAttempt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerformanceAssessmentViewModel @Inject constructor(
    private val quizStatisticsRepository: QuizStatisticsRepository,
    private val performanceEvaluationService: PerformanceEvaluationService
) : ViewModel() {

    val gemini_api = BuildConfig.GEMINI_API_KEY

    // Assessment loading state
    private val _assessmentState = MutableStateFlow<AssessmentState>(AssessmentState.Idle)
    val assessmentState: StateFlow<AssessmentState> = _assessmentState

    // Quizzes data
    val quizAttempts: Flow<List<QuizAttempt>> = quizStatisticsRepository.getQuizAttempts()

    init {
        performanceEvaluationService.initialize(gemini_api)
    }

    // Generate performance assessment
    fun generateAssessment() {
        viewModelScope.launch {
            try {
                _assessmentState.value = AssessmentState.Loading

                // Collect quiz attempts
                val attempts = quizAttempts.first()

                if (attempts.isEmpty()) {
                    _assessmentState.value = AssessmentState.Error("No quiz data available to analyze")
                    return@launch
                }

                // Generate the assessment
                val result = performanceEvaluationService.generatePerformanceAssessment(attempts)

                result.fold(
                    onSuccess = { assessment ->
                        _assessmentState.value = AssessmentState.Success(assessment)
                    },
                    onFailure = { error ->
                        if (error is com.example.sera.utils.QuotaExceededException) {
                            _assessmentState.value = AssessmentState.Error("API quota exceeded. Please try again later.")
                        } else {
                            _assessmentState.value = AssessmentState.Error("Failed to generate assessment: ${error.message}")
                        }
                    }
                )
            } catch (e: Exception) {
                _assessmentState.value = AssessmentState.Error("An error occurred: ${e.message}")
            }
        }
    }

    // Cancel assessment generation
    fun cancelAssessmentGeneration() {
        performanceEvaluationService.cancelOngoingOperations()
        _assessmentState.value = AssessmentState.Idle
    }

    // Reset assessment state
    fun resetAssessmentState() {
        _assessmentState.value = AssessmentState.Idle
    }

    // Assessment state
    sealed class AssessmentState {
        object Idle : AssessmentState()
        object Loading : AssessmentState()
        data class Success(val assessment: PerformanceAssessment) : AssessmentState()
        data class Error(val message: String) : AssessmentState()
    }
}
