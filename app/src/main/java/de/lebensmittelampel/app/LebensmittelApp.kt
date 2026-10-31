package de.lebensmittelampel.app

import android.app.Application
import de.lebensmittelampel.app.data.AppDatabase
import de.lebensmittelampel.app.data.ProductRepository
import de.lebensmittelampel.app.design.DesignPrefs
import de.lebensmittelampel.app.notify.ExpiryCheckWorker
import de.lebensmittelampel.app.notify.Notifications

class LebensmittelApp : Application() {
    val repository by lazy { ProductRepository(AppDatabase.get(this).productDao()) }
    val designPrefs by lazy { DesignPrefs(this) }

    override fun onCreate() {
        super.onCreate()
        Notifications.createChannel(this)
        ExpiryCheckWorker.scheduleDaily(this)
    }
}
