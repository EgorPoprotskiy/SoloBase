package com.egorpoprotskiy.solobase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egorpoprotskiy.solobase.domain.models.TaskCategory
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val category: TaskCategory = TaskCategory.DAILY,
    val status: String = "TODO",
    val isUrgent: Boolean = false,
    val isImportant: Boolean = false,
    val timestamp: Long? = System.currentTimeMillis(),
    val position: Int = 0,
    val tagId: String? = null,
    val isCompleted: Boolean = false,
    val projectId: String? = null
)
