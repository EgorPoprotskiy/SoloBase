
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
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> =
        taskDao.getTasksWithoutProject().map { tasks ->
            tasks.map { it.toDomain() }
        }

    override fun getTasksByProject(projectId: String): Flow<List<Task>> =
        taskDao.getTasksByProject(projectId).map { tasks ->
            tasks.map { it.toDomain() }
        }

    override suspend fun getFutureActiveReminderTasks(now: Long): List<Task> =
        taskDao.getFutureActiveReminderTasks(now).map { it.toDomain() }

    override suspend fun getTaskById(id: String): Task? =
        taskDao.getTaskById(id)?.toDomain()

    override suspend fun addTask(task: Task) {
        val now = System.currentTimeMillis()

        taskDao.insertTask(
            task.toEntity().copy(
                updatedAt = now,
                deletedAt = null,
                isSynced = false
            )
        )
    }

    override suspend fun updateTask(task: Task) {
        val existing = taskDao.getTaskById(task.id) ?: return
        if (existing.deletedAt != null) return
        val now = maxOf(System.currentTimeMillis(), existing.updatedAt + 1)

        taskDao.updateTask(
            task.toEntity().copy(
                updatedAt = now,
                deletedAt = null,
                isSynced = false
            )
        )
    }

    override suspend fun deleteTask(id: String) {
        val existing = taskDao.getTaskById(id) ?: return
        if (existing.deletedAt != null) return

        val now = maxOf(System.currentTimeMillis(), existing.updatedAt + 1)

        taskDao.markTaskDeleted(
            taskId = id,
            deletedAt = now,
            updatedAt = now
        )
    }
}
