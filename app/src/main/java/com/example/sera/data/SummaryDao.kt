package com.example.sera.data

import androidx.room.*
import com.example.sera.common.value_objects.entities.SummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryDao {
    @Query("SELECT * FROM summaries")
    fun observeAllSummaries(): Flow<List<SummaryEntity>>

    @Query("SELECT * FROM summaries WHERE id=:summaryId")
    fun observeSummary(summaryId: Long): Flow<SummaryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SummaryEntity): Long

    @Query("SELECT * FROM summaries ORDER BY createdAt DESC")
    fun getAllSummaries(): Flow<List<SummaryEntity>>

    @Query("SELECT * FROM summaries WHERE id = :summaryId")
    suspend fun getSummaryById(summaryId: Long): SummaryEntity?

    @Query("DELETE FROM summaries WHERE id = :summaryId")
    suspend fun deleteSummaryById(summaryId: Long)
}