package com.example.sera.screens.notepad

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.sera.screens.notepad.NotepadViewModel
import com.example.sera.common.value_objects.entities.NotesEntity
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sera.utils.Icons


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    navController: NavHostController,
    noteId: Long? = null,
    viewModel: NotepadViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    // State for note title and content
    var title by remember { mutableStateOf(TextFieldValue()) }
    var content by remember { mutableStateOf(TextFieldValue()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val currentNoteState by viewModel.currentNote.collectAsState()
    val note = currentNoteState

    // Load the note if it's in edit mode
    LaunchedEffect(noteId) {
        noteId?.let {
            viewModel.loadNoteById(it)
        }
    }

    // Observe the current note
    val currentNote by viewModel.currentNote.collectAsState()

    // Update UI when current note changes (for editing mode)
    currentNote?.let {
        title = TextFieldValue(it.title)
        content = TextFieldValue(it.content)
    }

    // Function to handle saving the note
    fun saveNote() {
        if (title.text.isEmpty() || content.text.isEmpty()) {
            Toast.makeText(navController.context, "Title and Content can't be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentNote == null) {
            viewModel.addNote(title.text, content.text)
        } else {
            viewModel.updateNote(NotesEntity(id = currentNote?.id ?: 0, title = title.text, content = content.text)) // Update note if editing
        }

        onBack()
    }

    if (showDeleteDialog && note != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNote(note.id)
                    showDeleteDialog = false
                    onBack()
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentNote == null) "Add Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { saveNote() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Check,
                            contentDescription = "Save Note"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (currentNote != null) {
                BottomAppBar(
                    tonalElevation = 0.dp,
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .height(56.dp) // Smaller height
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
        content = { paddingValues ->
            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            "Title",
                            fontSize = 24.sp
                        ) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )

}