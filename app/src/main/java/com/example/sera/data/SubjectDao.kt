package com.example.sera.data


import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.sera.common.value_objects.entities.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects")
    fun observeAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects")
    suspend fun getAllSubjects(): List<Subject>

    @Query("SELECT * FROM subjects WHERE id=:id")
    fun observeSubject(id: Int): Flow<Subject?>

    @Query("SELECT * FROM subjects WHERE id=:id")
    suspend fun getSubject(id: Int): Subject

    @Query("SELECT title FROM subjects WHERE id=:id")
    suspend fun getSubjectTitle(id: Int): String

    @Insert
    suspend fun insert(subject: Subject): Long

    @Update
    suspend fun update(subject: Subject)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectWithId(id: Int)

    @RawQuery
    suspend fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}