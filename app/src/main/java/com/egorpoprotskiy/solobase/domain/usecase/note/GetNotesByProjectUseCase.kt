package com.egorpoprotskiy.solobase.domain.usecase.note

import com.egorpoprotskiy.solobase.domain.models.Note
import com.egorpoprotskiy.solobase.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesByProjectUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(projectId: String): Flow<List<Note>> = noteRepository.getNotesByProject(projectId)
}
