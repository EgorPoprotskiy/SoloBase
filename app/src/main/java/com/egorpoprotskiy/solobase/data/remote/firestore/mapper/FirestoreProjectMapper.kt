package com.egorpoprotskiy.solobase.data.remote.firestore.mapper

import com.egorpoprotskiy.solobase.data.remote.firestore.model.FirestoreProject
import com.egorpoprotskiy.solobase.domain.models.Project

//Преобразует модель проекта домена в модель документа Firestore.
fun Project.toFirestore(): FirestoreProject {
    return FirestoreProject(
        name = name,
        description = description,
        colorHex = colorHex,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}

/*
Преобразует модель документа Firestore в модель доменного проекта.
 *
Идентификатор документа Firestore предоставляется отдельно, поскольку он не сохраняется внутри самого FirestoreProject.
 */
fun FirestoreProject.toDomain(id: String): Project {
    return Project(
        id = id,
        name = name,
        description = description,
        colorHex = colorHex,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}