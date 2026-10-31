
package com.egorpoprotskiy.solobase.data.repository

import com.egorpoprotskiy.solobase.data.local.dao.ProjectDao
import com.egorpoprotskiy.solobase.data.local.mapper.toDomain
import com.egorpoprotskiy.solobase.data.local.mapper.toEntity
import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao
) : ProjectRepository {

    override fun getProjects(): Flow<List<Project>> =
        projectDao.getProjects().map { projects ->
            projects.map { it.toDomain() }
        }

    override suspend fun addProject(project: Project) {
        val now = System.currentTimeMillis()

        projectDao.insertProject(
            project.toEntity().copy(
                updatedAt = now,
                deletedAt = null,
                isSynced = false
            )
        )
    }

    override suspend fun updateProject(project: Project) {
        val existing = projectDao.getProjectById(project.id) ?: return
        val now = maxOf(System.currentTimeMillis(), existing.updatedAt + 1)

        projectDao.updateProject(
            project.toEntity().copy(
                updatedAt = now,
                deletedAt = null,
                isSynced = false
            )
        )
    }

    override suspend fun deleteProject(id: String) {
        val existing = projectDao.getProjectById(id) ?: return
        if (existing.deletedAt != null) return

        val now = maxOf(System.currentTimeMillis(), existing.updatedAt + 1)

        projectDao.markProjectDeleted(
            projectId = id,
            deletedAt = now,
            updatedAt = now
        )
    }
}
