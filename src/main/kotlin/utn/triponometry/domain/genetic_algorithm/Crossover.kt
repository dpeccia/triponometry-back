package utn.triponometry.domain.genetic_algorithm

import utn.triponometry.domain.Place
import utn.triponometry.helpers.Random
import java.util.ArrayList

/*
    Crossover method: Order Crossover (OX)
 */

fun doCrossover(bestIndividuals: List<Individual>): List<Individual> {
    val listOfParents = bestIndividuals.chunked(2)

    return listOfParents.flatMap { parents ->
        orderCrossover(parents.first(), parents.last())
    }
}

fun orderCrossover(individual1: Individual, individual2: Individual): List<Individual> {
    val state = CrossoverState(individual1.places.toTypedArray(), individual2.places.toTypedArray())

    val totalCities = state.parent1.size
    val firstPoint = Random.pickFrom(state.parent1.indices)
    val secondPoint =
        if (firstPoint == totalCities)
            firstPoint
        else
            Random.pickFrom(firstPoint until totalCities)

    // Inherit the cities before and after the points selected.
    inheritPlaces(firstPoint, secondPoint, totalCities, state)

    // Get the cities of the opposite parent if the child does not already contain them.
    getPlacesFromOppositeParent(firstPoint, secondPoint, state)

    // Find all the cities that are still missing from each child.
    findMissingChildrenPlaces(totalCities, state)

    // Find which spots are still empty in each child.
    findEmptyChildrenSpots(totalCities, state)

    // Fill in the empty spots.
    fillEmptySpots(state)

    val firstChild = Individual(state.child1.toList() as List<Place>)
    val secondChild = Individual(state.child2.toList() as List<Place>)

    return listOf(firstChild, secondChild)
}

fun inheritPlaces(firstPoint: Int, secondPoint: Int, totalCities: Int, state: CrossoverState) {
    inheritPlacesInRange(0, firstPoint, state)
    inheritPlacesInRange(secondPoint, totalCities, state)
}

private fun inheritPlacesInRange(firstPosition: Int, lastPosition: Int, state: CrossoverState) {
    for (i in firstPosition until lastPosition) {
        state.child1[i] = state.parent1[i]
        state.child2[i] = state.parent2[i]
        state.citiesInChild1.add(state.parent1[i])
        state.citiesInChild2.add(state.parent2[i])
    }
}

fun getPlacesFromOppositeParent(firstPoint: Int, secondPoint: Int, state: CrossoverState) {
    for (i in firstPoint until secondPoint) {
        if (!state.citiesInChild1.contains(state.parent2[i])) {
            state.citiesInChild1.add(state.parent2[i])
            state.child1[i] = state.parent2[i]
        }
        if (!state.citiesInChild2.contains(state.parent1[i])) {
            state.citiesInChild2.add(state.parent1[i])
            state.child2[i] = state.parent1[i]
        }
    }
}

fun findMissingChildrenPlaces(totalCities: Int, state: CrossoverState) {
    for (i in 0 until totalCities) {
        if (!state.citiesInChild1.contains(state.parent2[i])) {
            state.citiesNotInChild1.add(state.parent2[i])
        }
        if (!state.citiesInChild2.contains(state.parent1[i])) {
            state.citiesNotInChild2.add(state.parent1[i])
        }
    }
}

fun findEmptyChildrenSpots(totalCities: Int, state: CrossoverState) {
    for (i in 0 until totalCities) {
        if (state.child1[i] == null) {
            state.emptySpotsChild1.add(i)
        }
        if (state.child2[i] == null) {
            state.emptySpotsChild2.add(i)
        }
    }
}

fun fillEmptySpots(state: CrossoverState) {
    for (city in state.citiesNotInChild1) {
        state.child1[state.emptySpotsChild1.removeAt(0)] = city
    }
    for (city in state.citiesNotInChild2) {
        state.child2[state.emptySpotsChild2.removeAt(0)] = city
    }
}

data class CrossoverState(
    val parent1: Array<Place>,
    val parent2: Array<Place>,
    val child1: Array<Place?> = arrayOfNulls(parent1.size),
    val child2: Array<Place?> = arrayOfNulls(parent2.size),
    val citiesInChild1: MutableList<Place> = mutableListOf(),
    val citiesInChild2: MutableList<Place> = mutableListOf(),
    val citiesNotInChild1: MutableList<Place> = mutableListOf(),
    val citiesNotInChild2: MutableList<Place> = mutableListOf(),
    val emptySpotsChild1: ArrayList<Int> = arrayListOf(),
    val emptySpotsChild2: ArrayList<Int> = arrayListOf()
)