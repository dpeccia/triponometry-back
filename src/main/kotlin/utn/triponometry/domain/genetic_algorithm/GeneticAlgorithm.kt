package utn.triponometry.domain.genetic_algorithm

import utn.triponometry.domain.Place
import utn.triponometry.helpers.Random
import utn.triponometry.properties.GeneticAlgorithm as Properties

object GeneticAlgorithm {
    fun run(properties: Properties, places: List<Place>): Individual {
        // Config
        val individualsQty = properties.individualsQty
        val cyclesQty = properties.cyclesQty

        // Initial Population
        val individuals = generateInitialPopulation(places, individualsQty)

        return runCycle(individuals, individualsQty, cyclesQty, 1)
    }

    fun runCycle(individuals: List<Individual>, individualsQty: Int, cyclesQty: Int, cycle: Int, previousCycleBest: Individual? = null): Individual {
        // Selection
        val bestIndividuals = selectWithExpectedNumberControl(individuals, individualsQty)

        // Crossover
        val children = doCrossover(bestIndividuals)

        // Mutation
        val randomNumber = Random.pickFrom(1..100)
        var childsAfterMutation = children
        if (randomNumber > 65)
            childsAfterMutation = mutate(children)

        val bestOfCycle = childsAfterMutation.maxByOrNull { it.fitness }!!
        
        val newBest = if (previousCycleBest == null || previousCycleBest.fitness < bestOfCycle.fitness) bestOfCycle else previousCycleBest

        return if (cycle < cyclesQty)
            runCycle(childsAfterMutation, individualsQty, cyclesQty, cycle + 1, newBest)
        else
            newBest
    }

    fun generateInitialPopulation(places: List<Place>, individualsQty: Int): List<Individual> {
        return List(individualsQty) {
            val shuffledPlaces = places.shuffled()
            Individual(shuffledPlaces)
        }
    }
}