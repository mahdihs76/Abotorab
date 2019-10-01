package ir.nilva.abotorab.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.webservices.callWebserviceWithFailure
import ir.nilva.abotorab.webservices.getServices
import kotlinx.coroutines.coroutineScope

class DeliveryWorker(
    context: Context,
    workParams: WorkerParameters
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result = coroutineScope {
        val offlineDeliveries = AppDatabase.getInstance().offlineDeliveryDao().getAll()
        if (offlineDeliveries.isEmpty()) {
            Result.success()
        } else {
            offlineDeliveries.forEach {
                callWebserviceWithFailure({ getServices().give(it.hashId) }) {
                    Result.retry()
                }?.run {
                    AppDatabase.getInstance().offlineDeliveryDao().delete(it)
                }
            }
            Result.success()
        }
    }
}