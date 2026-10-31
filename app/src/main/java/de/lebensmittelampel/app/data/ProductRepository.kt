package de.lebensmittelampel.app.data

import java.time.LocalDate

class ProductRepository(private val dao: ProductDao) {
    val products = dao.observeAll()

    suspend fun byId(id: Long) = dao.byId(id)

    suspend fun lastByBarcode(barcode: String) = dao.lastByBarcode(barcode)

    /**
     * Speichert ein Produkt. Wird das MHD so geändert, dass das Produkt nicht mehr rot ist,
     * wird die Benachrichtigung zurückgesetzt und kann beim nächsten Eintritt in Rot erneut einmal kommen.
     */
    suspend fun save(product: Product, today: LocalDate = LocalDate.now()) {
        val toSave = if (product.trafficLight(today) != TrafficLight.RED) {
            product.copy(redNotified = false)
        } else {
            product
        }
        if (toSave.id == 0L) dao.insert(toSave) else dao.update(toSave)
    }

    suspend fun delete(product: Product) = dao.delete(product)
}
