package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateTaskDetailsUseCaseTest {
    @Test
    fun `invoke updates task details and keeps other fields`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = UpdateTaskDetailsUseCase(repository, scheduler)
        val task = Task(
            id = "task-id",
            content = "Old content",
            isUrgent = false,
            isImportant = false,
            timestamp = 456L,
            position = 2,
            isCompleted = true,
            projectId = "project-id",
            tagId = "tag-id",
            reminderAt = 111L
        )

        useCase(
            task = task,
            content = "New content",
            isUrgent = true,
            isImportant = true,
            reminderAt = null
        )

        val updatedTask = repository.updatedTask!!
        assertEquals("task-id", updatedTask.id)
        assertEquals("New content", updatedTask.content)
        assertTrue(updatedTask.isUrgent)
        assertTrue(updatedTask.isImportant)
        assertEquals(456L, updatedTask.timestamp)
        assertEquals(2, updatedTask.position)
        assertTrue(updatedTask.isCompleted)
        assertEquals("project-id", updatedTask.projectId)
        assertEquals("tag-id", updatedTask.tagId)
        assertEquals(null, updatedTask.reminderAt)
    }

    @Test
    fun `invoke can unset urgency and importance`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = UpdateTaskDetailsUseCase(repository, scheduler)
        val task = Task(
            content = "Task",
            isUrgent = true,
            isImportant = true
        )

        useCase(
            task = task,
            content = "Task",
            isUrgent = false,
            isImportant = false,
            reminderAt = null
        )

        val updatedTask = repository.updatedTask!!
        assertFalse(updatedTask.isUrgent)
        assertFalse(updatedTask.isImportant)
    }

    @Test
    fun `invoke can clear reminder`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = UpdateTaskDetailsUseCase(repository, scheduler)
        val task = Task(
            content = "Task",
            reminderAt = 333L
        )

        useCase(
            task = task,
            content = "Task",
            isUrgent = false,
            isImportant = false,
            reminderAt = null
        )

        assertEquals(null, repository.updatedTask!!.reminderAt)
        assertEquals(listOf(task.id), scheduler.canceledTaskIds)
    }

    @Test
    fun `invoke cancels old reminder and schedules new future reminder`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = UpdateTaskDetailsUseCase(repository, scheduler)
        val task = Task(
            id = "task-id",
            content = "Old",
            reminderAt = System.currentTimeMillis() + 30_000L
        )
        val newReminderAt = System.currentTimeMillis() + 60_000L

        useCase(
            task = task,
            content = "New",
            isUrgent = false,
            isImportant = false,
            reminderAt = newReminderAt
        )

        assertEquals(listOf("task-id"), scheduler.canceledTaskIds)
        assertEquals(newReminderAt, scheduler.scheduledTasks.single().reminderAt)
    }
}
