package com.egorpoprotskiy.solobase.ui.projects

sealed interface ProjectsUiEvent {
    data class ShowSnackbar(val message: String) : ProjectsUiEvent
}
