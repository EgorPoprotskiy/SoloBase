package com.egorpoprotskiy.solobase.ui.tasks

sealed interface TasksUiEvent {
    data class ShowSnackbar(val message: String) : TasksUiEvent
}
