package de.lebensmittelampel.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY expiry_epoch_day ASC, name ASC")
    fun observeAll(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun byId(id: Long): Product?

    @Query("SELECT * FROM products WHERE red_notified = 0")
    suspend fun notYetNotified(): List<Product>

    @Query("SELECT * FROM products WHERE barcode = :barcode ORDER BY id DESC LIMIT 1")
    suspend fun lastByBarcode(barcode: String): Product?

    @Insert
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Query("UPDATE products SET red_notified = 1 WHERE id = :id")
    suspend fun markRedNotified(id: Long)

    @Delete
    suspend fun delete(product: Product)
}
