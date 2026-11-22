package com.egorpoprotskiy.solobase.data.remote.firestore.model

import com.google.firebase.firestore.PropertyName

data class FirestoreTask(
    val content: String = "",
    val category: String = "DAILY",
    val status: String = "TODO",
    @get:PropertyName("urgent")
    @set:PropertyName("urgent")
    var isUrgent: Boolean = false,
    @get:PropertyName("important")
    @set:PropertyName("important")
    var isImportant: Boolean = false,
    val timestamp: Long? = null,
    val reminderAt: Long? = null,
    val position: Int = 0,
    val tagId: String? = null,
    @get:PropertyName("completed")
    @set:PropertyName("completed")
    var isCompleted: Boolean = false,
    val projectId: String? = null,
    val updatedAt: Long = 0L,
    val deletedAt: Long? = null
)