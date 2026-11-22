
package com.egorpoprotskiy.solobase.data.remote.sync

import android.util.Log
import com.egorpoprotskiy.solobase.data.local.dao.NoteDao
import com.egorpoprotskiy.solobase.data.local.dao.ProjectDao
import com.egorpoprotskiy.solobase.data.local.dao.SyncStateDao
import com.egorpoprotskiy.solobase.data.local.dao.TaskDao
import com.egorpoprotskiy.solobase.data.local.entity.NoteEntity
import com.egorpoprotskiy.solobase.data.local.entity.ProjectEntity
import com.egorpoprotskiy.solobase.data.local.entity.SyncStateEntity
import com.egorpoprotskiy.solobase.data.local.entity.TaskEntity
import com.egorpoprotskiy.solobase.data.local.mapper.toDomain
import com.egorpoprotskiy.solobase.data.local.mapper.toEntity
import com.egorpoprotskiy.solobase.domain.models.Note
import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreNoteRepository
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreProjectRepository
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreTaskRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
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

    private val syncMutex = Mutex()

    // Синхронизация живёт в scope singleton-менеджера, а не в scope вызывающей ViewModel.
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val syncStateLock = Any()
    private val activeSyncJobs = mutableSetOf<Job>()

    @Volatile
    private var syncEnabled = true

    /**
     * Запрещает новые синхронизации и отменяет все активные вызовы.
     * Вызывать перед logout.
     */
    fun cancelActiveSync() {
        val jobsToCancel = synchronized(syncStateLock) {
            syncEnabled = false
            activeSyncJobs.toList()
        }

        jobsToCancel.forEach { job ->
            job.cancel(
                CancellationException("Synchronization cancelled: logout")
            )
        }
    }

    /**
     * Разрешает синхронизацию после успешной авторизации.
     */
    fun resumeSync() {
        synchronized(syncStateLock) {
            syncEnabled = true
        }
    }

    /**
     * Запускает синхронизацию в отдельной корутине, принадлежащей SyncManager.
     * Отмена синхронизации не отменяет корутину вызывающей ViewModel/Worker.
     */
    suspend fun sync(): Result<Unit> {
        val callerContext = currentCoroutineContext()
        callerContext.ensureActive()

        // LAZY важен: сначала регистрируем Job под lock, затем запускаем.
        // Иначе cancelActiveSync() мог бы не увидеть ещё не зарегистрированную работу.
        val syncDeferred = syncScope.async(start = CoroutineStart.LAZY) {
            performSync()
        }

        val accepted = synchronized(syncStateLock) {
            if (!syncEnabled) {
                false
            } else {
                activeSyncJobs.add(syncDeferred)
                true
            }
        }

        if (!accepted) {
            syncDeferred.cancel()
            return Result.failure(
                CancellationException("Synchronization is disabled")
            )
        }

        // Job остаётся зарегистрированным до собственного завершения даже если
        // вызывающая ViewModel будет уничтожена или её coroutine отменится.
        syncDeferred.invokeOnCompletion {
            synchronized(syncStateLock) {
                activeSyncJobs.remove(syncDeferred)
            }
        }

        syncDeferred.start()

        return try {
            syncDeferred.await()
        } catch (e: CancellationException) {
            // Внешняя отмена вызывающей корутины остаётся structured cancellation.
            // Внутренняя отмена менеджером (например, при logout) возвращается как Result.
            callerContext.ensureActive()
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Реальное выполнение синхронизации в manager-owned coroutine. */
    private suspend fun performSync(): Result<Unit> {
        return syncMutex.withLock {
            val uid = auth.currentUser?.uid
                ?: return@withLock Result.failure(
                    IllegalStateException("User is not authenticated")
                )

            try {
                ensureSyncValid(uid)
                // Получаем актуальные данные из Firestore строго для зафиксированного UID.
                val remoteTasks = firestoreTaskRepository
                    .getAllTasksFromServer(uid)
                    .getOrThrow()

                ensureSyncValid(uid)

                val remoteProjects = firestoreProjectRepository
                    .getAllProjectsFromServer(uid)
                    .getOrThrow()

                ensureSyncValid(uid)

                val remoteNotes = firestoreNoteRepository
                    .getAllNotesFromServer(uid)
                    .getOrThrow()

                ensureSyncValid(uid)

                // Получаем локальные данные, включая tombstones.
                val localTasks = taskDao.getAllTasksForSync()
                val localProjects = projectDao.getAllProjectsForSync()
                val localNotes = noteDao.getAllNotesForSync()

                ensureSyncValid(uid)

                // Объединяем данные.
                syncTasks(localTasks, remoteTasks, uid)
                ensureSyncValid(uid)

                syncProjects(localProjects, remoteProjects, uid)
                ensureSyncValid(uid)

                syncNotes(localNotes, remoteNotes, uid)
                ensureSyncValid(uid)

                syncStateDao.saveSyncState(
                    SyncStateEntity(
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
    }

    /**
     * Проверяет, что текущая корутина не отменена
     * и пользователь всё ещё тот же.
     */
    private suspend fun ensureSyncValid(expectedUid: String) {
        currentCoroutineContext().ensureActive()

        if (auth.currentUser?.uid != expectedUid) {
            throw CancellationException(
                "Synchronization cancelled: user changed"
            )
        }
    }

    // -------------------------------------------------------------------------
    // TASKS
    // -------------------------------------------------------------------------

    private suspend fun syncTasks(
        localTasks: List<TaskEntity>,
        remoteTasks: List<Task>,
        uid: String
    ) {
        val remoteMap = remoteTasks.associateBy { it.id }

        Log.d(
            "SoloBaseSync",
            "========== TASK SYNC START: local=${localTasks.size}, remote=${remoteTasks.size} =========="
        )

        for (local in localTasks) {
            ensureSyncValid(uid)

            val remote = remoteMap[local.id]

            val decision = when {
                remote == null -> "UPLOAD_LOCAL_REMOTE_MISSING"
                local.updatedAt > remote.updatedAt -> "UPLOAD_LOCAL_NEWER"
                else -> "APPLY_REMOTE"
            }

            Log.d(
                "SoloBaseSync",
                "Task ${local.id}: " +
                        "LOCAL completed=${local.isCompleted}, updatedAt=${local.updatedAt}, synced=${local.isSynced}; " +
                        "REMOTE completed=${remote?.isCompleted}, updatedAt=${remote?.updatedAt}; " +
                        "DECISION=$decision"
            )

            if (remote == null || local.updatedAt > remote.updatedAt) {
                uploadTask(local, uid)
            } else {
                ensureSyncValid(uid)
                taskDao.applySyncedTaskIfNewer(remote.toEntity())

                Log.d(
                    "SoloBaseSync",
                    "Task ${local.id}: remote version applied to Room"
                )
            }
        }

        val localIds = localTasks.mapTo(HashSet()) { it.id }

        for (remote in remoteTasks) {
            ensureSyncValid(uid)

            if (remote.id !in localIds) {
                Log.d(
                    "SoloBaseSync",
                    "Task ${remote.id}: only in Firestore; " +
                            "completed=${remote.isCompleted}, updatedAt=${remote.updatedAt}"
                )

                taskDao.applySyncedTaskIfNewer(remote.toEntity())
            }
        }

        Log.d("SoloBaseSync", "========== TASK SYNC FINISHED ==========")
    }

    private suspend fun uploadTask(
        task: TaskEntity,
        uid: String
    ) {
        ensureSyncValid(uid)

        Log.d(
            "SoloBaseSync",
            "Task ${task.id}: uploading LOCAL " +
                    "completed=${task.isCompleted}, updatedAt=${task.updatedAt}"
        )

        val result = firestoreTaskRepository
            .saveTask(task.toDomain(), uid)
            .getOrThrow()

        ensureSyncValid(uid)

        when (result) {
            FirestoreSyncResult.Uploaded -> {
                Log.d(
                    "SoloBaseSync",
                    "Task ${task.id}: UPLOAD SUCCESS"
                )

                taskDao.markTaskSynced(task.id, task.updatedAt)
            }

            is FirestoreSyncResult.RemoteNewer -> {
                Log.d(
                    "SoloBaseSync",
                    "Task ${task.id}: FIRESTORE VERSION WON " +
                            "completed=${result.remote.isCompleted}, " +
                            "updatedAt=${result.remote.updatedAt}"
                )

                ensureSyncValid(uid)
                taskDao.applySyncedTaskIfNewer(result.remote.toEntity())
            }
        }
    }

    // -------------------------------------------------------------------------
    // PROJECTS
    // -------------------------------------------------------------------------

    private suspend fun syncProjects(
        localProjects: List<ProjectEntity>,
        remoteProjects: List<Project>,
        uid: String
    ) {
        val remoteMap = remoteProjects.associateBy { it.id }

        for (local in localProjects) {
            ensureSyncValid(uid)

            val remote = remoteMap[local.id]

            if (remote == null || local.updatedAt > remote.updatedAt) {
                uploadProject(local, uid)
            } else {
                ensureSyncValid(uid)
                projectDao.applySyncedProjectIfNewer(remote.toEntity())
            }
        }

        val localIds = localProjects.mapTo(HashSet()) { it.id }

        for (remote in remoteProjects) {
            ensureSyncValid(uid)

            if (remote.id !in localIds) {
                projectDao.applySyncedProjectIfNewer(remote.toEntity())
            }
        }
    }

    private suspend fun uploadProject(
        project: ProjectEntity,
        uid: String
    ) {
        ensureSyncValid(uid)

        val result = firestoreProjectRepository
            .saveProject(project.toDomain(), uid)
            .getOrThrow()

        ensureSyncValid(uid)

        when (result) {
            FirestoreSyncResult.Uploaded -> {
                projectDao.markProjectSynced(
                    project.id,
                    project.updatedAt
                )
            }

            is FirestoreSyncResult.RemoteNewer -> {
                ensureSyncValid(uid)
                projectDao.applySyncedProjectIfNewer(
                    result.remote.toEntity()
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // NOTES
    // -------------------------------------------------------------------------

    private suspend fun syncNotes(
        localNotes: List<NoteEntity>,
        remoteNotes: List<Note>,
        uid: String
    ) {
        val remoteMap = remoteNotes.associateBy { it.id }

        for (local in localNotes) {
            ensureSyncValid(uid)

            val remote = remoteMap[local.id]

            if (remote == null || local.updatedAt > remote.updatedAt) {
                uploadNote(local, uid)
            } else {
                ensureSyncValid(uid)
                noteDao.applySyncedNoteIfNewer(remote.toEntity())
            }
        }

        val localIds = localNotes.mapTo(HashSet()) { it.id }

        for (remote in remoteNotes) {
            ensureSyncValid(uid)

            if (remote.id !in localIds) {
                noteDao.applySyncedNoteIfNewer(remote.toEntity())
            }
        }
    }

    private suspend fun uploadNote(
        note: NoteEntity,
        uid: String
    ) {
        ensureSyncValid(uid)

        val result = firestoreNoteRepository
            .saveNote(note.toDomain(), uid)
            .getOrThrow()

        ensureSyncValid(uid)

        when (result) {
            FirestoreSyncResult.Uploaded -> {
                noteDao.markNoteSynced(
                    note.id,
                    note.updatedAt
                )
            }

            is FirestoreSyncResult.RemoteNewer -> {
                ensureSyncValid(uid)
                noteDao.applySyncedNoteIfNewer(
                    result.remote.toEntity()
                )
            }
        }
    }
}
