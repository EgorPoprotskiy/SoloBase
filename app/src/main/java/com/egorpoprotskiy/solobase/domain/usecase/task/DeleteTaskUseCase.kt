package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String) = taskRepository.deleteTask(taskId)
}
