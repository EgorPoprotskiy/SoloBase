package com.egorpoprotskiy.solobase.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.models.TaskStatus
import com.egorpoprotskiy.solobase.domain.usecase.task.AddTaskUseCase
import com.egorpoprotskiy.solobase.domain.usecase.task.DeleteTaskUseCase
import com.egorpoprotskiy.solobase.domain.usecase.task.GetTasksByProjectUseCase
import com.egorpoprotskiy.solobase.domain.usecase.task.GetTasksUseCase
import com.egorpoprotskiy.solobase.domain.usecase.task.MoveTaskToStatusUseCase
import com.egorpoprotskiy.solobase.domain.usecase.task.SetTaskCompletedUseCase
import com.egorpoprotskiy.solobase.domain.usecase.task.UpdateTaskDetailsUseCase
import com.egorpoprotskiy.solobase.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel
@Inject constructor
    (
    private val getTasksUseCase: GetTasksUseCase,
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val setTaskCompletedUseCase: SetTaskCompletedUseCase,
    private val updateTaskDetailsUseCase: UpdateTaskDetailsUseCase,
    private val moveTaskToStatusUseCase: MoveTaskToStatusUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()
    private var allTasks: List<Task> = emptyList()
    private var observeTasksJob: Job? = null

    private val _uiEvent = MutableSharedFlow<TasksUiEvent>()
    val uiEvent: SharedFlow<TasksUiEvent> = _uiEvent.asSharedFlow()

    init {
        observeTasks()
    }

    private fun observeTasks(projectId: String? = _uiState.value.selectedProjectId) {
        observeTasksJob?.cancel()
        observeTasksJob = viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val tasksFlow = if (projectId == null) {
                    getTasksUseCase()
                } else {
                    getTasksByProjectUseCase(projectId)
                }
                tasksFlow.collect { tasks ->
                    allTasks = tasks
                    _uiState.value = _uiState.value.copy(
                        tasks = getVisibleTasks(tasks),
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

    fun selectProject(projectId: String?) {
        if (_uiState.value.selectedProjectId == projectId) return
        allTasks = emptyList()
        _uiState.value = _uiState.value.copy(
            selectedProjectId = projectId,
            searchQuery = "",
            tasks = emptyList()
        )
        observeTasks(projectId)
    }

    fun toggleDisplayMode() {
        val nextDisplayMode = when (_uiState.value.displayMode) {
            TasksDisplayMode.LIST -> TasksDisplayMode.MATRIX
            TasksDisplayMode.MATRIX -> TasksDisplayMode.KANBAN
            TasksDisplayMode.KANBAN -> TasksDisplayMode.LIST
        }
        _uiState.value = _uiState.value.copy(displayMode = nextDisplayMode)
    }

    fun selectDisplayMode(displayMode: TasksDisplayMode) {
        _uiState.value = _uiState.value.copy(displayMode = displayMode)
    }

    fun selectFilter(filter: TaskFilter) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            tasks = getVisibleTasks(allTasks, filter, _uiState.value.searchQuery)
        )
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            tasks = getVisibleTasks(allTasks, _uiState.value.selectedFilter, query)
        )
    }

    // 2. Метод для изменения статуса задачи (выполнено/нет)
    fun onTaskChecked(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                setTaskCompletedUseCase(task, isCompleted)
                clearError()
            } catch (exception: Exception) {
                handleOperationError(exception)
            }
        }
    }

    // 3. Метод для удаления
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(taskId)
                clearError()
            } catch (exception: Exception) {
                handleOperationError(exception)
            }
        }
    }

    //4. Метод клика по задаче
    fun onTaskClicked(task: Task) {
        // Логика навигации или открытия BottomSheet
    }

    // Метод для добавления новой задачи
    fun addTask(
        content: String,
        isUrgent: Boolean = false,
        isImportant: Boolean = false,
        reminderAt: Long? = null
    ) {
        viewModelScope.launch {
            try {
                addTaskUseCase(
                    content = content,
                    isUrgent = isUrgent,
                    isImportant = isImportant,
                    projectId = _uiState.value.selectedProjectId,
                    reminderAt = reminderAt
                )
                clearError()
            } catch (exception: Exception) {
                handleOperationError(exception)
            }
        }
    }
    //Обновление(редактирование) заметки
    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                updateTaskUseCase(task)
                clearError()
            } catch (exception: Exception) {
                handleOperationError(exception)
            }
        }
    }

    fun updateTaskDetails(
        task: Task,
        content: String,
        isUrgent: Boolean,
        isImportant: Boolean,
        reminderAt: Long?
    ) {
        viewModelScope.launch {
            try {
                updateTaskDetailsUseCase(task, content, isUrgent, isImportant, reminderAt)
                clearError()
            } catch (exception: Exception) {
                handleOperationError(exception)
            }
        }
    }

    fun moveTaskToStatus(task: Task, status: TaskStatus) {
        viewModelScope.launch {
            try {
                moveTaskToStatusUseCase(task, status)
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

    private fun filterTasks(tasks: List<Task>, filter: TaskFilter): List<Task> {
        return when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.ACTIVE -> tasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
            TaskFilter.URGENT -> tasks.filter { it.isUrgent }
            TaskFilter.IMPORTANT -> tasks.filter { it.isImportant }
        }
    }

    private fun getVisibleTasks(
        tasks: List<Task>,
        filter: TaskFilter = _uiState.value.selectedFilter,
        searchQuery: String = _uiState.value.searchQuery
    ): List<Task> {
        val filteredTasks = filterTasks(tasks, filter)
        val query = searchQuery.trim()
        return if (query.isBlank()) {
            filteredTasks
        } else {
            filteredTasks.filter { task ->
                task.content.contains(query, ignoreCase = true)
            }
        }
    }

    private suspend fun handleOperationError(exception: Exception) {
        setError(exception)
        _uiEvent.emit(
            TasksUiEvent.ShowSnackbar(
                message = exception.message ?: "Unknown error"
            )
        )
    }
}

enum class TasksDisplayMode(val label: String) {
    LIST("Список"),
    MATRIX("Матрица"),
    KANBAN("Kanban")
}
