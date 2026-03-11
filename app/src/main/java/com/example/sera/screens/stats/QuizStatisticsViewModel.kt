package com.example.sera.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.data.QuizStatisticsRepository
import com.example.sera.utils.QuizAttempt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizStatisticsViewModel @Inject constructor(
    private val quizStatisticsRepository: QuizStatisticsRepository
) : ViewModel() {

    // Get quiz attempts sorted by timestamp (most recent first)
    val quizAttempts: Flow<List<QuizAttempt>> = quizStatisticsRepository.getQuizAttempts()
        .map { attempts ->
            attempts.sortedByDescending { it.timestamp }
        }

    // Statistics data
    val totalQuizzesCompleted: Flow<Int> = quizStatisticsRepository.getTotalQuizzesCompleted()
    val totalCorrectAnswers: Flow<Int> = quizStatisticsRepository.getTotalCorrectAnswers()
    val totalQuestionsAnswered: Flow<Int> = quizStatisticsRepository.getTotalQuestionsAnswered()
    val averageScore: Flow<Double> = quizStatisticsRepository.getAverageScore()

    // Search functionality
    fun searchQuizAttempts(query: String): Flow<List<QuizAttempt>> {
        return quizStatisticsRepository.searchQuizAttempts(query)
            .map { attempts ->
                attempts.sortedByDescending { it.timestamp }
            }
    }

    // Delete a specific quiz attempt
    fun deleteQuizAttempt(id: String) {
        viewModelScope.launch {
            quizStatisticsRepository.deleteQuizAttempt(id)
        }
    }

    // Clear all statistics data
    fun clearAllData() {
        viewModelScope.launch {
            quizStatisticsRepository.clearAllData()
        }
    }
}