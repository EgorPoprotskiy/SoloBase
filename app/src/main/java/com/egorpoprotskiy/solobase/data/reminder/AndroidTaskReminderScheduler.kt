package com.egorpoprotskiy.solobase.data.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.reminder.TaskReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidTaskReminderScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) : TaskReminderScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(task: Task) {
        val reminderAt = task.reminderAt ?: return
        if (task.isCompleted || reminderAt <= System.currentTimeMillis()) return

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderAt,
            reminderPendingIntent(task.id, reminderAt)
        )
    }

    override fun cancel(taskId: String) {
        alarmManager.cancel(reminderPendingIntent(taskId, reminderAt = null))
    }

    private fun reminderPendingIntent(taskId: String, reminderAt: Long?): PendingIntent {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            action = TaskReminderConstants.ACTION_TASK_REMINDER
            data = Uri.parse("solobase://task-reminder/$taskId")
            putExtra(TaskReminderConstants.EXTRA_TASK_ID, taskId)
            if (reminderAt != null) {
                putExtra(TaskReminderConstants.EXTRA_REMINDER_AT, reminderAt)
            }
        }
        return PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
