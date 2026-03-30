package com.egorpoprotskiy.solobase.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {
    private val _displayMode = MutableStateFlow(TasksDisplayMode.LIST)
    val displayMode: StateFlow<TasksDisplayMode> = _displayMode.asStateFlow()
    fun toggleDisplayMode() {
        _displayMode.value = if (_displayMode.value == TasksDisplayMode.LIST) {
            TasksDisplayMode.MATRIX
        } else {
            TasksDisplayMode.LIST
        }
    }
    // 1. Получаем задачи из репозитория и конвертируем в StateFlow
    val tasks: StateFlow<List<Task>> = taskRepository.getTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    // 2. Метод для изменения статуса задачи (выполнено/нет)
    fun onTaskChecked(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            // Тут нужно вызвать repository.updateTask(...)
            taskRepository.updateTask(task.copy(isCompleted = isCompleted))
        }
    }

    // 3. Метод для удаления
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            // Тут repository.deleteTask(taskId)
            taskRepository.deleteTask(taskId)
        }
    }
}

enum class TasksDisplayMode {LIST, MATRIX}