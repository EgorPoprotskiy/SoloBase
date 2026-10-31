package com.egorpoprotskiy.solobase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.UUID

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val colorHex: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null,
    @ColumnInfo(defaultValue = "1")
    val isSynced: Boolean = true
)
