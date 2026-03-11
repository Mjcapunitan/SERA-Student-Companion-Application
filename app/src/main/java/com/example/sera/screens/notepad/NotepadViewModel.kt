package com.example.sera.screens.notepad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sera.data.NotesRepository
import com.example.sera.common.value_objects.entities.NotesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotepadViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<NotesEntity>>(emptyList())
    val notes: StateFlow<List<NotesEntity>> = _notes

    private val _filteredNotes = MutableStateFlow<List<NotesEntity>>(emptyList())
    val filteredNotes: StateFlow<List<NotesEntity>> = _filteredNotes

    private val _currentNote = MutableStateFlow<NotesEntity?>(null)
    val currentNote: StateFlow<NotesEntity?> = _currentNote

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        observeNotes()
        observeSearchQuery()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            notesRepository.getAllNotes().collect {
                _notes.value = it
                _loading.value = false
                filterNotes(_searchQuery.value)
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .collect { query ->
                    filterNotes(query)
                }
        }
    }

    fun loadNoteById(noteId: Long) {
        viewModelScope.launch {
            _currentNote.value = notesRepository.getNoteById(noteId)
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            notesRepository.saveNote(title, content)
        }
    }

    fun updateNote(note: NotesEntity) {
        viewModelScope.launch {
            notesRepository.updateNote(note)
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            notesRepository.deleteNoteById(noteId)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun filterNotes(query: String) {
        val trimmed = query.trim()
        _filteredNotes.value = if (trimmed.isEmpty()) {
            _notes.value
        } else {
            _notes.value.filter { note ->
                note.title.contains(trimmed, ignoreCase = true) ||
                        note.content.contains(trimmed, ignoreCase = true)
            }
        }
    }
}
