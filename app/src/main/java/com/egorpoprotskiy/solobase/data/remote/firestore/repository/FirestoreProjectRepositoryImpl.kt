package com.egorpoprotskiy.solobase.data.remote.firestore.repository

import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toDomain
import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toFirestore
import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreProjectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.egorpoprotskiy.solobase.data.remote.firestore.model.FirestoreProject

/* Удаленный репозиторий для проектов, хранящихся в облачном Firestore.
*
* Путь к Firestore:
* /users/{uid}/projects/{ProjectID}
*
* Этот репозиторий отвечает только за взаимодействие с Firestore.
* Локальная синхронизация в помещении будет подключена позже.
 */
class FirestoreProjectRepositoryImpl (
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): FirestoreProjectRepository {
    /**
     * Отслеживает все проекты текущего пользователя, прошедшего проверку подлинности.
     *
     * Изменения в Firestore передаются по потоку всякий раз, когда изменяется коллекция.
     */
    override fun observeProjects(): Flow<List<Project>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            close(
                IllegalStateException("User is not authenticated")
            )
            return@callbackFlow
        }
        val listenerRegistration: ListenerRegistration =
            firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(PROJECTS_COLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val project = snapshot.documents.mapNotNull { document ->
                        document
                            .toObject(FirestoreProject::class.java)
                            ?.toDomain(document.id)
                    }
                    trySend(project)
                }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    /**
     * Создает или полностью заменяет проектный документ в Firestore.
     *
     * В качестве идентификатора документа Firestore используется существующий project.id.
     */
    override suspend fun saveProject(project: Project): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(
                    IllegalStateException("User is not authenticated")
                )
            firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(PROJECTS_COLLECTION)
                .document(project.id)
                .set(project.toFirestore())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Документ проекта безвозвратно удаляется из Firestore.
     *
     * Синхронизация с программным удалением будет реализована позже.
     */
    override suspend fun deleteProject(projectId: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(
                    IllegalStateException("User is not authenticated")
                )
            firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(PROJECTS_COLLECTION)
                .document(projectId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private companion object {
        const val USERS_COLLECTION = "users"
        const val PROJECTS_COLLECTION = "projects"
    }
}