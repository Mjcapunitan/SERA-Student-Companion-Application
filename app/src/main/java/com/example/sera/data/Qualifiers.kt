package com.example.sera.data

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RoomSubjectRepositoryState

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RoomSubjectDatabaseState

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RoomSummaryDatabaseState

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RoomSummaryRepositoryState