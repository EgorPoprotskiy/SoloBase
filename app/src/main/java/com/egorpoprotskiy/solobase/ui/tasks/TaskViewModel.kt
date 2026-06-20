package com.egorpoprotskiy.solobase.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.usecase.task.AddTaskUseCase
import com.egorpoprotskiy.solobase.domain.usecase.task.DeleteTaskUseCase
import com.egorpoprotskiy.solobase.domain.usecase.task.GetTasksUseCase
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel
@Inject constructor
    (
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val setTaskCompletedUseCase: SetTaskCompletedUseCase,
    private val updateTaskDetailsUseCase: UpdateTaskDetailsUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()
    private var allTasks: List<Task> = emptyList()

    private val _uiEvent = MutableSharedFlow<TasksUiEvent>()
    val uiEvent: SharedFlow<TasksUiEvent> = _uiEvent.asSharedFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            try {
                getTasksUseCase().collect { tasks ->
                    allTasks = tasks
                    _uiState.value = _uiState.value.copy(
                        tasks = filterTasks(tasks, _uiState.value.selectedFilter),
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

    fun toggleDisplayMode() {
        val nextDisplayMode = if (_uiState.value.displayMode == TasksDisplayMode.LIST) {
            TasksDisplayMode.MATRIX
        } else {
            TasksDisplayMode.LIST
        }
        _uiState.value = _uiState.value.copy(displayMode = nextDisplayMode)
    }

    fun selectFilter(filter: TaskFilter) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            tasks = filterTasks(allTasks, filter)
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
    fun addTask(content: String, isUrgent: Boolean = false, isImportant: Boolean = false) {
        viewModelScope.launch {
            try {
                addTaskUseCase(content, isUrgent, isImportant)
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
        isImportant: Boolean
    ) {
        viewModelScope.launch {
            try {
                updateTaskDetailsUseCase(task, content, isUrgent, isImportant)
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

    private suspend fun handleOperationError(exception: Exception) {
        setError(exception)
        _uiEvent.emit(
            TasksUiEvent.ShowSnackbar(
                message = exception.message ?: "Unknown error"
            )
        )
    }
}

enum class TasksDisplayMode {LIST, MATRIX}
