package com.egorpoprotskiy.solobase.ui.tasks

import com.egorpoprotskiy.solobase.domain.models.Task

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val displayMode: TasksDisplayMode = TasksDisplayMode.LIST,
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val selectedProjectId: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
