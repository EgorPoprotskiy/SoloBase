package com.egorpoprotskiy.solobase.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.domain.usecase.project.AddProjectUseCase
import com.egorpoprotskiy.solobase.domain.usecase.project.DeleteProjectUseCase
import com.egorpoprotskiy.solobase.domain.usecase.project.GetProjectsUseCase
import com.egorpoprotskiy.solobase.domain.usecase.project.UpdateProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val addProjectUseCase: AddProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProjectsUiEvent>()
    val uiEvent: SharedFlow<ProjectsUiEvent> = _uiEvent.asSharedFlow()

    init {
        observeProjects()
    }

    private fun observeProjects() {
        viewModelScope.launch {
            try {
                getProjectsUseCase().collect { projects ->
                    _uiState.value = _uiState.value.copy(
                        projects = projects,
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

    fun addProject(name: String, description: String) {
        viewModelScope.launch {
            try {
                addProjectUseCase(
                    name = name,
                    description = description
                )
                clearError()
            } catch (exception: Exception) {
                handleOperationError(exception)
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            try {
                deleteProjectUseCase(projectId)
                clearError()
            } catch (exception: Exception) {
                handleOperationError(exception)
            }
        }
    }

    fun updateProjectDetails(project: Project, name: String, description: String) {
        viewModelScope.launch {
            try {
                updateProjectUseCase(
                    project = project,
                    name = name,
                    description = description
                )
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
            ProjectsUiEvent.ShowSnackbar(
                message = exception.message ?: "Unknown error"
            )
        )
    }
}
