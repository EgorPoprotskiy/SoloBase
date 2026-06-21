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
    override fun getProjects(): Flow<List<Project>> = projectDao.getProjects()
        .map { projects -> projects.map { it.toDomain() } }

    override suspend fun addProject(project: Project) = projectDao.insertProject(project.toEntity())

    override suspend fun deleteProject(id: String) = projectDao.deleteProjectById(id)
}
