package com.egorpoprotskiy.solobase.data.local.mapper

import com.egorpoprotskiy.solobase.data.local.entity.TaskEntity
import com.egorpoprotskiy.solobase.domain.models.Task

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    content = content,
    category = category,
    status = status,
    isUrgent = isUrgent,
    isImportant = isImportant,
    timestamp = timestamp,
    reminderAt = reminderAt,
    position = position,
    tagId = tagId,
    isCompleted = isCompleted,
    projectId = projectId
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    content = content,
    category = category,
    status = status,
    isUrgent = isUrgent,
    isImportant = isImportant,
    timestamp = timestamp,
    reminderAt = reminderAt,
    position = position,
    tagId = tagId,
    isCompleted = isCompleted,
    projectId = projectId
)
