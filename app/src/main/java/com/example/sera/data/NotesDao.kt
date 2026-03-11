package com.example.sera.data

import androidx.room.*
import com.example.sera.common.value_objects.entities.NotesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes")
    fun observeAllNotes(): Flow<List<NotesEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun observeNote(noteId: Long): Flow<NotesEntity?>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotesEntity): Long

    @Update
    suspend fun updateNote(note: NotesEntity)

    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NotesEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): NotesEntity?

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)
}