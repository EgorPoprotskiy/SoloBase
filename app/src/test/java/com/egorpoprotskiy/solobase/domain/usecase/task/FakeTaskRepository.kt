package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTaskRepository : TaskRepository {
    val tasks = MutableStateFlow<List<Task>>(emptyList())
    var addedTask: Task? = null
    var updatedTask: Task? = null
    var deletedTaskId: String? = null

    override fun getTasks(): Flow<List<Task>> = tasks

    override fun getTasksByProject(projectId: String): Flow<List<Task>> = tasks

    override suspend fun getTaskById(id: String): Task? = tasks.value.firstOrNull { it.id == id }

    override suspend fun addTask(task: Task) {
        addedTask = task
        tasks.value = tasks.value + task
    }

    override suspend fun updateTask(task: Task) {
        updatedTask = task
        tasks.value = tasks.value.map { currentTask ->
            if (currentTask.id == task.id) task else currentTask
        }
    }

    override suspend fun deleteTask(id: String) {
        deletedTaskId = id
        tasks.value = tasks.value.filterNot { it.id == id }
    }
}
