package com.example.sera.data

import androidx.room.TypeConverters
import com.example.sera.data.QuizAttemptDao
import com.example.sera.common.value_objects.entities.QuizAttemptEntity
import com.example.sera.utils.Converters
import com.example.sera.utils.QuizAttempt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@TypeConverters(Converters::class)
class QuizStatisticsRepository @Inject constructor(
    private val quizAttemptDao: QuizAttemptDao
) {
    private val converters = Converters()

    // Convert domain model to entity
    private fun QuizAttempt.toEntity(): QuizAttemptEntity {
        return QuizAttemptEntity(
            id = id,
            timestamp = converters.fromDate(timestamp), // Changed from fromLocalDateTime
            quizTitle = quizTitle,
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            completionTimeSeconds = completionTimeSeconds,
            timeoutExpired = timeoutExpired,
            questionResultsJson = converters.fromQuestionResults(questionResults)
        )
    }

    // Convert entity to domain model
    private fun QuizAttemptEntity.toDomain(): QuizAttempt {
        return QuizAttempt(
            id = id,
            timestamp = converters.toDate(timestamp), // Changed from toLocalDateTime
            quizTitle = quizTitle,
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            completionTimeSeconds = completionTimeSeconds,
            timeoutExpired = timeoutExpired,
            questionResults = converters.toQuestionResults(questionResultsJson)
        )
    }

    suspend fun saveQuizAttempt(quizAttempt: QuizAttempt) {
        quizAttemptDao.insertQuizAttempt(quizAttempt.toEntity())
    }

    fun getQuizAttempts(): Flow<List<QuizAttempt>> {
        return quizAttemptDao.getAllQuizAttempts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun searchQuizAttempts(query: String): Flow<List<QuizAttempt>> {
        return quizAttemptDao.searchQuizAttempts(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getTotalQuizzesCompleted(): Flow<Int> {
        return quizAttemptDao.getTotalQuizzesCompleted()
    }

    fun getTotalCorrectAnswers(): Flow<Int> {
        return quizAttemptDao.getTotalCorrectAnswers()
    }

    fun getTotalQuestionsAnswered(): Flow<Int> {
        return quizAttemptDao.getTotalQuestionsAnswered()
    }

    fun getAverageScore(): Flow<Double> {
        return getQuizAttempts().map { attempts ->
            if (attempts.isEmpty()) 0.0
            else attempts.sumOf { it.correctAnswers.toDouble() / it.totalQuestions } / attempts.size
        }
    }

    suspend fun clearAllData() {
        quizAttemptDao.deleteAllQuizAttempts()
    }

    suspend fun deleteQuizAttempt(id: String) {
        quizAttemptDao.deleteQuizAttempt(id)
    }
}