package com.egorpoprotskiy.solobase.domain.usecase.task

import org.junit.Assert.assertEquals
import org.junit.Test

class DeleteTaskUseCaseTest {
    @Test
    fun `invoke cancels reminder before deleting task`() = kotlinx.coroutines.runBlocking {
        val repository = FakeTaskRepository()
        val scheduler = FakeTaskReminderScheduler()
        val useCase = DeleteTaskUseCase(repository, scheduler)

        useCase("task-id")

        assertEquals(listOf("task-id"), scheduler.canceledTaskIds)
        assertEquals("task-id", repository.deletedTaskId)
    }
}
