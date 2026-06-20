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
        val useCase = UpdateTaskDetailsUseCase(repository)
        val task = Task(
            id = "task-id",
            content = "Old content",
            isUrgent = false,
            isImportant = false,
            timestamp = 456L,
            position = 2,
            isCompleted = true,
            projectId = "project-id",
            tagId = "tag-id"
        )

        useCase(
            task = task,
            content = "New content",
            isUrgent = true,
            isImportant = true
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
    }

    @Test
    fun `invoke can unset urgency and importance`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val useCase = UpdateTaskDetailsUseCase(repository)
        val task = Task(
            content = "Task",
            isUrgent = true,
            isImportant = true
        )

        useCase(
            task = task,
            content = "Task",
            isUrgent = false,
            isImportant = false
        )

        val updatedTask = repository.updatedTask!!
        assertFalse(updatedTask.isUrgent)
        assertFalse(updatedTask.isImportant)
    }
}
