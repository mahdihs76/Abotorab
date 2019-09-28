package ir.nilva.abotorab.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ir.nilva.abotorab.db.AppDatabase
import ir.nilva.abotorab.webservices.MyRetrofit
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
                try {
                    val response = MyRetrofit.getService().give(it.hashId)
                    if (response.isSuccessful) {
                        AppDatabase.getInstance().offlineDeliveryDao().delete(it)
                    } else {
                        Result.retry()
                    }
                } catch (e: Exception) {
                    Result.retry()
                }
            }
            Result.success()
        }
    }
}