package utn.triponometry

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import utn.triponometry.domain.Day
import utn.triponometry.domain.Place

class DayTest {
    private val hotel = Place(0, "Hotel", mapOf(1 to 30, 2 to 60, 3 to 35))
    private val colloseum = Place(1, "Colloseum", mapOf(0 to 25, 2 to 20, 3 to 25), 120)
    private val fontanaDiTrevi = Place(2, "Fontana Di Trevi", mapOf(0 to 50, 1 to 20, 3 to 25), 180)
    private val vaticano = Place(3, "Vaticano", mapOf(0 to 40, 1 to 30, 2 to 30), 120)

    @Test
    fun `a day which has no place for other activities in its route`() {
        val day = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))

        assertFalse(day.hasSpaceFor(vaticano, 534))

        /* If we add vaticano we will have:
        Durations => Time from hotel to colloseum (30) + Time from colloseum to fontanaDiTrevi (20) +
                   + Time from fontanaDiTrevi to vaticano (25) + Time from vaticano to hotel (40)
        Time spent on all places => colloseum (120) + fontanaDiTrevi (180) + vaticano (120)

        Total time = 535
         */
    }

    @Test
    fun `a day which has place for other activities in its route`() {
        val day = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val otherDay = Day(2, mutableListOf(hotel, colloseum, fontanaDiTrevi))

        assertTrue(day.hasSpaceFor(vaticano, 535))
        assertTrue(otherDay.hasSpaceFor(vaticano, 1000))
    }
}