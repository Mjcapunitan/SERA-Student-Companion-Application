package com.example.sera.data

import com.example.sera.data.SummaryDao
import com.example.sera.common.value_objects.entities.SummaryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SummaryRepository @Inject constructor(
    private val summaryDao: SummaryDao
) {
    suspend fun saveSummary(title: String, content: String): Long {
        val summary = SummaryEntity(
            title = title,
            content = content
        )
        return summaryDao.insertSummary(summary)
    }

    fun observeSummaryById(id: Long): Flow<SummaryEntity?> = summaryDao.observeSummary(id)

    fun getAllSummaries(): Flow<List<SummaryEntity>> {
        return summaryDao.getAllSummaries()
    }

    suspend fun getSummaryById(id: Long): SummaryEntity? {
        return summaryDao.getSummaryById(id)
    }

    suspend fun deleteSummaryById(summaryId: Long) {
        summaryDao.deleteSummaryById(summaryId)
    }
}