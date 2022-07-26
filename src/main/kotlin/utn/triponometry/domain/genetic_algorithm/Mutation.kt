package utn.triponometry.domain.genetic_algorithm

import utn.triponometry.domain.Place
import utn.triponometry.helpers.Random
import utn.triponometry.helpers.swap

/*
    Mutation method: Swap
    (Pick 2 random places and swap them)
 */

fun mutate(individuals: List<Individual>): List<Individual> {
    val (randomIndex, selectedIndividual) = pickRandomIndividual(individuals)
    val mutatedIndividual = swapRandomPlaces(selectedIndividual)

    val individualsBeforeSelected = individuals.take(randomIndex)
    val individualsAfterSelected = individuals.takeLast(individuals.size - (randomIndex + 1))

    return listOf(individualsBeforeSelected, listOf(mutatedIndividual), individualsAfterSelected).flatten()
}

private fun pickRandomIndividual(individuals: List<Individual>): Pair<Int, Individual> {
    val randomIndex = Random.pickFrom(individuals.indices)
    val individualToMutate = individuals[randomIndex]

    return Pair(randomIndex, individualToMutate)
}

private fun swapRandomPlaces(individual: Individual): Individual {
    val places = mutableListOf<Place>()
    places.addAll(individual.places)
    places.swap(Random.pickFrom(places.indices), Random.pickFrom(places.indices))

    return Individual(places)
}