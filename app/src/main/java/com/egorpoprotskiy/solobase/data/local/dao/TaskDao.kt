
package com.egorpoprotskiy.solobase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.egorpoprotskiy.solobase.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Все неудалённые задачи для UI.
    @Query("""
        SELECT * FROM tasks
        WHERE deletedAt IS NULL
        ORDER BY timestamp DESC
    """)
    fun getAllTasks(): Flow<List<TaskEntity>>

    // Неудалённые задачи без проекта.
    @Query("""
        SELECT * FROM tasks
        WHERE projectId IS NULL
          AND deletedAt IS NULL
        ORDER BY timestamp DESC
    """)
    fun getTasksWithoutProject(): Flow<List<TaskEntity>>

    // Неудалённые задачи выбранного проекта.
    @Query("""
        SELECT * FROM tasks
        WHERE projectId = :projectId
          AND deletedAt IS NULL
        ORDER BY timestamp DESC
    """)
    fun getTasksByProject(projectId: String): Flow<List<TaskEntity>>

    // Получение задачи по ID, включая удалённые.
    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): TaskEntity?

    // Напоминания только для активных, неудалённых задач.
    @Query("""
        SELECT * FROM tasks
        WHERE reminderAt IS NOT NULL
          AND reminderAt > :now
          AND isCompleted = 0
          AND deletedAt IS NULL
    """)
    suspend fun getFutureActiveReminderTasks(now: Long): List<TaskEntity>

    // Все задачи, ожидающие отправки в Firestore, включая tombstone-записи.
    @Query("SELECT * FROM tasks WHERE isSynced = 0")
    suspend fun getUnsyncedTasks(): List<TaskEntity>

    // Локальная вставка или замена задачи.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    // Локальное обновление задачи.
    @Update
    suspend fun updateTask(task: TaskEntity)

    // Применение данных из Firestore без постановки в очередь отправки.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSyncedTask(task: TaskEntity)

    // Мягкое удаление задачи.
    @Query("""
        UPDATE tasks
        SET deletedAt = :deletedAt,
            updatedAt = :updatedAt,
            isSynced = 0
        WHERE id = :taskId
    """)
    suspend fun markTaskDeleted(
        taskId: String,
        deletedAt: Long,
        updatedAt: Long
    )

    // Подтверждение отправки только той версии, которая была загружена.
    @Query("""
        UPDATE tasks
        SET isSynced = 1
        WHERE id = :taskId
          AND updatedAt = :uploadedUpdatedAt
    """)
    suspend fun markTaskSynced(
        taskId: String,
        uploadedUpdatedAt: Long
    )

    // Все задачи для первоначального объединения с Firestore,
// включая синхронизированные и помеченные удалёнными.
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksForSync(): List<TaskEntity>

    // Применяет облачную задачу, только если она не старее локальной.
// При одинаковом updatedAt побеждает Firestore.
    @Transaction
    suspend fun applySyncedTaskIfNewer(task: TaskEntity) {
        val localTask = getTaskById(task.id)

        if (localTask == null || task.updatedAt >= localTask.updatedAt) {
            upsertSyncedTask(task.copy(isSynced = true))
        }
    }
}

