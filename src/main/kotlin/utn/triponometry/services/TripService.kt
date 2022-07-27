package utn.triponometry.services

import com.google.maps.model.TravelMode
import org.springframework.stereotype.Service
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Place
import utn.triponometry.domain.external.GoogleApi
import utn.triponometry.domain.genetic_algorithm.GeneticAlgorithm
import utn.triponometry.domain.genetic_algorithm.Individual
import utn.triponometry.properties.TriponometryProperties

@Service
class TripService(val triponometryProperties: TriponometryProperties, val googleApi: GoogleApi) {
    fun getDurationBetween(coordinates: List<Coordinates>, travelMode: TravelMode): List<Place> {
        return googleApi.getListOfPlaces(coordinates, travelMode)
    }

    fun calculateOptimalRoute(places: List<Place>): Individual {
        val bestRoute = GeneticAlgorithm.run(triponometryProperties.geneticAlgorithm, places)

        println("Best: (Fitness = ${bestRoute.fitness}, Time = ${bestRoute.totalTime}) ${bestRoute.places.map { it.id }}")
        return bestRoute
    }
}