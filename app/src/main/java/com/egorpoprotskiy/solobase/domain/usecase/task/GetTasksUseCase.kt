package com.egorpoprotskiy.solobase.domain.usecase.task

import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> = taskRepository.getTasks()
}
