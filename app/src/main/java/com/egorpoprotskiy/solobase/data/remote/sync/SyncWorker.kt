package com.egorpoprotskiy.solobase.data.remote.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager
): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            syncManager.sync().fold(
                onSuccess = {
                    Result.success()
                },
                onFailure = { error ->
                    if (error is CancellationException) {
                        // Менеджер остановил синхронизацию (например, при logout).
                        Result.success()
                    } else {
                        Result.retry()
                    }
                }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.retry()
        }
    }
}