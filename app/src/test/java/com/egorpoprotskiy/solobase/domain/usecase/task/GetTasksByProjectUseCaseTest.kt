package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTasksByProjectUseCaseTest {
    @Test
    fun `invoke returns only tasks for selected project`() = runBlocking {
        val repository = FakeTaskRepository()
        val useCase = GetTasksByProjectUseCase(repository)
        repository.tasks.value = listOf(
            Task(id = "global", content = "Global task", projectId = null),
            Task(id = "project-1-task", content = "Project 1 task", projectId = "project-1"),
            Task(id = "project-2-task", content = "Project 2 task", projectId = "project-2")
        )

        val tasks = useCase("project-1").first()

        assertEquals(1, tasks.size)
        assertEquals("project-1-task", tasks.single().id)
        assertEquals("project-1", tasks.single().projectId)
    }
}
