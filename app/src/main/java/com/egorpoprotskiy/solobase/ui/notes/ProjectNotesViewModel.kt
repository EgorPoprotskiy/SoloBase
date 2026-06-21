package com.egorpoprotskiy.solobase.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.solobase.domain.models.Note
import com.egorpoprotskiy.solobase.domain.usecase.note.AddNoteUseCase
import com.egorpoprotskiy.solobase.domain.usecase.note.DeleteNoteUseCase
import com.egorpoprotskiy.solobase.domain.usecase.note.GetNotesByProjectUseCase
import com.egorpoprotskiy.solobase.domain.usecase.note.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectNotesViewModel @Inject constructor(
    private val getNotesByProjectUseCase: GetNotesByProjectUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectNotesUiState())
    val uiState: StateFlow<ProjectNotesUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProjectNotesUiEvent>()
    val uiEvent: SharedFlow<ProjectNotesUiEvent> = _uiEvent.asSharedFlow()

    private var observeNotesJob: Job? = null

    fun selectProject(projectId: String) {
        if (_uiState.value.projectId == projectId) return
        observeNotesJob?.cancel()
        _uiState.value = _uiState.value.copy(
            projectId = projectId,
            notes = emptyList(),
            isLoading = true,
            errorMessage = null
        )
        observeNotes(projectId)
    }

    private fun observeNotes(projectId: String) {
        observeNotesJob = viewModelScope.launch {
            try {
                getNotesByProjectUseCase(projectId).collect { notes ->
                    _uiState.value = _uiState.value.copy(
                        notes = notes,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message
                )
            }
        }
    }

    fun addNote(content: String) {
        val projectId = _uiState.value.projectId ?: return
        viewModelScope.launch {
            try {
                addNoteUseCase(
                    projectId = projectId,
                    content = content
                )
                clearError()
            } catch (exception: Exception) {
                handleOperationError(exception)
            }
        }
    }

    fun updateNote(note: Note, content: String) {
        viewModelScope.launch {
            try {
                updateNoteUseCase(note, content)
                clearError()
            } catch (exception: Exception) {
                handleOperationError(exception)
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            try {
                deleteNoteUseCase(noteId)
                clearError()
            } catch (exception: Exception) {
                handleOperationError(exception)
            }
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun setError(exception: Exception) {
        _uiState.value = _uiState.value.copy(errorMessage = exception.message)
    }

    private suspend fun handleOperationError(exception: Exception) {
        setError(exception)
        _uiEvent.emit(
            ProjectNotesUiEvent.ShowSnackbar(
                message = exception.message ?: "Unknown error"
            )
        )
    }
}
