package com.example.sera.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizDataHolder @Inject constructor() {
    var currentQuestions: List<GeneratedQuestion> = emptyList()
    var quizDurationMinutes: Int = 10
    var quizType: String = "General Knowledge"
}