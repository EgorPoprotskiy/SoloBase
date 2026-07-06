package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetTasksUseCaseTest {
    @Test
    fun `invoke returns only tasks without project`() = runBlocking {
        val repository = FakeTaskRepository()
        val useCase = GetTasksUseCase(repository)
        repository.tasks.value = listOf(
            Task(id = "global", content = "Global task", projectId = null),
            Task(id = "project", content = "Project task", projectId = "project-1")
        )

        val tasks = useCase().first()

        assertEquals(1, tasks.size)
        assertEquals("global", tasks.single().id)
        assertNull(tasks.single().projectId)
    }

    @Test
    fun `task created inside project does not appear in global task list`() = runBlocking {
        val repository = FakeTaskRepository()
        val addTaskUseCase = AddTaskUseCase(repository, FakeTaskReminderScheduler())
        val getTasksUseCase = GetTasksUseCase(repository)

        addTaskUseCase(
            content = "Project task",
            projectId = "project-1"
        )

        assertEquals(emptyList<Task>(), getTasksUseCase().first())
    }
}
