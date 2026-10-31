package com.egorpoprotskiy.solobase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.egorpoprotskiy.solobase.data.local.entity.SyncStateEntity

@Dao
interface SyncStateDao {

    // Получает состояние синхронизации конкретного Firebase-пользователя.
    @Query("SELECT * FROM sync_state WHERE uid = :uid")
    suspend fun getSyncState(uid: String): SyncStateEntity?

    // Сохраняет состояние синхронизации, заменяя существующую запись с тем же UID.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSyncState(syncState: SyncStateEntity)

    // Удаляет состояние конкретного пользователя.
    @Query("DELETE FROM sync_state WHERE uid = :uid")
    suspend fun deleteSyncState(uid: String)
}