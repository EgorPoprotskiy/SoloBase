package com.egorpoprotskiy.solobase.data.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskReminderReceiver : BroadcastReceiver() {
    @Inject lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TaskReminderConstants.ACTION_TASK_REMINDER) return
        val taskId = intent.getStringExtra(TaskReminderConstants.EXTRA_TASK_ID) ?: return
        val scheduledReminderAt = intent.getLongExtra(TaskReminderConstants.EXTRA_REMINDER_AT, -1L)
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val task = taskRepository.getTaskById(taskId)
                val reminderAt = task?.reminderAt
                if (
                    task != null &&
                    !task.isCompleted &&
                    reminderAt != null &&
                    reminderAt == scheduledReminderAt &&
                    reminderAt <= System.currentTimeMillis()
                ) {
                    TaskNotificationHelper.showTaskReminder(context, task)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
