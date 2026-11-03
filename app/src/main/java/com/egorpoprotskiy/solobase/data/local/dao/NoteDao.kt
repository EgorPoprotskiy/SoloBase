
package com.egorpoprotskiy.solobase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.egorpoprotskiy.solobase.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // Заметки выбранного проекта для UI.
    @Query("""
        SELECT * FROM notes
        WHERE projectId = :projectId
          AND deletedAt IS NULL
        ORDER BY timestamp DESC
    """)
    fun getNotesByProject(projectId: String): Flow<List<NoteEntity>>

    // Все заметки, ожидающие отправки в Firestore, включая tombstone-записи.
    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getUnsyncedNotes(): List<NoteEntity>

    // Получение заметки по ID, включая удалённые.
    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: String): NoteEntity?

    // Локальная вставка или замена заметки.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    // Локальное обновление заметки.
    @Update
    suspend fun updateNote(note: NoteEntity)

    // Применение данных из Firestore без постановки в очередь отправки.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSyncedNote(note: NoteEntity)

    // Мягкое удаление заметки.
    @Query("""
        UPDATE notes
        SET deletedAt = :deletedAt,
            updatedAt = :updatedAt,
            isSynced = 0
        WHERE id = :noteId
    """)
    suspend fun markNoteDeleted(
        noteId: String,
        deletedAt: Long,
        updatedAt: Long
    )

    // Подтверждение отправки только той версии, которая была загружена.
    @Query("""
        UPDATE notes
        SET isSynced = 1
        WHERE id = :noteId
          AND updatedAt = :uploadedUpdatedAt
    """)
    suspend fun markNoteSynced(
        noteId: String,
        uploadedUpdatedAt: Long
    )

    // Все заметки для первоначального объединения с Firestore.
    @Query("SELECT * FROM notes")
    suspend fun getAllNotesForSync(): List<NoteEntity>

    @Transaction
    suspend fun applySyncedNoteIfNewer(note: NoteEntity) {
        val localNote = getNoteById(note.id)

        if (localNote == null || note.updatedAt >= localNote.updatedAt) {
            upsertSyncedNote(note.copy(isSynced = true))
        }
    }
}
