package com.egorpoprotskiy.solobase.ui.projects

import com.egorpoprotskiy.solobase.domain.models.Project

data class ProjectsUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
