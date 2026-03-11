package com.example.sera.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.sera.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @AppPreferences
    @Singleton
    @Provides
    fun provideAppPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppPreferences