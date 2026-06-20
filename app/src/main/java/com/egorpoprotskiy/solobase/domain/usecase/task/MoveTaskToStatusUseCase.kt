package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.models.TaskStatus
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import javax.inject.Inject

class MoveTaskToStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task, newStatus: TaskStatus) {
        taskRepository.updateTask(
            task.copy(
                status = newStatus.name,
                isCompleted = newStatus == TaskStatus.DONE
            )
        )
    }
}
