package de.lebensmittelampel.app.scan

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

/** Startet den Barcode-Scanner von Google Play-Diensten (keine Kamera-Berechtigung nötig). */
object BarcodeScanner {
    fun scan(
        context: Context,
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E,
            )
            .enableAutoZoom()
            .build()
        GmsBarcodeScanning.getClient(context, options).startScan()
            .addOnSuccessListener { barcode -> barcode.rawValue?.let(onResult) ?: onError("Kein Barcode erkannt") }
            .addOnCanceledListener { /* Nutzer hat abgebrochen */ }
            .addOnFailureListener { onError("Scanner nicht verfügbar: ${it.localizedMessage ?: "unbekannter Fehler"}") }
    }
}
