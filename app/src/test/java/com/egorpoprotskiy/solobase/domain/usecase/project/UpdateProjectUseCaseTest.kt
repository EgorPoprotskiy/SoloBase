package com.egorpoprotskiy.solobase.domain.usecase.project

import com.egorpoprotskiy.solobase.domain.models.Project
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UpdateProjectUseCaseTest {
    @Test
    fun `invoke updates project name`() = runBlocking {
        val repository = FakeProjectRepository()
        val useCase = UpdateProjectUseCase(repository)
        val project = Project(
            id = "project-id",
            name = "SpaceMoto",
            description = ""
        )

        useCase(
            project = project,
            name = "SpaceMotor",
            description = ""
        )

        assertEquals("SpaceMotor", repository.updatedProject!!.name)
    }

    @Test
    fun `invoke updates project description`() = runBlocking {
        val repository = FakeProjectRepository()
        val useCase = UpdateProjectUseCase(repository)
        val project = Project(
            id = "project-id",
            name = "SpaceMotor",
            description = ""
        )

        useCase(
            project = project,
            name = "SpaceMotor",
            description = "Motorcycle project"
        )

        assertEquals("Motorcycle project", repository.updatedProject!!.description)
    }

    @Test
    fun `invoke keeps id color and created at`() = runBlocking {
        val repository = FakeProjectRepository()
        val useCase = UpdateProjectUseCase(repository)
        val project = Project(
            id = "project-id",
            name = "Old",
            description = "Old description",
            colorHex = "#123456",
            createdAt = 123L
        )

        useCase(
            project = project,
            name = "New",
            description = "New description"
        )

        val updatedProject = repository.updatedProject!!
        assertEquals("project-id", updatedProject.id)
        assertEquals("#123456", updatedProject.colorHex)
        assertEquals(123L, updatedProject.createdAt)
    }

    @Test
    fun `invoke does not save project with blank name`() = runBlocking {
        val repository = FakeProjectRepository()
        val useCase = UpdateProjectUseCase(repository)
        val project = Project(
            id = "project-id",
            name = "SpaceMotor",
            description = "Description"
        )

        useCase(
            project = project,
            name = "   ",
            description = "Updated description"
        )

        assertNull(repository.updatedProject)
    }
}
