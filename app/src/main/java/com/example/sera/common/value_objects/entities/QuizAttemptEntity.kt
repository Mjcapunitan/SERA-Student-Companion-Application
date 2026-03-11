package com.example.sera.common.value_objects.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.sera.utils.Converters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey
    val id: String,
    val timestamp: String, // Store as string in ISO format
    val quizTitle: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val completionTimeSeconds: Int,
    val timeoutExpired: Boolean,

    // Storing questionResults as JSON string
    val questionResultsJson: String
)