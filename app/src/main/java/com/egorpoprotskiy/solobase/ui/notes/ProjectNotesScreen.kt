package com.egorpoprotskiy.solobase.ui.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egorpoprotskiy.solobase.R
import com.egorpoprotskiy.solobase.domain.models.Note
import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.ui.components.DeleteConfirmationDialog
import com.egorpoprotskiy.solobase.ui.notes.components.NoteItem
import com.egorpoprotskiy.solobase.ui.tasks.SearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectNotesScreen(
    project: Project,
    topAppBarWindowInsets: WindowInsets = WindowInsets.statusBars,
    showTopAppBar: Boolean = true,
    viewModel: ProjectNotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery = uiState.searchQuery
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }
    var editingNote by remember { mutableStateOf<Note?>(null) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    var searchMode by remember { mutableStateOf(false) }

    LaunchedEffect(project.id) {
        viewModel.selectProject(project.id)
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ProjectNotesUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = if (showTopAppBar) {
            {
                TopAppBar(
                    windowInsets = topAppBarWindowInsets,
                    title = {
                        if (searchMode) {
                            SearchTextField(
                                value = searchQuery,
                                onValueChange = viewModel::updateSearchQuery,
                                placeholder = stringResource(R.string.search_notes_hint),
                                onClose = {
                                    searchMode = false
                                    viewModel.updateSearchQuery("")
                                }
                            )
                        } else {
                            Text(stringResource(R.string.project_notes_title, project.name))
                        }
                    },
                    actions = {
                        if (!searchMode) {
                            IconButton(onClick = { searchMode = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.content_description_search),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        } else {
            {}
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.content_description_add_note)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.notes.isEmpty()) {
                NotesEmptyState(
                    isSearching = searchQuery.isNotBlank(),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.notes,
                        key = { it.id }
                    ) { note ->
                        NoteItem(
                            note = note,
                            onClick = {
                                editingNote = note
                                noteText = note.content
                                showDialog = true
                            },
                            onDeleteClick = { noteToDelete = note }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    editingNote = null
                    noteText = ""
                },
                title = {
                    Text(
                        if (editingNote == null) {
                            stringResource(R.string.note_new_title)
                        } else {
                            stringResource(R.string.note_edit_title)
                        }
                    )
                },
                text = {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text(stringResource(R.string.note_text_label)) },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (noteText.isNotBlank()) {
                                val currentNote = editingNote
                                if (currentNote == null) {
                                    viewModel.addNote(noteText)
                                } else {
                                    viewModel.updateNote(currentNote, noteText)
                                }
                                showDialog = false
                                editingNote = null
                                noteText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(stringResource(R.string.action_save))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            editingNote = null
                            noteText = ""
                        }
                    ) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }

        if (noteToDelete != null) {
            DeleteConfirmationDialog(
                title = stringResource(R.string.note_delete_title),
                onConfirm = {
                    viewModel.deleteNote(noteToDelete!!.id)
                    noteToDelete = null
                },
                onDismiss = {
                    noteToDelete = null
                }
            )
        }
    }
}

@Composable
private fun NotesEmptyState(
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isSearching) {
                    stringResource(R.string.empty_search_results)
                } else {
                    stringResource(R.string.empty_notes_title)
                },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            if (!isSearching) {
                Text(
                    text = stringResource(R.string.empty_notes_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
