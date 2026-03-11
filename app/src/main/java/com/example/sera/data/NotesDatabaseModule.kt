package com.example.sera.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotesDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NotesDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            NotesDatabase::class.java,
            "notes_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNotesDao(database: NotesDatabase): NotesDao {
        return database.notesDao()
    }

    @Provides
    @Singleton
    fun provideNotesRepository(dao: NotesDao): NotesRepository {
        return NotesRepository(dao)
    }
}