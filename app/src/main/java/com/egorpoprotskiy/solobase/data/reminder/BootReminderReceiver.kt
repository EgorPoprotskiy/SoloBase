package com.egorpoprotskiy.solobase.data.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.egorpoprotskiy.solobase.domain.reminder.TaskReminderScheduler
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BootReminderReceiver : BroadcastReceiver() {
    @Inject lateinit var taskRepository: TaskRepository
    @Inject lateinit var taskReminderScheduler: TaskReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                taskRepository.getFutureActiveReminderTasks(System.currentTimeMillis())
                    .forEach { task -> taskReminderScheduler.schedule(task) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
