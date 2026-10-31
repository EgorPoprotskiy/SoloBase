package com.egorpoprotskiy.solobase.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.egorpoprotskiy.solobase.data.local.AppDatabase
import com.egorpoprotskiy.solobase.data.local.dao.NoteDao
import com.egorpoprotskiy.solobase.data.local.dao.ProjectDao
import com.egorpoprotskiy.solobase.data.local.dao.SyncStateDao
import com.egorpoprotskiy.solobase.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(
                MIGRATION_3_4,
                MIGRATION_5_6,
                MIGRATION_6_7
            )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    @Provides
    @Singleton
    fun provideSyncStateDao(database: AppDatabase): SyncStateDao {
        return database.syncStateDao()
    }
    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideProjectDao(database: AppDatabase): ProjectDao {
        return database.projectDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE tasks ADD COLUMN reminderAt INTEGER")
        }
    }
    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE projects ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 1"
            )
            db.execSQL(
                "ALTER TABLE tasks ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 1"
            )
            db.execSQL(
                "ALTER TABLE notes ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 1"
            )
        }
    }
    private val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS sync_state (
                uid TEXT NOT NULL,
                initialSyncCompleted INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(uid)
            )
            """.trimIndent()
            )
        }
    }
}
