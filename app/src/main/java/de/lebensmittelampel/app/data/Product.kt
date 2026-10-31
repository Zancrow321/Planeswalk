package de.lebensmittelampel.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val barcode: String? = null,
    /** MHD als Tage seit 1970-01-01 (LocalDate.toEpochDay). */
    @ColumnInfo(name = "expiry_epoch_day") val expiryEpochDay: Long,
    val quantity: Int? = null,
    val note: String? = null,
    /** Wurde für dieses Produkt die einmalige Rot-Benachrichtigung schon verschickt? */
    @ColumnInfo(name = "red_notified") val redNotified: Boolean = false,
    /** Liste, zu der das Produkt gehört (Name aus [Category]). */
    @ColumnInfo(name = "category", defaultValue = "'VORRAT'") val category: String = Category.VORRAT.name,
) {
    val cat: Category get() = Category.from(category)

    val expiry: LocalDate get() = LocalDate.ofEpochDay(expiryEpochDay)

    fun trafficLight(today: LocalDate = LocalDate.now()) = TrafficLight.of(expiry, today)
}
