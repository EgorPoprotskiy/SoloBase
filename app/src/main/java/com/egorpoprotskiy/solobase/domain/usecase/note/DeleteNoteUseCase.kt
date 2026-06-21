package com.egorpoprotskiy.solobase.domain.usecase.note

import com.egorpoprotskiy.solobase.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: String) = noteRepository.deleteNote(noteId)
}
