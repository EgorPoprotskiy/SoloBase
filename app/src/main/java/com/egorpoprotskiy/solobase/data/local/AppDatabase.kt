package com.egorpoprotskiy.solobase.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.egorpoprotskiy.solobase.data.local.dao.ProjectDao
import com.egorpoprotskiy.solobase.data.local.dao.TaskDao
import com.egorpoprotskiy.solobase.data.local.entity.ProjectEntity
import com.egorpoprotskiy.solobase.data.local.entity.TaskEntity

@Database(entities = [TaskEntity::class, ProjectEntity::class], version = 2, exportSchema = false)
@TypeConverters(TaskConverters::class) // Подключаем наши конвертеры для Enum
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    companion object {
        const val DATABASE_NAME = "task_database"
    }
}

// При использовании Hilt, экземпляр БД создается в DatabaseModule.
