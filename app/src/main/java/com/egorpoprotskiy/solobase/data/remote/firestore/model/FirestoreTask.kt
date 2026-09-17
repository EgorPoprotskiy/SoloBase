package com.egorpoprotskiy.solobase.data.remote.firestore.model

data class FirestoreTask(
    val content: String = "",
    val category: String = "DAILY",
    val status: String = "TODO",
    val isUrgent: Boolean = false,
    val isImportant: Boolean = false,
    val timestamp: Long? = null,
    val reminderAt: Long? = null,
    val position: Int = 0,
    val tagId: String? = null,
    val isCompleted: Boolean = false,
    val projectId: String? = null,
    val updatedAt: Long = 0L,
    val deletedAt: Long? = null
)