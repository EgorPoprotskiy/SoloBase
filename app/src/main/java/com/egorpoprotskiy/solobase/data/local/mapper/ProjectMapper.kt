package com.egorpoprotskiy.solobase.data.local.mapper

import com.egorpoprotskiy.solobase.data.local.entity.ProjectEntity
import com.egorpoprotskiy.solobase.domain.models.Project

fun ProjectEntity.toDomain(): Project = Project(
    id = id,
    name = name,
    description = description,
    colorHex = colorHex,
    createdAt = createdAt
)

fun Project.toEntity(): ProjectEntity = ProjectEntity(
    id = id,
    name = name,
    description = description,
    colorHex = colorHex,
    createdAt = createdAt
)
