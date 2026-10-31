package de.lebensmittelampel.app.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Ampelfarbe eines Produkts abhängig von der Zeit bis zum Mindesthaltbarkeitsdatum. */
enum class TrafficLight(val label: String) {
    RED("Rot"),
    YELLOW("Gelb"),
    GREEN("Grün");

    companion object {
        /** Rot gilt bis 3 Monate vor Ablauf (inklusive bereits abgelaufen). */
        const val RED_MONTHS = 3L

        /** Gelb gilt die 3 Monate vor dem roten Bereich, also 3 bis 6 Monate vor Ablauf. */
        const val YELLOW_MONTHS = 6L

        fun of(expiry: LocalDate, today: LocalDate = LocalDate.now()): TrafficLight = when {
            !expiry.isAfter(today.plusMonths(RED_MONTHS)) -> RED
            !expiry.isAfter(today.plusMonths(YELLOW_MONTHS)) -> YELLOW
            else -> GREEN
        }
    }
}

/** Kurzer deutscher Text zur Restzeit, z. B. "noch 45 Tage" oder "seit 3 Tagen abgelaufen". */
fun remainingText(expiry: LocalDate, today: LocalDate = LocalDate.now()): String {
    val days = ChronoUnit.DAYS.between(today, expiry)
    return when {
        days < -1 -> "seit ${-days} Tagen abgelaufen"
        days == -1L -> "seit gestern abgelaufen"
        days == 0L -> "läuft heute ab"
        days == 1L -> "läuft morgen ab"
        else -> "noch $days Tage"
    }
}
