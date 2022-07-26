package utn.triponometry.domain.genetic_algorithm

import io.mockk.every
import io.mockk.mockkObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import utn.triponometry.domain.Place
import utn.triponometry.helpers.Random

class SelectionTest {
    val hotel = Place(1, "Hotel", mapOf(2 to 30, 3 to 60))
    val colloseum = Place(2, "Colloseum", mapOf(1 to 25, 3 to 20))
    val fontanaDiTrevi = Place(3, "Fontana Di Trevi", mapOf(1 to 50, 2 to 20))

    val trip1 = Individual(listOf(hotel, colloseum, fontanaDiTrevi))
    val trip2 = Individual(listOf(colloseum, fontanaDiTrevi, hotel))
    val trip3 = Individual(listOf(fontanaDiTrevi, colloseum, hotel))

    val individuals = listOf(trip1, trip2, trip3)

    @Test
    fun `best individuals are preselected`() {
        val bestIndividuals = preSelectBestIndividuals(individuals, 0.0039)

        assertEquals(listOf(trip1, trip1), bestIndividuals)
    }

    @Test
    fun `new fitness is calculated`() {
        val individualsWithNewFitness = calculateNewFitness(individuals, 0.0039).toList()

        assertEquals(0.5641025641025643, individualsWithNewFitness[0].first)
        assertEquals(0.25641025641025644, individualsWithNewFitness[1].first)
        assertEquals(0.24420024420024425, individualsWithNewFitness[2].first)
    }

    @Test
    fun `individuals with best new fitness have better belonging percentage`() {
        val individualsWithNewFitness = mapOf(0.5 to trip1, 0.2 to trip2, 0.3 to trip3)

        val individualsWithBelongingPercentages = calculateBelongingPercentages(individualsWithNewFitness, 1.0)

        assertEquals(50.0, individualsWithBelongingPercentages[0].first)
        assertEquals(20.0, individualsWithBelongingPercentages[1].first)
        assertEquals(30.0, individualsWithBelongingPercentages[2].first)
        assertEquals(
            listOf(trip1, trip3, trip2),
            individualsWithBelongingPercentages.sortedByDescending { it.first }.map { it.second }
        )
    }

    @Test
    fun `individuals with more belonging percentage have more cumulative probability width`() {
        val individualsWithBelongingPercentages = listOf(50.0 to trip1, 20.0 to trip2, 30.0 to trip3)

        val individualsWithCumulativeProbabilities = calculateCumulativeProbabilities(individualsWithBelongingPercentages)

        assertEquals(50.0, individualsWithCumulativeProbabilities[0].first)
        assertEquals(70.0, individualsWithCumulativeProbabilities[1].first)
        assertEquals(100.0, individualsWithCumulativeProbabilities[2].first)
    }

    @Test
    fun `when the roulette wheel is spin, one individual is selected`() {
        val individualsWithCumulativeProbabilities = listOf(50.0 to trip1, 70.0 to trip2, 100.0 to trip3)
        mockkObject(Random)
        every { Random.pickFrom(any()) } returns 20 andThenMany listOf(55, 75, 100)

        val firstSpinWheelIndividual = spinWheel(individualsWithCumulativeProbabilities)
        val secondSpinWheelIndividual = spinWheel(individualsWithCumulativeProbabilities)
        val thirdSpinWheelIndividual = spinWheel(individualsWithCumulativeProbabilities)
        val fourthSpinWheelIndividual = spinWheel(individualsWithCumulativeProbabilities)

        assertEquals(trip1, firstSpinWheelIndividual)
        assertEquals(trip2, secondSpinWheelIndividual)
        assertEquals(trip3, thirdSpinWheelIndividual)
        assertEquals(trip3, fourthSpinWheelIndividual)
    }

    @Test
    fun `selected individuals have better fitness average than initial individuals`() {
        mockkObject(Random)
        every { Random.pickFrom(any()) } returns 55

        val selectedIndividuals = selectWithExpectedNumberControl(individuals, 3)

        val selectedIndividualsFitnessAverage = selectedIndividuals.map { it.fitness }.average()
        val individualsFitnessAverage = individuals.map { it.fitness }.average()

        assertTrue(selectedIndividualsFitnessAverage > individualsFitnessAverage)
    }
}