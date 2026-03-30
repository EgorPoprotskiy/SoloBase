package com.egorpoprotskiy.solobase.domain.models

data class Tag (
    val id: String = "",            // Уникальный ID из БД
    val name: String,              // Название тега
    val colorHex: String              // Храним цвет как строку (напр. "#FF0000")
)