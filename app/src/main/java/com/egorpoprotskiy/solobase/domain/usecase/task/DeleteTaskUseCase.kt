package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.reminder.TaskReminderScheduler
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) {
    suspend operator fun invoke(taskId: String) {
        taskReminderScheduler.cancel(taskId)
        taskRepository.deleteTask(taskId)
    }
}
