package com.egorpoprotskiy.solobase.data

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor() : TaskRepository {
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
//    override fun getTasks(): Flow<List<Task>> = flowOf(emptyList())
    override fun getTasks(): Flow<List<Task>> = tasksFlow

    override fun getTasksByProject(projectId: String): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTaskById(id: String): Task? {
        TODO("Not yet implemented")
    }

    override suspend fun addTask(task: Task) {
        tasksFlow.update { currentList ->
            currentList + task
        }
    }

    override suspend fun updateTask(task: Task) {
        tasksFlow.update { list ->
            list.map { if (it.id == task.id) task else it }
        }
    }

    override suspend fun deleteTask(id: String) {
        tasksFlow.update { list ->
            list.filter { it.id != id }
        }
    }
}