package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.reminder.TaskReminderScheduler
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) {
    suspend operator fun invoke(
        content: String,
        isUrgent: Boolean = false,
        isImportant: Boolean = false,
        projectId: String? = null,
        reminderAt: Long? = null
    ) {
        val task = Task(
            content = content,
            isUrgent = isUrgent,
            isImportant = isImportant,
            timestamp = System.currentTimeMillis(),
            reminderAt = reminderAt,
            isCompleted = false,
            position = 0,
            projectId = projectId
        )
        taskRepository.addTask(task)
        if (task.shouldScheduleReminder()) {
            taskReminderScheduler.schedule(task)
        }
    }

    private fun Task.shouldScheduleReminder(): Boolean {
        return reminderAt != null && reminderAt > System.currentTimeMillis() && !isCompleted
    }
}
