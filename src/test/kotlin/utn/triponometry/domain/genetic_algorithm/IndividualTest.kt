package utn.triponometry.domain.genetic_algorithm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import utn.triponometry.domain.Place

class IndividualTest {
    val hotel = Place(0, "Hotel", mapOf(1 to 30, 2 to 60))
    val colloseum = Place(1, "Colloseum", mapOf(0 to 25, 2 to 20))
    val fontanaDiTrevi = Place(2, "Fontana Di Trevi", mapOf(0 to 50, 1 to 20))

    @Test
    fun `the fitness function is calculated with the total time it takes to go from each place to the others, counting the return to the first one`() {
        val trip = Individual(listOf(hotel, colloseum, fontanaDiTrevi))

        assertEquals(100, trip.totalTime)
        assertEquals(0.01, trip.fitness)
    }

    @Test
    fun `the longer the trip takes, the smaller the value of the fitness function will be`() {
        val trip1 = Individual(listOf(hotel, colloseum, fontanaDiTrevi))
        val trip2 = Individual(listOf(hotel, fontanaDiTrevi, colloseum))

        assertTrue(trip2.totalTime > trip1.totalTime)
        assertTrue(trip2.fitness < trip1.fitness)
    }

    @Test
    fun `individuals with trips that doesn't begin with the hotel will have less fitness`() {
        val trip1 = Individual(listOf(hotel, fontanaDiTrevi, colloseum))
        val trip2 = Individual(listOf(fontanaDiTrevi, hotel, colloseum))

        assertTrue(trip1.totalTime > trip2.totalTime)
        assertTrue(trip1.fitness > trip2.fitness)
    }
}