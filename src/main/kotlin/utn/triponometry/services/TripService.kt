package utn.triponometry.services

import com.google.maps.model.TravelMode
import org.springframework.stereotype.Service
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Day
import utn.triponometry.domain.Place
import utn.triponometry.domain.external.Directions
import utn.triponometry.domain.external.GoogleApi
import utn.triponometry.domain.genetic_algorithm.GeneticAlgorithm
import utn.triponometry.domain.genetic_algorithm.Individual
import utn.triponometry.properties.TriponometryProperties

@Service
class TripService(val triponometryProperties: TriponometryProperties, val googleApi: GoogleApi) {
    fun getDurationBetween(coordinates: List<Coordinates>, travelMode: TravelMode): List<Place> {
        return googleApi.getListOfPlaces(coordinates, travelMode)
    }

    fun calculateOptimalRoute(calculatorInputs: CalculatorInputs): List<Day> {
        val bestCompleteRoute = calculateCompleteRoute(calculatorInputs)
        return splitCompleteRouteInDays(bestCompleteRoute, calculatorInputs)
    }

    private fun calculateCompleteRoute(calculatorInputs: CalculatorInputs): Individual {
        val bestCompleteRoute = GeneticAlgorithm.run(triponometryProperties.geneticAlgorithm, calculatorInputs.places)

        println("Best: (Fitness = ${bestCompleteRoute.fitness}, Time = ${bestCompleteRoute.totalTime}) ${bestCompleteRoute.places.map { it.id }}")
        return bestCompleteRoute
    }

    fun splitCompleteRouteInDays(bestCompleteRoute: Individual, calculatorInputs: CalculatorInputs): List<Day> {
        val places = bestCompleteRoute.places
        val accommodation = places.first()
        val activities = places.takeLast(places.size - 1)

        val days = mutableListOf<Day>()
        val activitiesNotInRoutes = activities.toMutableList()
        var number = 1
        while (activitiesNotInRoutes.isNotEmpty()) {
            val day = Day(number, mutableListOf(accommodation))

            createRouteForDay(day, activitiesNotInRoutes, calculatorInputs.timePerDay)

            days.add(day)
            number += 1
        }

        println(days.map { day -> "Day ${day.number}: [" + day.route.joinToString(",") { it.id.toString() } + "]" })

        return days.toList()
    }

    fun createRouteForDay(day: Day, activitiesNotInRoutes: MutableList<Place>, timePerDay: Int) {
        val activitiesAlreadyInRoute = mutableListOf<Place>()

        run block@{
            activitiesNotInRoutes.forEach { activity ->
                if (!day.hasSpaceFor(activity, timePerDay))
                    return@block

                day.addToRoute(activity)
                activitiesAlreadyInRoute.add(activity)
            }
        }

        activitiesNotInRoutes.removeAll(activitiesAlreadyInRoute)
    }


    fun getMapFileData(locations: List<Day>, travelMode: TravelMode): String =
        Directions(triponometryProperties, googleApi).createKMLFile(locations, travelMode)

}