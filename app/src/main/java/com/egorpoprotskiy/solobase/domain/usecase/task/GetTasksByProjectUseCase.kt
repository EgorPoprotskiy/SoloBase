package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksByProjectUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(projectId: String): Flow<List<Task>> = taskRepository.getTasksByProject(projectId)
}
