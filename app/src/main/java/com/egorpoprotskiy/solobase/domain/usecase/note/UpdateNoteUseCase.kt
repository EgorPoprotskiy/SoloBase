package com.egorpoprotskiy.solobase.domain.usecase.note

import com.egorpoprotskiy.solobase.domain.models.Note
import com.egorpoprotskiy.solobase.domain.repository.NoteRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(
        note: Note,
        content: String
    ) {
        noteRepository.updateNote(
            note.copy(
                content = content,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
