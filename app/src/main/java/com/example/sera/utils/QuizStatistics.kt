package com.example.sera.utils

import java.time.LocalDateTime
import java.util.Date

data class QuizAttempt(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Date = Date(),
    val quizTitle: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val completionTimeSeconds: Int,
    val timeoutExpired: Boolean,
    val questionResults: List<QuestionResult>
)
data class QuestionResult(
    val questionIndex: Int,
    val questionText: String,
    val questionType: String,
    val correctAnswer: String,
    val userAnswer: String?,
    val isCorrect: Boolean,
    val timeTakenSeconds: Int? = null
)