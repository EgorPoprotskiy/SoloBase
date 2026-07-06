package com.egorpoprotskiy.solobase.domain.reminder

import com.egorpoprotskiy.solobase.domain.models.Task

interface TaskReminderScheduler {
    fun schedule(task: Task)
    fun cancel(taskId: String)
}
