package com.egorpoprotskiy.solobase.data.repository

import com.egorpoprotskiy.solobase.data.local.dao.TaskDao
import com.egorpoprotskiy.solobase.data.local.mapper.toDomain
import com.egorpoprotskiy.solobase.data.local.mapper.toEntity
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao // Hilt сам подставит его сюда
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> = taskDao.getAllTasks()
        .map { tasks -> tasks.map { it.toDomain() } }

    override fun getTasksByProject(projectId: String): Flow<List<Task>> = taskDao
        .getTasksByProject(projectId)
        .map { tasks -> tasks.map { it.toDomain() } }

    override suspend fun getTaskById(id: String): Task? = taskDao.getTaskById(id)?.toDomain()

    override suspend fun addTask(task: Task) = taskDao.insertTask(task.toEntity())

    override suspend fun updateTask(task: Task) = taskDao.updateTask(task.toEntity())

    override suspend fun deleteTask(id: String) = taskDao.deleteTaskById(id)
}
