package com.egorpoprotskiy.solobase.data.remote.firestore.model

data class FirestoreNote(
    val projectId: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val colorHex: String? = null,
    val updatedAt: Long = 0L,
    val deletedAt: Long? = null
)