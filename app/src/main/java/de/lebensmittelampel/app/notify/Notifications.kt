package de.lebensmittelampel.app.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import de.lebensmittelampel.app.MainActivity
import de.lebensmittelampel.app.R
import de.lebensmittelampel.app.data.Product
import de.lebensmittelampel.app.data.remainingText
import de.lebensmittelampel.app.design.DesignPrefs
import de.lebensmittelampel.app.design.wordsFor
import java.time.format.DateTimeFormatter

object Notifications {
    private const val CHANNEL_ID = "mhd_rot"
    private val DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "MHD-Warnungen",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply { description = "Einmalige Meldung, wenn ein Produkt in den roten Bereich kommt" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun canNotify(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    /** Zeigt die Rot-Meldung für ein Produkt. Gibt false zurück, wenn Benachrichtigungen nicht erlaubt sind. */
    fun notifyRed(context: Context, product: Product): Boolean {
        if (!canNotify(context)) return false
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context, product.id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val words = wordsFor(DesignPrefs.read(context))
        val body = words.notifBody(product.expiry.format(DATE), remainingText(product.expiry))
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(words.notifTitle(product.name))
            .setSubText(product.cat.label)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        return try {
            NotificationManagerCompat.from(context).notify(product.id.toInt(), notification)
            true
        } catch (e: SecurityException) {
            false
        }
    }
}
