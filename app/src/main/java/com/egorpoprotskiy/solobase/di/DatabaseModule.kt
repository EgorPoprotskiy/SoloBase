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
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7
            )
//            .fallbackToDestructiveMigration(dropAllTables = true)
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

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {

            // TASKS
            db.execSQL(
                """
                CREATE TABLE tasks_new (
                    id TEXT NOT NULL,
                    content TEXT NOT NULL,
                    category TEXT NOT NULL,
                    status TEXT NOT NULL,
                    isUrgent INTEGER NOT NULL,
                    isImportant INTEGER NOT NULL,
                    timestamp INTEGER,
                    reminderAt INTEGER,
                    position INTEGER NOT NULL,
                    tagId TEXT,
                    isCompleted INTEGER NOT NULL,
                    projectId TEXT,
                    updatedAt INTEGER NOT NULL,
                    deletedAt INTEGER,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                INSERT INTO tasks_new (
                    id, content, category, status,
                    isUrgent, isImportant, timestamp,
                    reminderAt, position, tagId,
                    isCompleted, projectId,
                    updatedAt, deletedAt
                )
                SELECT
                    id, content, category, status,
                    isUrgent, isImportant, timestamp,
                    reminderAt, position, tagId,
                    isCompleted, projectId,
                    COALESCE(timestamp, 0), NULL
                FROM tasks
                """.trimIndent()
            )

            db.execSQL("DROP TABLE tasks")
            db.execSQL("ALTER TABLE tasks_new RENAME TO tasks")


            // PROJECTS
            db.execSQL(
                """
                CREATE TABLE projects_new (
                    id TEXT NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT NOT NULL,
                    colorHex TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    deletedAt INTEGER,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                INSERT INTO projects_new (
                    id, name, description, colorHex,
                    createdAt, updatedAt, deletedAt
                )
                SELECT
                    id, name, description, colorHex,
                    createdAt, createdAt, NULL
                FROM projects
                """.trimIndent()
            )

            db.execSQL("DROP TABLE projects")
            db.execSQL("ALTER TABLE projects_new RENAME TO projects")


            // NOTES
            db.execSQL(
                """
                CREATE TABLE notes_new (
                    id TEXT NOT NULL,
                    projectId TEXT NOT NULL,
                    content TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    colorHex TEXT,
                    updatedAt INTEGER NOT NULL,
                    deletedAt INTEGER,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                INSERT INTO notes_new (
                    id, projectId, content,
                    timestamp, colorHex,
                    updatedAt, deletedAt
                )
                SELECT
                    id, projectId, content,
                    timestamp, colorHex,
                    timestamp, NULL
                FROM notes
                """.trimIndent()
            )

            db.execSQL("DROP TABLE notes")
            db.execSQL("ALTER TABLE notes_new RENAME TO notes")
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
