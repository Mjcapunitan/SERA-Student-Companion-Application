package com.example.sera.data

import android.content.Context
import androidx.room.Room
import androidx.room.TypeConverters
import com.example.sera.data.QuizAttemptDao
import com.example.sera.data.QuizDatabase
import com.example.sera.data.QuizStatisticsRepository
import com.example.sera.utils.Converters
import com.example.sera.utils.QuizDataHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuizModule {
    @Provides
    @Singleton
    fun provideQuizDataHolder(): QuizDataHolder {
        return QuizDataHolder()
    }

    @Provides
    @Singleton
    fun provideQuizDatabase(@ApplicationContext context: Context): QuizDatabase {
        return Room.databaseBuilder(
            context,
            QuizDatabase::class.java,
            "quiz_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideQuizAttemptDao(quizDatabase: QuizDatabase): QuizAttemptDao {
        return quizDatabase.quizAttemptDao()
    }

    @Provides
    @Singleton
    fun provideQuizStatisticsRepository(quizAttemptDao: QuizAttemptDao): QuizStatisticsRepository {
        return QuizStatisticsRepository(quizAttemptDao)
    }
}