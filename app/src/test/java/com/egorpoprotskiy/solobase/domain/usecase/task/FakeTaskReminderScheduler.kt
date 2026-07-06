package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.reminder.TaskReminderScheduler

class FakeTaskReminderScheduler : TaskReminderScheduler {
    val scheduledTasks = mutableListOf<Task>()
    val canceledTaskIds = mutableListOf<String>()

    override fun schedule(task: Task) {
        scheduledTasks += task
    }

    override fun cancel(taskId: String) {
        canceledTaskIds += taskId
    }
}
