package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.models.TaskStatus
import com.egorpoprotskiy.solobase.domain.reminder.TaskReminderScheduler
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import javax.inject.Inject

class MoveTaskToStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) {
    suspend operator fun invoke(task: Task, newStatus: TaskStatus) {
        val updatedTask = task.copy(
            status = newStatus.name,
            isCompleted = newStatus == TaskStatus.DONE
        )
        taskRepository.updateTask(updatedTask)
        if (newStatus == TaskStatus.DONE) {
            taskReminderScheduler.cancel(task.id)
        } else if (updatedTask.shouldScheduleReminder()) {
            taskReminderScheduler.schedule(updatedTask)
        }
    }

    private fun Task.shouldScheduleReminder(): Boolean {
        return reminderAt != null && reminderAt > System.currentTimeMillis() && !isCompleted
    }
}
