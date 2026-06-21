package com.egorpoprotskiy.solobase.domain.models

data class Note(
    val id: String = "",
    val projectId: String,
    val content: String,          // Текст заметки
    val timestamp: Long = System.currentTimeMillis(),
    val colorHex: String? = null    // Можно задать цвет самой карточке заметки
)
