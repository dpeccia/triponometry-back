package utn.triponometry.services

import com.google.maps.model.TravelMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.Day
import utn.triponometry.domain.Place
import utn.triponometry.domain.genetic_algorithm.Individual

@SpringBootTest
class TripServiceTest {
    @Autowired
    lateinit var tripService: TripService

    private val hotel = Place(0, "Hotel", mapOf(1 to 24, 2 to 31, 3 to 33, 4 to 25, 5 to 27, 6 to 11))
    private val activity1 = Place(1, "Activity 1", mapOf(0 to 26, 2 to 24, 3 to 25, 4 to 29, 5 to 33, 6 to 16), 120)
    private val activity2 = Place(2, "Activity 2", mapOf(0 to 32, 1 to 22, 3 to 37, 4 to 36, 5 to 29, 6 to 22), 120)
    private val activity3 = Place(3, "Activity 3", mapOf(0 to 34, 1 to 20, 2 to 34, 4 to 32, 5 to 41, 6 to 22), 120)
    private val activity4 = Place(4, "Activity 4", mapOf(0 to 25, 1 to 27, 2 to 34, 3 to 30, 5 to 22, 6 to 16), 120)
    private val activity5 = Place(5, "Activity 5", mapOf(0 to 28, 1 to 32, 2 to 31, 3 to 43, 4 to 25, 6 to 21), 120)
    private val activity6 = Place(6, "Activity 6", mapOf(0 to 13, 1 to 13, 2 to 20, 3 to 22, 4 to 19, 5 to 22), 120)

    private val activities = listOf(activity6, activity3, activity1, activity2, activity5, activity4)
    private val timePerDay = 660 // minutes (11 hours)

    @Test
    fun `creating route for a day`() {
        val day = Day(1, mutableListOf(hotel))

        tripService.createRouteForDay(day, activities.toMutableList(), timePerDay)

        val expectedRoute = listOf(hotel, activity6, activity3, activity1, activity2)
        assertEquals(expectedRoute, day.route.toList())
    }

    @Test
    fun `split complete route in days`() {
        val places = mutableListOf(hotel)
        places.addAll(activities)
        val bestCompleteRoute = Individual(places)
        val calculatorInputs = CalculatorInputs(0,0, timePerDay, TravelMode.DRIVING, listOf())

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
}