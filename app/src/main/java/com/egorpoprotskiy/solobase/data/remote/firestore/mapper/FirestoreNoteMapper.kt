package com.egorpoprotskiy.solobase.data.remote.firestore.mapper

import com.egorpoprotskiy.solobase.data.remote.firestore.model.FirestoreNote
import com.egorpoprotskiy.solobase.domain.models.Note

//Преобразует модель доменной заметки в модель документа Firestore.
fun Note.toFirestore(): FirestoreNote {
    return FirestoreNote(
        projectId = projectId,
        content = content,
        timestamp = timestamp,
        colorHex = colorHex,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}

//Преобразует модель документа Firestore в модель доменной заметки.
fun FirestoreNote.toDomain(id: String): Note {
    return Note(
        id = id,
        projectId = projectId,
        content = content,
        timestamp = timestamp,
        colorHex = colorHex,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}