package com.animeboynz.kmd.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.Constraints
import com.animeboynz.kmd.database.ALMDatabase
import java.util.concurrent.TimeUnit
import org.koin.core.context.GlobalContext

class StockSyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val database = runCatching { GlobalContext.get().get<ALMDatabase>() }.getOrElse {
            return Result.retry()
        }
        val pending = database.pendingTransactionDao().getUnsynced()
        if (pending.isEmpty()) return Result.success()
        // Rows stay pending until a server acknowledges them.
        Log.i(TAG, "Holding ${pending.size} stock changes until a server is configured")
        return Result.success()
    }

    companion object {
        private const val TAG = "StockSync"
        private const val PERIODIC_NAME = "stock-sync"
        private const val ONCE_NAME = "stock-sync-once"

        private val connected = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun schedulePeriodic(context: Context) {
            val request = PeriodicWorkRequestBuilder<StockSyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(connected)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }

        fun scheduleSoon(context: Context) {
            val request = OneTimeWorkRequestBuilder<StockSyncWorker>()
                .setConstraints(connected)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                ONCE_NAME,
                ExistingWorkPolicy.KEEP,
                request,
            )
        }
    }
}
