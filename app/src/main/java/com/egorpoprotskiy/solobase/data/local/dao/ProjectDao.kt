package com.egorpoprotskiy.solobase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.egorpoprotskiy.solobase.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    // Поток проектов для UI: удалённые проекты не отображаются.
    @Query("""
        SELECT * FROM projects
        WHERE deletedAt IS NULL
        ORDER BY createdAt DESC
    """)
    fun getProjects(): Flow<List<ProjectEntity>>

    // Все проекты, ожидающие отправки в Firestore, включая tombstone-записи.
    @Query("SELECT * FROM projects WHERE isSynced = 0")
    suspend fun getUnsyncedProjects(): List<ProjectEntity>

    // Получение проекта по ID, в том числе удалённого.
    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: String): ProjectEntity?

    // Локальная вставка или замена проекта.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    // Локальное обновление проекта.
    @Update
    suspend fun updateProject(project: ProjectEntity)

    // Применение данных из Firestore без постановки их в очередь отправки.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSyncedProject(project: ProjectEntity)

    // Мягкое удаление проекта.
    @Query("""
        UPDATE projects
        SET deletedAt = :deletedAt,
            updatedAt = :updatedAt,
            isSynced = 0
        WHERE id = :projectId
    """)
    suspend fun markProjectDeleted(
        projectId: String,
        deletedAt: Long,
        updatedAt: Long
    )

    // Подтверждение отправки только той версии, которая была загружена.
    @Query("""
        UPDATE projects
        SET isSynced = 1
        WHERE id = :projectId
          AND updatedAt = :uploadedUpdatedAt
    """)
    suspend fun markProjectSynced(
        projectId: String,
        uploadedUpdatedAt: Long
    )

    // Все проекты для первоначального объединения с Firestore.
    @Query("SELECT * FROM projects")
    suspend fun getAllProjectsForSync(): List<ProjectEntity>

    @Transaction
    suspend fun applySyncedProjectIfNewer(project: ProjectEntity) {
        val localProject = getProjectById(project.id)

        if (localProject == null || project.updatedAt >= localProject.updatedAt) {
            upsertSyncedProject(project.copy(isSynced = true))
        }
    }
}
