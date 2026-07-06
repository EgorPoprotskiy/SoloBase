package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.models.TaskStatus
import com.egorpoprotskiy.solobase.domain.reminder.TaskReminderScheduler
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import javax.inject.Inject

class SetTaskCompletedUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) {
    suspend operator fun invoke(task: Task, isCompleted: Boolean) {
        val status = if (isCompleted) TaskStatus.DONE else TaskStatus.TODO
        val updatedTask = task.copy(
            isCompleted = isCompleted,
            status = status.name
        )
        taskRepository.updateTask(updatedTask)
        if (isCompleted) {
            taskReminderScheduler.cancel(task.id)
        } else if (updatedTask.shouldScheduleReminder()) {
            taskReminderScheduler.schedule(updatedTask)
        }
    }

    private fun Task.shouldScheduleReminder(): Boolean {
        return reminderAt != null && reminderAt > System.currentTimeMillis() && !isCompleted
    }
}
