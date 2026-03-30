package com.egorpoprotskiy.solobase.domain.repository

import com.egorpoprotskiy.solobase.domain.models.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getProjects(): Flow<List<Project>>
    suspend fun addProject(project: Project)
    suspend fun deleteProject(id: String)
}