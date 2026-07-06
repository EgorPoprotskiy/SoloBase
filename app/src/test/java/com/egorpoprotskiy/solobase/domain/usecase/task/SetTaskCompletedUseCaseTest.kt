package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.models.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SetTaskCompletedUseCaseTest {
    @Test
    fun `invoke updates task completed state and keeps other fields`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = SetTaskCompletedUseCase(repository, scheduler)
        val task = Task(
            id = "task-id",
            content = "Read",
            isUrgent = true,
            isImportant = true,
            timestamp = 123L,
            position = 4,
            isCompleted = false
        )

        useCase(task, true)

        val updatedTask = repository.updatedTask!!
        assertEquals("task-id", updatedTask.id)
        assertEquals("Read", updatedTask.content)
        assertTrue(updatedTask.isUrgent)
        assertTrue(updatedTask.isImportant)
        assertEquals(123L, updatedTask.timestamp)
        assertEquals(4, updatedTask.position)
        assertTrue(updatedTask.isCompleted)
        assertEquals(TaskStatus.DONE.name, updatedTask.status)
        assertEquals(listOf("task-id"), scheduler.canceledTaskIds)
    }

    @Test
    fun `invoke can mark completed task as not completed`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = SetTaskCompletedUseCase(repository, scheduler)
        val task = Task(
            id = "task-id",
            content = "Read",
            isCompleted = true
        )

        useCase(task, false)

        val updatedTask = repository.updatedTask!!
        assertFalse(updatedTask.isCompleted)
        assertEquals(TaskStatus.TODO.name, updatedTask.status)
    }

    @Test
    fun `invoke schedules future reminder when marking task not completed`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = SetTaskCompletedUseCase(repository, scheduler)
        val task = Task(
            id = "task-id",
            content = "Read",
            isCompleted = true,
            reminderAt = System.currentTimeMillis() + 60_000L
        )

        useCase(task, false)

        assertEquals(repository.updatedTask, scheduler.scheduledTasks.single())
    }
}
