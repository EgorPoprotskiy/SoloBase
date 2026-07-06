package com.egorpoprotskiy.solobase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egorpoprotskiy.solobase.domain.models.ProjectDefaults
import java.util.UUID

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val colorHex: String = ProjectDefaults.DEFAULT_COLOR_HEX,
    val createdAt: Long = System.currentTimeMillis()
)
