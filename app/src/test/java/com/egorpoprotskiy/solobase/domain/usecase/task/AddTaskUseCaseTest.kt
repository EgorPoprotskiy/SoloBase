package com.egorpoprotskiy.solobase.domain.usecase.task

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class AddTaskUseCaseTest {
    @Test
    fun `invoke creates task with provided details and default values`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val useCase = AddTaskUseCase(repository)

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
        assertNotNull(task.timestamp)
        assertEquals(task, repository.tasks.value.single())
    }
}
