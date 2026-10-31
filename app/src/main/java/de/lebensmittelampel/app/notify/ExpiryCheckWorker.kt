package de.lebensmittelampel.app.notify

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import de.lebensmittelampel.app.data.AppDatabase
import de.lebensmittelampel.app.data.TrafficLight
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * Prüft alle Produkte, die noch keine Rot-Meldung bekommen haben.
 * Ist eines rot, kommt genau eine Benachrichtigung und das Produkt wird als gemeldet markiert.
 */
class ExpiryCheckWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase.get(applicationContext).productDao()
        val today = LocalDate.now()
        for (product in dao.notYetNotified()) {
            if (product.trafficLight(today) == TrafficLight.RED) {
                // Nur als gemeldet markieren, wenn die Meldung wirklich angezeigt werden konnte.
                if (Notifications.notifyRed(applicationContext, product)) {
                    dao.markRedNotified(product.id)
                }
            }
        }
        return Result.success()
    }

    companion object {
        private const val DAILY = "mhd-pruefung-taeglich"

        fun scheduleDaily(context: Context) {
            val request = PeriodicWorkRequestBuilder<ExpiryCheckWorker>(1, TimeUnit.DAYS).build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(DAILY, ExistingPeriodicWorkPolicy.KEEP, request)
        }

        /** Sofortige Prüfung, z. B. nach dem Speichern eines Produkts oder beim App-Start. */
        fun runNow(context: Context) {
            WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<ExpiryCheckWorker>().build())
        }
    }
}
