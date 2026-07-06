package com.egorpoprotskiy.solobase.domain.usecase.project

import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.domain.models.ProjectDefaults
import com.egorpoprotskiy.solobase.domain.repository.ProjectRepository
import javax.inject.Inject

class AddProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String = "",
        colorHex: String = ProjectDefaults.DEFAULT_COLOR_HEX
    ) {
        val project = Project(
            name = name,
            description = description,
            colorHex = colorHex,
            createdAt = System.currentTimeMillis()
        )
        projectRepository.addProject(project)
    }
}
