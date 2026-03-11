package com.example.sera


import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.example.sera.domain.interactors.GetAllSubjects
import com.example.sera.data.CurrentSubjectDatabase
import com.example.sera.data.SubjectDatabase
import com.example.sera.utils.di.ApplicationScope
import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.entities.Subject
import com.example.sera.common.value_objects.schedule.Schedule
import com.example.sera.data.CurrentSummaryDatabase
import com.example.sera.data.NotesRepository
import com.example.sera.data.SummaryDatabase
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var currentSubjectDatabase: CurrentSubjectDatabase

    @Inject
    lateinit var currentSummaryDatabase: CurrentSummaryDatabase

    @Inject
    lateinit var notesRepository: NotesRepository


    @ApplicationScope
    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var getAllSubjects: GetAllSubjects


    override fun onCreate() {
        super.onCreate()

        scope.launch {
            initializeDatabase()
        }
    }

    private fun initializeDatabase() {
        scope.launch {
            currentSubjectDatabase.updateDatabase(
                SubjectDatabase.getInstance(
                    context = this@App,
                    databaseName = "subjects_database"
                )
            )
        }
    }

    private fun List<Subject>.getFirstScheduleWithSubjectAndDay(): Triple<Subject, Schedule, Day>? {
        this.forEach { subject ->
            subject.daySchedulesMap.forEach { (day, schedules) ->
                val firstSchedule = schedules.firstOrNull()
                if (firstSchedule != null) {
                    return Triple(subject, firstSchedule, day)
                }
            }
        }
        return null
    }
}
