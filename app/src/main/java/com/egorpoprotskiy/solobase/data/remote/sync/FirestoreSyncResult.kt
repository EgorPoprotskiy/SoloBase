package com.egorpoprotskiy.solobase.data.remote.sync

/**
 * Результат попытки синхронизировать локальную запись с Firestore.
 *
 * @param T Тип доменной модели: Project, Task или Note.
 */
sealed interface FirestoreSyncResult<out T> {

    /**
     * Локальная версия успешно записана в Firestore.
     */
    data object Uploaded : FirestoreSyncResult<Nothing>

    /**
     * В Firestore уже находится более новая или равная версия.
     *
     * Передаём её обратно, чтобы координатор обновил Room.
     */
    data class RemoteNewer<T>(
        val remote: T
    ) : FirestoreSyncResult<T>
}