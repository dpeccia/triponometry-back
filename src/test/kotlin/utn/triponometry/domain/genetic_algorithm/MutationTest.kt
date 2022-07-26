package utn.triponometry.domain.genetic_algorithm

import io.mockk.every
import io.mockk.mockkObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utn.triponometry.domain.Place
import utn.triponometry.helpers.Random

class MutationTest {
    val hotel = Place(1, "Hotel", mapOf(2 to 30, 3 to 60))
    val colloseum = Place(2, "Colloseum", mapOf(1 to 25, 3 to 20))
    val fontanaDiTrevi = Place(3, "Fontana Di Trevi", mapOf(1 to 50, 3 to 15))

    @Test
    fun `swap mutation picks a random trip and swaps 2 random places`() {
        val trip1 = Individual(listOf(hotel, colloseum, fontanaDiTrevi))
        val trip2 = Individual(listOf(hotel, fontanaDiTrevi, colloseum))
        val trip3 = Individual(listOf(fontanaDiTrevi, colloseum, hotel))
        val initialIndividuals = listOf(trip1, trip2, trip3)

        // pick trip3, swap colloseum and hotel
        mockkObject(Random)
        every { Random.pickFrom(any()) } returns 2 andThenMany listOf(1, 2)

        val individualsAfterMutation = mutate(initialIndividuals)

        val newTrip3 = Individual(listOf(fontanaDiTrevi, hotel, colloseum))
        val expectedIndividuals = listOf(trip1, trip2, newTrip3)
        assertEquals(expectedIndividuals, individualsAfterMutation)
    }
}