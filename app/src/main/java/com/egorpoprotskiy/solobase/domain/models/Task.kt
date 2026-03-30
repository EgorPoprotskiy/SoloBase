package com.egorpoprotskiy.solobase.domain.models

import java.util.UUID

data class Task (
    val id: String = UUID.randomUUID().toString(),
    val content: String,              // Заголовок
    val category: TaskCategory = TaskCategory.DAILY, // Тип (задача или элемент канбана)
    val status: String = "TODO",    // Для канбана: "BACKLOG", "IN_PROGRESS", "DONE"

    //Матрица Эйзенхаузера
    val isUrgent: Boolean = false,          // Срочная задача
    val isImportant: Boolean = false,       // Важная задача

    // Поле для порядка (пригодится для Канбана и сортировки в списке)
    val timestamp: Long? = null,            // Время исполнения (для 1-го экрана)
    val position: Int = 0,
    val tagId: String? = null,             // Привязка к цветному тегу
    val isCompleted: Boolean = false,
    val projectId: String? = null             // Привязка к проекту (для 3-го экрана)
)

enum class TaskCategory {
    DAILY,      // Ежедневная задача
    KANBAN      // Элемент канбана
}