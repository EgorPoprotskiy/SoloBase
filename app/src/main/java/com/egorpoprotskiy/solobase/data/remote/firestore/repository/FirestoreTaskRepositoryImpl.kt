
package com.egorpoprotskiy.solobase.data.remote.firestore.repository

import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toDomain
import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toFirestore
import com.egorpoprotskiy.solobase.data.remote.firestore.model.FirestoreTask
import com.egorpoprotskiy.solobase.data.remote.sync.FirestoreSyncResult
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreTaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.firestore.Source

/**
 * Реализация облачного репозитория задач.
 * Все документы находятся в users/{uid}/tasks/{taskId}.
 */
class FirestoreTaskRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FirestoreTaskRepository {

    override fun observeTasks(): Flow<List<Task>> = callbackFlow {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            close(IllegalStateException("User is not authenticated"))
            return@callbackFlow
        }

        val listenerRegistration: ListenerRegistration =
            firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(TASKS_COLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val tasks = snapshot.documents.mapNotNull { document ->
                        document.toObject(FirestoreTask::class.java)
                            ?.toDomain(document.id)
                    }

                    // Включаем и помеченные удалёнными задачи:
                    // они понадобятся для корректной синхронизации.
                    trySend(tasks)
                }

        // Отключает Firestore listener, когда Flow больше не собирается.
        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun saveTask(
        task: Task
    ): Result<FirestoreSyncResult<Task>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: throw IllegalStateException("User is not authenticated")

            // Ссылка на документ задачи текущего пользователя.
            val documentRef = firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(TASKS_COLLECTION)
                .document(task.id)

            // Проверяем облачную версию и записываем локальную,
            // только если она новее.
            val result = firestore.runTransaction { transaction ->

                val snapshot = transaction.get(documentRef)

                val remoteTask = snapshot
                    .toObject(FirestoreTask::class.java)
                    ?.toDomain(snapshot.id)

                // При равенстве updatedAt приоритет у Firestore.
                if (remoteTask != null && remoteTask.updatedAt >= task.updatedAt) {
                    return@runTransaction FirestoreSyncResult.RemoteNewer(remoteTask)
                }

                // Локальная версия новее — сохраняем её.
                transaction.set(documentRef, task.toFirestore())

                FirestoreSyncResult.Uploaded
            }.await()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun deleteTask(taskId: String): Result<Unit> = try {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(
                IllegalStateException("User is not authenticated")
            )

        val now = System.currentTimeMillis()

        firestore
            .collection(USERS_COLLECTION)
            .document(uid)
            .collection(TASKS_COLLECTION)
            .document(taskId)
            .set(
                mapOf(
                    "deletedAt" to now,
                    "updatedAt" to now
                ),
                SetOptions.merge()
            )
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getAllTasksFromServer(): Result<List<Task>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: throw IllegalStateException("User is not authenticated")

            // Получаем актуальные данные непосредственно с сервера Firestore.
            val snapshot = firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(TASKS_COLLECTION)
                .get(Source.SERVER)
                .await()

            // Преобразуем документы Firestore в доменные модели Task.
            val tasks = snapshot.documents.mapNotNull { document ->
                document.toObject(FirestoreTask::class.java)
                    ?.toDomain(document.id)
            }

            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private companion object {
        const val USERS_COLLECTION = "users"
        const val TASKS_COLLECTION = "tasks"
    }
}
