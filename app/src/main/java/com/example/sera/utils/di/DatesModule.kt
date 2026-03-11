package com.example.sera.utils.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.util.*
import javax.inject.Qualifier

@Module
@InstallIn(ActivityRetainedComponent::class)
class DatesModule {

    @DateToday
    @ActivityRetainedScoped
    @Provides
    fun provideDateToday(): Calendar = Calendar.getInstance()

    @DateTomorrow
    @ActivityRetainedScoped
    @Provides
    fun provideDateTomorrow(): Calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }

    @HourOfDay
    @ActivityRetainedScoped
    @Provides
    fun provideHourOfDay(@DateToday dateToday: Calendar) = dateToday.get(Calendar.HOUR_OF_DAY)

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DateToday

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DateTomorrow

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HourOfDay