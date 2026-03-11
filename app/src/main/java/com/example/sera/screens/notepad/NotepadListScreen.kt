package com.example.sera.screens.notepad

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sera.R
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import com.example.sera.common.value_objects.entities.NotesEntity
import com.example.sera.screens.notepad.NotepadViewModel
import com.example.sera.utils.Icons
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotepadListScreen(
    viewModel: NotepadViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNoteClick: (Long) -> Unit,
    onAddNoteClick: () -> Unit
) {
    val notes by viewModel.filteredNotes.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val loading by viewModel.loading.collectAsState(initial = true)
    var selectedNoteId by remember { mutableStateOf<Long?>(null) }
    val selectedNote = notes.find { it.id == selectedNoteId }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Notes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(androidx.compose.material.icons.Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNoteClick,
                text = { Text(text = stringResource(id = R.string.add_note)) },
                icon = {
                    Icon(
                        androidx.compose.material.icons.Icons.Outlined.Add,
                        contentDescription = stringResource(id = R.string.CD_add_note)
                    )
                },
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        // Clear focus when tapped outside the search field
                        focusManager.clearFocus()
                    })
                }
        ) {
            // Search TextField with magnifying glass icon and round corners
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Search Notes") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Search, // Magnifying glass icon
                        contentDescription = "Search Icon"
                    )
                },
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,

                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                !loading && notes.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.no_notes),
                            contentDescription = "No notes illustration",
                            modifier = Modifier
                                .size(250.dp)
                                .padding(16.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(notes) { note ->
                            NoteCard(
                                title = note.title,
                                date = note.createdAt,
                                onClick = { onNoteClick(note.id) },
                                onLongPress = { selectedNoteId = note.id }
                            )
                        }
                    }
                }
            }
        }
    }

    selectedNote?.let { note ->
        AlertDialog(
            onDismissRequest = { selectedNoteId = null },
            title = { Text("Delete \"${note.title}\"?") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNote(note.id)
                    selectedNoteId = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedNoteId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}




@Composable
fun NoteCard(
    title: String,
    date: Date,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onClick() }
                )
            },
        colors = CardDefaults.outlinedCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateFormat.format(date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
