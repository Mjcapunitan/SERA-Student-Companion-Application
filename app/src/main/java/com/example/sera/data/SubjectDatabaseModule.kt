package com.example.sera.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class SubjectDatabaseModule {

    @RoomSubjectDatabaseState
    @Singleton
    @Provides
    fun provideSubjectDatabase(currentSubjectDatabase: CurrentSubjectDatabase) =
        currentSubjectDatabase.subjectDatabase

    @RoomSubjectRepositoryState
    @Singleton
    @Provides
    fun provideSubjectRepository(currentSubjectDatabase: CurrentSubjectDatabase) =
        currentSubjectDatabase.subjectRepository
}
