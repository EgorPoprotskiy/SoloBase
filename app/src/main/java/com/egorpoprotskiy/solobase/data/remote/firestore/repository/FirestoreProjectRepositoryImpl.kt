package com.egorpoprotskiy.solobase.data.remote.firestore.repository

import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toDomain
import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toFirestore
import com.egorpoprotskiy.solobase.domain.models.Project
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreProjectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.egorpoprotskiy.solobase.data.remote.firestore.model.FirestoreProject
import com.egorpoprotskiy.solobase.data.remote.sync.FirestoreSyncResult
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import com.google.firebase.firestore.Source

/* Удаленный репозиторий для проектов, хранящихся в облачном Firestore.
*
* Путь к Firestore:
* /users/{uid}/projects/{ProjectID}
*
* Этот репозиторий отвечает только за взаимодействие с Firestore.
* Локальная синхронизация в помещении будет подключена позже.
 */
class FirestoreProjectRepositoryImpl @Inject constructor (
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
    override suspend fun saveProject(
        project: Project,
        uid: String
    ): Result<FirestoreSyncResult<Project>> {
        return try {

            val documentRef = firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(PROJECTS_COLLECTION)
                .document(project.id)

            val result = firestore.runTransaction { transaction ->
                val snapshot = transaction.get(documentRef)

                val remoteProject = snapshot
                    .toObject(FirestoreProject::class.java)
                    ?.toDomain(snapshot.id)

                // При одинаковом updatedAt побеждает Firestore.
                if (remoteProject != null &&
                    remoteProject.updatedAt >= project.updatedAt
                ) {
                    return@runTransaction FirestoreSyncResult.RemoteNewer(remoteProject)
                }

                transaction.set(documentRef, project.toFirestore())

                FirestoreSyncResult.Uploaded
            }.await()

            Result.success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Помечает проект удалённым в Firestore с помощью tombstone.
     * Документ сохраняется для корректной синхронизации между устройствами.
     */
    override suspend fun deleteProject(projectId: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(
                    IllegalStateException("User is not authenticated")
                )

            val now = System.currentTimeMillis()

            val tombstone = mapOf(
                "deletedAt" to now,
                "updatedAt" to now
            )

            firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(PROJECTS_COLLECTION)
                .document(projectId)
                .set(tombstone, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getAllProjectsFromServer(uid: String): Result<List<Project>> {
        return try {
            // Получаем актуальные данные непосредственно с сервера Firestore.
            val snapshot = firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(PROJECTS_COLLECTION)
                .get(Source.SERVER)
                .await()

            // Преобразуем документы Firestore в доменные модели Project.
            val items = snapshot.documents.mapNotNull { document ->
                document.toObject(FirestoreProject::class.java)
                    ?.toDomain(document.id)
            }

            Result.success(items)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private companion object {
        const val USERS_COLLECTION = "users"
        const val PROJECTS_COLLECTION = "projects"
    }
}