package com.egorpoprotskiy.solobase.data.repository

import com.egorpoprotskiy.solobase.data.local.dao.TaskDao
import com.egorpoprotskiy.solobase.data.local.mapper.toDomain
import com.egorpoprotskiy.solobase.data.local.mapper.toEntity
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao // Hilt сам подставит его сюда
) : TaskRepository {
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
//    override fun getTasks(): Flow<List<Task>> = flowOf(emptyList())
//    override fun getTasks(): Flow<List<Task>> = tasksFlow
    override fun getTasks(): Flow<List<Task>> = taskDao.getAllTasks()
        .map { tasks -> tasks.map { it.toDomain() } }

    override fun getTasksByProject(projectId: String): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTaskById(id: String): Task? {
        TODO("Not yet implemented")
    }

//    override suspend fun addTask(task: Task) {
//        tasksFlow.update { currentList ->
//            currentList + task
//        }
//    }
    override suspend fun addTask(task: Task) = taskDao.insertTask(task.toEntity())

//    override suspend fun updateTask(task: Task) {
//        tasksFlow.update { list ->
//            list.map { if (it.id == task.id) task else it }
//        }
//    }
    override suspend fun updateTask(task: Task) = taskDao.updateTask(task.toEntity())

//    override suspend fun deleteTask(id: String) {
//        tasksFlow.update { list ->
//            list.filter { it.id != id }
//        }
//    }
    override suspend fun deleteTask(id: String) = taskDao.deleteTaskById(id)
}
