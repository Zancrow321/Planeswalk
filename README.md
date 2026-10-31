# Lebensmittel-Ampel

Android-App zur Verwaltung von Lebensmitteln nach Mindesthaltbarkeitsdatum (MHD) mit Ampelsystem.

| Farbe | Bedeutung |
|-------|-----------|
| Rot   | 3 Monate oder weniger bis zum MHD, oder bereits abgelaufen |
| Gelb  | 3 bis 6 Monate bis zum MHD |
| Grün  | mehr als 6 Monate bis zum MHD |

## Funktionen

- Produkte anlegen, bearbeiten und löschen (Name, MHD, Menge, Notiz)
- Barcode scannen; der Produktname wird über [Open Food Facts](https://world.openfoodfacts.org) vorgeschlagen
- Liste nach MHD sortiert, Filter nach Ampelfarbe mit Anzahl
- Einmal täglich prüft die App im Hintergrund alle Produkte. Kommt ein Produkt in den roten Bereich,
  gibt es **genau eine** Benachrichtigung pro Produkt. Wird das MHD später so geändert, dass es nicht mehr rot ist,
  kann es beim nächsten Eintritt in Rot wieder einmal melden.
- Alle Daten bleiben lokal auf dem Handy.

## Installation

Jeder Push auf den Branch `lebensmittel-ampel` baut die App über GitHub Actions.
Die fertige APK liegt unter **Releases → "Lebensmittel-Ampel (neueste Version)"**.
Auf dem Handy herunterladen, öffnen und die Installation aus unbekannten Quellen erlauben.

Der Barcode-Scanner nutzt Google Play-Dienste (keine Kamera-Berechtigung nötig).

## Technik

Kotlin, Jetpack Compose (Material 3), Room, WorkManager, ML Kit Code Scanner. minSdk 26 (Android 8).
