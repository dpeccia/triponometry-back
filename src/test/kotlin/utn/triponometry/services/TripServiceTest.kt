package utn.triponometry.services

import com.google.gson.Gson
import com.google.maps.model.TravelMode
import io.mockk.every
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utn.triponometry.domain.*
import utn.triponometry.domain.dtos.NewTripRequest
import utn.triponometry.domain.dtos.Review
import utn.triponometry.domain.dtos.ReviewRequest
import utn.triponometry.domain.external.GoogleApi
import utn.triponometry.domain.genetic_algorithm.Individual
import utn.triponometry.helpers.IllegalTripException
import utn.triponometry.properties.TriponometryProperties
import utn.triponometry.repos.TripRepository
import utn.triponometry.repos.UserRepository
import java.io.File
import java.util.*
import kotlin.math.roundToInt

class TripServiceTest {

    private val hotel = Place(0, "Hotel", mapOf(1 to 24, 2 to 31, 3 to 33, 4 to 25, 5 to 27, 6 to 11))
    private val activity1 = Place(1, "Activity 1", mapOf(0 to 26, 2 to 24, 3 to 25, 4 to 29, 5 to 33, 6 to 16), 120)
    private val activity2 = Place(2, "Activity 2", mapOf(0 to 32, 1 to 22, 3 to 37, 4 to 36, 5 to 29, 6 to 22), 120)
    private val activity3 = Place(3, "Activity 3", mapOf(0 to 34, 1 to 20, 2 to 34, 4 to 32, 5 to 41, 6 to 22), 120)
    private val activity4 = Place(4, "Activity 4", mapOf(0 to 25, 1 to 27, 2 to 34, 3 to 30, 5 to 22, 6 to 16), 120)
    private val activity5 = Place(5, "Activity 5", mapOf(0 to 28, 1 to 32, 2 to 31, 3 to 43, 4 to 25, 6 to 21), 120)
    private val activity6 = Place(6, "Activity 6", mapOf(0 to 13, 1 to 13, 2 to 20, 3 to 22, 4 to 19, 5 to 22), 120)

    private val activities = listOf(activity6, activity3, activity1, activity2, activity5, activity4)

    val tripRepository: TripRepository = mockk()
    val userRepository: UserRepository = mockk()

    var tripService = TripService(
        TriponometryProperties(),tripRepository,userRepository, GoogleApi(TriponometryProperties())
    )

    @Test
    fun `creating route for a day`() {
        val day = Day(1, mutableListOf(hotel))

        tripService.createRouteForDay(day, activities.toMutableList(), 660)

        val expectedRoute = listOf(hotel, activity6, activity3, activity1, activity2)
        assertEquals(expectedRoute, day.route.toList())
    }

    @Test
    fun `split complete route in days`() {
        val places = mutableListOf(hotel)
        places.addAll(activities)
        val bestCompleteRoute = Individual(places)

        val timeInput = TimeInput("09:00", "21:00", 30, 30, 0, 0, 0)
        val calculatorInputs = CalculatorInputs(TravelMode.DRIVING, listOf(), timeInput)

        val days = tripService.splitCompleteRouteInDays(bestCompleteRoute, calculatorInputs)

        val expectedDays = listOf(
            Day(1, mutableListOf(hotel, activity6, activity3, activity1, activity2)),
            Day(2, mutableListOf(hotel, activity5, activity4))
        )

        /*
        In Day 1 we have:
        11 + 22 + 20 + 24 + 32 ++ 120*4 = 589 minutes

        If we want to add activity5 to Day 1:
        11 + 22 + 20 + 24 + 29 + 28 ++ 120*5 = 734 minutes

        Because 734 > timePerDay (660), activity5 is on a separate day
         */

        assertEquals(expectedDays, days)
    }

    @Test
    fun `trip is created successfully`() {
        val file = File("src/test/resources/request_new_trip.json")
        val fileContent = file.readText()
        val request = Gson().fromJson(fileContent, NewTripRequest::class.java)
        val user = User("mail@gmail.com","password")

        every { tripRepository.findByUserAndName(any(),any()) } returns Optional.empty()
        every { userRepository.findById(ObjectId("666f6f2d6261722d71757578")) } returns Optional.of(user)
        every { tripRepository.save(any()) } answers { firstArg() }

        val newTrip = tripService.createNewTrip(request, ObjectId("666f6f2d6261722d71757578"))
        assertNotNull(newTrip.id)
        assertEquals("Francia",newTrip.name)
    }

    @Test
    fun `trip is not created if it already exists`() {
        val file = File("src/test/resources/request_new_trip.json")
        val fileContent = file.readText()
        val request = Gson().fromJson(fileContent, NewTripRequest::class.java)
        val user = User("mail@gmail.com","password")
        val trip = Trip("Francia",request.calculatorInputs,user,TripStatus.ACTIVE,request.calculatorOutputs)

        every { tripRepository.findByUserAndName(any(),any()) } returns Optional.of(trip)
        every { userRepository.findById(ObjectId("666f6f2d6261722d71757578")) } returns Optional.of(user)
        every { tripRepository.save(any()) } answers { firstArg() }
        
        val exception = assertThrows<IllegalTripException> { tripService.createNewTrip(request, ObjectId("666f6f2d6261722d71757578")) }
        assertEquals("There is already a trip under that name", exception.message)
    }

    @Test
    fun `trip is updated successfully`() {
        val file = File("src/test/resources/request_new_trip.json")
        val fileContent = file.readText()
        val request = Gson().fromJson(fileContent, NewTripRequest::class.java)
        val user = User("mail@gmail.com","password")
        val trip = Trip("Francia",request.calculatorInputs,user,TripStatus.ACTIVE,request.calculatorOutputs)

        every { tripRepository.findByUserAndId(any(),any()) } returns Optional.of(trip)
        every { userRepository.findById(ObjectId("666f6f2d6261722d71757578")) } returns Optional.of(user)
        every { tripRepository.save(any()) } answers { firstArg() }

        val newTrip = tripService.updateTripStatus(ObjectId("666f6f2d6261722d71757578"),ObjectId("666f6f2d6261722d71757578"),TripStatus.DRAFT)
        assertNotNull(newTrip.id)
        assertEquals("Francia",newTrip.name)
        assertEquals(newTrip.status,TripStatus.DRAFT)
    }

    @Test
    fun `trip is not updated if it doesn't exist`() {
        val file = File("src/test/resources/request_new_trip.json")
        val fileContent = file.readText()
        val request = Gson().fromJson(fileContent, NewTripRequest::class.java)
        val user = User("mail@gmail.com","password")
        val trip = Trip("Francia",request.calculatorInputs,user,TripStatus.ACTIVE,request.calculatorOutputs)

        every { tripRepository.findByUserAndId(any(),any()) } returns Optional.empty()
        every { userRepository.findById(ObjectId("666f6f2d6261722d71757578")) } returns Optional.of(user)

        val exception = assertThrows<IllegalTripException> {
            tripService.updateTripStatus(ObjectId("666f6f2d6261722d71757578"),ObjectId("666f6f2d6261722d71757578"),TripStatus.DRAFT)
        }
        assertEquals("A trip under that id does not exist", exception.message)

    }

    @Test
    fun `calculating the user's time per day when there are meal times`() {
        val timeInput = TimeInput("09:00", "22:30", 30, 30, 60, 30, 0)

        val timePerDay = tripService.calculateTimePerDay(timeInput)

        assertEquals(660, timePerDay) // 11 hours = 660 minutes
    }

    @Test
    fun `calculating the user's time per day when there are no meal times`() {
        val timeInput = TimeInput("09:00", "21:00", 0, 0, 0, 0, 0)
        val timeInput2 = TimeInput("07:00", "23:30", 0, 0, 0, 0, 0)

        val timePerDay = tripService.calculateTimePerDay(timeInput)
        val timePerDay2 = tripService.calculateTimePerDay(timeInput2)

        assertEquals(720, timePerDay) // 12 hours = 720 minutes
        assertEquals(990, timePerDay2) // 16 hours and 30 minutes = 990 minutes

    }

    @Test
    fun `trip is completed if status is ACTIVE or ARCHIVED`() {
        val file = File("src/test/resources/request_new_trip.json")
        val fileContent = file.readText()
        val request = Gson().fromJson(fileContent, NewTripRequest::class.java)
        val user = User("mail@gmail.com","password")
        val trip = Trip("Francia",request.calculatorInputs,user,TripStatus.ACTIVE,request.calculatorOutputs)

        assertTrue(trip.isComplete())

        trip.status = TripStatus.ARCHIVED
        assertTrue(trip.isComplete())

        trip.status = TripStatus.DRAFT
        assertFalse(trip.isComplete())
    }

    @Test
    fun `trip is created as draft if it doesn't contain calculator outputs`() {
        val file = File("src/test/resources/request_new_draft.json")
        val fileContent = file.readText()
        val request = Gson().fromJson(fileContent, NewTripRequest::class.java)
        val user = User("mail@gmail.com","password")

        every { tripRepository.findByUserAndName(any(),any()) } returns Optional.empty()
        every { userRepository.findById(ObjectId("666f6f2d6261722d71757578")) } returns Optional.of(user)
        every { tripRepository.save(any()) } answers { firstArg() }

        val newTrip = tripService.createNewDraft(request, ObjectId("666f6f2d6261722d71757578"))
        assertNotNull(newTrip.id)
        assertEquals("Francia",newTrip.name)
        assertEquals(TripStatus.DRAFT,newTrip.status)
    }

    @Test
    fun `trip draft is updated successfully`() {
        val file = File("src/test/resources/request_new_draft.json")
        val fileContent = file.readText()
        val request = Gson().fromJson(fileContent, NewTripRequest::class.java)
        request.name = "Francia - Paris"
        val user = User("mail@gmail.com","password")
        val trip = Trip("Francia",request.calculatorInputs,user,TripStatus.DRAFT)

        every { tripRepository.findByUserAndId(any(),any()) } returns Optional.of(trip)
        every { userRepository.findById(ObjectId("666f6f2d6261722d71757578")) } returns Optional.of(user)
        every { tripRepository.save(any()) } answers { firstArg() }

        val newDraft = tripService.updateTrip(ObjectId("666f6f2d6261722d71757578"),ObjectId("666f6f2d6261722d71757578"),request)
        assertNotNull(newDraft.id)
        assertEquals("Francia - Paris",newDraft.name)
        assertEquals(TripStatus.DRAFT,newDraft.status)
        assertNull(newDraft.calculatorOutputs)
    }

    @Test
    fun `trip draft is not updated if it doesn't exist`() {
        val file = File("src/test/resources/request_new_draft.json")
        val fileContent = file.readText()
        val request = Gson().fromJson(fileContent, NewTripRequest::class.java)
        val user = User("mail@gmail.com","password")

        every { userRepository.findById(ObjectId("666f6f2d6261722d71757578")) } returns Optional.of(user)
        every { tripRepository.findByUserAndId(any(),any()) } returns Optional.empty()

        val exception = assertThrows<IllegalTripException> {
            tripService.updateTrip(ObjectId("666f6f2d6261722d71757578"),ObjectId("666f6f2d6261722d71757578"),request)
        }
        assertEquals("A trip under that id does not exist", exception.message)
    }

    @Test
    fun `A review is added successfully to a trip`() {
        val file = File("src/test/resources/request_new_trip.json")
        val fileContent = file.readText()
        val request = Gson().fromJson(fileContent, NewTripRequest::class.java)
        val user = User("mail@gmail.com","password")
        val trip = Trip("Francia",request.calculatorInputs,user,TripStatus.ACTIVE,request.calculatorOutputs)

        val userId = ObjectId("6312585af244650fd3c36762")
        val tripId = ObjectId("631258770e6cb8702cb599b4")
        every { tripRepository.findById(tripId) } returns Optional.of(trip)
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { tripRepository.save(any()) } answers { firstArg() }

        val reviewRequest = ReviewRequest(4,true,"Muy bueno!")

        val review = tripService.addReview(userId, tripId,reviewRequest)

        assertNotNull(review.id)
        assertEquals(1,trip.reviews.size)
        assertEquals(review.id,trip.reviews[0].id.toString())
    }

    @Test
    fun `A review is not added because trip doesn't exist`() {
        val user = User("mail@gmail.com","password")

        val userId = ObjectId("6312585af244650fd3c36762")
        val tripId = ObjectId("631258770e6cb8702cb599b4")

        every { tripRepository.findById(tripId) } returns Optional.empty()
        every { userRepository.findById(userId) } returns Optional.of(user)

        val reviewRequest = ReviewRequest(4,true,"Muy bueno!")

        val exception = assertThrows<IllegalTripException> {
            tripService.addReview(userId, tripId,reviewRequest)
        }
        assertEquals("A trip under that id does not exist", exception.message)
    }

}