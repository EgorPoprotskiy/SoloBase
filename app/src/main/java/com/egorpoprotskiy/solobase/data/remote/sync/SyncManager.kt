
package com.egorpoprotskiy.solobase.data.remote.sync

import com.egorpoprotskiy.solobase.data.local.dao.NoteDao
import com.egorpoprotskiy.solobase.data.local.dao.ProjectDao
import com.egorpoprotskiy.solobase.data.local.dao.SyncStateDao
import com.egorpoprotskiy.solobase.data.local.dao.TaskDao
import com.egorpoprotskiy.solobase.data.local.mapper.toDomain
import com.egorpoprotskiy.solobase.data.local.mapper.toEntity
import com.egorpoprotskiy.solobase.data.remote.sync.FirestoreSyncResult
import com.egorpoprotskiy.solobase.domain.models.Note
import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreNoteRepository
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreProjectRepository
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreTaskRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao,
    private val noteDao: NoteDao,
    private val syncStateDao: SyncStateDao,
    private val firestoreTaskRepository: FirestoreTaskRepository,
    private val firestoreProjectRepository: FirestoreProjectRepository,
    private val firestoreNoteRepository: FirestoreNoteRepository
) {

    // Не допускает одновременного запуска нескольких синхронизаций.
    private val syncMutex = Mutex()

    /**
     * Выполняет полное объединение локальных и облачных данных.
     * Используется как для первой, так и для последующих синхронизаций.
     */
    suspend fun sync(): Result<Unit> = syncMutex.withLock {
        try {
            val uid = auth.currentUser?.uid
                ?: return@withLock Result.failure(
                    IllegalStateException("User is not authenticated")
                )

            // Сначала получаем актуальные данные непосредственно с сервера.
            val remoteTasks = firestoreTaskRepository
                .getAllTasksFromServer()
                .getOrThrow()

            val remoteProjects = firestoreProjectRepository
                .getAllProjectsFromServer()
                .getOrThrow()

            val remoteNotes = firestoreNoteRepository
                .getAllNotesFromServer()
                .getOrThrow()

            // Читаем все локальные записи, включая tombstones.
            val localTasks = taskDao.getAllTasksForSync()
            val localProjects = projectDao.getAllProjectsForSync()
            val localNotes = noteDao.getAllNotesForSync()

            // Объединяем каждую категорию данных.
            syncTasks(localTasks, remoteTasks)
            syncProjects(localProjects, remoteProjects)
            syncNotes(localNotes, remoteNotes)

            // Отмечаем первоначальную синхронизацию завершённой
            // только после успешной обработки всех трёх категорий.
            syncStateDao.saveSyncState(
                com.egorpoprotskiy.solobase.data.local.entity.SyncStateEntity(
                    uid = uid,
                    initialSyncCompleted = true
                )
            )

            Result.success(Unit)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun syncTasks(
        localTasks: List<com.egorpoprotskiy.solobase.data.local.entity.TaskEntity>,
        remoteTasks: List<Task>
    ) {
        val remoteMap = remoteTasks.associateBy { it.id }

        for (local in localTasks) {
            val remote = remoteMap[local.id]

            if (remote == null || local.updatedAt > remote.updatedAt) {
                uploadTask(local)
            } else {
                taskDao.applySyncedTaskIfNewer(remote.toEntity())
            }
        }

        // Добавляем облачные задачи, которых ещё нет локально.
        val localIds = localTasks.mapTo(HashSet()) { it.id }

        for (remote in remoteTasks) {
            if (remote.id !in localIds) {
                taskDao.applySyncedTaskIfNewer(remote.toEntity())
            }
        }
    }

    private suspend fun uploadTask(
        task: com.egorpoprotskiy.solobase.data.local.entity.TaskEntity
    ) {
        when (
            val result = firestoreTaskRepository
                .saveTask(task.toDomain())
                .getOrThrow()
        ) {
            FirestoreSyncResult.Uploaded -> {
                taskDao.markTaskSynced(task.id, task.updatedAt)
            }

            is FirestoreSyncResult.RemoteNewer -> {
                taskDao.applySyncedTaskIfNewer(result.remote.toEntity())
            }
        }
    }

    private suspend fun syncProjects(
        localProjects: List<com.egorpoprotskiy.solobase.data.local.entity.ProjectEntity>,
        remoteProjects: List<Project>
    ) {
        val remoteMap = remoteProjects.associateBy { it.id }

        for (local in localProjects) {
            val remote = remoteMap[local.id]

            if (remote == null || local.updatedAt > remote.updatedAt) {
                uploadProject(local)
            } else {
                projectDao.applySyncedProjectIfNewer(remote.toEntity())
            }
        }

        val localIds = localProjects.mapTo(HashSet()) { it.id }

        for (remote in remoteProjects) {
            if (remote.id !in localIds) {
                projectDao.applySyncedProjectIfNewer(remote.toEntity())
            }
        }
    }

    private suspend fun uploadProject(
        project: com.egorpoprotskiy.solobase.data.local.entity.ProjectEntity
    ) {
        when (
            val result = firestoreProjectRepository
                .saveProject(project.toDomain())
                .getOrThrow()
        ) {
            FirestoreSyncResult.Uploaded -> {
                projectDao.markProjectSynced(project.id, project.updatedAt)
            }

            is FirestoreSyncResult.RemoteNewer -> {
                projectDao.applySyncedProjectIfNewer(result.remote.toEntity())
            }
        }
    }

    private suspend fun syncNotes(
        localNotes: List<com.egorpoprotskiy.solobase.data.local.entity.NoteEntity>,
        remoteNotes: List<Note>
    ) {
        val remoteMap = remoteNotes.associateBy { it.id }

        for (local in localNotes) {
            val remote = remoteMap[local.id]

            if (remote == null || local.updatedAt > remote.updatedAt) {
                uploadNote(local)
            } else {
                noteDao.applySyncedNoteIfNewer(remote.toEntity())
            }
        }

        val localIds = localNotes.mapTo(HashSet()) { it.id }

        for (remote in remoteNotes) {
            if (remote.id !in localIds) {
                noteDao.applySyncedNoteIfNewer(remote.toEntity())
            }
        }
    }

    private suspend fun uploadNote(
        note: com.egorpoprotskiy.solobase.data.local.entity.NoteEntity
    ) {
        when (
            val result = firestoreNoteRepository
                .saveNote(note.toDomain())
                .getOrThrow()
        ) {
            FirestoreSyncResult.Uploaded -> {
                noteDao.markNoteSynced(note.id, note.updatedAt)
            }

            is FirestoreSyncResult.RemoteNewer -> {
                noteDao.applySyncedNoteIfNewer(result.remote.toEntity())
            }
        }
    }
}
