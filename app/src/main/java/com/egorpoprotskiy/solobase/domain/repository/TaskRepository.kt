package com.egorpoprotskiy.solobase.domain.repository

import com.egorpoprotskiy.solobase.domain.models.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    // Получение потока задач (Flow — чтобы UI обновлялся сам при синхронизации)
    fun getTasks(): Flow<List<Task>>
    fun getTasksByProject(projectId: String): Flow<List<Task>>
    suspend fun getTaskById(id: String): Task?
    suspend fun addTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
}