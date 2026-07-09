package com.egorpoprotskiy.solobase.domain.usecase.project

import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.domain.repository.ProjectRepository
import javax.inject.Inject

class UpdateProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(
        project: Project,
        name: String,
        description: String
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return

        projectRepository.updateProject(
            project.copy(
                name = trimmedName,
                description = description
            )
        )
    }
}
