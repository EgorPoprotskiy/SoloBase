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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
    private val _displayMode = MutableStateFlow(TasksDisplayMode.LIST)
    val displayMode: StateFlow<TasksDisplayMode> = _displayMode.asStateFlow()
    fun toggleDisplayMode() {
        _displayMode.value = if (_displayMode.value == TasksDisplayMode.LIST) {
            TasksDisplayMode.MATRIX
        } else {
            TasksDisplayMode.LIST
        }
    }
    // 1. Получаем задачи и конвертируем в StateFlow
    val tasks: StateFlow<List<Task>> = getTasksUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf(
                Task(id = "1", content = "База данных подключена", isUrgent = true),
                Task(id = "2", content = "Hilt работает", isImportant = true)
            )
        )
    // 2. Метод для изменения статуса задачи (выполнено/нет)
    fun onTaskChecked(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            setTaskCompletedUseCase(task, isCompleted)
        }
    }

    // 3. Метод для удаления
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId)
        }
    }

    //4. Метод клика по задаче
    fun onTaskClicked(task: Task) {
        // Логика навигации или открытия BottomSheet
    }

    // Метод для добавления новой задачи
    fun addTask(content: String, isUrgent: Boolean = false, isImportant: Boolean = false) {
        viewModelScope.launch {
            addTaskUseCase(content, isUrgent, isImportant)
        }
    }
    //Обновление(редактирование) заметки
    fun updateTask(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task)
        }
    }

    fun updateTaskDetails(
        task: Task,
        content: String,
        isUrgent: Boolean,
        isImportant: Boolean
    ) {
        viewModelScope.launch {
            updateTaskDetailsUseCase(task, content, isUrgent, isImportant)
        }
    }
}

enum class TasksDisplayMode {LIST, MATRIX}
