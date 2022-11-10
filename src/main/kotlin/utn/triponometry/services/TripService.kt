package utn.triponometry.services

import com.google.maps.model.TravelMode
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import utn.triponometry.domain.*
import utn.triponometry.domain.dtos.*
import utn.triponometry.domain.external.CalendarAdapter
import utn.triponometry.domain.external.MapCreator
import utn.triponometry.domain.external.GoogleApi
import utn.triponometry.domain.external.Storage
import utn.triponometry.domain.external.dtos.AgendaRequest
import utn.triponometry.domain.external.dtos.TripDto
import utn.triponometry.domain.external.dtos.TripServiceResponse
import utn.triponometry.domain.genetic_algorithm.GeneticAlgorithm
import utn.triponometry.domain.genetic_algorithm.Individual
import utn.triponometry.helpers.IllegalTripException
import utn.triponometry.properties.TriponometryProperties
import utn.triponometry.repos.TripRepository
import utn.triponometry.repos.UserRepository
import java.time.Duration

@Service
class TripService(
    val triponometryProperties: TriponometryProperties,
    val tripRepository: TripRepository,
    val userRepository: UserRepository,
    val googleApi: GoogleApi
) {
    fun calculateOptimalRoute(calculatorInputs: CalculatorInputs): TripServiceResponse {

        validateMealTimes(calculatorInputs.time)
        val maxDuration = calculateMaxDurationOfActivity(calculatorInputs)
        calculatorInputs.places.map { p -> validateActivityTimes(p, maxDuration) }

        val places = getDurationBetween(calculatorInputs.places, calculatorInputs.travelMode)
        val bestCompleteRoute = calculateCompleteRoute(places)
        val optimalRouteInDays = splitCompleteRouteInDays(bestCompleteRoute, calculatorInputs)
        val xmlMap = getMapFileData(optimalRouteInDays, calculatorInputs.travelMode)
        val idOfKml = Storage(triponometryProperties).createAgenda(AgendaRequest(xmlMap))
        val listOfEvents = CalendarAdapter().getListOfEvents(optimalRouteInDays, calculatorInputs)

        val daysAmount = optimalRouteInDays.size + calculatorInputs.time.freeDays
        return TripServiceResponse(daysAmount, idOfKml, listOfEvents)
    }

    fun validateActivityTimes(place: PlaceInput, maxDuration: Int) {
        if (place.timeSpent!= null && place.timeSpent > maxDuration)
            throw IllegalTripException("La duración de la actividad ${place.name} sobrepasa los límites del día")
    }

    fun calculateMaxDurationOfActivity(calculatorInputs: CalculatorInputs): Int {
        var meals = listOf(
            -Duration.between(calculatorInputs.time.startTime, calculatorInputs.time.finishTime).toMinutes().toInt(),
            calculatorInputs.time.breakfast,
            calculatorInputs.time.lunch,
            calculatorInputs.time.snack,
            calculatorInputs.time.dinner
        )

        return meals.fold(0) { total, item -> total - item }
    }

    fun validateMealTimes(times: TimeInput) {
        var meals = listOf(
            times.breakfast,
            times.lunch,
            times.snack,
            times.dinner
        )
        meals.map { m ->  if (m > 240) throw IllegalTripException("La duración máxima de las comidas es de 4 horas")
        }
    }

    fun getDurationBetween(placesInputs: List<PlaceInput>, travelMode: TravelMode): List<Place> {
        return googleApi.getListOfPlaces(placesInputs, travelMode)
    }

    fun calculateCompleteRoute(places: List<Place>): Individual {
        val bestCompleteRoute = GeneticAlgorithm.run(triponometryProperties.geneticAlgorithm, places)

        println("Best: (Fitness = ${bestCompleteRoute.fitness}, Time = ${bestCompleteRoute.totalTime}) ${bestCompleteRoute.places.map { it.id }}")
        return bestCompleteRoute
    }

    fun splitCompleteRouteInDays(bestCompleteRoute: Individual, calculatorInputs: CalculatorInputs): List<Day> {
        val timePerDay = calculateTimePerDay(calculatorInputs.time)
        val places = bestCompleteRoute.places
        val accommodation = places.first()
        val activities = places.takeLast(places.size - 1)

        val days = mutableListOf<Day>()
        val activitiesNotInRoutes = activities.toMutableList()
        var number = 1
        while (activitiesNotInRoutes.isNotEmpty()) {
            val day = Day(number, mutableListOf(accommodation))

            createRouteForDay(day, activitiesNotInRoutes, timePerDay)

            days.add(day)
            number += 1
        }

        println(days.map { day -> "Day ${day.number}: [" + day.route.joinToString(",") { it.id.toString() } + "]" })

        return days.toList()
    }

    fun calculateTimePerDay(time: TimeInput) =
        (Duration.between(time.startTime, time.finishTime)
            .toMinutes() - time.breakfast - time.lunch - time.snack - time.dinner).toInt()


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

    fun getAgendaFromAws(id: String): String {
        return Storage(triponometryProperties).getAgendaFromAws(id)
    }

    fun getMapFileData(locations: List<Day>, travelMode: TravelMode): String =
        MapCreator(googleApi).createKMLFile(locations, travelMode)

    fun createNewTrip(newTripRequest: NewTripRequest, userId: ObjectId): TripDto {
        val user = userRepository.findById(userId).get()
        if (tripRepository.findByUserAndName(user, newTripRequest.name).isPresent) {
            throw IllegalTripException("Ya existe un viaje con el nombre: ${newTripRequest.name}")
        }
        val trip = Trip(
            newTripRequest.name,
            newTripRequest.calculatorInputs,
            user,
            TripStatus.ACTIVE,
            newTripRequest.calculatorOutputs
        )
        return tripRepository.save(trip).dto()
    }

    fun createNewDraft(newTripRequest: NewTripRequest, userId: ObjectId): TripDto {
        val user = userRepository.findById(userId).get()
        if (tripRepository.findByUserAndName(user, newTripRequest.name).isPresent) {
            throw IllegalTripException("Ya existe un viaje con el nombre: ${newTripRequest.name}")
        }
        val trip = Trip(newTripRequest.name, newTripRequest.calculatorInputs, user, TripStatus.DRAFT)
        return tripRepository.save(trip).dto()
    }

    fun getAllTrips(): List<TripDto> {
        return tripRepository.findAll().filter { t -> t.isComplete() }.map { trip -> trip.dto() }
    }

    fun getTrips(userId: ObjectId): TripsResponse {
        val user = userRepository.findById(userId).get()
        val trips = tripRepository.findByUser(user)

        val active = trips.filter { t -> t.isStatus(TripStatus.ACTIVE) }.map { t -> t.dto() }
        val archived = trips.filter { t -> t.isStatus(TripStatus.ARCHIVED) }.map { t -> t.dto() }
        val draft = trips.filter { t -> t.isStatus(TripStatus.DRAFT) }.map { t -> t.dto() }
        return TripsResponse(active, archived, draft)
    }

    fun updateTripStatus(userId: ObjectId, id: ObjectId, newStatus: TripStatus): TripDto {
        val user = userRepository.findById(userId).get()
        val tripOptional = tripRepository.findByUserAndId(user, id)

        if (tripOptional.isPresent) {
            val trip = tripOptional.get()
            trip.status = newStatus
            return tripRepository.save(trip).dto()
        }
        throw IllegalTripException("El viaje al cuál querés actualizar no existe")
    }

    fun getTrip(userId: ObjectId, tripId: ObjectId): TripDto {
        val user = userRepository.findById(userId).get()
        val tripOptional = tripRepository.findByUserAndId(user, tripId)

        if (tripOptional.isPresent) {
            val trip = tripOptional.get()
            return trip.dto()
        }
        throw IllegalTripException("El viaje que querés ver no existe")
    }

    fun updateTrip(userId: ObjectId, draftId: ObjectId, newDraft: NewTripRequest): TripDto {
        val user = userRepository.findById(userId).get()
        val tripOptional = tripRepository.findByUserAndId(user, draftId)

        if (tripOptional.isPresent) {
            val trip = tripOptional.get()
            trip.calculatorInputs = newDraft.calculatorInputs
            trip.name = newDraft.name

            if (newDraft.calculatorOutputs != null) {
                trip.calculatorOutputs = newDraft.calculatorOutputs
                trip.status = TripStatus.ACTIVE
            }

            return tripRepository.save(trip).dto()
        }
        throw IllegalTripException("El viaje al cuál querés actualizar no existe")
    }

    fun shareTrip(tripId: ObjectId): TripDto {
        return tripRepository.findById(tripId).get().dto()
    }

    fun addReview(userId: ObjectId, tripId: ObjectId, reviewRequest: ReviewRequest): ReviewDto {
        val user = userRepository.findById(userId).get()
        val tripOptional = tripRepository.findById(tripId)

        if (tripOptional.isPresent) {
            val trip = tripOptional.get()

            if (trip.reviews.any { r -> r.fromUser(userId) }) {
                throw IllegalTripException("Ya opinaste sobre este viaje")
            }

            val review = Review(ObjectId(), user, reviewRequest.stars, reviewRequest.done, reviewRequest.description)
            trip.reviews.add(review)

            tripRepository.save(trip).dto()
            return review.dto()
        }
        throw IllegalTripException("El viaje al cuál querés opinar no existe")
    }

    fun deleteDraft(userId: ObjectId, tripId: ObjectId) {
        val user = userRepository.findById(userId).get()
        val trip = tripRepository.findByUserAndId(user, tripId).get()
        if (trip.status != TripStatus.DRAFT) {
            throw IllegalTripException("No es posible eliminar viajes que no sean borradores")
        }
        return tripRepository.delete(trip)
    }

    fun getActivityInfo(cityName: String, activityName: String): ActivityInfoResponse? {
        val trips = tripRepository.findAllTripsThatContainsActivity(cityName, activityName)
        if (trips.isEmpty()) return null

        val activityTimeSpentList = trips.mapNotNull { trip ->
            trip.calculatorInputs.activities?.find { it.name == activityName }?.timeSpent
        }

        return ActivityInfoResponse(
            trips.size, activityTimeSpentList.minOf { it }, activityTimeSpentList.maxOf { it }
        )
    }

    fun updateImage(userId: ObjectId, tripId: ObjectId, image: String): TripDto {
        val user = userRepository.findById(userId).get()
        val tripOptional = tripRepository.findByUserAndId(user, tripId)
        if (tripOptional.isPresent) {
            val trip = tripOptional.get()
            trip.calculatorInputs.city.imageUrl = image
            return tripRepository.save(trip).dto()
        }
        throw IllegalTripException("El viaje al cuál le querés cambiar la imagen no existe")
    }
}