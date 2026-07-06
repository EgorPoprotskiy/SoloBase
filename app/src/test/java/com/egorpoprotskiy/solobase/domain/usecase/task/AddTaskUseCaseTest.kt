package com.egorpoprotskiy.solobase.domain.usecase.task

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class AddTaskUseCaseTest {
    @Test
    fun `invoke creates task with provided details and default values`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = AddTaskUseCase(repository, scheduler)

        useCase(
            content = "Write tests",
            isUrgent = true,
            isImportant = false
        )

        val task = repository.addedTask
        assertNotNull(task)
        assertEquals("Write tests", task!!.content)
        assertEquals(true, task.isUrgent)
        assertEquals(false, task.isImportant)
        assertFalse(task.isCompleted)
        assertEquals(0, task.position)
        assertEquals(null, task.projectId)
        assertEquals(null, task.reminderAt)
        assertNotNull(task.timestamp)
        assertEquals(task, repository.tasks.value.single())
    }

    @Test
    fun `invoke saves project id when provided`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = AddTaskUseCase(repository, scheduler)

        useCase(
            content = "Project task",
            projectId = "project-1"
        )

        assertEquals("project-1", repository.addedTask!!.projectId)
    }

    @Test
    fun `invoke saves reminder when provided`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = AddTaskUseCase(repository, scheduler)
        val reminderAt = System.currentTimeMillis() + 60_000L

        useCase(
            content = "Task with reminder",
            reminderAt = reminderAt
        )

        assertEquals(reminderAt, repository.addedTask!!.reminderAt)
        assertEquals(repository.addedTask, scheduler.scheduledTasks.single())
    }

    @Test
    fun `invoke does not schedule reminder when reminder is null`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = AddTaskUseCase(repository, scheduler)

        useCase(content = "Task")

        assertEquals(emptyList<com.egorpoprotskiy.solobase.domain.models.Task>(), scheduler.scheduledTasks)
    }

    @Test
    fun `invoke does not schedule reminder when reminder is in the past`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = AddTaskUseCase(repository, scheduler)

        useCase(
            content = "Past reminder",
            reminderAt = System.currentTimeMillis() - 60_000L
        )

        assertEquals(emptyList<com.egorpoprotskiy.solobase.domain.models.Task>(), scheduler.scheduledTasks)
    }
}
