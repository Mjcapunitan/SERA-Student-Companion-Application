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
object SummaryDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SummaryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SummaryDatabase::class.java,
            "summaries_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideSummaryDao(database: SummaryDatabase): SummaryDao {
        return database.summaryDao()
    }

    @Provides
    @Singleton
    fun provideSummaryRepository(dao: SummaryDao): SummaryRepository {
        return SummaryRepository(dao)
    }

}
