package utn.triponometry.services

import com.google.maps.model.TravelMode
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import utn.triponometry.domain.*
import utn.triponometry.domain.dtos.NewTripRequest
import utn.triponometry.domain.dtos.TripsResponse
import utn.triponometry.domain.external.CalendarAdapter
import utn.triponometry.domain.external.Directions
import utn.triponometry.domain.external.GoogleApi
import utn.triponometry.domain.external.Storage
import utn.triponometry.domain.external.dtos.AgendaRequest
import utn.triponometry.domain.external.dtos.TripDto
import utn.triponometry.domain.external.dtos.TripServiceResponse
import utn.triponometry.domain.genetic_algorithm.GeneticAlgorithm
import utn.triponometry.domain.genetic_algorithm.Individual
import utn.triponometry.helpers.IllegalTripException
import utn.triponometry.helpers.IllegalUserException
import utn.triponometry.properties.TriponometryProperties
import utn.triponometry.repos.TripRepository
import utn.triponometry.repos.UserRepository

@Service
class TripService(
    val triponometryProperties: TriponometryProperties,
    val tripRepository: TripRepository,
    val userRepository: UserRepository,
    val googleApi: GoogleApi
) {
    fun calculateOptimalRoute(calculatorInputs: CalculatorInputs): TripServiceResponse {
        val places = getDurationBetween(calculatorInputs.places, calculatorInputs.travelMode)
        val bestCompleteRoute = calculateCompleteRoute(places)
        val optimalRouteInDays = splitCompleteRouteInDays(bestCompleteRoute, calculatorInputs)
        val xmlMap = getMapFileData(optimalRouteInDays, calculatorInputs.travelMode)
        val idOfKml = Storage(triponometryProperties).createAgenda(AgendaRequest( xmlMap))
        val listOfEvents = CalendarAdapter().getListOfEvents(optimalRouteInDays,calculatorInputs)
        return TripServiceResponse(idOfKml,listOfEvents)
    }

    fun getDurationBetween(placesInputs: List<PlaceInput>, travelMode: TravelMode): List<Place> {
        return googleApi.getListOfPlaces(placesInputs, travelMode)
    }

    private fun calculateCompleteRoute(places: List<Place>): Individual {
        val bestCompleteRoute = GeneticAlgorithm.run(triponometryProperties.geneticAlgorithm, places)

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

    fun createNewTrip(newTripRequest: NewTripRequest, userId: ObjectId): TripDto {
        val user = userRepository.findById(userId).get()
        if(tripRepository.findByUserAndName(user,newTripRequest.name).isPresent){
            throw IllegalTripException("There is already a trip under that name")
        }
        val trip = Trip(newTripRequest.name ,newTripRequest.calculatorInputs, newTripRequest.calculatorOutputs, user,TripStatus.ACTIVE)
        return tripRepository.save(trip).dto()
    }

    fun getAllTrips(): List<TripDto> {
        return tripRepository.findAll().map{ trip -> trip.dto()}
    }

    fun getTrips(userId: ObjectId): TripsResponse {
        val user = userRepository.findById(userId).get()
        val trips = tripRepository.findByUser(user)

        val active = trips.filter { t -> t.isStatus(TripStatus.ACTIVE) }.map { t -> t.dto() }
        val archived = trips.filter { t -> t.isStatus(TripStatus.ARCHIVED) }.map { t -> t.dto() }
        val draft = trips.filter { t -> t.isStatus(TripStatus.DRAFT) }.map { t -> t.dto() }
        return TripsResponse(active,archived,draft)
    }

    fun updateTripStatus(userId: ObjectId, id: ObjectId, newStatus: TripStatus): TripDto {
        val user = userRepository.findById(userId).get()
        val tripOptional = tripRepository.findByUserAndId(user,id)

        if(tripOptional.isPresent){
            val trip = tripOptional.get()
            trip.status = newStatus
            return  tripRepository.save(trip).dto()
        }
            throw IllegalTripException("A trip under that name does not exist")
    }




}