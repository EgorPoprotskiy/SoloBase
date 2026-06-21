package com.egorpoprotskiy.solobase.ui.notes

sealed interface ProjectNotesUiEvent {
    data class ShowSnackbar(val message: String) : ProjectNotesUiEvent
}
