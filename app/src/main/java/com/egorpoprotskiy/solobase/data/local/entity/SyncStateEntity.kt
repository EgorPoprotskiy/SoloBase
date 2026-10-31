package com.egorpoprotskiy.solobase.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Хранит состояние первичной синхронизации для каждого Firebase-пользователя.
 */
@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey
    val uid: String,

    // true — первичная синхронизация для этого пользователя завершена.
    @ColumnInfo(defaultValue = "0")
    val initialSyncCompleted: Boolean = false
)