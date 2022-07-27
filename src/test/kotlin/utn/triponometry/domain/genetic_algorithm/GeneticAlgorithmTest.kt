package utn.triponometry.domain.genetic_algorithm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import utn.triponometry.domain.Place
import utn.triponometry.properties.GeneticAlgorithm as GeneticAlgorithmProperties

class GeneticAlgorithmTest {
    val places = listOf(
        Place(0, "Plaza Aristobulo del Valle", mapOf(1 to 21, 2 to 27, 3 to 14, 4 to 24, 5 to 18, 6 to 28, 7 to 18)),
        Place(1, "UTN Lugano", mapOf(0 to 19, 2 to 11, 3 to 18, 4 to 18, 5 to 21, 6 to 20, 7 to 25)),
        Place(2, "Hospital Garrahan", mapOf(1 to 14, 0 to 27, 3 to 24, 4 to 16, 5 to 27, 6 to 14, 7 to 28)),
        Place(3, "Parque Yrigoyen", mapOf(1 to 16, 2 to 22, 0 to 13, 4 to 24, 5 to 9, 6 to 19, 7 to 13)),
        Place(4, "UTN Medrano", mapOf(1 to 19, 2 to 13, 3 to 23, 0 to 20, 5 to 24, 6 to 12, 7 to 16)),
        Place(5, "Dot Baires Shopping", mapOf(1 to 23, 2 to 25, 3 to 9, 4 to 21, 0 to 20, 6 to 14, 7 to 10)),
        Place(6, "Retiro", mapOf(1 to 22, 2 to 15, 3 to 21, 4 to 10, 5 to 20, 0 to 31, 7 to 18)),
        Place(7, "Hospital Pirovano", mapOf(1 to 26, 2 to 25, 3 to 13, 4 to 17, 5 to 9, 6 to 22, 0 to 18))
    )

    @Test
    fun `initial population has the size defined in config`() {
        val initialPopulation = GeneticAlgorithm.generateInitialPopulation(places, 20)

        assertEquals(20, initialPopulation.size)
        assertTrue(initialPopulation.all { it.places.size == 8 })
    }

    @Test
    fun `only one individual is returned as the result of the genetic algorithm`() {
        val properties = GeneticAlgorithmProperties(1000, 5)

        val bestIndividual = GeneticAlgorithm.run(properties, places)

        assertEquals(8, bestIndividual.places.size)
    }
}