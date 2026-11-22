
package com.egorpoprotskiy.solobase.domain.repository.remote

import com.egorpoprotskiy.solobase.data.remote.sync.FirestoreSyncResult
import com.egorpoprotskiy.solobase.domain.models.Note
import kotlinx.coroutines.flow.Flow

/**
 * Контракт облачного хранилища заметок.
 *
 * Реализация находится в data-слое и работает с Cloud Firestore.
 * Domain-слой не зависит от Firebase SDK.
 */
interface FirestoreNoteRepository {

    /**
     * Наблюдает за заметками текущего авторизованного пользователя.
     * Изменения Firestore поступают через Flow.
     */
    fun observeNotes(): Flow<List<Note>>

    /**
     * Сохраняет заметку в облаке или обновляет существующую.
     */
    suspend fun saveNote(note: Note, uid: String): Result<FirestoreSyncResult<Note>>
    /**
     * Помечает заметку как удалённую, сохраняя её документ в Firestore.
     */
    suspend fun deleteNote(noteId: String): Result<Unit>
    suspend fun getAllNotesFromServer(uid: String): Result<List<Note>>
}
