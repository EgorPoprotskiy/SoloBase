package com.egorpoprotskiy.solobase.domain.models

data class Note(
    val id: String = "",
    val content: String,          // Текст заметки
    val timestamp: Long = System.currentTimeMillis(),
    val tagIds: List<String> = emptyList(), // Список тегов (одна заметка может быть в разных категориях)
    val colorHex: String? = null    // Можно задать цвет самой карточке заметки
)