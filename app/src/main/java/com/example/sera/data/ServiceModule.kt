package com.example.sera.data

import com.example.sera.utils.QuestionGenerationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideQuestionGenerationService(): QuestionGenerationService {
        return QuestionGenerationService()
    }
}