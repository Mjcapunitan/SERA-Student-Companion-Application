package com.example.sera.data

import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentSummaryDatabase @Inject constructor(
    private val summaryDatabase: SummaryDatabase
) {
    private val _summaryRepository = MutableStateFlow<SummaryRepository?>(null)
    val summaryRepository: StateFlow<SummaryRepository?> = _summaryRepository

    init {
        updateDatabase(summaryDatabase)
    }

    fun updateDatabase(db: SummaryDatabase?) {
        if (db == null) {
            _summaryRepository.value = null
            return
        }
        _summaryRepository.value = SummaryRepository(db.summaryDao())
    }
}