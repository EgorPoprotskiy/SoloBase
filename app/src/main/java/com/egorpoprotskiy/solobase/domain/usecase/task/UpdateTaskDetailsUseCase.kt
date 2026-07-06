package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.reminder.TaskReminderScheduler
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskDetailsUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) {
    suspend operator fun invoke(
        task: Task,
        content: String,
        isUrgent: Boolean,
        isImportant: Boolean,
        reminderAt: Long?
    ) {
        val updatedTask = task.copy(
            content = content,
            isUrgent = isUrgent,
            isImportant = isImportant,
            reminderAt = reminderAt
        )
        taskReminderScheduler.cancel(task.id)
        taskRepository.updateTask(updatedTask)
        if (updatedTask.shouldScheduleReminder()) {
            taskReminderScheduler.schedule(updatedTask)
        }
    }

    private fun Task.shouldScheduleReminder(): Boolean {
        return reminderAt != null && reminderAt > System.currentTimeMillis() && !isCompleted
    }
}
