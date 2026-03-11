package com.example.sera.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.data.QuizStatisticsRepository
import com.example.sera.utils.QuizAttempt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizDetailViewModel @Inject constructor(
    private val quizStatisticsRepository: QuizStatisticsRepository
) : ViewModel() {

    private val _quizAttempt = MutableStateFlow<QuizAttempt?>(null)
    val quizAttempt: StateFlow<QuizAttempt?> = _quizAttempt

    // Load a specific quiz attempt by ID
    fun loadQuizAttempt(quizId: String) {
        viewModelScope.launch {
            val attempts = quizStatisticsRepository.getQuizAttempts().firstOrNull() ?: emptyList()
            _quizAttempt.value = attempts.find { it.id == quizId }
        }
    }

    // Delete a specific quiz attempt
    fun deleteQuizAttempt(id: String) {
        viewModelScope.launch {
            quizStatisticsRepository.deleteQuizAttempt(id)
        }
    }
}