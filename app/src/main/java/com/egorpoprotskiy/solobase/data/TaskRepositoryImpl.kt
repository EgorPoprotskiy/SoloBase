package com.egorpoprotskiy.solobase.data

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor() : TaskRepository {
    override fun getTasks(): Flow<List<Task>> = flowOf(emptyList())
    override fun getTasksByProject(projectId: String): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTaskById(id: String): Task? {
        TODO("Not yet implemented")
    }

    override suspend fun addTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(id: String) {
        TODO("Not yet implemented")
    }
}