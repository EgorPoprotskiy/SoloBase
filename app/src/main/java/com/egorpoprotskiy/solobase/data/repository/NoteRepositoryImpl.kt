
package com.egorpoprotskiy.solobase.data.repository

import com.egorpoprotskiy.solobase.data.local.dao.NoteDao
import com.egorpoprotskiy.solobase.data.local.mapper.toDomain
import com.egorpoprotskiy.solobase.data.local.mapper.toEntity
import com.egorpoprotskiy.solobase.domain.models.Note
import com.egorpoprotskiy.solobase.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getNotesByProject(projectId: String): Flow<List<Note>> =
        noteDao.getNotesByProject(projectId).map { notes ->
            notes.map { it.toDomain() }
        }

    override suspend fun addNote(note: Note) {
        val now = System.currentTimeMillis()

        noteDao.insertNote(
            note.toEntity().copy(
                updatedAt = now,
                deletedAt = null,
                isSynced = false
            )
        )
    }

    override suspend fun updateNote(note: Note) {
        val existing = noteDao.getNoteById(note.id) ?: return
        if (existing.deletedAt != null) return
        val now = maxOf(System.currentTimeMillis(), existing.updatedAt + 1)

        noteDao.updateNote(
            note.toEntity().copy(
                updatedAt = now,
                deletedAt = null,
                isSynced = false
            )
        )
    }

    override suspend fun deleteNote(id: String) {
        val existing = noteDao.getNoteById(id) ?: return
        if (existing.deletedAt != null) return

        val now = maxOf(System.currentTimeMillis(), existing.updatedAt + 1)

        noteDao.markNoteDeleted(
            noteId = id,
            deletedAt = now,
            updatedAt = now
        )
    }
}
