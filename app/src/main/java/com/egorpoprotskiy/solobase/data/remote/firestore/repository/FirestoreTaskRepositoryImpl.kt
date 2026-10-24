
package com.egorpoprotskiy.solobase.data.remote.firestore.repository

import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toDomain
import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toFirestore
import com.egorpoprotskiy.solobase.data.remote.firestore.model.FirestoreTask
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreTaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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

    override suspend fun saveTask(task: Task): Result<Unit> = try {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(
                IllegalStateException("User is not authenticated")
            )

        firestore
            .collection(USERS_COLLECTION)
            .document(uid)
            .collection(TASKS_COLLECTION)
            .document(task.id)
            .set(task.toFirestore())
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
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
            .update(
                mapOf(
                    "deletedAt" to now,
                    "updatedAt" to now
                )
            )
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val TASKS_COLLECTION = "tasks"
    }
}
