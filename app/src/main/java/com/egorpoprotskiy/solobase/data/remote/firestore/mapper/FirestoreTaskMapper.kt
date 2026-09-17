package com.egorpoprotskiy.solobase.data.remote.firestore.mapper

import com.egorpoprotskiy.solobase.data.remote.firestore.model.FirestoreTask
import com.egorpoprotskiy.solobase.domain.models.Task
import com.egorpoprotskiy.solobase.domain.models.TaskCategory

//Преобразует модель задачи домена в модель документа Firestore.
fun Task.toFirestore(): FirestoreTask {
    return FirestoreTask(
        content = content,
        category = category.name,
        status = status,
        isUrgent = isUrgent,
        isImportant = isImportant,
        timestamp = timestamp,
        reminderAt = reminderAt,
        position = position,
        tagId = tagId,
        isCompleted = isCompleted,
        projectId = projectId,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}

/*
Преобразует модель документа Firestore в модель задачи домена.
Неизвестные значения перечислений заменяются на DAILY, чтобы исключить искажение или устаревание удаленные данные не приводят к аварийному завершению работы приложения.
 */
fun FirestoreTask.toDomain(id: String): Task {
    return Task(
        id = id,
        content = content,
        category = runCatching {
            TaskCategory.valueOf(category)
        }.getOrDefault(TaskCategory.DAILY),
        status = status,
        isUrgent = isUrgent,
        isImportant = isImportant,
        timestamp = timestamp,
        reminderAt = reminderAt,
        position = position,
        tagId = tagId,
        isCompleted = isCompleted,
        projectId = projectId,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}