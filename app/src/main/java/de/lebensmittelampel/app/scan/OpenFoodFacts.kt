package de.lebensmittelampel.app.scan

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** Sucht den Produktnamen zu einem Barcode in der freien Datenbank Open Food Facts. */
object OpenFoodFacts {
    suspend fun lookupName(barcode: String): String? = withContext(Dispatchers.IO) {
        try {
            val code = URLEncoder.encode(barcode, "UTF-8")
            val url = URL(
                "https://world.openfoodfacts.org/api/v2/product/$code.json" +
                    "?fields=product_name,product_name_de,brands",
            )
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("User-Agent", "Lebensmittel-Ampel/1.0 (Android)")
            }
            try {
                if (conn.responseCode != 200) return@withContext null
                val json = JSONObject(conn.inputStream.bufferedReader().use { it.readText() })
                if (json.optInt("status") != 1) return@withContext null
                val p = json.getJSONObject("product")
                val name = p.optString("product_name_de").ifBlank { p.optString("product_name") }.trim()
                val brand = p.optString("brands").split(",").firstOrNull()?.trim().orEmpty()
                when {
                    name.isBlank() -> null
                    brand.isNotBlank() && !name.contains(brand, ignoreCase = true) -> "$name ($brand)"
                    else -> name
                }
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            null
        }
    }
}
