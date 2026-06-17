package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskDetailsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        task: Task,
        content: String,
        isUrgent: Boolean,
        isImportant: Boolean
    ) {
        taskRepository.updateTask(
            task.copy(
                content = content,
                isUrgent = isUrgent,
                isImportant = isImportant
            )
        )
    }
}
