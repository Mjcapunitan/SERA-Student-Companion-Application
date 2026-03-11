package com.example.sera.data

import androidx.room.*
import com.example.sera.common.value_objects.entities.QuizAttemptEntity
import com.example.sera.utils.Converters
import kotlinx.coroutines.flow.Flow

@Dao
@TypeConverters(Converters::class)
interface QuizAttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(quizAttempt: QuizAttemptEntity)

    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllQuizAttempts(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts WHERE quizTitle LIKE '%' || :searchQuery || '%' ORDER BY timestamp DESC")
    fun searchQuizAttempts(searchQuery: String): Flow<List<QuizAttemptEntity>>

    @Query("SELECT COUNT(*) FROM quiz_attempts")
    fun getTotalQuizzesCompleted(): Flow<Int>

    @Query("SELECT SUM(correctAnswers) FROM quiz_attempts")
    fun getTotalCorrectAnswers(): Flow<Int>

    @Query("SELECT SUM(totalQuestions) FROM quiz_attempts")
    fun getTotalQuestionsAnswered(): Flow<Int>

    @Query("DELETE FROM quiz_attempts")
    suspend fun deleteAllQuizAttempts()

    @Query("DELETE FROM quiz_attempts WHERE id = :id")
    suspend fun deleteQuizAttempt(id: String)
}