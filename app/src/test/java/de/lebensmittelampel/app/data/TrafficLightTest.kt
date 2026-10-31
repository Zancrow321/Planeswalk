package de.lebensmittelampel.app.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TrafficLightTest {
    private val today = LocalDate.of(2026, 10, 31)

    @Test fun abgelaufen_ist_rot() =
        assertEquals(TrafficLight.RED, TrafficLight.of(today.minusDays(5), today))

    @Test fun heute_ist_rot() =
        assertEquals(TrafficLight.RED, TrafficLight.of(today, today))

    @Test fun genau_drei_monate_ist_rot() =
        assertEquals(TrafficLight.RED, TrafficLight.of(today.plusMonths(3), today))

    @Test fun drei_monate_plus_ein_tag_ist_gelb() =
        assertEquals(TrafficLight.YELLOW, TrafficLight.of(today.plusMonths(3).plusDays(1), today))

    @Test fun genau_sechs_monate_ist_gelb() =
        assertEquals(TrafficLight.YELLOW, TrafficLight.of(today.plusMonths(6), today))

    @Test fun sechs_monate_plus_ein_tag_ist_gruen() =
        assertEquals(TrafficLight.GREEN, TrafficLight.of(today.plusMonths(6).plusDays(1), today))

    @Test fun ein_jahr_ist_gruen() =
        assertEquals(TrafficLight.GREEN, TrafficLight.of(today.plusYears(1), today))

    @Test fun restzeit_texte() {
        assertEquals("läuft heute ab", remainingText(today, today))
        assertEquals("läuft morgen ab", remainingText(today.plusDays(1), today))
        assertEquals("noch 10 Tage", remainingText(today.plusDays(10), today))
        assertEquals("seit gestern abgelaufen", remainingText(today.minusDays(1), today))
        assertEquals("seit 4 Tagen abgelaufen", remainingText(today.minusDays(4), today))
    }
}

class CategoryTest {
    @Test fun unbekannt_ist_vorrat() = assertEquals(Category.VORRAT, Category.from(null))

    @Test fun tiefkuehl_wird_erkannt() = assertEquals(Category.TIEFKUEHL, Category.from("TIEFKUEHL"))

    @Test fun alte_produkte_sind_vorrat() =
        assertEquals(Category.VORRAT, Product(name = "Reis", expiryEpochDay = 0).cat)
}
