
package com.egorpoprotskiy.solobase.domain.repository.remote

import com.egorpoprotskiy.solobase.data.remote.sync.FirestoreSyncResult
import com.egorpoprotskiy.solobase.domain.models.Task
import kotlinx.coroutines.flow.Flow

/**
 * Контракт для работы с задачами в облачном Firestore.
 * Используется будущими UseCase и реализацией в data-слое.
 */
interface FirestoreTaskRepository {

    // Наблюдает за задачами текущего пользователя в облаке.
    fun observeTasks(): Flow<List<Task>>

    // Создаёт новую задачу или обновляет существующую.
//    suspend fun saveTask(task: Task): Result<Unit>
    suspend fun saveTask(task: Task): Result<FirestoreSyncResult<Task>>
    // Помечает задачу удалённой, сохраняя её документ для синхронизации.
    suspend fun deleteTask(taskId: String): Result<Unit>

    suspend fun getAllTasksFromServer(): Result<List<Task>>
}
