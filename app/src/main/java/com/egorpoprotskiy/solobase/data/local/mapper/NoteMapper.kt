package com.egorpoprotskiy.solobase.data.local.mapper

import com.egorpoprotskiy.solobase.data.local.entity.NoteEntity
import com.egorpoprotskiy.solobase.domain.models.Note

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    projectId = projectId,
    content = content,
    timestamp = timestamp,
    colorHex = colorHex
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    projectId = projectId,
    content = content,
    timestamp = timestamp,
    colorHex = colorHex
)
