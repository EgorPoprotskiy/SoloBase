package com.egorpoprotskiy.solobase.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.egorpoprotskiy.solobase.data.local.dao.TaskDao
import com.egorpoprotskiy.solobase.domain.models.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(TaskConverters::class) // Подключаем наши конвертеры для Enum
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    companion object {
        const val DATABASE_NAME = "task_database"
    }
}

// При использовании Hilt, экземпляр БД создается в DatabaseModule.