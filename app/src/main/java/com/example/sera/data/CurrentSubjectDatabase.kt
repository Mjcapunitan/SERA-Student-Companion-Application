package com.example.sera.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentSubjectDatabase @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val _subjectDatabase = MutableStateFlow<SubjectDatabase?>(null)
    val subjectDatabase: StateFlow<SubjectDatabase?> = _subjectDatabase

    private val _subjectRepository = MutableStateFlow<SubjectRepository?>(null)
    val subjectRepository: StateFlow<SubjectRepository?> = _subjectRepository

    init {
        updateDatabase(
            SubjectDatabase.getInstance(
                context = context,
                databaseName = "subjects_database"
            )
        )
    }

    fun updateDatabase(db: SubjectDatabase?) {
        _subjectDatabase.value = db
        if (db == null) {
            _subjectRepository.value = null
            return
        }
        _subjectRepository.value = SubjectRepository(db.subjectDao())
    }
}