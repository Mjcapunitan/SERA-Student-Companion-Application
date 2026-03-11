package com.example.sera.data

import com.example.sera.common.value_objects.entities.Subject
import androidx.sqlite.db.SimpleSQLiteQuery

class SubjectRepository(private val subjectDao: SubjectDao) {
    fun observeAllSubjects() = subjectDao.observeAllSubjects()

    suspend fun getAllSubjects() = subjectDao.getAllSubjects()

    fun observeSubject(id: Int) = subjectDao.observeSubject(id)

    suspend fun getSubject(id: Int) = subjectDao.getSubject(id)

    suspend fun getSubjectTitle(id: Int) = subjectDao.getSubjectTitle(id)

    suspend fun insert(subject: Subject): Long {
        return subjectDao.insert(subject)
    }

    suspend fun update(subject: Subject) {
        subjectDao.update(subject)
        checkpoint()
    }

    suspend fun delete(id: Int) {
        subjectDao.deleteSubjectWithId(id)
    }

    suspend fun checkpoint(): Int {
        return subjectDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
    }
}