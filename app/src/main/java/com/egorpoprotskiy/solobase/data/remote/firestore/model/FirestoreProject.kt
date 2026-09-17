package com.egorpoprotskiy.solobase.data.remote.firestore.model

data class FirestoreProject (
    val name: String = "",
    val description: String = "",
    val colorHex: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val deletedAt: Long? = null
)