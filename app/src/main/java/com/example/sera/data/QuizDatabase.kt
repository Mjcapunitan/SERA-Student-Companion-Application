package com.example.sera.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sera.common.value_objects.entities.QuizAttemptEntity
import com.example.sera.data.QuizAttemptDao
import com.example.sera.utils.Converters

@Database(entities = [QuizAttemptEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizAttemptDao(): QuizAttemptDao
}