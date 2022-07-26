package utn.triponometry.services

import org.springframework.stereotype.Service
import utn.triponometry.domain.Place
import utn.triponometry.domain.genetic_algorithm.GeneticAlgorithm
import utn.triponometry.domain.genetic_algorithm.Individual
import utn.triponometry.properties.TriponometryProperties

@Service
class TripService(val triponometryProperties: TriponometryProperties) {
    fun runGeneticAlgorithm(): Individual {
        val places = listOf(
            Place(1, "Plaza Aristobulo del Valle", mapOf(2 to 21, 3 to 27, 4 to 14, 5 to 24, 6 to 18, 7 to 28, 8 to 18)),
            Place(2, "UTN Lugano", mapOf(1 to 19, 3 to 11, 4 to 18, 5 to 18, 6 to 21, 7 to 20, 8 to 25)),
            Place(3, "Hospital Garrahan", mapOf(2 to 14, 1 to 27, 4 to 24, 5 to 16, 6 to 27, 7 to 14, 8 to 28)),
            Place(4, "Parque Yrigoyen", mapOf(2 to 16, 3 to 22, 1 to 13, 5 to 24, 6 to 9, 7 to 19, 8 to 13)),
            Place(5, "UTN Medrano", mapOf(2 to 19, 3 to 13, 4 to 23, 1 to 20, 6 to 24, 7 to 12, 8 to 16)),
            Place(6, "Dot Baires Shopping", mapOf(2 to 23, 3 to 25, 4 to 9, 5 to 21, 1 to 20, 7 to 14, 8 to 10)),
            Place(7, "Retiro", mapOf(2 to 22, 3 to 15, 4 to 21, 5 to 10, 6 to 20, 1 to 31, 8 to 18)),
            Place(8, "Hospital Pirovano", mapOf(2 to 26, 3 to 25, 4 to 13, 5 to 17, 6 to 9, 7 to 22, 1 to 18))
        )

        return GeneticAlgorithm.run(triponometryProperties.geneticAlgorithm, places)
    }
}