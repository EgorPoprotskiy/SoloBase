package com.egorpoprotskiy.solobase.data.local

import androidx.room.TypeConverter
import com.egorpoprotskiy.solobase.domain.models.TaskCategory

// Конвертеры для Enum
class TaskConverters {
    @TypeConverter
    fun fromTaskCategory(category: TaskCategory): String {
        return category.name
    }

    @TypeConverter
    fun toTaskCategory(value: String): TaskCategory {
        return TaskCategory.valueOf(value)
    }
}