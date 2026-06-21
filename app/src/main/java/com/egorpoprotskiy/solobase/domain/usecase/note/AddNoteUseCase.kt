package com.egorpoprotskiy.solobase.domain.usecase.note

import com.egorpoprotskiy.solobase.domain.models.Note
import com.egorpoprotskiy.solobase.domain.repository.NoteRepository
import java.util.UUID
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(
        projectId: String,
        content: String,
        colorHex: String? = null
    ) {
        val note = Note(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            content = content,
            timestamp = System.currentTimeMillis(),
            colorHex = colorHex
        )
        noteRepository.addNote(note)
    }
}
