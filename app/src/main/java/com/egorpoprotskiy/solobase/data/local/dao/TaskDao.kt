package com.egorpoprotskiy.solobase.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.egorpoprotskiy.solobase.domain.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Получаем все задачи, отсортированные по времени (свежие сверху)
    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<Task>>
    // Добавляем или заменяем (если ID совпал)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)
    @Update
    suspend fun updateTask(task: Task)
    @Delete
    suspend fun deleteTask(task: Task)
    // Удобный метод для удаления по ID
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)
}