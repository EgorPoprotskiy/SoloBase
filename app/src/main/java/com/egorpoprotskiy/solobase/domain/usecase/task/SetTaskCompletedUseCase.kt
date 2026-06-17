package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import javax.inject.Inject

class SetTaskCompletedUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task, isCompleted: Boolean) {
        taskRepository.updateTask(task.copy(isCompleted = isCompleted))
    }
}
