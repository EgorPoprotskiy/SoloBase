package com.egorpoprotskiy.solobase.domain.models

import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(), // Генерируем уникальный ID
    val name: String,                              // Название проекта (например, "SoloBase App")
    val description: String = "",                  // Краткое описание, о чем этот проект
    val colorHex: String = "#6200EE",             // Цвет обложки или иконки проекта
    val createdAt: Long = System.currentTimeMillis()
)