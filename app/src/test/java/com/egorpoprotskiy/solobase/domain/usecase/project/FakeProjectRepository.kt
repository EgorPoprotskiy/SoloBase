package com.egorpoprotskiy.solobase.domain.usecase.project

import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeProjectRepository : ProjectRepository {
    val projects = MutableStateFlow<List<Project>>(emptyList())
    var addedProject: Project? = null
    var updatedProject: Project? = null
    var deletedProjectId: String? = null

    override fun getProjects(): Flow<List<Project>> = projects

    override suspend fun addProject(project: Project) {
        addedProject = project
        projects.value = projects.value + project
    }

    override suspend fun updateProject(project: Project) {
        updatedProject = project
        projects.value = projects.value.map { currentProject ->
            if (currentProject.id == project.id) project else currentProject
        }
    }

    override suspend fun deleteProject(id: String) {
        deletedProjectId = id
        projects.value = projects.value.filterNot { it.id == id }
    }
}
