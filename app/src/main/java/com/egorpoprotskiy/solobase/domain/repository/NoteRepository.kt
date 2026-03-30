package com.egorpoprotskiy.solobase.domain.repository

import com.egorpoprotskiy.solobase.domain.models.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: String)
}