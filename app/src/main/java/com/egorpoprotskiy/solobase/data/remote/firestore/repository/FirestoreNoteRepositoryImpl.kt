
package com.egorpoprotskiy.solobase.data.remote.firestore.repository

import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toDomain
import com.egorpoprotskiy.solobase.data.remote.firestore.mapper.toFirestore
import com.egorpoprotskiy.solobase.data.remote.firestore.model.FirestoreNote
import com.egorpoprotskiy.solobase.domain.models.Note
import com.egorpoprotskiy.solobase.domain.repository.remote.FirestoreNoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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

    override suspend fun saveNote(note: Note): Result<Unit> = try {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(
                IllegalStateException("User is not authenticated")
            )

        firestore.collection("users")
            .document(uid)
            .collection("notes")
            .document(note.id)
            .set(note.toFirestore())
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
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
}
