package utn.triponometry.domain.genetic_algorithm

import utn.triponometry.helpers.Random
import kotlin.math.floor

/*
    Selection method: Expected Number Control
    (Preselection of the best individuals + Roulette Wheel Selection)
 */

fun selectWithExpectedNumberControl(individuals: List<Individual>, individualsQty: Int): List<Individual> {
    val fitnessAverage = individuals.map { it.fitness }.average()

    val selectedIndividuals = mutableListOf<Individual>()

    val bestIndividualsPreSelection = preSelectBestIndividuals(individuals, fitnessAverage)

    selectedIndividuals.addAll(bestIndividualsPreSelection)

    val individualsWithNewFitness = calculateNewFitness(individuals, fitnessAverage)
    val rouletteSpinsAmount = individualsQty - bestIndividualsPreSelection.size
    val individualsSelectedWithRoulette = selectWithRoulette(individualsWithNewFitness, rouletteSpinsAmount)

    selectedIndividuals.addAll(individualsSelectedWithRoulette)

    return selectedIndividuals
}

fun preSelectBestIndividuals(individuals: List<Individual>, fitnessAverage: Double): List<Individual> {
    val bestIndividuals = individuals.map {
        val fitnessVsFitnessAverageRelation = it.fitness / fitnessAverage
        floor(fitnessVsFitnessAverageRelation) to it
    }.filter { it.first > 0 }

    return bestIndividuals.flatMap { (times, individual) ->
        List(times.toInt()) { individual }
    }
}

fun calculateNewFitness(individuals: List<Individual>, fitnessAverage: Double): Map<Double, Individual> {
    return individuals.map {
        val fitnessVsFitnessAverageRelation = it.fitness / fitnessAverage
        val newFitness = fitnessVsFitnessAverageRelation - floor(fitnessVsFitnessAverageRelation)

        newFitness to it
    }.toMap()
}

private fun selectWithRoulette(bestIndividualsPreSelection: Map<Double, Individual>, rouletteSpinsAmount: Int): List<Individual> {
    val totalNewFitness = bestIndividualsPreSelection.keys.sum()

    val individualsWithBelongingPercentage = calculateBelongingPercentages(bestIndividualsPreSelection, totalNewFitness)
    val individualsWithCumulativeProbability = calculateCumulativeProbabilities(individualsWithBelongingPercentage)

    return List(rouletteSpinsAmount) {
        spinWheel(individualsWithCumulativeProbability)
    }
}

fun calculateBelongingPercentages(bestIndividualsPreSelection: Map<Double, Individual>, totalNewFitness: Double) =
    bestIndividualsPreSelection.map { (newFitness, individual) ->
        val belongingPercentage = (newFitness / totalNewFitness) * 100

        belongingPercentage to individual
    }

fun calculateCumulativeProbabilities(individualsWithBelongingPercentage: List<Pair<Double, Individual>>) =
    individualsWithBelongingPercentage.mapIndexed { index, (_, individual) ->
        val previousIndividuals = individualsWithBelongingPercentage.take(index + 1).map { it.first }
        val cumulativeProbability = previousIndividuals.sum()

        cumulativeProbability to individual
    }

fun spinWheel(individualsWithCumulativeProbability: List<Pair<Double, Individual>>): Individual {
    val randomNumber = Random.pickFrom(1..100)
    val foundIndividual = individualsWithCumulativeProbability.find { (cumulativeProbability, _) -> cumulativeProbability >= randomNumber }

    if (foundIndividual == null)
        return individualsWithCumulativeProbability.last().second

    val index = individualsWithCumulativeProbability.indexOf(foundIndividual)
    return individualsWithCumulativeProbability[index].second
}