
package com.egorpoprotskiy.solobase.data.remote.firestore.repository

import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toDomain
import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toFirestore
import com.egorpoprotskiy.solobase.data.remote.firestore.model.FirestoreNote
import com.egorpoprotskiy.solobase.data.remote.sync.FirestoreSyncResult
import com.egorpoprotskiy.solobase.domain.models.Note
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreNoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.firestore.Source

/**
 * Реализация облачного хранилища заметок через Cloud Firestore.
 * Все заметки изолированы по UID текущего пользователя.
 */
class FirestoreNoteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FirestoreNoteRepository {

    override fun observeNotes(): Flow<List<Note>> = callbackFlow {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            close(IllegalStateException("User is not authenticated"))
            return@callbackFlow
        }

        val listenerRegistration: ListenerRegistration =
            firestore.collection("users")
                .document(uid)
                .collection("notes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val notes = snapshot.documents.mapNotNull { document ->
                        document.toObject(FirestoreNote::class.java)
                            ?.toDomain(document.id)
                    }

                    trySend(notes)
                }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun saveNote(
        note: Note,
        uid: String
    ): Result<FirestoreSyncResult<Note>> {
        return try {

            val documentRef = firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(NOTE_COLLECTION)
                .document(note.id)

            val result = firestore.runTransaction { transaction ->
                val snapshot = transaction.get(documentRef)

                val remoteNote = snapshot
                    .toObject(FirestoreNote::class.java)
                    ?.toDomain(snapshot.id)

                // При одинаковом updatedAt побеждает Firestore.
                if (remoteNote != null &&
                    remoteNote.updatedAt >= note.updatedAt
                ) {
                    return@runTransaction FirestoreSyncResult.RemoteNewer(remoteNote)
                }

                transaction.set(documentRef, note.toFirestore())

                FirestoreSyncResult.Uploaded
            }.await()

            Result.success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun deleteNote(noteId: String): Result<Unit> = try {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(
                IllegalStateException("User is not authenticated")
            )

        val now = System.currentTimeMillis()

        firestore.collection("users")
            .document(uid)
            .collection("notes")
            .document(noteId)
            .set(
                mapOf(
                    "deletedAt" to now,
                    "updatedAt" to now
                ),
                SetOptions.merge()
            )
            .await()

        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getAllNotesFromServer(uid: String): Result<List<Note>> {
        return try {
            // Получаем актуальные данные непосредственно с сервера Firestore.
            val snapshot = firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .collection(NOTE_COLLECTION)
                .get(Source.SERVER)
                .await()

            // Преобразуем документы Firestore в доменные модели Note.
            val items = snapshot.documents.mapNotNull { document ->
                document.toObject(FirestoreNote::class.java)
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
        const val NOTE_COLLECTION = "notes"
    }
}
