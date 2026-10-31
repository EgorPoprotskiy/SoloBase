package com.egorpoprotskiy.solobase.domain.repository.remote

import com.egorpoprotskiy.solobase.data.remote.sync.FirestoreSyncResult
import com.egorpoprotskiy.solobase.domain.models.Project
import kotlinx.coroutines.flow.Flow

interface FirestoreProjectRepository {

    fun observeProjects(): Flow<List<Project>>

//    suspend fun saveProject(project: Project): Result<Unit>
suspend fun saveProject(project: Project): Result<FirestoreSyncResult<Project>>
    suspend fun deleteProject(projectId: String): Result<Unit>

    suspend fun getAllProjectsFromServer(): Result<List<Project>>
}