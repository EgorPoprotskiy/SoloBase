package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.models.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MoveTaskToStatusUseCaseTest {
    @Test
    fun `invoke marks task completed when moved to done`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val useCase = MoveTaskToStatusUseCase(repository)
        val task = Task(
            id = "task-id",
            content = "Ship",
            status = TaskStatus.IN_PROGRESS.name,
            isCompleted = false
        )

        useCase(task, TaskStatus.DONE)

        val updatedTask = repository.updatedTask!!
        assertEquals(TaskStatus.DONE.name, updatedTask.status)
        assertTrue(updatedTask.isCompleted)
    }

    @Test
    fun `invoke marks task not completed when moved from done to another status`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val useCase = MoveTaskToStatusUseCase(repository)
        val task = Task(
            id = "task-id",
            content = "Ship",
            status = TaskStatus.DONE.name,
            isCompleted = true
        )

        useCase(task, TaskStatus.IN_PROGRESS)

        val updatedTask = repository.updatedTask!!
        assertEquals(TaskStatus.IN_PROGRESS.name, updatedTask.status)
        assertFalse(updatedTask.isCompleted)
    }

    @Test
    fun `invoke updates status and keeps task not completed when moved between active columns`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val useCase = MoveTaskToStatusUseCase(repository)
        val task = Task(
            id = "task-id",
            content = "Ship",
            status = TaskStatus.TODO.name,
            isCompleted = false
        )

        useCase(task, TaskStatus.IN_PROGRESS)

        val updatedTask = repository.updatedTask!!
        assertEquals(TaskStatus.IN_PROGRESS.name, updatedTask.status)
        assertFalse(updatedTask.isCompleted)
    }
}
