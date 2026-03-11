package com.example.sera.data

import com.example.sera.data.NotesDao
import com.example.sera.common.value_objects.entities.NotesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val notesDao: NotesDao
) {
    suspend fun saveNote(title: String, content: String): Long {
        val note = NotesEntity(
            title = title,
            content = content
        )
        return notesDao.insertNote(note)
    }

    fun observeNoteById(id: Long): Flow<NotesEntity?> = notesDao.observeNote(id)

    fun getAllNotes(): Flow<List<NotesEntity>> {
        return notesDao.getAllNotes()
    }

    suspend fun getNoteById(id: Long): NotesEntity? {
        return notesDao.getNoteById(id)
    }

    suspend fun deleteNoteById(noteId: Long) {
        notesDao.deleteNoteById(noteId)
    }

    suspend fun updateNote(note: NotesEntity) {
        notesDao.updateNote(note)
    }
}