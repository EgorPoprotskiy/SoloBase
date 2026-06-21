package com.egorpoprotskiy.solobase.ui.notes

import com.egorpoprotskiy.solobase.domain.models.Note

data class ProjectNotesUiState(
    val notes: List<Note> = emptyList(),
    val projectId: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
