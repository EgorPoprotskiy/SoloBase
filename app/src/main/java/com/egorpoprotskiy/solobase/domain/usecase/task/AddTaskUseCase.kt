package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        content: String,
        isUrgent: Boolean = false,
        isImportant: Boolean = false,
        projectId: String? = null
    ) {
        val task = Task(
            content = content,
            isUrgent = isUrgent,
            isImportant = isImportant,
            timestamp = System.currentTimeMillis(),
            isCompleted = false,
            position = 0,
            projectId = projectId
        )
        taskRepository.addTask(task)
    }
}
